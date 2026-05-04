package com.university.dto.request.student;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DangKyTinChiRequestDTO {

    @NotNull(message = "lopHocPhanId khong duoc de trong")
    private UUID lopHocPhanId;

    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
}
