/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.repository;

import com.crowninteractive.net.nercreport.domainobject.QueueType;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author christabeloji
 */
@Repository
@Transactional
public class QueueTypeDao {

    @PersistenceContext
    private EntityManager em;

    public String getQueueTypeName(int ownerId, int queueTypeId) {
        List<String> resultList = em.createNativeQuery("select name from queue_type where id=?1 and owner_id=?2").
                setParameter(1, queueTypeId).setParameter(2, ownerId).getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);

    }

    public QueueType getQueueTypeByQueueNameQueueTypeName(String queueName, String queueTypeName) {

        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where queue_id=(select id from queue where name= ?1) and name=?2 ", QueueType.class).
                setParameter(1, queueName).
                setParameter(2, queueTypeName).
                getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<QueueType> getQueueTypeByQueueIdList(int queueId) {

        return em.createNativeQuery("select * from queue_type where queue_id=?1", QueueType.class).
                setParameter(1, queueId).
                getResultList();
    }

    public List<QueueType> getEnumerationQueueTypeByQueueIdList(String token) {

        return em.createNativeQuery("select * from queue_type where queue_id=(select id from queue where token=?1) and is_active=1", QueueType.class).
                setParameter(1, token).
                getResultList();
    }

    public QueueType getQueueTypeByNameByQueueTypeAndOwnerId(String queueName, String ownerId, String queueTypeName) {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where name=?1 and owner_id=?2 and queue_id=(select id from queue where name=?3 limit 1)", QueueType.class).
                setParameter(1, queueTypeName).setParameter(2, ownerId).setParameter(3, queueName).getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public boolean isValidQueueTypeByName(String ownerId, String queueTypeName, String queueName) {

        BigInteger qt = (BigInteger) em.createNativeQuery("select count(*) from queue_type qt, queue q where q.owner_id=?1 and qt.queue_id=q.id and qt.name=?2 and q.name=?3 and q.is_active=1 and qt.is_active=1").
                setParameter(1, ownerId).
                setParameter(2, queueTypeName).
                setParameter(3, queueName).
                getSingleResult();

        return qt.intValue() > 0;
    }

    public QueueType getEmccConfigDisconnectQueueTypeAndQueue() {
        return (QueueType) em.createNativeQuery("select * from queue_type where token=(select config_value from emcc_config where config_key='disconnect_queue_type')", QueueType.class).
                getResultList().stream().findFirst().orElse(null);

    }

    public QueueType getEmccConfigReconnectQueueTypeAndQueue() {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where token=(select config_value from emcc_config where config_key='reconnect_queue_type')", QueueType.class).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public QueueType getEmccConfigInflightQueueTypeAndQueue() {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where token=(select config_value from emcc_config where config_key='inflight_queue_type')", QueueType.class).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public QueueType getEmccNonVendQueueType() {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where name='PPM NON-VEND'", QueueType.class).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public QueueType getQueueTypeByQueueTypeId(Integer id) {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where id=?1 ", QueueType.class).
                setParameter(1, id).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public QueueType getQueueTypeByQueueTypeToken(String id) {
        List<QueueType> resultList = em.createNativeQuery("select * from queue_type where token=?1 ", QueueType.class).
                setParameter(1, id).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<QueueType> getQueueTypeByResource(String email) {
        return em.createNativeQuery(" select qt.* from queue_type qt, users u, engineer e, engineer_type et, engineer_type_to_queue_type etqt where u.id = e.user_id and et.id = e.eng_type and etqt.queue_type_id = qt.id and et.id = etqt.engineer_type_id and u.email = ?1", QueueType.class).setParameter(1, email).getResultList();
    }

    public QueueType getQueueTypeByName(String queueTypeName) {
        queueTypeName = "%".concat(queueTypeName);
        return (QueueType)em.createNativeQuery("select * from queue_type where name like ?1", QueueType.class).
                setParameter(1, queueTypeName).getResultList().stream().findFirst().orElse(null);

    }

    
    
}
