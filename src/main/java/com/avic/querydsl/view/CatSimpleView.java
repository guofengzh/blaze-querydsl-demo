package com.avic.querydsl.view;

import com.avic.querydsl.model.Cat;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

@EntityView(Cat.class)
public interface CatSimpleView {
    
    @IdMapping
    Long getId();

    String getName();
}
