package com.demo.querydsl.model;

import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@CTE    // from com.blazebit.persistence
@Entity // from javax.persistence
public class CatCte {
    @Id // from javax.persistence
    private Long id;
    private String name;
    @ManyToOne
    private Cat ancestor;
}