package com.university.dto.request.student;

import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChuongTrinhDaoTaoRequestDTO {

    @Size(max = 100, message = "Keyword khong duoc vuot qua 100 ky tu")
    private String keyword;
}
