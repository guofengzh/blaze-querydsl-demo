package com.demo.querydsl;

import com.demo.querydsl.model.Cat;
import com.demo.querydsl.model.Person;
import com.demo.querydsl.model.QCat;
import com.demo.querydsl.model.QPerson;
import com.demo.querydsl.view.CatSimpleView;
import com.demo.querydsl.view.PersonView;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.impl.CriteriaBuilderImpl;
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
class QuerydslSimpleTest {
    private final static Logger log = LoggerFactory.getLogger(QuerydslSimpleTest.class);

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

        Person person = new Person("John", 10);
        person.getKittens().add(p1);
        p1.setOwner(person);
        em.persist(person);
    }

    @Test
    public void testCatViewBlazeWay() {
        CriteriaBuilder<Cat> cb0 = cbf.create(em, Cat.class)
                //.from(Cat.class, "cat")
                //.select("cat")
                ;

        System.out.println(cb0.getQueryString());

        CriteriaBuilderImpl<Cat> cb = (CriteriaBuilderImpl<Cat>)cb0;
        //cb.resetSelectInfos();

        EntityViewSetting<CatSimpleView, CriteriaBuilder<CatSimpleView>> setting = EntityViewSetting.create(CatSimpleView.class);
        CriteriaBuilder<CatSimpleView> catViewBuilder = evm.applySetting(setting, cb);

        System.out.println(catViewBuilder.getQueryString());

        List<CatSimpleView> playerViews = catViewBuilder.getResultList();

        playerViews.forEach(p -> log.info(p.getId() + " " + p.getName()));

    }

    @Test
    public void testCatViewQuerydslWay() {
        QCat cat = QCat.cat;
        BlazeJPAQuery<Cat> query = new BlazeJPAQuery<>(em, JPQLNextTemplates.DEFAULT, cbf)
                .from(cat)
                .select(cat)
                ;
        System.out.println(query.toString());

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        // render() simply turns the query into a blaze content representation by traversing the Querydsl syntax tree
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        //CriteriaBuilder<Cat> cb = (CriteriaBuilder<Cat>) catQueryable;
        CriteriaBuilderImpl<Cat> cb = (CriteriaBuilderImpl<Cat>)catQueryable;
        cb.resetSelectInfos();

        System.out.println(cb.getQueryString());

        EntityViewSetting<CatSimpleView, CriteriaBuilder<CatSimpleView>> setting = EntityViewSetting.create(CatSimpleView.class);
        CriteriaBuilder<CatSimpleView> catSimpleViewCriteriaBuilder = evm.applySetting(setting, cb);

        System.out.println(catSimpleViewCriteriaBuilder.getQueryString());

        List<CatSimpleView> catViews = catSimpleViewCriteriaBuilder.getResultList();
        catViews.forEach(p -> log.info(p.getId() + " " + p.getName()));
    }

    @Test
    public void personViewTest() {
        QPerson qPerson = QPerson.person;
        BlazeJPAQuery<Person> query = new BlazeJPAQuery<>(em, JPQLNextTemplates.DEFAULT, cbf)
                .from(qPerson)
                .select(qPerson)
                ;

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        //CriteriaBuilder<Cat> cb = (CriteriaBuilder<Cat>) catQueryable;
        CriteriaBuilderImpl<Cat> cb = (CriteriaBuilderImpl<Cat>)catQueryable;
        cb.resetSelectInfos();

        //CriteriaBuilder<Cat> cb = cbf.create(em, Cat.class);  // This is ok
        CriteriaBuilder<PersonView> catSimpleViewCriteriaBuilder = evm.applySetting(EntityViewSetting.create(PersonView.class), cb);

        List<PersonView> catViews = catSimpleViewCriteriaBuilder.getResultList();
        catViews.forEach(p -> log.info(p.getId() + " " + p.getName() ));
    }

}
