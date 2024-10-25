package com.avic.querydsl;

import com.avic.querydsl.model.Cat;
import com.avic.querydsl.model.Person;
import com.avic.querydsl.model.QCat;
import com.avic.querydsl.model.QPerson;
import com.avic.querydsl.view.CatSimpleView;
import com.avic.querydsl.view.PersonView;
import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.Queryable;
import com.blazebit.persistence.querydsl.BlazeCriteriaBuilderRenderer;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.querydsl.JPQLNextTemplates;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.querydsl.core.Tuple;
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

        Person person = new Person("John");
        person.getKittens().add(p1);
        p1.setOwner(person);
        em.persist(person);
    }

    @Test
    public void catViewTest() {
        QCat cat = QCat.cat;
        BlazeJPAQuery<Tuple> query = new BlazeJPAQuery<>(em, JPQLNextTemplates.DEFAULT, cbf)
                .from(cat)
                .select(cat.id, cat.name, cat.kittens)
                ;

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        CriteriaBuilder<Cat> cb = (CriteriaBuilder<Cat>) catQueryable;
        //CriteriaBuilder<Cat> cb = cbf.create(em, Cat.class);  // This is ok
        CriteriaBuilder<CatSimpleView> catSimpleViewCriteriaBuilder = evm.applySetting(EntityViewSetting.create(CatSimpleView.class), cb);

        List<CatSimpleView> catViews = catSimpleViewCriteriaBuilder.getResultList();
        catViews.forEach(p -> log.info(p.getId() + " " + p.getName() ));
    }

    /**
     * TODO: the query for association does not work
     */
    @Test
    public void personViewTest() {
        QPerson qPerson = QPerson.person;
        BlazeJPAQuery<Person> query = new BlazeJPAQuery<>(em, JPQLNextTemplates.DEFAULT, cbf)
                .from(qPerson)
                .select(qPerson)
                ;

        BlazeCriteriaBuilderRenderer<Cat> bbr = new BlazeCriteriaBuilderRenderer<>(cbf, em, JPQLNextTemplates.DEFAULT);
        Queryable<Cat, ?> catQueryable = bbr.render(query);
        CriteriaBuilder<Cat> cb = (CriteriaBuilder<Cat>) catQueryable;
        //CriteriaBuilder<Cat> cb = cbf.create(em, Cat.class);  // This is ok
        CriteriaBuilder<PersonView> catSimpleViewCriteriaBuilder = evm.applySetting(EntityViewSetting.create(PersonView.class), cb);

        List<PersonView> catViews = catSimpleViewCriteriaBuilder.getResultList();
        catViews.forEach(p -> log.info(p.getId() + " " + p.getName() ));
    }

}
