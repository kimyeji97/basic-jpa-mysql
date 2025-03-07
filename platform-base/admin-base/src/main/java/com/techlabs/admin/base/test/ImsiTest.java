package com.techlabs.admin.base.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="imsi_test")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImsiTest {
    @Id
    private Integer id;
    private String name;
}
