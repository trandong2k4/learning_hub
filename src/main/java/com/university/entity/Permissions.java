package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permissions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 30, unique = true, nullable = false)
    private String maPermissions;

    private String moTa;

    @OneToMany(mappedBy = "permissions", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermissions> dRolePermissions = new ArrayList<>();

}
