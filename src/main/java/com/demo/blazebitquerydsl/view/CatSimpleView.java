package com.demo.blazebitquerydsl.view;

import com.demo.blazebitquerydsl.model.Cat;
import com.blazebit.persistence.view.*;

@EntityView(Cat.class)
public interface CatSimpleView {
    
    @IdMapping
    Long getId();

    String getName();
}
