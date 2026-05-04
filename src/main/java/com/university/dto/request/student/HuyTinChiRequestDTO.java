package com.university.dto.request.student;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HuyTinChiRequestDTO {

    @NotNull(message = "lopHocPhanId khong duoc de trong")
    private UUID lopHocPhanId;
}
