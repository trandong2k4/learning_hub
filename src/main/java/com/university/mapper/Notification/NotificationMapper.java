package com.university.mapper.Notification;

import org.springframework.stereotype.Component;

import com.university.dto.response.Notification.NotificationResponse;
import com.university.entity.ThongBao;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(ThongBao tb, Boolean daNhan) {
        return NotificationResponse.builder()
                .id(tb.getId())
                .tieuDe(tb.getTieuDe())
                .noiDung(tb.getNoiDung())
                .loaiThongBao(tb.getLoaiThongBao())
                .createdAt(tb.getCreatedAt())
                .daNhan(daNhan)
                .build();
    }
}
