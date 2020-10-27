/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.repository;

import com.crowninteractive.net.nercreport.domainobject.Queue;
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
public class QueueDao {

    @PersistenceContext
    private EntityManager em;

    public List<Queue> findQueue(String tenantid, String queueId) {

        return em.createNativeQuery("select * from queue where owner_id=?1 and id=?2 and is_active=1", Queue.class).
                setParameter(1, tenantid).
                setParameter(2, queueId).
                getResultList();

    }

    public Queue getQueue(String name) {

        List<Queue> resultList = em.createNativeQuery("select * from queue where name=?1 ", Queue.class).
                setParameter(1, name).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
