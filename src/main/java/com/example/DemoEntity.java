package com.example;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Data
@Entity
public class DemoEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
}
