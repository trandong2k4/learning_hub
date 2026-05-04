package com.university.service.student;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.SubmitExerciseRequestDTO;
import com.university.entity.Exercise;
import com.university.entity.HocVien;
import com.university.entity.SubmitExercise;
import com.university.repository.student.ExerciseStudentsRepository;
import com.university.repository.student.HocVienStudentsRepository;
import com.university.repository.student.SubmitExerciseStudentsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmitExerciseService {

    private final SubmitExerciseStudentsRepository submitRepo;
    private final ExerciseStudentsRepository exerciseRepo;
    private final HocVienStudentsRepository hocVienRepo;

    public String submit(SubmitExerciseRequestDTO request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        Exercise exercise = exerciseRepo.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai tap"));

        HocVien hocVien = hocVienRepo.findById(hocVienId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay hoc vien"));

        LocalDateTime now = LocalDateTime.now();

        if (exercise.getThoiGianKetThuc() != null
                && now.isAfter(exercise.getThoiGianKetThuc())) {
            return "Qua han nop bai!";
        }

        Optional<SubmitExercise> existing =
                submitRepo.findByExercise_IdAndHocVien_Id(request.getExerciseId(), hocVienId);

        if (existing.isPresent()) {
            SubmitExercise submit = existing.get();
            submit.setFileExerciseUrl(request.getFileExerciseUrl());
            submit.setThoiGianNop(now);

            submitRepo.save(submit);
            return "Cap nhat bai nop thanh cong!";
        }

        SubmitExercise submit = new SubmitExercise();
        submit.setExercise(exercise);
        submit.setHocVien(hocVien);
        submit.setFileExerciseUrl(request.getFileExerciseUrl());
        submit.setThoiGianNop(now);

        submitRepo.save(submit);

        return "Nop bai thanh cong!";
    }
}
