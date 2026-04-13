package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LienHeAdminResponseDTO {

    private UUID id;
    private String tenLienHe;
    private String fanPageUrl;
    private String email;
    private String soDienThoai;
    private UUID khoaId;

    public interface LienHeView {
        UUID getId();
    }
}