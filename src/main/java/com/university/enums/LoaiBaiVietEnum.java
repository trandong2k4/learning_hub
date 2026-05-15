package com.university.enums;

public enum LoaiBaiVietEnum {
    THONG_BAO("Thông báo"),
    TAI_LIEU("Tài liệu"),
    CANH_BAO("Cảnh báo"),
    HUONG_DAN("Hướng dẫn"),
    CONG_KHAI("Công khai");

    private final String displayName;

    LoaiBaiVietEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
