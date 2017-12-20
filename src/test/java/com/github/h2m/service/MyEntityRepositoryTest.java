package com.github.h2m.service;

import com.github.h2m.config.Transactor;
import com.github.h2m.entity.MyEntity;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Reproducer to show that when
 *
 * @author Marc Häbich
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
public class MyEntityRepositoryTest {

    private byte[] titleWorking = new byte[500]; // Sometimes it still worked with 1054 bytes
    private byte[] titleFailing = new byte[2000]; // Sometimes it already fails with 1055 bytes
    private byte[] description = new byte[10000];

    @Autowired
    MyEntityRepository underTest;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Transactor transactor;

    long myEntityId;

    @Before
    public void initLongStrings() {
        Random random = new Random();
        random.nextBytes(description);
        random.nextBytes(titleWorking);
        random.nextBytes(titleFailing);
    }


    @Test
    public void testOne() {
        MyEntity entity = underTest.save(new MyEntity(new String(description), new String(titleWorking)));
        Long id = entity.getId();
        Assert.notNull(id, "id should be set");
    }

    @Test
    public void testWorking() {
        runTest(titleWorking);
    }

    @Test
    public void testFailing() {
        runTest(titleFailing);
    }

    private void runTest(byte[] titleByteArray) {
        String title = new String(titleByteArray);
        String reverseTitle = new StringBuilder(title).reverse().toString();

        assertTrue("Rollback!!", transactor.perform(() -> {
            addMyEntity(title);
        }));
        assertTrue("Rollback!!", transactor.perform(() -> {
            updateMyEntity(myEntityId, reverseTitle);
        }));
        assertTrue("Rollback!!", transactor.perform(() -> {
            checkRevisions(myEntityId, title, reverseTitle);
        }));
    }



    private void addMyEntity(String title) {
        MyEntity myEntity = new MyEntity(new String(description), title);
        entityManager.persist(myEntity);
        entityManager.flush();
        myEntityId = myEntity.getId();
    }

    private void updateMyEntity(long myEntityId, String title) {
        MyEntity updateMyEntity = entityManager.find(MyEntity.class, myEntityId);
        updateMyEntity.setTitle(title);
        entityManager.persist(updateMyEntity);
        entityManager.flush();
    }

    private void checkRevisions(long myEntityId, String firstTitle, String secondTitle) {
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = reader.getRevisions(MyEntity.class, myEntityId);
        assertEquals("2 revisions expected", 2, revisions.size());

        MyEntity firstRevision = reader.find(MyEntity.class, myEntityId, revisions.get(0));
        assertThat(firstRevision.getTitle(), is(firstTitle));

        MyEntity secondRevision = reader.find(MyEntity.class, myEntityId, revisions.get(1));
        assertThat(secondRevision.getTitle(), is(secondTitle));
    }

}
