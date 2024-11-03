package com.demo.querydsl.view;

import com.demo.querydsl.model.Person;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

import java.util.List;

@EntityView(Person.class)
public interface PersonView {
    @IdMapping
    Long getId();

    String getName();

    List<CatSimpleView> getKittens();

}