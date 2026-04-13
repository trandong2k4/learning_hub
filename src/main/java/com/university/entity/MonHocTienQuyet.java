package com.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "mon_hoc_tien_quyet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonHocTienQuyet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Ma mon ton tai trong bang mon hoc
    @Column(length = 10, nullable = false)
    private String maMonHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_hoc_id", nullable = false)
    private MonHoc monHoc;
    // Môn học tiên quyết 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_tien_quyet_id", nullable = false)
    private MonHoc monTienQuyet;
}