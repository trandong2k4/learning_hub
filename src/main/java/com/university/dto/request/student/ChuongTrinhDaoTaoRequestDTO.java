package com.university.dto.request.student;

import java.util.UUID;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChuongTrinhDaoTaoRequestDTO {
    private UUID nganhId;
    private String keyword;

}
