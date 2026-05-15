package com.university.service.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.request.student.ExerciseSubmitRequestDTO;
import com.university.dto.request.student.ExerciseSubmitRequestDTO.ExerciseQuestionAnswerDTO;
import com.university.entity.Answers;
import com.university.entity.Exercise;
import com.university.entity.ExerciseSubmitAnswer;
import com.university.entity.HocVien;
import com.university.entity.Questions;
import com.university.entity.SubmitExercise;
import com.university.repository.student.AnswersRepository;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.ExerciseSubmitAnswerRepository;
import com.university.repository.student.ExerciseStudentsRepository;
import com.university.repository.student.SubmitExerciseStudentsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitExerciseService {

    private final SubmitExerciseStudentsRepository submitRepo;
    private final ExerciseStudentsRepository exerciseRepo;
    private final ExerciseSubmitAnswerRepository submitAnswerRepo;
    private final AnswersRepository answersRepo;
    private final DangKyTinChiRepository dangKyTinChiRepo;
    private final CurrentHocVienService currentHocVienService;

    public String submit(ExerciseSubmitRequestDTO request) {
        if (request == null || request.getExerciseId() == null) {
            throw new IllegalArgumentException("Mã bài tập không được để trống.");
        }

        HocVien hocVien = currentHocVienService.getCurrentHocVien();
        UUID hocVienId = hocVien.getId();

        Exercise exercise = exerciseRepo.findDetailById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài tập."));

        LocalDateTime now = LocalDateTime.now();
        validateCanSubmit(exercise, hocVienId, now);

        int attemptLimit = exercise.getGioiHanLanLam() != null && exercise.getGioiHanLanLam() > 0
                ? exercise.getGioiHanLanLam()
                : 1;
        int submittedAttempts = submitRepo.countByExercise_IdAndHocVien_Id(exercise.getId(), hocVienId);
        if (submittedAttempts >= attemptLimit) {
            throw new IllegalStateException("Bạn đã vượt quá giới hạn số lần làm bài.");
        }

        SubmitExercise submit = new SubmitExercise();
        submit.setExercise(exercise);
        submit.setHocVien(hocVien);
        submit.setPhienThucHien(submittedAttempts + 1);
        submit.setFileExerciseUrl(trimToNull(request.getFileExerciseUrl()));
        submit.setThoiGianNop(now);
        submit = submitRepo.save(submit);

        SubmitResult submitResult = gradeAndPersistAnswers(submit, exercise, request.getAnswers());
        if (submitResult.hasAutoGradedQuestion()) {
            submit.setDiem(submitResult.autoScore());
            submitRepo.save(submit);
        }

        if (submitResult.hasAutoGradedQuestion()) {
            return "Nộp bài thành công. Phiên thực hiện: " + submit.getPhienThucHien()
                    + ". Điểm tự động: " + submitResult.autoScore();
        }
        return "Nộp bài thành công. Phiên thực hiện: " + submit.getPhienThucHien();
    }

    private void validateCanSubmit(Exercise exercise, UUID hocVienId, LocalDateTime now) {
        if (exercise.getLopHocPhan() == null || exercise.getLopHocPhan().getId() == null) {
            throw new IllegalStateException("Bài tập chưa được gán lớp học phần.");
        }

        boolean registered = dangKyTinChiRepo.existsByHocVienIdAndLopHocPhanId(
                hocVienId,
                exercise.getLopHocPhan().getId());
        if (!registered) {
            throw new IllegalStateException("Học viên chưa đăng ký lớp học phần này.");
        }

        if (exercise.getThoiGianBatDau() != null && now.isBefore(exercise.getThoiGianBatDau())) {
            throw new IllegalStateException("Bài tập chưa mở.");
        }
        if (exercise.getThoiGianKetThuc() != null && now.isAfter(exercise.getThoiGianKetThuc())) {
            throw new IllegalStateException("Bài tập đã hết hạn, không thể nộp bài.");
        }
    }

    private SubmitResult gradeAndPersistAnswers(
            SubmitExercise submit,
            Exercise exercise,
            List<ExerciseQuestionAnswerDTO> answerRequests) {

        Map<UUID, Questions> questionMap = exercise.getDQuestions().stream()
                .collect(Collectors.toMap(Questions::getId, question -> question));
        Map<UUID, List<ExerciseQuestionAnswerDTO>> answersByQuestion = groupAnswersByQuestion(answerRequests, questionMap);

        List<ExerciseSubmitAnswer> details = new ArrayList<>();
        double autoScore = 0.0;
        boolean hasAutoGradedQuestion = false;

        for (Questions question : exercise.getDQuestions()) {
            List<ExerciseQuestionAnswerDTO> submittedAnswers = answersByQuestion.getOrDefault(question.getId(), List.of());
            if (Boolean.TRUE.equals(question.getLoaiCauHoi())) {
                hasAutoGradedQuestion = true;
                QuestionGrade grade = gradeMultipleChoiceQuestion(question, submittedAnswers);
                autoScore += grade.earnedScore();
                details.addAll(toMultipleChoiceDetails(submit, question, grade));
            } else {
                details.addAll(toSubjectiveDetails(submit, question, submittedAnswers));
            }
        }

        if (!details.isEmpty()) {
            submitAnswerRepo.saveAll(details);
        }

        return new SubmitResult(hasAutoGradedQuestion, autoScore);
    }

    private Map<UUID, List<ExerciseQuestionAnswerDTO>> groupAnswersByQuestion(
            List<ExerciseQuestionAnswerDTO> answerRequests,
            Map<UUID, Questions> questionMap) {
        if (answerRequests == null || answerRequests.isEmpty()) {
            return Map.of();
        }

        Map<UUID, List<ExerciseQuestionAnswerDTO>> grouped = new LinkedHashMap<>();
        for (ExerciseQuestionAnswerDTO answerRequest : answerRequests) {
            UUID questionId = answerRequest.getQuestionId();
            if (questionId == null || !questionMap.containsKey(questionId)) {
                throw new IllegalArgumentException("Câu hỏi không thuộc bài tập đang nộp.");
            }
            grouped.computeIfAbsent(questionId, ignored -> new ArrayList<>()).add(answerRequest);
        }
        return grouped;
    }

    private QuestionGrade gradeMultipleChoiceQuestion(
            Questions question,
            List<ExerciseQuestionAnswerDTO> submittedAnswers) {
        Map<UUID, Answers> answerMap = question.getDAnswers().stream()
                .collect(Collectors.toMap(Answers::getId, answer -> answer));
        List<Answers> selectedAnswers = new ArrayList<>();
        Map<UUID, Boolean> seenAnswerIds = new LinkedHashMap<>();

        for (ExerciseQuestionAnswerDTO submittedAnswer : submittedAnswers) {
            if (submittedAnswer.getAnswerId() == null) {
                continue;
            }
            Answers answer = answersRepo.findById(submittedAnswer.getAnswerId())
                    .orElseThrow(() -> new IllegalArgumentException("Đáp án không tồn tại."));
            if (!answerMap.containsKey(answer.getId())) {
                throw new IllegalArgumentException("Đáp án không thuộc câu hỏi đang nộp.");
            }
            if (!seenAnswerIds.containsKey(answer.getId())) {
                seenAnswerIds.put(answer.getId(), Boolean.TRUE);
                selectedAnswers.add(answer);
            }
        }

        if (!Boolean.TRUE.equals(question.getNhieuDapAn()) && selectedAnswers.size() > 1) {
            throw new IllegalArgumentException("Câu hỏi một đáp án chỉ được chọn 1 đáp án.");
        }

        List<UUID> correctAnswerIds = question.getDAnswers().stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .map(Answers::getId)
                .toList();
        List<UUID> selectedAnswerIds = selectedAnswers.stream()
                .map(Answers::getId)
                .toList();
        boolean exactCorrect = !correctAnswerIds.isEmpty()
                && correctAnswerIds.size() == selectedAnswerIds.size()
                && selectedAnswerIds.containsAll(correctAnswerIds);
        float questionScore = question.getDiem() != null ? question.getDiem() : 0.0f;
        double earnedScore = exactCorrect ? questionScore : 0.0;
        return new QuestionGrade(selectedAnswers, exactCorrect, earnedScore);
    }

    private List<ExerciseSubmitAnswer> toMultipleChoiceDetails(
            SubmitExercise submit,
            Questions question,
            QuestionGrade grade) {
        if (grade.selectedAnswers().isEmpty()) {
            ExerciseSubmitAnswer detail = baseDetail(submit, question);
            detail.setIsCorrect(false);
            detail.setDiemDatDuoc(0.0f);
            return List.of(detail);
        }

        float scorePerSelectedAnswer = grade.exactCorrect()
                ? (float) (grade.earnedScore() / grade.selectedAnswers().size())
                : 0.0f;
        return grade.selectedAnswers().stream()
                .map(answer -> {
                    ExerciseSubmitAnswer detail = baseDetail(submit, question);
                    detail.setAnswers(answer);
                    detail.setIsCorrect(Boolean.TRUE.equals(answer.getIsCorrect()));
                    detail.setDiemDatDuoc(scorePerSelectedAnswer);
                    return detail;
                })
                .toList();
    }

    private List<ExerciseSubmitAnswer> toSubjectiveDetails(
            SubmitExercise submit,
            Questions question,
            List<ExerciseQuestionAnswerDTO> submittedAnswers) {
        if (submittedAnswers.isEmpty()) {
            return List.of();
        }

        return submittedAnswers.stream()
                .map(ExerciseQuestionAnswerDTO::getNoiDungTuLuan)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(content -> {
                    ExerciseSubmitAnswer detail = baseDetail(submit, question);
                    detail.setNoiDungTuLuan(content);
                    detail.setIsCorrect(null);
                    detail.setDiemDatDuoc(null);
                    return detail;
                })
                .toList();
    }

    private ExerciseSubmitAnswer baseDetail(SubmitExercise submit, Questions question) {
        ExerciseSubmitAnswer detail = new ExerciseSubmitAnswer();
        detail.setSubmitExercise(submit);
        detail.setQuestions(question);
        return detail;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private record SubmitResult(boolean hasAutoGradedQuestion, double autoScore) {
    }

    private record QuestionGrade(List<Answers> selectedAnswers, boolean exactCorrect, double earnedScore) {
    }
}
