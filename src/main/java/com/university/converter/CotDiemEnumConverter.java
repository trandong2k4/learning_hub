package com.university.converter;

import com.university.enums.CotDiemEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CotDiemEnumConverter implements AttributeConverter<CotDiemEnum, String> {

    @Override
    public String convertToDatabaseColumn(CotDiemEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public CotDiemEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return CotDiemEnum.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // DB có giá trị số (ordinal cũ) — map sang enum tương ứng
            return switch (dbData.trim()) {
                case "0"  -> CotDiemEnum.CHUYEN_CAN;
                case "1"  -> CotDiemEnum.BAI_TAP;
                case "2"  -> CotDiemEnum.THAO_LUAN;
                case "3"  -> CotDiemEnum.KIEM_TRA_15_PHUT;
                case "4"  -> CotDiemEnum.KIEM_TRA_1_TIET;
                case "5"  -> CotDiemEnum.THUC_HANH;
                case "6"  -> CotDiemEnum.THI_GIUA_KY;
                case "7"  -> CotDiemEnum.DO_AN;
                case "8"  -> CotDiemEnum.TIEU_LUAN;
                case "9"  -> CotDiemEnum.BAO_CAO;
                case "10" -> CotDiemEnum.THI_CUOI_KY;
                case "11" -> CotDiemEnum.THI_LAI;
                default   -> null;
            };
        }
    }
}
