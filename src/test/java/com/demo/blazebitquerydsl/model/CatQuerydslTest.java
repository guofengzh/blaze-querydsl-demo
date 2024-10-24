package com.demo.blazebitquerydsl.model;

import com.demo.blazebitquerydsl.view.CatSimpleView;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.querydsl.BlazeCriteriaBuilderRenderer;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CatQuerydslTest {
    private final static Logger log = LoggerFactory.getLogger(CatQuerydslTest.class);

    @Autowired
    private CriteriaBuilderFactory cbf;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private EntityViewManager evm;

    @BeforeEach
    public void init() {
        Cat p1 = new Cat();
        p1.setName("Tom");
        em.persist(p1);
    }

    @Test
    public void entityViewTest() {
        QCat cat = QCat.cat;
        BlazeJPAQuery<Cat> query = new BlazeJPAQuery<>(em, JPQLNextTemplates.DEFAULT, cbf)
                .from(cat)
                .select(cat);

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        CriteriaBuilder<Cat> cb = (CriteriaBuilder<Cat>) catQueryable;
        //CriteriaBuilder<Cat> cb = cbf.create(em, Cat.class);  // This is ok
        CriteriaBuilder<CatSimpleView> catSimpleViewCriteriaBuilder = evm.applySetting(EntityViewSetting.create(CatSimpleView.class), cb);

        List<CatSimpleView> catViews = catSimpleViewCriteriaBuilder.getResultList();
        catViews.forEach(p -> log.info(p.getId() + " " + p.getName()));
    }
}
