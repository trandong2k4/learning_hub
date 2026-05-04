package com.university.service.student;

import com.university.dto.response.student.*;
import com.university.entity.*;
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
    private final AnswersRepository answersRepository;
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
                .map(quiz -> mapper.toQuizListDTO(quiz, resolveQuizStatus(quiz, latestAttempts.get(quiz.getId()))))
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
                .map(quiz -> mapper.toQuizListDTO(quiz, resolveQuizStatus(quiz, latestAttempts.get(quiz.getId()))))
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
                .map(quiz -> mapper.toQuizListDTO(quiz, resolveQuizStatus(quiz, latestAttempts.get(quiz.getId()))))
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
        if (now.isBefore(quiz.getThoiGianBatDau())) {
            throw new QuizNotOpenException("Quiz chưa bắt đầu");
        }
        if (now.isAfter(quiz.getThoiGianKetThuc())) {
            throw new QuizNotOpenException("Quiz đã kết thúc");
        }

        // 👉 4. Kiểm tra đã nộp bài chưa
        boolean daNop = quizAttemptRepository
                .existsByQuiz_IdAndHocVien_IdAndStatusTrue(quizId, hocVien.getId());
        if (daNop) {
            throw new AlreadySubmittedException("Bạn đã hoàn thành quiz này rồi");
        }

        // 👉 5. Có attempt đang dở → trả về luôn, không tạo mới
        Optional<QuizAttempt> attemptDangDo = quizAttemptRepository
                .findByQuiz_IdAndHocVien_IdAndStatusFalse(quizId, hocVien.getId());
        if (attemptDangDo.isPresent()) {
            return mapper.toQuizStartDTO(attemptDangDo.get());
        }

        // 👉 6. Tạo attempt mới
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setHocVien(hocVien);
        attempt.setStartTime(now);
        attempt.setStatus(false);
        attempt.setRemainingTime(quiz.getThoiGianLam() * SECONDS_PER_MINUTE);
        quizAttemptRepository.save(attempt);

        return mapper.toQuizStartDTO(attempt);
    }

    // ================================================================
    // 📌 6. NỘP BÀI
    // ================================================================
    @Override
    @Transactional
    public QuizResultStudentResponse submitQuiz(UUID attemptId, Map<UUID, UUID> answers) {

        // 👉 1. Lấy học viên từ JWT
        HocVien hocVien = getCurrentHocVien();

        // 👉 2. Lấy attempt
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", attemptId));

        // 👉 3. Kiểm tra quyền sở hữu
        if (!attempt.getHocVien().getId().equals(hocVien.getId())) {
            throw new SimpleMessageException("Bạn không có quyền nộp bài này");
        }

        // 👉 4. Kiểm tra đã nộp chưa
        if (Boolean.TRUE.equals(attempt.getStatus())) {
            throw new AlreadySubmittedException("Bài thi này đã được nộp rồi");
        }

        // 👉 5. Kiểm tra hết giờ
        Quiz quiz = attempt.getQuiz();
        if (LocalDateTime.now().isAfter(quiz.getThoiGianKetThuc())) {
            throw new QuizNotOpenException("Đã hết thời gian làm bài");
        }

        // 👉 6. Load toàn bộ answers 1 lần — tránh N+1 query
        Set<UUID> answerIds = answers.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Answers> answerMap = answersRepository.findAllById(answerIds)
                .stream()
                .collect(Collectors.toMap(Answers::getId, a -> a));

        // 👉 7. Tính điểm
        int correct = 0;
        int total = 0;

        for (QuizExercise qe : quiz.getDQuizExercises()) {
            if (qe.getExercise() == null) continue;

            for (Questions question : qe.getExercise().getDQuestions()) {
                total++;

                UUID selectedAnswerId = answers.get(question.getId());
                if (selectedAnswerId == null) continue;

                Answers answer = answerMap.get(selectedAnswerId);

                if (answer != null
                        && answer.getQuestions() != null
                        && answer.getQuestions().getId().equals(question.getId())
                        && Boolean.TRUE.equals(answer.getIsCorrect())) {
                    correct++;
                }
            }
        }

        // 👉 8. Tính điểm thang 10, làm tròn 2 chữ số
        float score = 0f;
        if (total > 0) {
            score = Math.round(((float) correct / total) * 10 * 100) / 100f;
        }

        // 👉 9. Lưu kết quả
        attempt.setScore(score);
        attempt.setEndTime(LocalDateTime.now());
        attempt.setStatus(true);
        quizAttemptRepository.save(attempt);

        return mapper.toQuizResultDTO(attempt, correct, total);
    }

    private Map<UUID, QuizAttempt> getLatestAttemptsByQuiz(List<Quiz> quizzes, UUID hocVienId) {
        if (quizzes == null || quizzes.isEmpty()) {
            return Map.of();
        }

        List<UUID> quizIds = quizzes.stream()
                .map(Quiz::getId)
                .toList();

        return quizAttemptRepository.findByHocVien_IdAndQuiz_IdIn(hocVienId, quizIds)
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
                : deadlineByDuration.isBefore(quiz.getThoiGianKetThuc()) ? deadlineByDuration : quiz.getThoiGianKetThuc();

        if (!now.isBefore(effectiveDeadline)) {
            return 0;
        }

        long seconds = Duration.between(now, effectiveDeadline).getSeconds();
        return (int) Math.max(0, Math.min(seconds, fullDurationSeconds));
    }
}
