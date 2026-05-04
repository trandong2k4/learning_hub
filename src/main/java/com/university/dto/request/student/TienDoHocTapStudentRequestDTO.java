package com.university.dto.request.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TienDoHocTapStudentRequestDTO {

    private UUID hocKiId;
    private String keyword;
    private Boolean daHoanThanh;
    private Boolean daDat;
}
