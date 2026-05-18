package com.university.service.lecturer;

import com.university.dto.request.lecturer.QuizRequestDTO;
import com.university.dto.response.lecturer.AnswerResponseDTO;
import com.university.dto.response.lecturer.QuestionResponseDTO;
import com.university.dto.response.lecturer.QuizAttemptAnswerDetailDTO;
import com.university.dto.response.lecturer.QuizAttemptAnswerOptionDTO;
import com.university.dto.response.lecturer.QuizAttemptDetailResponseDTO;
import com.university.dto.response.lecturer.QuizResponseDTO;
import com.university.dto.response.lecturer.QuizResultResponseDTO;
import com.university.dto.response.lecturer.StudentQuizResultDTO;
import com.university.entity.Answers;
import com.university.entity.AttemptAnswers;
import com.university.entity.AttemptAnswersLog;
import com.university.entity.Exercise;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.entity.Questions;
import com.university.entity.Quiz;
import com.university.entity.QuizExercise;
import com.university.entity.QuizAttempt;
import com.university.entity.QuizQuestions;
import com.university.entity.Users;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerAnswersRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import com.university.repository.lecturer.LecturerExerciseRepository;
import com.university.repository.lecturer.LecturerQuizExerciseRepository;
import com.university.repository.lecturer.LecturerQuizAttemptRepository;
import com.university.repository.lecturer.LecturerQuizQuestionsRepository;
import com.university.repository.lecturer.LecturerQuizRepository;
import com.university.repository.lecturer.LecturerQuestionRepository;
import com.university.repository.student.AttemptAnswersLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerQuizService {

    private final LecturerQuizRepository quizRepository;
    private final LecturerQuizQuestionsRepository quizQuestionsRepository;
    private final LecturerQuizExerciseRepository quizExerciseRepository;
    private final LecturerQuestionRepository questionRepository;
    private final LecturerAnswersRepository answersRepository;
    private final LecturerExerciseRepository exerciseRepository;
    private final LecturerQuizAttemptRepository quizAttemptRepository;
    private final AttemptAnswersLogRepository attemptAnswersLogRepository;
    private final LecturerDangKyTinChiRepository dangKyTinChiRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final UsersAdminRepository userRepository;
    private final LecturerNotificationService notificationService;
    private final LecturerValidationService validationService;

    public QuizResponseDTO createQuiz(QuizRequestDTO request, UUID userId) {
        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new RuntimeException("Lớp học phần không tồn tại."));
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại."));
        validationService.validateLecturerAssignment(userId, request.getLopHocPhanId());
        if (quizRepository.existsByLopHocPhan_IdAndTieuDeAndThoiGianBatDauAndThoiGianKetThuc(
                request.getLopHocPhanId(),
                request.getTieuDe().trim(),
                request.getThoiGianBatDau(),
                request.getThoiGianKetThuc())) {
            throw new RuntimeException("Bài kiểm tra này đã được tạo. Vui lòng kiểm tra danh sách bài kiểm tra.");
        }

        Quiz quiz = new Quiz();
        quiz.setTieuDe(request.getTieuDe().trim());
        quiz.setMoTa(request.getMoTa());
        quiz.setThoiGianBatDau(request.getThoiGianBatDau());
        quiz.setThoiGianKetThuc(request.getThoiGianKetThuc());
        quiz.setThoiGianLam(request.getThoiGianLam());
        quiz.setSoLanLam(request.getSoLanLam() != null ? request.getSoLanLam() : 1);
        quiz.setTrinhTrang(request.getTrinhTrang() != null ? request.getTrinhTrang() : false);
        applyQuizSettings(quiz, request);
        quiz.setLopHocPhan(lopHocPhan);
        Quiz savedQuiz = quizRepository.save(quiz);

        String thoiGianText = (savedQuiz.getThoiGianBatDau() != null && savedQuiz.getThoiGianKetThuc() != null)
                ? ". Thời gian: " + savedQuiz.getThoiGianBatDau().toLocalDate() + " - " + savedQuiz.getThoiGianKetThuc().toLocalDate()
                : "";
        notificationService.sendToClassStudents(user, lopHocPhan.getId(),
                "Bài kiểm tra mới: " + savedQuiz.getTieuDe(),
                "Giảng viên " + user.getHoTen() + " đã tạo bài kiểm tra mới cho lớp " +
                        lopHocPhan.getMonHoc().getTenMonHoc() +
                        ". Thời gian làm bài: " + savedQuiz.getThoiGianLam() + " phút" + thoiGianText);

        List<QuestionResponseDTO> questionDTOs = buildQuestionSource(savedQuiz, request);

        return toQuizResponseDTO(savedQuiz, questionDTOs);
    }

    public QuizResponseDTO getQuiz(UUID quizId, UUID userId) {
        Quiz quiz = quizRepository.findByIdWithClassAndSubject(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        List<QuestionResponseDTO> questionDTOs = getQuestionsWithAnswers(quiz).stream()
                .map(this::toQuestionResponseDTO)
                .collect(Collectors.toList());

        return toQuizResponseDTO(quiz, questionDTOs);
    }

    public QuizResponseDTO updateQuiz(UUID quizId, QuizRequestDTO request, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        if (quiz.getThoiGianKetThuc() != null && quiz.getThoiGianKetThuc().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể sửa bài kiểm tra đã kết thúc.");
        }

        boolean hasStarted = quiz.getThoiGianBatDau() != null
                && quiz.getThoiGianBatDau().isBefore(LocalDateTime.now());

        if (hasStarted) {
            // Chỉ cho phép cập nhật các trường không ảnh hưởng đến câu hỏi
            if (request.getTrinhTrang() != null) quiz.setTrinhTrang(request.getTrinhTrang());
            if (request.getSoLanLam() != null) quiz.setSoLanLam(request.getSoLanLam());
            if (request.getShowResult() != null && !request.getShowResult().isBlank()) {
                quiz.setShowResult(request.getShowResult());
            }
            Quiz savedQuiz = quizRepository.save(quiz);
            List<QuestionResponseDTO> questionDTOs = getQuestionsWithAnswers(savedQuiz).stream()
                    .map(this::toQuestionResponseDTO)
                    .collect(Collectors.toList());
            return toQuizResponseDTO(savedQuiz, questionDTOs);
        }

        quiz.setTieuDe(request.getTieuDe());
        quiz.setMoTa(request.getMoTa());
        quiz.setThoiGianBatDau(request.getThoiGianBatDau());
        quiz.setThoiGianKetThuc(request.getThoiGianKetThuc());
        quiz.setThoiGianLam(request.getThoiGianLam());
        if (request.getSoLanLam() != null) quiz.setSoLanLam(request.getSoLanLam());
        if (request.getTrinhTrang() != null) quiz.setTrinhTrang(request.getTrinhTrang());
        applyQuizSettings(quiz, request);
        Quiz savedQuiz = quizRepository.save(quiz);

        quizQuestionsRepository.deleteAll(quizQuestionsRepository.findByQuiz_Id(quizId));
        quizExerciseRepository.deleteByQuiz_Id(quizId);
        quiz.getDQuizQuestions().clear();
        quiz.getDQuizExercises().clear();

        List<QuestionResponseDTO> questionDTOs = buildQuestionSource(savedQuiz, request);
        return toQuizResponseDTO(savedQuiz, questionDTOs);
    }

    public void deleteQuiz(UUID quizId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        if (quiz.getThoiGianKetThuc() != null && quiz.getThoiGianKetThuc().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể xóa bài kiểm tra đã kết thúc.");
        }
        quizRepository.delete(quiz);
    }

    public List<QuizResponseDTO> getQuizzesByLopHocPhan(UUID lopHocPhanId, UUID userId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        List<Quiz> quizzes = quizRepository.findByLopHocPhanIdWithClassAndSubject(lopHocPhanId);
        List<UUID> quizIds = quizzes.stream().map(Quiz::getId).toList();
        if (quizIds.isEmpty()) {
            return List.of();
        }
        Map<UUID, Integer> manualCounts = toCountMap(quizRepository.countManualQuestionsByQuizIds(quizIds));
        Map<UUID, Integer> exerciseCounts = toCountMap(quizRepository.countExerciseQuestionsByQuizIds(quizIds));

        return quizzes.stream()
                .map(quiz -> {
                    int questionCount = manualCounts.getOrDefault(quiz.getId(), 0)
                            + exerciseCounts.getOrDefault(quiz.getId(), 0);
                    return new QuizResponseDTO(quiz.getId(), quiz.getLopHocPhan().getId(),
                            quiz.getLopHocPhan().getMonHoc().getTenMonHoc(), quiz.getTieuDe(), quiz.getMoTa(),
                            quiz.getThoiGianBatDau(), quiz.getThoiGianKetThuc(), quiz.getThoiGianLam(),
                            quiz.getSoLanLam(), quiz.getTrinhTrang(),
                            quiz.getCreatedAt(), questionCount, null,
                            quiz.getQuizType(), quiz.getRandomQuestionCount(), quiz.getRandomQuestionTypes(),
                            quiz.getShuffleQuestions(), quiz.getShuffleAnswers(), quiz.getShowResult(), quiz.getPassScore());
                })
                .collect(Collectors.toList());
    }

    public QuizResultResponseDTO getQuizResults(UUID lopHocPhanId, UUID quizId, UUID userId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        if (!quiz.getLopHocPhan().getId().equals(lopHocPhanId)) {
            throw new RuntimeException("Quiz không thuộc lớp học phần này.");
        }

        final int tongCauHoi = getQuestions(quiz).size();

        Long tongSoHocVien = dangKyTinChiRepository.countByLopHocPhan_Id(lopHocPhanId);
        List<QuizAttempt> allAttempts = quizAttemptRepository.findByQuiz_Id(quizId);
        List<QuizAttempt> completedAttempts = allAttempts.stream()
                .filter(a -> Boolean.TRUE.equals(a.getStatus())).collect(Collectors.toList());

        Float diemTrungBinh = null;
        if (!completedAttempts.isEmpty()) {
            double sum = completedAttempts.stream()
                    .mapToDouble(a -> a.getScore() != null ? a.getScore() : 0.0).sum();
            diemTrungBinh = (float) (sum / completedAttempts.size());
        }

        // Tính số câu đúng cho từng attempt
        Map<UUID, Integer> correctCountMap = completedAttempts.stream()
                .collect(Collectors.toMap(
                        QuizAttempt::getId,
                        attempt -> calculateCorrectCount(attempt, tongCauHoi)
                ));

        List<StudentQuizResultDTO> studentResults = allAttempts.stream()
                .map(attempt -> {
                    HocVien hocVien = attempt.getHocVien();
                    Users user = hocVien.getUsers();
                    int soCauDung = Boolean.TRUE.equals(attempt.getStatus())
                            ? correctCountMap.getOrDefault(attempt.getId(), 0) : 0;
                    return new StudentQuizResultDTO(
                            hocVien.getId(), user.getHoTen(), hocVien.getMaHocVien(),
                            attempt.getScore(),
                            soCauDung,
                            tongCauHoi,
                            attempt.getUsedTime(), attempt.getRemainingTime(),
                            Boolean.TRUE.equals(attempt.getStatus()) ? "COMPLETED" : "IN_PROGRESS",
                            Boolean.TRUE.equals(attempt.getStatus()) && tongCauHoi > 0
                    );
                })
                .collect(Collectors.toList());

        return new QuizResultResponseDTO(
                quiz.getId(), quiz.getTieuDe(),
                quiz.getThoiGianLam(), quiz.getSoLanLam(), quiz.getTrinhTrang(),
                quiz.getThoiGianBatDau(), quiz.getThoiGianKetThuc(),
                tongSoHocVien.intValue(), completedAttempts.size(), diemTrungBinh,
                tongCauHoi, studentResults);
    }

    public QuizAttemptDetailResponseDTO getQuizAttemptDetail(UUID quizId, UUID hocVienId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        QuizAttempt attempt = quizAttemptRepository.findByQuiz_Id(quizId).stream()
                .filter(a -> a.getHocVien().getId().equals(hocVienId))
                .max((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài làm của học viên này."));

        HocVien hocVien = attempt.getHocVien();
        Users user = hocVien.getUsers();
        List<Questions> questions = getQuestions(quiz);
        Map<UUID, AttemptAnswers> selectedByQuestion = attempt.getDAttemptAnswers().stream()
                .filter(aa -> aa.getQuestions() != null)
                .collect(Collectors.toMap(
                        aa -> aa.getQuestions().getId(),
                        aa -> aa,
                        (a, b) -> a));

        List<QuizAttemptAnswerDetailDTO> answerDetails = questions.stream()
                .map(question -> toAttemptAnswerDetail(attempt.getId(), question, selectedByQuestion.get(question.getId())))
                .toList();

        int correctCount = (int) answerDetails.stream()
                .filter(detail -> Boolean.TRUE.equals(detail.getSelectedCorrect()))
                .count();

        return new QuizAttemptDetailResponseDTO(
                attempt.getId(),
                quiz.getId(),
                hocVien.getId(),
                user.getHoTen(),
                hocVien.getMaHocVien(),
                attempt.getScore(),
                correctCount,
                questions.size(),
                attempt.getUsedTime(),
                attempt.getRemainingTime(),
                Boolean.TRUE.equals(attempt.getStatus()) ? "COMPLETED" : "IN_PROGRESS",
                attempt.getStartTime(),
                attempt.getEndTime(),
                answerDetails);
    }

    private int calculateCorrectCount(QuizAttempt attempt, int totalMCQ) {
        if (attempt.getDAttemptAnswers() == null || attempt.getDAttemptAnswers().isEmpty()) {
            return 0;
        }
        int correct = 0;
        for (AttemptAnswers aa : attempt.getDAttemptAnswers()) {
            Answers answer = aa.getAnswers();
            if (answer != null && Boolean.TRUE.equals(answer.getIsCorrect())) {
                correct++;
            }
        }
        return correct;
    }

    private QuizAttemptAnswerDetailDTO toAttemptAnswerDetail(UUID attemptId, Questions question, AttemptAnswers selectedAnswer) {
        Answers selected = selectedAnswer != null ? selectedAnswer.getAnswers() : null;
        UUID selectedId = selected != null ? selected.getId() : null;

        List<QuizAttemptAnswerOptionDTO> options = question.getDAnswers() == null
                ? List.of()
                : question.getDAnswers().stream()
                        .map(answer -> new QuizAttemptAnswerOptionDTO(
                                answer.getId(),
                                answer.getKeyAnswers(),
                                answer.getConText(),
                                answer.getIsCorrect(),
                                selectedId != null && selectedId.equals(answer.getId())))
                        .toList();

        List<com.university.dto.response.lecturer.QuizAttemptAnswerLogDTO> logs = attemptAnswersLogRepository
                .findByQuizAttempt_IdOrderByChangedAtAsc(attemptId)
                .stream()
                .filter(log -> log.getQuestions() != null && log.getQuestions().getId().equals(question.getId()))
                .map(this::toAnswerLogDTO)
                .toList();

        return new QuizAttemptAnswerDetailDTO(
                question.getId(),
                question.getNoiDung(),
                question.getDiem(),
                selectedId,
                selected != null ? selected.getKeyAnswers() : null,
                selected != null ? selected.getConText() : null,
                selectedAnswer != null ? selectedAnswer.getTextAnswer() : null,
                selected != null ? selected.getIsCorrect() : null,
                selectedAnswer != null ? selectedAnswer.getScoreReceived() : null,
                options,
                logs);
    }

    private List<Questions> getQuestions(Quiz quiz) {
        return getQuestionsWithAnswers(quiz);
    }

    private List<Questions> getQuestionsWithAnswers(Quiz quiz) {
        Map<UUID, Questions> questions = new LinkedHashMap<>();

        if (quiz == null || quiz.getId() == null) {
            return List.of();
        }

        questionRepository.findManualQuizQuestionsWithAnswers(quiz.getId()).stream()
                .filter(q -> q != null && q.getId() != null)
                .forEach(q -> questions.putIfAbsent(q.getId(), q));
        questionRepository.findExerciseQuizQuestionsWithAnswers(quiz.getId()).stream()
                .filter(q -> q != null && q.getId() != null)
                .forEach(q -> questions.putIfAbsent(q.getId(), q));

        return new ArrayList<>(questions.values());
    }

    public void gradeQuizAttempt(UUID quizId, UUID hocVienId, UUID userId, Float diem, String nhanXet) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        List<QuizAttempt> attempts = quizAttemptRepository.findByQuiz_Id(quizId).stream()
                .filter(a -> a.getHocVien().getId().equals(hocVienId))
                .toList();

        if (attempts.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bài làm của học viên này.");
        }

        QuizAttempt latest = attempts.stream()
                .max((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .orElseThrow();

        if (diem != null && (diem < 0 || diem > 10)) {
            throw new RuntimeException("Điểm phải từ 0 đến 10.");
        }

        if (diem != null) {
            latest.setScore(diem);
        }
        quizAttemptRepository.save(latest);
    }

    private void applyQuizSettings(Quiz quiz, QuizRequestDTO request) {
        String quizType = request.getQuizType() == null || request.getQuizType().isBlank()
                ? "manual_question"
                : request.getQuizType();
        quiz.setQuizType(quizType);
        quiz.setRandomQuestionCount(request.getRandomQuestionCount());
        quiz.setRandomQuestionTypes(request.getRandomQuestionTypes());
        quiz.setShuffleQuestions(Boolean.TRUE.equals(request.getShuffleQuestions()));
        quiz.setShuffleAnswers(Boolean.TRUE.equals(request.getShuffleAnswers()));
        quiz.setShowResult(request.getShowResult() == null || request.getShowResult().isBlank()
                ? "immediately"
                : request.getShowResult());
        quiz.setPassScore(request.getPassScore());
    }

    private List<QuestionResponseDTO> buildQuestionSource(Quiz savedQuiz, QuizRequestDTO request) {
        String quizType = savedQuiz.getQuizType() == null ? "manual_question" : savedQuiz.getQuizType();
        if ("exercise_based".equalsIgnoreCase(quizType)) {
            return attachExercises(savedQuiz, request);
        }
        if ("random_question".equalsIgnoreCase(quizType)) {
            return List.of();
        }
        return buildQuestions(savedQuiz, request);
    }

    private List<QuestionResponseDTO> attachExercises(Quiz savedQuiz, QuizRequestDTO request) {
        if (request.getExerciseIds() == null || request.getExerciseIds().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 bài tập cho quiz theo bài tập.");
        }

        List<UUID> exerciseIds = request.getExerciseIds().stream().distinct().toList();
        List<Exercise> exercises = exerciseRepository.findAllByIdInWithClass(exerciseIds);
        if (exercises.size() != exerciseIds.size()) {
            throw new RuntimeException("Một số bài tập không tồn tại.");
        }

        exercises.forEach(exercise -> {
            if (!exercise.getLopHocPhan().getId().equals(savedQuiz.getLopHocPhan().getId())) {
                throw new RuntimeException("Bài tập không thuộc lớp học phần của quiz.");
            }
        });

        List<QuizExercise> quizExercises = exercises.stream()
                .map(exercise -> {
                    QuizExercise quizExercise = new QuizExercise();
                    quizExercise.setQuiz(savedQuiz);
                    quizExercise.setExercise(exercise);
                    return quizExercise;
                })
                .toList();
        quizExerciseRepository.saveAll(quizExercises);

        Map<UUID, Questions> questions = new LinkedHashMap<>();
        questionRepository.findByExerciseIdsWithAnswers(exerciseIds).stream()
                .filter(q -> q != null && q.getId() != null)
                .forEach(q -> questions.putIfAbsent(q.getId(), q));
        return questions.values().stream()
                .map(this::toQuestionResponseDTO)
                .toList();
    }

    private List<QuestionResponseDTO> buildQuestions(Quiz savedQuiz, QuizRequestDTO request) {
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new RuntimeException("Phải có ít nhất 1 câu hỏi.");
        }
        List<Questions> savedQuestions = request.getQuestions().stream()
                .map(questionReq -> {
                    Questions question = new Questions();
                    question.setNoiDung(questionReq.getNoiDung());
                    question.setLoaiCauHoi(questionReq.getLoaiCauHoi());
                    question.setDiem(questionReq.getDiem() != null ? questionReq.getDiem() : 1.0f);
                    question.setExercise(null);
                    return question;
                })
                .collect(Collectors.toList());
        savedQuestions = questionRepository.saveAll(savedQuestions);

        List<QuizQuestions> quizQuestions = savedQuestions.stream()
                .map(savedQuestion -> {
                    QuizQuestions quizQuestion = new QuizQuestions();
                    quizQuestion.setQuiz(savedQuiz);
                    quizQuestion.setQuestions(savedQuestion);
                    return quizQuestion;
                })
                .toList();
        quizQuestionsRepository.saveAll(quizQuestions);

        List<Answers> answers = new ArrayList<>();
        for (int i = 0; i < savedQuestions.size(); i++) {
            Questions savedQuestion = savedQuestions.get(i);
            List<com.university.dto.request.lecturer.AnswerRequestDTO> answerRequests =
                    request.getQuestions().get(i).getAnswers() == null
                            ? List.of()
                            : request.getQuestions().get(i).getAnswers();
            answerRequests.forEach(answerReq -> {
                Answers answer = new Answers();
                answer.setKeyAnswers(answerReq.getKeyAnswers());
                answer.setConText(answerReq.getConText());
                answer.setIsCorrect(answerReq.getIsCorrect());
                answer.setQuestions(savedQuestion);
                answers.add(answer);
            });
        }
        answersRepository.saveAll(answers);

        Map<UUID, List<AnswerResponseDTO>> answersByQuestion = answers.stream()
                .collect(Collectors.groupingBy(
                        answer -> answer.getQuestions().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                answer -> new AnswerResponseDTO(answer.getId(), answer.getKeyAnswers(),
                                        answer.getConText(), answer.getIsCorrect()),
                                Collectors.toList())));

        return savedQuestions.stream()
                .map(savedQuestion -> new QuestionResponseDTO(savedQuestion.getId(), savedQuestion.getNoiDung(),
                        savedQuestion.getLoaiCauHoi(), savedQuestion.getDiem(),
                        answersByQuestion.getOrDefault(savedQuestion.getId(), List.of())))
                .toList();
    }

    private QuestionResponseDTO toQuestionResponseDTO(Questions question) {
        List<AnswerResponseDTO> answerDTOs = question.getDAnswers() == null
                ? List.of()
                : question.getDAnswers().stream()
                        .map(a -> new AnswerResponseDTO(a.getId(), a.getKeyAnswers(), a.getConText(), a.getIsCorrect()))
                        .collect(Collectors.toList());
        return new QuestionResponseDTO(question.getId(), question.getNoiDung(),
                question.getLoaiCauHoi(), question.getDiem(), answerDTOs);
    }

    private com.university.dto.response.lecturer.QuizAttemptAnswerLogDTO toAnswerLogDTO(AttemptAnswersLog log) {
        return new com.university.dto.response.lecturer.QuizAttemptAnswerLogDTO(
                log.getQuestions() != null ? log.getQuestions().getId() : null,
                log.getOldAnswer() != null ? log.getOldAnswer().getId() : null,
                log.getNewAnswer() != null ? log.getNewAnswer().getId() : null,
                log.getOldTextAnswer(),
                log.getNewTextAnswer(),
                log.getTimeOnQuestion(),
                log.getChangedAt());
    }

    private QuizResponseDTO toQuizResponseDTO(Quiz quiz, List<QuestionResponseDTO> questions) {
        return new QuizResponseDTO(quiz.getId(), quiz.getLopHocPhan().getId(),
                quiz.getLopHocPhan().getMonHoc().getTenMonHoc(), quiz.getTieuDe(), quiz.getMoTa(),
                quiz.getThoiGianBatDau(), quiz.getThoiGianKetThuc(), quiz.getThoiGianLam(),
                quiz.getSoLanLam(), quiz.getTrinhTrang(),
                quiz.getCreatedAt(), questions.size(), questions,
                quiz.getQuizType(), quiz.getRandomQuestionCount(), quiz.getRandomQuestionTypes(),
                quiz.getShuffleQuestions(), quiz.getShuffleAnswers(), quiz.getShowResult(), quiz.getPassScore());
    }

    private Map<UUID, Integer> toCountMap(List<Object[]> rows) {
        Map<UUID, Integer> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((UUID) row[0], ((Number) row[1]).intValue());
        }
        return result;
    }
}
