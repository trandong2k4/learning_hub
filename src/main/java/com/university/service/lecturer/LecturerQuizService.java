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
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.entity.Questions;
import com.university.entity.Quiz;
import com.university.entity.QuizAttempt;
import com.university.entity.QuizQuestions;
import com.university.entity.Users;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerAnswersRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import com.university.repository.lecturer.LecturerQuizAttemptRepository;
import com.university.repository.lecturer.LecturerQuizQuestionsRepository;
import com.university.repository.lecturer.LecturerQuizRepository;
import com.university.repository.lecturer.LecturerQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final LecturerQuestionRepository questionRepository;
    private final LecturerAnswersRepository answersRepository;
    private final LecturerQuizAttemptRepository quizAttemptRepository;
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

        Quiz quiz = new Quiz();
        quiz.setTieuDe(request.getTieuDe());
        quiz.setMoTa(request.getMoTa());
        quiz.setThoiGianBatDau(request.getThoiGianBatDau());
        quiz.setThoiGianKetThuc(request.getThoiGianKetThuc());
        quiz.setThoiGianLam(request.getThoiGianLam());
        quiz.setSoLanLam(request.getSoLanLam() != null ? request.getSoLanLam() : 1);
        quiz.setTrinhTrang(request.getTrinhTrang() != null ? request.getTrinhTrang() : false);
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

        List<QuestionResponseDTO> questionDTOs = buildQuestions(savedQuiz, request);

        return toQuizResponseDTO(savedQuiz, questionDTOs);
    }

    public QuizResponseDTO getQuiz(UUID quizId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        List<QuestionResponseDTO> questionDTOs = quiz.getDQuizQuestions().stream()
                .map(qq -> {
                    Questions question = qq.getQuestions();
                    List<AnswerResponseDTO> answerDTOs = question.getDAnswers().stream()
                            .map(a -> new AnswerResponseDTO(a.getId(), a.getKeyAnswers(), a.getConText(), a.getIsCorrect()))
                            .collect(Collectors.toList());
                    return new QuestionResponseDTO(question.getId(), question.getNoiDung(),
                            question.getLoaiCauHoi(), question.getDiem(), answerDTOs);
                })
                .collect(Collectors.toList());

        return toQuizResponseDTO(quiz, questionDTOs);
    }

    public QuizResponseDTO updateQuiz(UUID quizId, QuizRequestDTO request, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        if (quiz.getThoiGianBatDau() != null && quiz.getThoiGianBatDau().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể sửa bài kiểm tra đã bắt đầu.");
        }

        quiz.setTieuDe(request.getTieuDe());
        quiz.setMoTa(request.getMoTa());
        quiz.setThoiGianBatDau(request.getThoiGianBatDau());
        quiz.setThoiGianKetThuc(request.getThoiGianKetThuc());
        quiz.setThoiGianLam(request.getThoiGianLam());
        if (request.getSoLanLam() != null) quiz.setSoLanLam(request.getSoLanLam());
        if (request.getTrinhTrang() != null) quiz.setTrinhTrang(request.getTrinhTrang());
        Quiz savedQuiz = quizRepository.save(quiz);

        quizQuestionsRepository.findByQuiz_Id(quizId).forEach(qq ->
                answersRepository.deleteByQuestions_Id(qq.getQuestions().getId()));
        quizQuestionsRepository.deleteAll(quizQuestionsRepository.findByQuiz_Id(quizId));
        quiz.getDQuizQuestions().clear();

        List<QuestionResponseDTO> questionDTOs = buildQuestions(savedQuiz, request);
        return toQuizResponseDTO(savedQuiz, questionDTOs);
    }

    public void deleteQuiz(UUID quizId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz không tồn tại."));
        validationService.validateLecturerAssignment(userId, quiz.getLopHocPhan().getId());

        if (quiz.getThoiGianBatDau() != null && quiz.getThoiGianBatDau().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không thể xóa bài kiểm tra đã bắt đầu.");
        }
        quizRepository.delete(quiz);
    }

    public List<QuizResponseDTO> getQuizzesByLopHocPhan(UUID lopHocPhanId, UUID userId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        return quizRepository.findByLopHocPhan_Id(lopHocPhanId).stream()
                .map(quiz -> {
                    int questionCount = quiz.getDQuizQuestions() != null ? quiz.getDQuizQuestions().size() : 0;
                    return new QuizResponseDTO(quiz.getId(), quiz.getLopHocPhan().getId(),
                            quiz.getLopHocPhan().getMonHoc().getTenMonHoc(), quiz.getTieuDe(), quiz.getMoTa(),
                            quiz.getThoiGianBatDau(), quiz.getThoiGianKetThuc(), quiz.getThoiGianLam(),
                            quiz.getSoLanLam(), quiz.getTrinhTrang(),
                            quiz.getCreatedAt(), questionCount, null);
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
                .filter(aa -> aa.getQuestions() != null && aa.getAnswers() != null)
                .collect(Collectors.toMap(
                        aa -> aa.getQuestions().getId(),
                        aa -> aa,
                        (a, b) -> a));

        List<QuizAttemptAnswerDetailDTO> answerDetails = questions.stream()
                .map(question -> toAttemptAnswerDetail(question, selectedByQuestion.get(question.getId())))
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

    private QuizAttemptAnswerDetailDTO toAttemptAnswerDetail(Questions question, AttemptAnswers selectedAnswer) {
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

        return new QuizAttemptAnswerDetailDTO(
                question.getId(),
                question.getNoiDung(),
                question.getDiem(),
                selectedId,
                selected != null ? selected.getKeyAnswers() : null,
                selected != null ? selected.getConText() : null,
                selected != null ? selected.getIsCorrect() : null,
                options);
    }

    private List<Questions> getQuestions(Quiz quiz) {
        Map<UUID, Questions> questions = new LinkedHashMap<>();

        if (quiz.getDQuizQuestions() != null) {
            quiz.getDQuizQuestions().stream()
                    .map(QuizQuestions::getQuestions)
                    .filter(q -> q != null && q.getId() != null)
                    .forEach(q -> questions.putIfAbsent(q.getId(), q));
        }

        if (quiz.getDQuizExercises() != null) {
            quiz.getDQuizExercises().stream()
                    .filter(qe -> qe.getExercise() != null && qe.getExercise().getDQuestions() != null)
                    .flatMap(qe -> qe.getExercise().getDQuestions().stream())
                    .filter(q -> q != null && q.getId() != null)
                    .forEach(q -> questions.putIfAbsent(q.getId(), q));
        }

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

    private List<QuestionResponseDTO> buildQuestions(Quiz savedQuiz, QuizRequestDTO request) {
        return request.getQuestions().stream()
                .map(questionReq -> {
                    Questions question = new Questions();
                    question.setNoiDung(questionReq.getNoiDung());
                    question.setLoaiCauHoi(questionReq.getLoaiCauHoi());
                    question.setDiem(questionReq.getDiem() != null ? questionReq.getDiem() : 1.0f);
                    question.setExercise(null);
                    Questions savedQuestion = questionRepository.save(question);

                    QuizQuestions quizQuestion = new QuizQuestions();
                    quizQuestion.setQuiz(savedQuiz);
                    quizQuestion.setQuestions(savedQuestion);
                    quizQuestionsRepository.save(quizQuestion);

                    List<AnswerResponseDTO> answerDTOs = questionReq.getAnswers().stream()
                            .map(answerReq -> {
                                Answers answer = new Answers();
                                answer.setKeyAnswers(answerReq.getKeyAnswers());
                                answer.setConText(answerReq.getConText());
                                answer.setIsCorrect(answerReq.getIsCorrect());
                                answer.setQuestions(savedQuestion);
                                answersRepository.save(answer);
                                return new AnswerResponseDTO(answer.getId(), answer.getKeyAnswers(),
                                        answer.getConText(), answer.getIsCorrect());
                            })
                            .collect(Collectors.toList());

                    return new QuestionResponseDTO(savedQuestion.getId(), savedQuestion.getNoiDung(),
                            savedQuestion.getLoaiCauHoi(), savedQuestion.getDiem(), answerDTOs);
                })
                .collect(Collectors.toList());
    }

    private QuizResponseDTO toQuizResponseDTO(Quiz quiz, List<QuestionResponseDTO> questions) {
        return new QuizResponseDTO(quiz.getId(), quiz.getLopHocPhan().getId(),
                quiz.getLopHocPhan().getMonHoc().getTenMonHoc(), quiz.getTieuDe(), quiz.getMoTa(),
                quiz.getThoiGianBatDau(), quiz.getThoiGianKetThuc(), quiz.getThoiGianLam(),
                quiz.getSoLanLam(), quiz.getTrinhTrang(),
                quiz.getCreatedAt(), questions.size(), questions);
    }
}
