package ru.gb.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DeliveryTest extends AbstractTest{

    @Test
    @Order(1)
    void getDelivery_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM delivery WHERE taken='Yes'";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery("SELECT * FROM delivery").addEntity(DeliveryEntity.class);
        //then
        Assertions.assertEquals(12, countTableSize);
        Assertions.assertEquals(15, query.list().size());
    }

    @Test
    @Order(2)
    void getDelivery_whenNotTaken_paymentShouldBeNull() {
        //given
        final Query query = getSession().createSQLQuery("SELECT * FROM delivery WHERE taken='No'").addEntity(DeliveryEntity.class);
        //then
        DeliveryEntity deliveryEntity = (DeliveryEntity) query.uniqueResult();
        Assertions.assertEquals("NULL", deliveryEntity.getPaymentMethod());
    }

    @Test
    @Order(3)
    void addDelivery_WhenValid_shouldSave() {
        //given
        DeliveryEntity entity = new DeliveryEntity();
        entity.setDeliveryId((short) 16);
        entity.setOrderId((short) 16);
        entity.setCourierId((short) 1);
        entity.setDateArrived("2024-04-27 17:59:15");
        entity.setTaken("Yes");
        entity.setPaymentMethod("Cash");
        //when
        Session session = getSession();
        session.beginTransaction();
        session.persist(entity);
        session.getTransaction().commit();

        final Query query = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id="+16).addEntity(DeliveryEntity.class);
        DeliveryEntity deliveryEntity = (DeliveryEntity) query.uniqueResult();
        //then
        Assertions.assertNotNull(deliveryEntity);
        Assertions.assertEquals("Cash", deliveryEntity.getPaymentMethod());
    }

    @Test
    @Order(4)
    void updateDelivery_courierId() {

        final Query query = getSession().createSQLQuery("SELECT * FROM delivery WHERE delivery_id="+16).addEntity(DeliveryEntity.class);
        Optional<DeliveryEntity> deliveryEntity = (Optional<DeliveryEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(deliveryEntity.isPresent());

        Session session = getSession();
        session.beginTransaction();
        DeliveryEntity updatedEntity = session.get(DeliveryEntity.class, deliveryEntity.get().getDeliveryId());
        updatedEntity.setCourierId((short) 2);
        session.getTransaction().commit();

        // then
        // проверяем, что запись была успешно обновлена
        final Query queryAfterUpdate = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id=" + deliveryEntity.get().getDeliveryId())
                .addEntity(DeliveryEntity.class);
        DeliveryEntity updDeliveryEntity = (DeliveryEntity) queryAfterUpdate.uniqueResult();

        Assertions.assertNotNull(updDeliveryEntity);
        Assertions.assertEquals(2, updDeliveryEntity.getCourierId());
    }

    @Test
    @Order(5)
    void deleteDelivery_whenValid_shouldDelete() {
        //given
        final Query query = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id=" + 16).addEntity(DeliveryEntity.class);
        Optional<DeliveryEntity> deliveryEntity = (Optional<DeliveryEntity>) query.uniqueResultOptional();
        Assumptions.assumeTrue(deliveryEntity.isPresent());
        //when
        Session session = getSession();
        session.beginTransaction();
        session.delete(deliveryEntity.get());
        session.getTransaction().commit();
        //then
        final Query queryAfterDelete = getSession()
                .createSQLQuery("SELECT * FROM delivery WHERE delivery_id=" + 16).addEntity(DeliveryEntity.class);
        Optional<DeliveryEntity> customersEntityAfterDelete = (Optional<DeliveryEntity>) queryAfterDelete.uniqueResultOptional();
        Assertions.assertFalse(customersEntityAfterDelete.isPresent());
    }
}
