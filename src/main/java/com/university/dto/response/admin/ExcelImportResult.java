package com.university.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportResult {
    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<String> errors = new ArrayList<>();
}