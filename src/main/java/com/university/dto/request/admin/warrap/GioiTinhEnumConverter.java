package com.university.dto.request.admin.warrap;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.university.enums.GioiTinhEnum;

public class GioiTinhEnumConverter implements Converter<GioiTinhEnum> {

    @Override
    public Class<GioiTinhEnum> supportJavaTypeKey() {
        return GioiTinhEnum.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public GioiTinhEnum convertToJavaData(ReadCellData<?> cellData,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }
        String trimmed = stringValue.trim().toUpperCase();
        if ("NAM".equals(trimmed)) {
            return GioiTinhEnum.NAM;
        } else if ("NU".equals(trimmed) || "NỮ".equals(trimmed)) {
            return GioiTinhEnum.NU;
        }
        return GioiTinhEnum.NAM; // default
    }

    @Override
    public WriteCellData<?> convertToExcelData(GioiTinhEnum value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value == GioiTinhEnum.NAM ? "Nam" : "Nữ");
    }
}
