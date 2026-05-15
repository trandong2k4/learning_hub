package com.university.dto.request.admin;

import com.university.enums.TrangThaiXuLyLienHeEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhanHoiLienHeStatusRequestDTO {

    @NotNull(message = "Trạng thái không được để trống")
    private TrangThaiXuLyLienHeEnum trangThai;

    @Size(max = 100, message = "Người xử lý tối đa 100 ký tự")
    private String nguoiXuLy;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String ghiChu;
}
