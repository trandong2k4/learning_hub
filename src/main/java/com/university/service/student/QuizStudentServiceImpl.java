package com.university.service.student;

import com.university.dto.request.student.QuizAnswerSaveItemRequest;
import com.university.dto.request.student.QuizAttemptEventRequest;
import com.university.dto.request.student.QuizAutoSaveRequest;
import com.university.dto.response.student.QuizAttemptStudentResponse;
import com.university.dto.response.student.*;
import com.university.entity.*;
import com.university.enums.AttemptActionEnum;
import com.university.enums.QuizStatusEnum;
import com.university.exception.SimpleMessageException;
import com.university.exception.students.AlreadySubmittedException;
import com.university.exception.students.QuizNotOpenException;
import com.university.exception.students.ResourceNotFoundException;
import com.university.mapper.student.QuizStudentMapper;
import com.university.repository.student.*;
import com.university.util.students.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizStudentServiceImpl implements QuizStudentService {

    private static final int SECONDS_PER_MINUTE = 60;

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AttemptAnswersRepository attemptAnswersRepository;
    private final AttemptAnswersLogRepository attemptAnswersLogRepository;
    private final AnswersRepository answersRepository;
    private final QuestionsRepository questionsRepository;
    private final QuizAttemptLogRepository quizAttemptLogRepository;
    private final UserRepository usersRepository;
    private final QuizStudentMapper mapper;

    // ================================================================
    // 👉 Helper: lấy HocVien từ JWT
    // ================================================================
    private HocVien getCurrentHocVien() {
        String username = SecurityUtil.getCurrentUsername();

        Users users = usersRepository.findByUserName(username)
                .orElseThrow(() -> new SimpleMessageException("Người dùng không tồn tại"));

        HocVien hocVien = users.getHocVien();
        if (hocVien == null) {
            throw new SimpleMessageException("Tài khoản này không phải học viên");
        }

        return hocVien;
    }

    // ================================================================
    // 📌 1. DANH SÁCH QUIZ
    // ================================================================
    @Override
    @Transactional(readOnly = true)
    public Page<QuizListStudentResponse> getQuizList(UUID lopHocPhanId, Pageable pageable) {
        HocVien hocVien = getCurrentHocVien();
        Page<Quiz> quizPage = quizRepository.findByLopHocPhan_Id(lopHocPhanId, pageable);
        Map<UUID, QuizAttempt> latestAttempts = getLatestAttemptsByQuiz(quizPage.getContent(), hocVien.getId());

        List<QuizListStudentResponse> content = quizPage.getContent().stream()
                .map(quiz -> mapper.toQuizListDTO(
                        quiz,
                        resolveQuizStatus(quiz, latestAttempts.get(quiz.getId())),
                        latestAttempts.get(quiz.getId())))
                .toList();

        return new PageImpl<>(content, pageable, quizPage.getTotalElements());
    }

    // ================================================================
    // 📌 2. QUIZ ĐANG MỞ
    // ================================================================
    @Override
    @Transactional(readOnly = true)
    public List<QuizListStudentResponse> getQuizDangMo(UUID lopHocPhanId) {
        HocVien hocVien = getCurrentHocVien();
        List<Quiz> quizzes = quizRepository.findDangMoByLopHocPhanId(lopHocPhanId, LocalDateTime.now());
        Map<UUID, QuizAttempt> latestAttempts = getLatestAttemptsByQuiz(quizzes, hocVien.getId());

        return quizzes
                .stream()
                .map(quiz -> mapper.toQuizListDTO(
                        quiz,
                        resolveQuizStatus(quiz, latestAttempts.get(quiz.getId())),
                        latestAttempts.get(quiz.getId())))
                .toList();
    }

    // ================================================================
    // 📌 3. CHI TIẾT QUIZ
    // ================================================================
    @Override
    @Transactional(readOnly = true)
    public QuizDetailStudentResponse getQuizDetail(UUID quizId) {
        HocVien hocVien = getCurrentHocVien();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));
        QuizAttempt latestAttempt = quizAttemptRepository
                .findTopByQuiz_IdAndHocVien_IdOrderByStartTimeDesc(quizId, hocVien.getId())
                .orElse(null);
        return mapper.toQuizDetailDTO(quiz, calculateRemainingTime(quiz, latestAttempt));
    }

    // ================================================================
    // 📌 4. TÌM KIẾM QUIZ
    // ================================================================
    @Override
    @Transactional(readOnly = true)
    public Page<QuizListStudentResponse> searchQuiz(UUID lopHocPhanId, String keyword, Pageable pageable) {
        HocVien hocVien = getCurrentHocVien();
        Page<Quiz> quizPage = quizRepository.searchByTieuDe(lopHocPhanId, keyword, pageable);
        Map<UUID, QuizAttempt> latestAttempts = getLatestAttemptsByQuiz(quizPage.getContent(), hocVien.getId());

        List<QuizListStudentResponse> content = quizPage.getContent().stream()
                .map(quiz -> mapper.toQuizListDTO(
                        quiz,
                        resolveQuizStatus(quiz, latestAttempts.get(quiz.getId())),
                        latestAttempts.get(quiz.getId())))
                .toList();

        return new PageImpl<>(content, pageable, quizPage.getTotalElements());
    }

    // ================================================================
    // 📌 5. BẮT ĐẦU QUIZ
    // ================================================================
    @Override
    @Transactional
    public QuizStartStudentResponse startQuiz(UUID quizId) {

        // 👉 1. Lấy học viên từ JWT
        HocVien hocVien = getCurrentHocVien();

        // 👉 2. Lấy quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));

        // 👉 3. Kiểm tra thời gian
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getThoiGianBatDau() != null && now.isBefore(quiz.getThoiGianBatDau())) {
            throw new QuizNotOpenException("Quiz chưa bắt đầu");
        }
        if (quiz.getThoiGianKetThuc() != null && now.isAfter(quiz.getThoiGianKetThuc())) {
            throw new QuizNotOpenException("Quiz đã kết thúc");
        }

        // 👉 4. Có attempt đang dở → trả về luôn, không tạo mới
        Optional<QuizAttempt> attemptDangDo = quizAttemptRepository
                .findOpenByQuizIdAndHocVienIdWithQuizAndHocVien(quizId, hocVien.getId());
        if (attemptDangDo.isPresent()) {
            QuizAttempt existing = attemptDangDo.get();
            existing.setRemainingTime(calculateRemainingTime(quiz, existing));
            existing.setUsedTime(calculateUsedTime(quiz, existing, existing.getRemainingTime()));
            quizAttemptRepository.save(existing);
            logAttempt(existing, AttemptActionEnum.RESUME, null, null, null);
            return toQuizStartResponse(existing);
        }

        // 👉 5. Kiểm tra số lần làm
        long attemptCount = quizAttemptRepository.countByQuiz_IdAndHocVien_Id(quizId, hocVien.getId());
        if (quiz.getSoLanLam() != null && quiz.getSoLanLam() > 0 && attemptCount >= quiz.getSoLanLam()) {
            throw new AlreadySubmittedException("Bạn đã hết số lần làm bài kiểm tra này");
        }

        // 👉 6. Tạo attempt mới
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setHocVien(hocVien);
        attempt.setStartTime(now);
        attempt.setStatus(false);
        attempt.setAttemptNumber((int) attemptCount + 1);
        attempt.setRemainingTime(quiz.getThoiGianLam() == null ? 0 : quiz.getThoiGianLam() * SECONDS_PER_MINUTE);
        attempt.setUsedTime(0);
        quizAttemptRepository.save(attempt);

        if ("random_question".equalsIgnoreCase(quiz.getQuizType())) {
            createRandomQuestionSlots(attempt);
        }

        logAttempt(attempt, AttemptActionEnum.START, null, null, null);

        return toQuizStartResponse(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptStudentResponse getAttempt(UUID attemptId) {
        HocVien hocVien = getCurrentHocVien();
        QuizAttempt attempt = quizAttemptRepository.findByIdWithQuizAndHocVien(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", attemptId));
        validateAttemptOwner(attempt, hocVien);
        return toAttemptResponse(attempt);
    }

    @Override
    @Transactional
    public QuizAttemptStudentResponse autoSaveAnswers(UUID attemptId, QuizAutoSaveRequest request) {
        HocVien hocVien = getCurrentHocVien();
        QuizAttempt attempt = quizAttemptRepository.findByIdWithQuizAndHocVien(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", attemptId));
        validateAttemptOwner(attempt, hocVien);
        ensureAttemptOpen(attempt);

        List<QuizAnswerSaveItemRequest> answers = request == null || request.getAnswers() == null
                ? List.of()
                : request.getAnswers();
        saveAttemptAnswers(attempt, answers);

        int remaining = calculateRemainingTime(attempt.getQuiz(), attempt);
        attempt.setRemainingTime(remaining);
        attempt.setUsedTime(request != null && request.getUsedTime() != null
                ? request.getUsedTime()
                : calculateUsedTime(attempt.getQuiz(), attempt, remaining));
        quizAttemptRepository.save(attempt);
        logAttempt(attempt, AttemptActionEnum.AUTO_SAVE, null, null, request != null ? request.getEventData() : null);
        return toAttemptResponse(attempt);
    }

    @Override
    @Transactional
    public void logAttemptEvent(UUID attemptId, QuizAttemptEventRequest request) {
        HocVien hocVien = getCurrentHocVien();
        QuizAttempt attempt = quizAttemptRepository.findByIdWithQuizAndHocVien(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", attemptId));
        validateAttemptOwner(attempt, hocVien);

        Questions question = null;
        if (request != null && request.getQuestionId() != null) {
            question = questionsRepository.findById(request.getQuestionId()).orElse(null);
        }
        AttemptActionEnum action = request != null && request.getAction() != null
                ? request.getAction()
                : AttemptActionEnum.NAVIGATE_TO;
        logAttempt(attempt, action, question, request != null ? request.getValue() : null,
                request != null ? request.getEventData() : null);
    }

    // ================================================================
    // 📌 6. NỘP BÀI
    // ================================================================
    @Override
    @Transactional
    public QuizResultStudentResponse submitQuiz(UUID attemptId, Map<UUID, UUID> answers) {
        List<QuizAnswerSaveItemRequest> payload = answers == null
                ? List.of()
                : answers.entrySet().stream().map(entry -> {
                    QuizAnswerSaveItemRequest item = new QuizAnswerSaveItemRequest();
                    item.setQuestionId(entry.getKey());
                    item.setAnswerId(entry.getValue());
                    return item;
                }).toList();
        return submitQuiz(attemptId, payload);
    }

    @Override
    @Transactional
    public QuizResultStudentResponse submitQuiz(UUID attemptId, List<QuizAnswerSaveItemRequest> answers) {
        HocVien hocVien = getCurrentHocVien();
        QuizAttempt attempt = quizAttemptRepository.findByIdWithQuizAndHocVien(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", attemptId));
        validateAttemptOwner(attempt, hocVien);
        ensureAttemptOpen(attempt);

        saveAttemptAnswers(attempt, answers == null ? List.of() : answers);

        List<Questions> quizQuestions = getQuestionsForAttempt(attempt);
        int correct = 0;
        int autoGradable = 0;

        Map<UUID, AttemptAnswers> answerByQuestion = attemptAnswersRepository
                .findByQuizAttemptIdWithQuestionAnswerData(attempt.getId())
                .stream()
                .collect(Collectors.toMap(a -> a.getQuestions().getId(), a -> a, (a, b) -> a));

        for (Questions question : quizQuestions) {
            AttemptAnswers attemptAnswer = answerByQuestion.get(question.getId());
            if (Boolean.TRUE.equals(question.getLoaiCauHoi())) {
                autoGradable++;
                boolean isCorrect = attemptAnswer != null
                        && attemptAnswer.getAnswers() != null
                        && Boolean.TRUE.equals(attemptAnswer.getAnswers().getIsCorrect());
                if (attemptAnswer != null) {
                    attemptAnswer.setIsCorrect(isCorrect);
                    attemptAnswer.setScoreReceived(BigDecimal.valueOf(isCorrect ? (question.getDiem() == null ? 1f : question.getDiem()) : 0f));
                    attemptAnswersRepository.save(attemptAnswer);
                }
                if (isCorrect) {
                    correct++;
                }
            } else if (attemptAnswer != null) {
                attemptAnswer.setIsCorrect(null);
                attemptAnswer.setScoreReceived(null);
                attemptAnswersRepository.save(attemptAnswer);
            }
        }

        float score = 0f;
        if (autoGradable > 0) {
            score = Math.round(((float) correct / autoGradable) * 10 * 100) / 100f;
        }

        int remainingAtSubmit = calculateRemainingTime(attempt.getQuiz(), attempt);
        attempt.setScore(score);
        attempt.setEndTime(LocalDateTime.now());
        attempt.setRemainingTime(remainingAtSubmit);
        attempt.setUsedTime(calculateUsedTime(attempt.getQuiz(), attempt, remainingAtSubmit));
        attempt.setStatus(true);
        if (attempt.getQuiz().getPassScore() != null) {
            attempt.setIsPassed(score * 10 >= attempt.getQuiz().getPassScore());
        }
        quizAttemptRepository.save(attempt);

        logAttempt(attempt, remainingAtSubmit <= 0 ? AttemptActionEnum.TIMEOUT : AttemptActionEnum.SUBMIT, null, null, null);

        return mapper.toQuizResultDTO(attempt, correct, quizQuestions.size());
    }

    private Map<UUID, QuizAttempt> getLatestAttemptsByQuiz(List<Quiz> quizzes, UUID hocVienId) {
        if (quizzes == null || quizzes.isEmpty()) {
            return Map.of();
        }

        List<UUID> quizIds = quizzes.stream()
                .map(Quiz::getId)
                .toList();

        return quizAttemptRepository.findByHocVienIdAndQuizIdsWithQuiz(hocVienId, quizIds)
                .stream()
                .collect(Collectors.toMap(
                        attempt -> attempt.getQuiz().getId(),
                        attempt -> attempt,
                        (a, b) -> a.getStartTime().isAfter(b.getStartTime()) ? a : b));
    }

    private QuizStatusEnum resolveQuizStatus(Quiz quiz, QuizAttempt attempt) {
        if (attempt != null && Boolean.TRUE.equals(attempt.getStatus())) {
            return QuizStatusEnum.DONE;
        }

        LocalDateTime now = LocalDateTime.now();

        if (quiz.getThoiGianBatDau() != null && now.isBefore(quiz.getThoiGianBatDau())) {
            return QuizStatusEnum.UPCOMING;
        }

        if (quiz.getThoiGianKetThuc() != null && now.isAfter(quiz.getThoiGianKetThuc())) {
            return QuizStatusEnum.EXPIRED;
        }

        return QuizStatusEnum.DOING;
    }

    private Integer calculateRemainingTime(Quiz quiz, QuizAttempt attempt) {
        if (quiz.getThoiGianLam() == null || quiz.getThoiGianLam() <= 0) {
            return 0;
        }

        int fullDurationSeconds = quiz.getThoiGianLam() * SECONDS_PER_MINUTE;
        LocalDateTime now = LocalDateTime.now();

        if (attempt == null) {
            if (quiz.getThoiGianBatDau() != null && now.isBefore(quiz.getThoiGianBatDau())) {
                return fullDurationSeconds;
            }
            if (quiz.getThoiGianKetThuc() != null && now.isAfter(quiz.getThoiGianKetThuc())) {
                return 0;
            }
            return fullDurationSeconds;
        }

        if (Boolean.TRUE.equals(attempt.getStatus())) {
            return 0;
        }

        LocalDateTime deadlineByDuration = attempt.getStartTime().plusMinutes(quiz.getThoiGianLam());
        LocalDateTime effectiveDeadline = quiz.getThoiGianKetThuc() == null
                ? deadlineByDuration
                : deadlineByDuration.isBefore(quiz.getThoiGianKetThuc()) ? deadlineByDuration
                        : quiz.getThoiGianKetThuc();

        if (!now.isBefore(effectiveDeadline)) {
            return 0;
        }

        long seconds = Duration.between(now, effectiveDeadline).getSeconds();
        return (int) Math.max(0, Math.min(seconds, fullDurationSeconds));
    }

    private List<Questions> getQuestions(Quiz quiz) {
        Map<UUID, Questions> questions = new LinkedHashMap<>();

        if (quiz == null || quiz.getId() == null) {
            return List.of();
        }

        questionsRepository.findManualQuizQuestionsWithAnswers(quiz.getId()).stream()
                .filter(q -> q != null && q.getId() != null)
                .forEach(q -> questions.putIfAbsent(q.getId(), q));
        questionsRepository.findExerciseQuizQuestionsWithAnswers(quiz.getId()).stream()
                .filter(q -> q != null && q.getId() != null)
                .forEach(q -> questions.putIfAbsent(q.getId(), q));

        return List.copyOf(questions.values());
    }

    private List<Questions> getQuestionsForAttempt(QuizAttempt attempt) {
        return getQuestionsForAttempt(
                attempt,
                attemptAnswersRepository.findByQuizAttemptIdWithQuestionAnswerData(attempt.getId()));
    }

    private List<Questions> getQuestionsForAttempt(QuizAttempt attempt, List<AttemptAnswers> attemptAnswers) {
        if ("random_question".equalsIgnoreCase(attempt.getQuiz().getQuizType())) {
            List<Questions> randomQuestions = attemptAnswers
                    .stream()
                    .map(AttemptAnswers::getQuestions)
                    .filter(q -> q != null && q.getId() != null)
                    .distinct()
                    .toList();
            if (!randomQuestions.isEmpty()) {
                return randomQuestions;
            }
        }
        return getQuestions(attempt.getQuiz());
    }

    private QuizStartStudentResponse toQuizStartResponse(QuizAttempt attempt) {
        List<AttemptAnswers> attemptAnswers = attemptAnswersRepository
                .findByQuizAttemptIdWithQuestionAnswerData(attempt.getId());
        List<Questions> questions = getQuestionsForAttempt(attempt, attemptAnswers);

        QuizStartStudentResponse dto = new QuizStartStudentResponse();
        dto.setAttemptId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setRemainingTime(attempt.getRemainingTime());
        dto.setUsedTime(attempt.getUsedTime());
        dto.setStartTime(attempt.getStartTime());
        dto.setQuestions(mapper.toQuestionDTOs(questions, attempt.getQuiz()));
        dto.setSelectedAnswers(toSelectedAnswers(attemptAnswers));
        dto.setTextAnswers(toTextAnswers(attemptAnswers));
        return dto;
    }

    private QuizAttemptStudentResponse toAttemptResponse(QuizAttempt attempt) {
        List<AttemptAnswers> attemptAnswers = attemptAnswersRepository
                .findByQuizAttemptIdWithQuestionAnswerData(attempt.getId());
        List<Questions> questions = getQuestionsForAttempt(attempt, attemptAnswers);
        int remaining = calculateRemainingTime(attempt.getQuiz(), attempt);
        QuizAttemptStudentResponse dto = new QuizAttemptStudentResponse();
        dto.setAttemptId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setTieuDe(attempt.getQuiz().getTieuDe());
        dto.setRemainingTime(remaining);
        dto.setUsedTime(calculateUsedTime(attempt.getQuiz(), attempt, remaining));
        dto.setSubmitted(Boolean.TRUE.equals(attempt.getStatus()));
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());
        dto.setQuestions(mapper.toQuestionDTOs(questions, attempt.getQuiz()));
        dto.setSelectedAnswers(toSelectedAnswers(attemptAnswers));
        dto.setTextAnswers(toTextAnswers(attemptAnswers));
        return dto;
    }

    private void createRandomQuestionSlots(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        int count = quiz.getRandomQuestionCount() == null || quiz.getRandomQuestionCount() <= 0
                ? 0
                : quiz.getRandomQuestionCount();
        if (count == 0) {
            return;
        }

        List<Boolean> questionTypes = parseQuestionTypes(quiz.getRandomQuestionTypes());
        List<Questions> pool = questionTypes.isEmpty()
                ? questionsRepository.findByExercise_LopHocPhan_Id(quiz.getLopHocPhan().getId())
                : questionsRepository.findByExercise_LopHocPhan_IdAndLoaiCauHoiIn(quiz.getLopHocPhan().getId(), questionTypes);
        if (pool.isEmpty()) {
            throw new SimpleMessageException("Ngân hàng câu hỏi của lớp học phần chưa có câu hỏi phù hợp");
        }

        List<Questions> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, new Random(attempt.getId().getLeastSignificantBits()));
        List<AttemptAnswers> slots = shuffled.stream()
                .limit(Math.min(count, shuffled.size()))
                .map(question -> {
                    AttemptAnswers slot = new AttemptAnswers();
                    slot.setQuizAttempt(attempt);
                    slot.setQuestions(question);
                    return slot;
                })
                .toList();
        attemptAnswersRepository.saveAll(slots);
        attempt.getDAttemptAnswers().addAll(slots);
    }

    private List<Boolean> parseQuestionTypes(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of(true, false);
        }
        List<Boolean> result = new ArrayList<>();
        String normalized = raw.toLowerCase(Locale.ROOT);
        if (normalized.contains("multiple_choice") || normalized.contains("trac") || normalized.contains("true")) {
            result.add(true);
        }
        if (normalized.contains("essay") || normalized.contains("tu_luan") || normalized.contains("false")) {
            result.add(false);
        }
        return result.stream().distinct().toList();
    }

    private void saveAttemptAnswers(QuizAttempt attempt, List<QuizAnswerSaveItemRequest> answers) {
        if (answers == null || answers.isEmpty()) {
            return;
        }

        List<UUID> payloadQuestionIds = answers.stream()
                .filter(Objects::nonNull)
                .map(QuizAnswerSaveItemRequest::getQuestionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (payloadQuestionIds.isEmpty()) {
            return;
        }

        Set<UUID> answerIds = answers.stream()
                .map(QuizAnswerSaveItemRequest::getAnswerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<UUID, Answers> answerMap = answerIds.isEmpty()
                ? Map.of()
                : answersRepository.findAllByIdWithQuestion(new ArrayList<>(answerIds)).stream()
                .collect(Collectors.toMap(Answers::getId, a -> a));

        Map<UUID, Questions> allowedQuestionMap = getQuestionsForAttempt(attempt).stream()
                .collect(Collectors.toMap(Questions::getId, q -> q, (a, b) -> a, LinkedHashMap::new));

        Map<UUID, AttemptAnswers> currentAnswers = attemptAnswersRepository
                .findByQuizAttemptIdAndQuestionIdsWithAnswer(attempt.getId(), payloadQuestionIds)
                .stream()
                .filter(a -> a.getQuestions() != null)
                .collect(Collectors.toMap(a -> a.getQuestions().getId(), a -> a, (a, b) -> a));

        for (QuizAnswerSaveItemRequest item : answers) {
            if (item == null || item.getQuestionId() == null) {
                continue;
            }

            Questions question = allowedQuestionMap.get(item.getQuestionId());
            if (question == null) {
                continue;
            }

            Answers newAnswer = item.getAnswerId() == null ? null : answerMap.get(item.getAnswerId());
            if (newAnswer != null && (newAnswer.getQuestions() == null
                    || !newAnswer.getQuestions().getId().equals(question.getId()))) {
                continue;
            }

            AttemptAnswers current = currentAnswers.computeIfAbsent(question.getId(), ignored -> {
                        AttemptAnswers created = new AttemptAnswers();
                        created.setQuizAttempt(attempt);
                        created.setQuestions(question);
                        return created;
                    });

            boolean changed = !Objects.equals(current.getAnswers() == null ? null : current.getAnswers().getId(), item.getAnswerId())
                    || !Objects.equals(normalizeText(current.getTextAnswer()), normalizeText(item.getTextAnswer()));
            if (!changed) {
                continue;
            }

            AttemptAnswersLog log = new AttemptAnswersLog();
            log.setQuizAttempt(attempt);
            log.setQuestions(question);
            log.setOldAnswer(current.getAnswers());
            log.setNewAnswer(newAnswer);
            log.setOldTextAnswer(current.getTextAnswer());
            log.setNewTextAnswer(item.getTextAnswer());
            log.setTimeOnQuestion(item.getTimeOnQuestion());
            attemptAnswersLogRepository.save(log);

            current.setAnswers(newAnswer);
            current.setTextAnswer(item.getTextAnswer());
            attemptAnswersRepository.save(current);
        }
    }

    private Map<UUID, UUID> toSelectedAnswers(List<AttemptAnswers> attemptAnswers) {
        Map<UUID, UUID> values = new LinkedHashMap<>();
        attemptAnswers.stream()
                .filter(a -> a.getQuestions() != null && a.getAnswers() != null)
                .forEach(a -> values.put(a.getQuestions().getId(), a.getAnswers().getId()));
        return values;
    }

    private Map<UUID, String> toTextAnswers(List<AttemptAnswers> attemptAnswers) {
        Map<UUID, String> values = new LinkedHashMap<>();
        attemptAnswers.stream()
                .filter(a -> a.getQuestions() != null && a.getTextAnswer() != null)
                .forEach(a -> values.put(a.getQuestions().getId(), a.getTextAnswer()));
        return values;
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private void validateAttemptOwner(QuizAttempt attempt, HocVien hocVien) {
        if (!attempt.getHocVien().getId().equals(hocVien.getId())) {
            throw new SimpleMessageException("Bạn không có quyền truy cập bài làm này");
        }
    }

    private void ensureAttemptOpen(QuizAttempt attempt) {
        if (Boolean.TRUE.equals(attempt.getStatus())) {
            throw new AlreadySubmittedException("Bài thi này đã được nộp rồi");
        }
        Quiz quiz = attempt.getQuiz();
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getThoiGianKetThuc() != null && now.isAfter(quiz.getThoiGianKetThuc())) {
            throw new QuizNotOpenException("Đã hết thời hạn làm bài");
        }
    }

    private Integer calculateUsedTime(Quiz quiz, QuizAttempt attempt, Integer remainingTime) {
        if (quiz.getThoiGianLam() == null || quiz.getThoiGianLam() <= 0 || remainingTime == null) {
            return attempt.getUsedTime();
        }
        return Math.max(0, quiz.getThoiGianLam() * SECONDS_PER_MINUTE - remainingTime);
    }

    private void logAttempt(QuizAttempt attempt, AttemptActionEnum action, Questions question, String value, String eventData) {
        QuizAttemptLog log = new QuizAttemptLog();
        log.setQuizAttempt(attempt);
        log.setAction(action);
        log.setQuestions(question);
        log.setValue(value);
        log.setEventData(eventData);
        quizAttemptLogRepository.save(log);
    }
}
