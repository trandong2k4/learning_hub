package com.university.service.lecturer;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.lecturer.AnswerRequestDTO;
import com.university.dto.request.lecturer.AssignmentExcelRowDTO;
import com.university.dto.request.lecturer.AssignmentRequestDTO;
import com.university.dto.request.lecturer.QuestionRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.lecturer.AnswerResponseDTO;
import com.university.dto.response.lecturer.AssignmentResponseDTO;
import com.university.dto.response.lecturer.QuestionResponseDTO;
import com.university.dto.response.lecturer.SubmissionDetailResponseDTO;
import com.university.dto.response.lecturer.SubmissionResponseDTO;
import com.university.entity.Answers;
import com.university.entity.DiemThanhPhan;
import com.university.entity.Exercise;
import com.university.entity.ExerciseSubmitAnswer;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.entity.Questions;
import com.university.entity.SubmitExercise;
import com.university.entity.Users;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerAnswersRepository;
import com.university.repository.lecturer.LecturerAssignmentRepository;
import com.university.repository.lecturer.LecturerDiemThanhPhanRepository;
import com.university.repository.lecturer.LecturerQuestionRepository;
import com.university.repository.lecturer.LecturerSubmitExerciseRepository;
import com.university.repository.student.ExerciseSubmitAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerAssignmentService {

        private final LecturerAssignmentRepository assignmentRepository;
        private final LecturerSubmitExerciseRepository submitExerciseRepository;
        private final LecturerQuestionRepository questionRepository;
        private final LecturerAnswersRepository answersRepository;
        private final LecturerDiemThanhPhanRepository diemThanhPhanRepository;
        private final ExerciseSubmitAnswerRepository exerciseSubmitAnswerRepository;
        private final LopHocPhanAdminRepository lopHocPhanRepository;
        private final UsersAdminRepository userRepository;
        private final LecturerNotificationService notificationService;
        private final LecturerValidationService validationService;

        public List<AssignmentResponseDTO> getAssignments(UUID lopHocPhanId, UUID userId) {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);

                List<Exercise> exercises = assignmentRepository.findByLopHocPhan_Id(lopHocPhanId);
                if (exercises.isEmpty()) {
                        return List.of();
                }

                List<UUID> exerciseIds = exercises.stream().map(Exercise::getId).toList();

                List<Object[]> countRows = submitExerciseRepository.countByExercise_IdIn(exerciseIds);
                Map<UUID, Long> submissionCounts = countRows.stream()
                                .collect(Collectors.toMap(
                                        row -> (UUID) row[0],
                                        row -> (Long) row[1]));

                List<Questions> allQuestions = questionRepository.findByExerciseIdsWithAnswers(exerciseIds);
                Map<UUID, List<Questions>> questionsByExercise = allQuestions.stream()
                                .collect(Collectors.groupingBy(q -> q.getExercise().getId()));

                return exercises.stream()
                                .map(exercise -> {
                                        int submissionCount = submissionCounts
                                                        .getOrDefault(exercise.getId(), 0L).intValue();
                                        List<QuestionResponseDTO> questions = questionsByExercise
                                                        .getOrDefault(exercise.getId(), List.of()).stream()
                                                        .map(this::toQuestionResponseFromEntity)
                                                        .collect(Collectors.toList());
                                        return toAssignmentResponse(exercise, submissionCount, questions);
                                })
                                .collect(Collectors.toList());
        }

        public AssignmentResponseDTO createAssignment(UUID userId, AssignmentRequestDTO request) {
                validateAssignmentRequest(request);
                validationService.validateLecturerAssignment(userId, request.getLopHocPhanId());
                LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học phần."));
                Users user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại."));

                LocalDateTime startTime = request.getThoiGianBatDau() != null
                                ? request.getThoiGianBatDau()
                                : LocalDateTime.now();
                LocalDateTime endTime = request.getThoiGianKetThuc() != null
                                ? request.getThoiGianKetThuc()
                                : startTime.plusWeeks(1);
                validateTimeRange(startTime, endTime);
                String title = request.getTieuDe().trim();
                if (assignmentRepository.existsByLopHocPhan_IdAndTieuDeAndThoiGianBatDauAndThoiGianKetThuc(
                                request.getLopHocPhanId(), title, startTime, endTime)) {
                        throw new IllegalArgumentException("Bài tập này đã được tạo. Vui lòng kiểm tra danh sách bài tập.");
                }

                Exercise exercise = new Exercise();
                exercise.setTieuDe(title);
                exercise.setMoTa(trimToNull(request.getMoTa()));
                exercise.setFileExerciseUrl(trimToNull(request.getFileExerciseUrl()));
                exercise.setLopHocPhan(lopHocPhan);
                exercise.setGioiHanLanLam(resolveAttemptLimit(request.getGioiHanLanLam()));
                exercise.setThoiGianBatDau(startTime);
                exercise.setThoiGianKetThuc(endTime);
                Exercise saved = assignmentRepository.save(exercise);
                List<QuestionResponseDTO> questions = saveQuestions(saved, request.getQuestions());

                String deadline = saved.getThoiGianKetThuc() != null
                                ? saved.getThoiGianKetThuc().toLocalDate().toString()
                                : "1 tuần";
                notificationService.sendToClassStudents(user, lopHocPhan.getId(),
                                "Bài tập mới: " + saved.getTieuDe(),
                                "Giảng viên " + user.getHoTen() + " đã giao bài tập mới cho lớp " +
                                                lopHocPhan.getMonHoc().getTenMonHoc() + ". Hạn nộp: " + deadline);

                return toAssignmentResponse(saved, 0, questions);
        }

        public AssignmentResponseDTO updateAssignment(UUID userId, UUID assignmentId, AssignmentRequestDTO request) {
                Exercise existing = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài tập không tồn tại."));
                validationService.validateLecturerAssignment(userId, existing.getLopHocPhan().getId());

                if (request.getTieuDe() == null || request.getTieuDe().trim().isEmpty()) {
                        throw new IllegalArgumentException("Tiêu đề bài tập không được để trống.");
                }

                existing.setTieuDe(request.getTieuDe().trim());
                existing.setMoTa(trimToNull(request.getMoTa()));
                existing.setFileExerciseUrl(trimToNull(request.getFileExerciseUrl()));
                if (request.getThoiGianBatDau() != null)
                        existing.setThoiGianBatDau(request.getThoiGianBatDau());
                if (request.getThoiGianKetThuc() != null)
                        existing.setThoiGianKetThuc(request.getThoiGianKetThuc());
                if (request.getGioiHanLanLam() != null)
                        existing.setGioiHanLanLam(resolveAttemptLimit(request.getGioiHanLanLam()));
                validateTimeRange(existing.getThoiGianBatDau(), existing.getThoiGianKetThuc());

                int submissionCount = submitExerciseRepository
                                .countByExercise_Id(existing.getId());
                Exercise saved = assignmentRepository.save(existing);
                if (request.getQuestions() != null && submissionCount == 0) {
                        questionRepository.deleteByExercise_Id(saved.getId());
                        saved.getDQuestions().clear();
                        saveQuestions(saved, request.getQuestions());
                }
                List<Questions> questions = questionRepository.findByExerciseIdsWithAnswers(List.of(saved.getId()));
                List<QuestionResponseDTO> questionDTOs = questions.stream()
                                .map(this::toQuestionResponseFromEntity)
                                .collect(Collectors.toList());
                return toAssignmentResponse(saved, submissionCount, questionDTOs);
        }

        public ExcelImportResult importAssignmentsFromExcel(UUID userId, UUID lopHocPhanId, MultipartFile file)
                        throws IOException {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);

                List<AssignmentExcelRowDTO> rows = EasyExcel.read(file.getInputStream())
                                .head(AssignmentExcelRowDTO.class)
                                .sheet()
                                .doReadSync();

                ExcelImportResult result = new ExcelImportResult();
                result.setTotalRows(rows.size());
                List<String> errors = new ArrayList<>();
                Map<String, AssignmentRequestDTO> assignments = new LinkedHashMap<>();

                for (int i = 0; i < rows.size(); i++) {
                        int rowNumber = i + 2;
                        AssignmentExcelRowDTO row = rows.get(i);
                        String title = trim(row.getTieuDe());
                        if (title == null) {
                                errors.add("Dòng " + rowNumber + ": Tiêu đề bài tập không được để trống");
                                continue;
                        }

                        LocalDateTime startTime;
                        LocalDateTime endTime;
                        Integer attemptLimit;
                        try {
                                startTime = parseDateTime(row.getThoiGianBatDau());
                                endTime = parseDateTime(row.getThoiGianKetThuc());
                                validateTimeRange(startTime, endTime);
                                attemptLimit = parsePositiveInteger(row.getGioiHanLanLam(), 1, "GioiHanLanLam");
                        } catch (RuntimeException ex) {
                                errors.add("Dòng " + rowNumber + ": " + ex.getMessage());
                                continue;
                        }

                        AssignmentRequestDTO request = assignments.computeIfAbsent(title.toLowerCase(Locale.ROOT),
                                        key -> {
                                                AssignmentRequestDTO dto = new AssignmentRequestDTO();
                                                dto.setLopHocPhanId(lopHocPhanId);
                                                dto.setTieuDe(title);
                                                dto.setMoTa(trim(row.getMoTa()));
                                                dto.setFileExerciseUrl(trim(row.getFileExerciseUrl()));
                                                dto.setThoiGianBatDau(startTime);
                                                dto.setThoiGianKetThuc(endTime);
                                                dto.setGioiHanLanLam(attemptLimit);
                                                dto.setQuestions(new ArrayList<>());
                                                return dto;
                                        });

                        QuestionRequestDTO question = toQuestionRequest(row, rowNumber, errors);
                        if (question != null) {
                                request.getQuestions().add(question);
                        }
                }

                int successCount = 0;
                for (AssignmentRequestDTO request : assignments.values()) {
                        try {
                                createAssignment(userId, request);
                                successCount++;
                        } catch (Exception ex) {
                                errors.add("Bài tập '" + request.getTieuDe() + "': " + ex.getMessage());
                        }
                }

                result.setSuccessCount(successCount);
                result.setErrorCount(errors.size());
                result.setErrors(errors);
                if (successCount > 0) {
                        result.setMessage("Đã import " + successCount + " bài tập thành công"
                                        + (errors.isEmpty() ? "" : ". " + errors.size() + " lỗi"));
                } else {
                        result.setMessage(errors.isEmpty() ? "Không có dòng nào được xử lý" : "Import thất bại");
                }
                return result;
        }

        public void deleteAssignment(UUID userId, UUID assignmentId) {
                Exercise existing = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài tập không tồn tại."));
                validationService.validateLecturerAssignment(userId, existing.getLopHocPhan().getId());

                int submissionCount = submitExerciseRepository.countByExercise_Id(assignmentId);
                if (submissionCount > 0) {
                        throw new IllegalStateException(
                                "Không thể xóa bài tập vì đã có " + submissionCount + " bài nộp. Vui lòng xóa các bài nộp trước.");
                }

                assignmentRepository.delete(existing);
        }

        public List<SubmissionResponseDTO> getSubmissions(UUID lopHocPhanId, UUID exerciseId, UUID userId) {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);
                Exercise exercise = assignmentRepository.findById(exerciseId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài tập không tồn tại."));
                if (!exercise.getLopHocPhan().getId().equals(lopHocPhanId)) {
                        throw new IllegalArgumentException("Bài tập không thuộc lớp học phần này.");
                }

                List<SubmitExercise> submissions = submitExerciseRepository.findByExercise_IdWithHocVien(exerciseId);
                if (submissions.isEmpty()) {
                        return List.of();
                }

                List<UUID> hocVienIds = submissions.stream()
                                .map(s -> s.getHocVien().getId()).distinct().toList();
                Map<UUID, Float> grades = buildAverageGrades(lopHocPhanId, hocVienIds);

                return submissions.stream()
                                .map(submission -> {
                                        HocVien hocVien = submission.getHocVien();
                                        Users user = hocVien.getUsers();
                                        Float grade = grades.getOrDefault(hocVien.getId(), null);
                                        return new SubmissionResponseDTO(submission.getId(),
                                                        exercise.getId(),
                                                        exercise.getTieuDe(), hocVien.getId(), user.getHoTen(),
                                                        hocVien.getMaHocVien(),
                                                        submission.getFileExerciseUrl(), submission.getThoiGianNop(),
                                                        grade, null);
                                })
                                .collect(Collectors.toList());
        }

        public SubmissionResponseDTO getSubmissionDetail(UUID submissionId, UUID userId) {
                SubmitExercise submission = submitExerciseRepository.findById(submissionId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài nộp không tồn tại."));
                Exercise exercise = submission.getExercise();
                LopHocPhan lopHocPhan = exercise.getLopHocPhan();
                validationService.validateLecturerAssignment(userId, lopHocPhan.getId());

                HocVien hocVien = submission.getHocVien();
                Users user = hocVien.getUsers();
                Float grade = validationService.findAverageGrade(lopHocPhan.getId(), hocVien.getId());
                return new SubmissionResponseDTO(submission.getId(), exercise.getId(), exercise.getTieuDe(),
                                 hocVien.getId(), user.getHoTen(), hocVien.getMaHocVien(),
                                 submission.getFileExerciseUrl(), submission.getThoiGianNop(), grade,
                                 submission.getGhiChu());
        }

        public void gradeSubmission(UUID submissionId, UUID userId, Double diem, String feedback) {
                SubmitExercise submission = submitExerciseRepository.findById(submissionId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài nộp không tồn tại."));
                Exercise exercise = submission.getExercise();
                validationService.validateLecturerAssignment(userId, exercise.getLopHocPhan().getId());

                submission.setDiem(diem);
                submission.setGhiChu(feedback);
                submitExerciseRepository.save(submission);
        }

        public SubmissionDetailResponseDTO getSubmissionDetailFull(UUID submissionId, UUID userId) {
                SubmitExercise submission = submitExerciseRepository.findById(submissionId)
                                .orElseThrow(() -> new IllegalArgumentException("Bài nộp không tồn tại."));
                Exercise exercise = submission.getExercise();
                validationService.validateLecturerAssignment(userId, exercise.getLopHocPhan().getId());

                HocVien hocVien = submission.getHocVien();
                Users user = hocVien.getUsers();
                Float grade = validationService.findAverageGrade(exercise.getLopHocPhan().getId(), hocVien.getId());

                List<ExerciseSubmitAnswer> submitAnswers = exerciseSubmitAnswerRepository
                                .findBySubmissionIdWithQuestions(submissionId);

                List<SubmissionDetailResponseDTO.QuestionAnswerDTO> answers = submitAnswers.stream()
                                .map(sa -> buildQuestionAnswer(sa))
                                .collect(Collectors.toList());

                return new SubmissionDetailResponseDTO(
                                submission.getId(),
                                exercise.getId(),
                                exercise.getTieuDe(),
                                hocVien.getId(),
                                user.getHoTen(),
                                hocVien.getMaHocVien(),
                                submission.getFileExerciseUrl(),
                                submission.getThoiGianNop(),
                                grade,
                                submission.getGhiChu(),
                                answers);
        }

        private SubmissionDetailResponseDTO.QuestionAnswerDTO buildQuestionAnswer(ExerciseSubmitAnswer sa) {
                Questions question = sa.getQuestions();
                List<Answers> allOptions = question.getDAnswers();

                if (Boolean.TRUE.equals(question.getLoaiCauHoi())) {
                        // Multiple choice
                        List<SubmissionDetailResponseDTO.AnswerOptionDTO> options = allOptions.stream()
                                        .map(a -> new SubmissionDetailResponseDTO.AnswerOptionDTO(
                                                        a.getId(),
                                                        a.getConText(),
                                                        a.getIsCorrect(),
                                                        sa.getAnswers() != null && sa.getAnswers().getId().equals(a.getId())))
                                        .collect(Collectors.toList());
                        return new SubmissionDetailResponseDTO.QuestionAnswerDTO(
                                        question.getId(),
                                        question.getNoiDung(),
                                        question.getLoaiCauHoi(),
                                        question.getNhieuDapAn(),
                                        question.getDiem(),
                                        sa.getDiemDatDuoc(),
                                        null,
                                        options,
                                        sa.getAnswers() != null ? sa.getAnswers().getId() : null,
                                        sa.getIsCorrect());
                } else {
                        // Essay / file
                        return new SubmissionDetailResponseDTO.QuestionAnswerDTO(
                                        question.getId(),
                                        question.getNoiDung(),
                                        question.getLoaiCauHoi(),
                                        question.getNhieuDapAn(),
                                        question.getDiem(),
                                        sa.getDiemDatDuoc(),
                                        sa.getNoiDungTuLuan(),
                                        null,
                                        null,
                                        sa.getIsCorrect());
                }
        }

        private List<QuestionResponseDTO> saveQuestions(Exercise exercise, List<QuestionRequestDTO> questionRequests) {
                if (questionRequests == null || questionRequests.isEmpty()) {
                        return List.of();
                }

                List<QuestionResponseDTO> response = new ArrayList<>();
                for (QuestionRequestDTO questionRequest : questionRequests) {
                        validateQuestion(questionRequest);

                        Questions question = new Questions();
                        question.setNoiDung(questionRequest.getNoiDung().trim());
                        question.setLoaiCauHoi(Boolean.TRUE.equals(questionRequest.getLoaiCauHoi()));
                        question.setNhieuDapAn(Boolean.TRUE.equals(questionRequest.getNhieuDapAn()));
                        question.setDiem(questionRequest.getDiem() != null ? questionRequest.getDiem() : 1.0f);
                        question.setExercise(exercise);
                        Questions savedQuestion = questionRepository.save(question);

                        List<AnswerResponseDTO> answers = new ArrayList<>();
                        if (Boolean.TRUE.equals(savedQuestion.getLoaiCauHoi())) {
                                for (AnswerRequestDTO answerRequest : questionRequest.getAnswers()) {
                                        if (answerRequest.getConText() == null || answerRequest.getConText().trim().isEmpty()) {
                                                continue;
                                        }
                                        Answers answer = new Answers();
                                        answer.setKeyAnswers(trimToEmpty(answerRequest.getKeyAnswers()).toUpperCase(Locale.ROOT));
                                        answer.setConText(answerRequest.getConText().trim());
                                        answer.setIsCorrect(Boolean.TRUE.equals(answerRequest.getIsCorrect()));
                                        answer.setQuestions(savedQuestion);
                                        Answers savedAnswer = answersRepository.save(answer);
                                        answers.add(new AnswerResponseDTO(savedAnswer.getId(), savedAnswer.getKeyAnswers(),
                                                        savedAnswer.getConText(), savedAnswer.getIsCorrect()));
                                }
                        }

                        response.add(new QuestionResponseDTO(savedQuestion.getId(), savedQuestion.getNoiDung(),
                                        savedQuestion.getLoaiCauHoi(), savedQuestion.getNhieuDapAn(),
                                        savedQuestion.getDiem(), answers));
                }
                return response;
        }

        private void validateQuestion(QuestionRequestDTO questionRequest) {
                if (questionRequest.getNoiDung() == null || questionRequest.getNoiDung().trim().isEmpty()) {
                        throw new IllegalArgumentException("Nội dung câu hỏi không được để trống.");
                }
                if (questionRequest.getDiem() != null && questionRequest.getDiem() < 0) {
                        throw new IllegalArgumentException("Điểm câu hỏi phải lớn hơn hoặc bằng 0.");
                }
                if (Boolean.TRUE.equals(questionRequest.getLoaiCauHoi())) {
                        List<AnswerRequestDTO> answers = questionRequest.getAnswers() != null
                                        ? questionRequest.getAnswers()
                                        : List.of();
                        List<AnswerRequestDTO> validAnswers = answers.stream()
                                        .filter(a -> a.getConText() != null && !a.getConText().trim().isEmpty())
                                        .toList();
                        long correctCount = validAnswers.stream()
                                        .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                                        .count();
                        if (validAnswers.size() < 2) {
                                throw new IllegalArgumentException("Câu hỏi trắc nghiệm phải có ít nhất 2 đáp án.");
                        }
                        if (correctCount == 0) {
                                throw new IllegalArgumentException("Câu hỏi trắc nghiệm phải có ít nhất 1 đáp án đúng.");
                        }
                        if (!Boolean.TRUE.equals(questionRequest.getNhieuDapAn()) && correctCount > 1) {
                                throw new IllegalArgumentException("Câu hỏi một đáp án chỉ được có 1 đáp án đúng.");
                        }

                        Set<String> keys = new HashSet<>();
                        for (AnswerRequestDTO answer : validAnswers) {
                                String key = trimToEmpty(answer.getKeyAnswers()).toUpperCase(Locale.ROOT);
                                if (key.isBlank()) {
                                        throw new IllegalArgumentException("Mã đáp án không được để trống.");
                                }
                                if (!keys.add(key)) {
                                        throw new IllegalArgumentException("Mã đáp án bị trùng: " + key);
                                }
                        }
                }
        }

        private QuestionRequestDTO toQuestionRequest(AssignmentExcelRowDTO row, int rowNumber, List<String> errors) {
                String content = trim(row.getNoiDungCauHoi());
                if (content == null) {
                        return null;
                }

                boolean isMultipleChoice = isMultipleChoice(row.getLoaiCauHoi());
                QuestionRequestDTO question = new QuestionRequestDTO();
                question.setNoiDung(content);
                question.setLoaiCauHoi(isMultipleChoice);
                Float diem = parseNonNegativeFloat(row.getDiem(), 1.0f, rowNumber, errors);
                if (diem == null) {
                        return null;
                }
                question.setDiem(diem);
                question.setAnswers(new ArrayList<>());

                if (!isMultipleChoice) {
                        return question;
                }

                Map<String, String> answerTexts = Map.of(
                                "A", trimToEmpty(row.getDapAnA()),
                                "B", trimToEmpty(row.getDapAnB()),
                                "C", trimToEmpty(row.getDapAnC()),
                                "D", trimToEmpty(row.getDapAnD()),
                                "E", trimToEmpty(row.getDapAnE()),
                                "F", trimToEmpty(row.getDapAnF()));
                String correctText = trimToEmpty(row.getDapAnDung()).toUpperCase(Locale.ROOT);
                List<String> correctKeys = correctText.isBlank()
                                ? List.of()
                                : List.of(correctText.split("[,;\\s]+"));
                boolean singleChoice = isSingleChoice(row.getLoaiCauHoi());
                if (singleChoice && correctKeys.size() > 1) {
                        errors.add("Dòng " + rowNumber + ": Câu hỏi một đáp án chỉ được có 1 đáp án đúng");
                        return null;
                }
                Set<String> validKeys = Set.of("A", "B", "C", "D", "E", "F");
                List<String> invalidCorrectKeys = correctKeys.stream()
                                .filter(key -> !validKeys.contains(key))
                                .toList();
                if (!invalidCorrectKeys.isEmpty()) {
                        errors.add("Dòng " + rowNumber + ": Đáp án đúng không hợp lệ: "
                                        + String.join(",", invalidCorrectKeys));
                        return null;
                }
                question.setNhieuDapAn(!singleChoice && correctKeys.size() > 1);

                answerTexts.forEach((key, value) -> {
                        if (!value.isBlank()) {
                                question.getAnswers().add(new AnswerRequestDTO(key, value, correctKeys.contains(key)));
                        }
                });

                if (question.getAnswers().size() < 2) {
                        errors.add("Dòng " + rowNumber + ": Câu hỏi trắc nghiệm phải có ít nhất 2 đáp án");
                        return null;
                }
                if (question.getAnswers().stream().noneMatch(a -> Boolean.TRUE.equals(a.getIsCorrect()))) {
                        errors.add("Dòng " + rowNumber + ": Câu hỏi trắc nghiệm phải có đáp án đúng (A-F)");
                        return null;
                }
                return question;
        }

        private AssignmentResponseDTO toAssignmentResponse(Exercise exercise, int submissionCount,
                        List<QuestionResponseDTO> questions) {
                int questionCount = questions != null ? questions.size()
                                : (exercise.getDQuestions() != null ? exercise.getDQuestions().size() : 0);
                return new AssignmentResponseDTO(exercise.getId(), exercise.getTieuDe(), exercise.getMoTa(),
                                exercise.getCreatedAt(), exercise.getLopHocPhan().getId(), submissionCount,
                                exercise.getFileExerciseUrl(), exercise.getThoiGianBatDau(), exercise.getThoiGianKetThuc(),
                                questionCount, questions, exercise.getGioiHanLanLam());
        }

        private QuestionResponseDTO toQuestionResponseFromEntity(Questions question) {
                List<AnswerResponseDTO> answers = question.getDAnswers().stream()
                                .map(answer -> new AnswerResponseDTO(answer.getId(), answer.getKeyAnswers(),
                                                answer.getConText(), answer.getIsCorrect()))
                                .collect(Collectors.toList());
                return new QuestionResponseDTO(question.getId(), question.getNoiDung(),
                                question.getLoaiCauHoi(), question.getNhieuDapAn(), question.getDiem(), answers);
        }

        private void validateAssignmentRequest(AssignmentRequestDTO request) {
                if (request == null) {
                        throw new IllegalArgumentException("Dữ liệu bài tập không được để trống.");
                }
                if (request.getTieuDe() == null || request.getTieuDe().trim().isEmpty()) {
                        throw new IllegalArgumentException("Tiêu đề bài tập không được để trống.");
                }
        }

        private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
                if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
                        throw new IllegalArgumentException("Thời gian kết thúc phải lớn hơn thời gian bắt đầu.");
                }
        }

        private int resolveAttemptLimit(Integer value) {
                return value != null && value > 0 ? value : 1;
        }

        private boolean isEssay(String value) {
                String normalized = normalizeType(value);
                return normalized.equals("tu_luan")
                                || normalized.equals("tuluan")
                                || normalized.equals("tu-luan")
                                || normalized.equals("tự luận")
                                || normalized.equals("essay")
                                || normalized.equals("file")
                                || normalized.equals("file_dinh_kem")
                                || normalized.equals("cau_hoi_file")
                                || normalized.equals("false")
                                || normalized.equals("0");
        }

        private boolean isMultipleChoice(String value) {
                return !isEssay(value);
        }

        private boolean isSingleChoice(String value) {
                String normalized = normalizeType(value);
                return normalized.contains("mot")
                                || normalized.contains("một")
                                || normalized.contains("single")
                                || normalized.contains("one")
                                || normalized.equals("1");
        }

        private String normalizeType(String value) {
                return trimToEmpty(value)
                                .toLowerCase(Locale.ROOT)
                                .replace(" ", "_");
        }

        private LocalDateTime parseDateTime(String value) {
                String trimmed = trim(value);
                if (trimmed == null) {
                        return null;
                }
                List<DateTimeFormatter> formatters = List.of(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                for (DateTimeFormatter formatter : formatters) {
                        try {
                                return LocalDateTime.parse(trimmed, formatter);
                        } catch (DateTimeParseException ignored) {
                        }
                        try {
                                return LocalDateTime.parse(trimmed.replace("T", " "), formatter);
                        } catch (DateTimeParseException ignored) {
                        }
                }
                throw new RuntimeException("Định dạng thời gian không hợp lệ: " + value);
        }

        private Float parseNonNegativeFloat(String value, Float fallback, int rowNumber, List<String> errors) {
                String trimmed = trim(value);
                if (trimmed == null) {
                        return fallback;
                }
                try {
                        Float parsed = Float.parseFloat(trimmed);
                        if (parsed < 0) {
                                errors.add("Dòng " + rowNumber + ": Điểm câu hỏi phải lớn hơn hoặc bằng 0");
                                return null;
                        }
                        return parsed;
                } catch (NumberFormatException ex) {
                        errors.add("Dòng " + rowNumber + ": Điểm câu hỏi không đúng định dạng số");
                        return null;
                }
        }

        private Integer parsePositiveInteger(String value, Integer fallback, String columnName) {
                String trimmed = trim(value);
                if (trimmed == null) {
                        return fallback;
                }
                try {
                        int parsed = Integer.parseInt(trimmed);
                        if (parsed < 1) {
                                throw new RuntimeException(columnName + " phải lớn hơn hoặc bằng 1");
                        }
                        return parsed;
                } catch (NumberFormatException ex) {
                        throw new RuntimeException(columnName + " không đúng định dạng số nguyên");
                }
        }

        private String trim(String value) {
                if (value == null || value.trim().isEmpty()) {
                        return null;
                }
                return value.trim();
        }

        private String trimToNull(String value) {
                return trim(value);
        }

        private String trimToEmpty(String value) {
                return value == null ? "" : value.trim();
        }

        private Map<UUID, Float> buildAverageGrades(UUID lopHocPhanId, List<UUID> hocVienIds) {
                if (hocVienIds == null || hocVienIds.isEmpty()) {
                        return Map.of();
                }

                List<DiemThanhPhan> allGrades = diemThanhPhanRepository
                                .findByLopHocPhanIdWithRelations(lopHocPhanId);

                Map<UUID, Map<UUID, DiemThanhPhan>> latestByHocVienAndColumn = new LinkedHashMap<>();
                for (DiemThanhPhan grade : allGrades) {
                        UUID hvId = grade.getDangKyTinChi().getHocVien().getId();
                        if (!hocVienIds.contains(hvId)) {
                                continue;
                        }
                        UUID colId = grade.getCotDiem() != null ? grade.getCotDiem().getId() : null;
                        if (colId == null) {
                                continue;
                        }
                        latestByHocVienAndColumn
                                        .computeIfAbsent(hvId, k -> new LinkedHashMap<>())
                                        .putIfAbsent(colId, grade);
                }

                Map<UUID, Float> results = new LinkedHashMap<>();
                for (UUID hocVienId : hocVienIds) {
                        Map<UUID, DiemThanhPhan> columnGrades = latestByHocVienAndColumn.get(hocVienId);
                        if (columnGrades == null || columnGrades.isEmpty()) {
                                results.put(hocVienId, null);
                                continue;
                        }

                        double weightedSum = 0d;
                        double totalWeight = 0d;
                        boolean hasValidGrade = false;
                        for (DiemThanhPhan grade : columnGrades.values()) {
                                if (grade.getDiemSo() == null) {
                                        continue;
                                }
                                double weight = parseGradeWeight(grade.getCotDiem().getTiTrong());
                                if (weight <= 0d) {
                                        continue;
                                }
                                weightedSum += grade.getDiemSo() * weight;
                                totalWeight += weight;
                                hasValidGrade = true;
                        }

                        if (!hasValidGrade || totalWeight == 0d) {
                                results.put(hocVienId, null);
                        } else {
                                results.put(hocVienId, (float) Math.round((weightedSum / totalWeight) * 100.0d) / 100.0f);
                        }
                }
                return results;
        }

        private double parseGradeWeight(String tiTrong) {
                if (tiTrong == null || tiTrong.isBlank()) {
                        return 0d;
                }
                String normalized = tiTrong.trim().replace("%", "").replace(",", ".");
                try {
                        double parsed = Double.parseDouble(normalized);
                        return parsed > 1d ? parsed / 100d : parsed;
                } catch (NumberFormatException ex) {
                        return 0d;
                }
        }
}
