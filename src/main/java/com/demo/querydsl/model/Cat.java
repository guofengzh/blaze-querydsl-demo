package com.demo.querydsl.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Data
@DynamicUpdate
@Entity
public class Cat {
    
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Person owner;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cat ancestor;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cat mother;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Cat father;
    @ManyToMany
    private Set<Cat> kittens = new HashSet<>();

    public Cat() {
    }

    public Cat(String name, Integer age, Person owner) {
        this.name = name;
        this.age = age;
        this.owner = owner;
    }
}
