package com.university.dto.response.admin;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyStatDTO {
    private List<String> labels; // ["Tuần 1", "Tuần 2", ...]
    private List<Long> values; // [12, 18, 25, ...]
}
