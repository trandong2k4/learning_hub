package com.university.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.university.enums.CotDiemEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cot_diem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, nullable = false)
    private String tenCotDiem;

    @Column(length = 10, nullable = false)
    private String tiTrong;

    private CotDiemEnum loai;

    private Integer thuTuHienThi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lop_hoc_phan_id", nullable = false)
    private LopHocPhan lopHocPhan;

    @OneToMany(mappedBy = "cotDiem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiemThanhPhan> dDiemThanhPhans = new ArrayList<>();
}
