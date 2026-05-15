package com.university.dto.response.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.LoaiThongBaoEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private UUID id;
    private String tieuDe;
    private String noiDung;
    private LoaiThongBaoEnum loaiThongBao;
    private LocalDateTime createdAt;
    private Boolean daNhan;
}