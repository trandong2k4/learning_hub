package com.university.dto.response.admin;

import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDeleteResultDTO {

    private int totalRequested;
    private int deletedCount;
    private int failedCount;
    private List<FailedUserDTO> failedUsers;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedUserDTO {
        private UUID id;
        private String hoTen;
        private String reason;
    }

    public static BatchDeleteResultDTO success(int deletedCount) {
        return BatchDeleteResultDTO.builder()
                .totalRequested(deletedCount)
                .deletedCount(deletedCount)
                .failedCount(0)
                .failedUsers(Collections.emptyList())
                .build();
    }

    public static BatchDeleteResultDTO partial(int total, int deleted, int failed, List<FailedUserDTO> failedList) {
        return BatchDeleteResultDTO.builder()
                .totalRequested(total)
                .deletedCount(deleted)
                .failedCount(failed)
                .failedUsers(failedList)
                .build();
    }
}
