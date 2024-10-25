package com.avic.querydsl;

import com.avic.querydsl.model.*;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.querydsl.BlazeCriteriaBuilderRenderer;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.JPQLNextExpressions;
import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.blazebit.persistence.view.EntityViewManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class QuerydslCTETest {
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

    /**
     * Blaze-Persistence Querydsl Integration
     */
    @Test
    public void recursiveCTETest1() {
        QCatCte parentCat = new QCatCte("parentCat");
        Long someCatId = 1L;

        BlazeJPAQuery<CatCte> query = new BlazeJPAQuery<CatCte>(em, cbf)
                .withRecursive(QCatCte.catCte, new BlazeJPAQuery<>().unionAll(
                        new BlazeJPAQuery<>()
                                .from(QCat.cat)
                                .bind(QCatCte.catCte.id, QCat.cat.id)
                                .bind(QCatCte.catCte.name, QCat.cat.name)
                                .bind(QCatCte.catCte.ancestor, QCat.cat.ancestor)
                                .where(QCat.cat.id.eq(someCatId)),
                        new BlazeJPAQuery<>()
                                .from(QCat.cat)
                                .from(QCatCte.catCte, parentCat)
                                .bind(QCatCte.catCte.id, QCat.cat.id)
                                .bind(QCatCte.catCte.name, QCat.cat.name)
                                .bind(QCatCte.catCte.ancestor, QCat.cat.ancestor)
                                .where(QCat.cat.id.eq(parentCat.ancestor.id)))
                )
                .select(QCatCte.catCte)
                .from(QCatCte.catCte);

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        System.out.println(catQueryable.getClass().getCanonicalName()); // com.blazebit.persistence.impl.CriteriaBuilderImpl

        List<CatCte> result = query.fetch();
    }

    /**
     * Blaze-Persistence Core
     */
    @Test
    public void recursiveCTETest2() {
        Long someCatId = 1L;
        CriteriaBuilder<CatCte> cb = cbf.create(em, CatCte.class)
                .withRecursive(CatCte.class)
                .from(Cat.class, "cat")
                .bind("id").select("cat.id")
                .bind("name").select("cat.name")
                .bind("ancestor").select("cat.ancestor")
                .where("cat.id").eq(someCatId)
                .unionAll()
                .from(Cat.class, "cat")
                .from(CatCte.class, "parentCat")
                .bind("id").select("cat.id")
                .bind("name").select("cat.name")
                .bind("ancestor").select("cat.ancestor")
                .where("cat.id").eqExpression("parentCat.ancestor.id")
                .end();

        List<CatCte> result = cb.getResultList();
    }
}
