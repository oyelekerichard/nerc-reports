/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.repository;

import com.crowninteractive.net.nercreport.domainobject.Users;
import com.crowninteractive.net.nercreport.domainobject.WorkOrder;
import com.crowninteractive.net.nercreport.dto.ExtraDataDetails;
import com.crowninteractive.net.nercreport.exception.NercReportException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author johnson3yo
 */
@Repository
@Transactional
public class WorkOrderDao {

    @PersistenceContext
    private EntityManager em;

    public List<WorkOrder> getWorkOrderByParams(String district, String from, String to, String queueTypeIds, String tariffs) {
        String qry = "select * from work_order where is_active=1 and business_unit='%s' and date(create_time) >= date('%s') and date(create_time) <= date('%s') "
                + "and queue_type_id in (" + queueTypeIds + ")";
        if (tariffs.length() > 0) {
            qry.concat(" and customer_tariff in (" + tariffs + ")");
        }
        Query q = em.createNativeQuery(String.format(qry, district, from, to), WorkOrder.class);
        return q.getResultList();
    }

    public String getDateResolved(WorkOrder w) {
        if (w.getIsClosed() != null) {
            if (w.getIsClosed() == 1) {
                return w.getClosedTime() == null ? null : DateFormatUtils.format(w.getClosedTime(), "yyyy-MM-dd HH:mm:SS");
            } else if (w.getCurrentStatus().toLowerCase().equals("completed") || w.getCurrentStatus().toLowerCase().equals("resolved")) {
                if (w.getWorkOrderStatusId() == null) {
                    return null;
                }
                Query q = em.createNativeQuery("select DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') from work_order_update  where work_order_id=?1 and "
                        + "work_order_status_id=?2 order by id desc limit 1").
                        setParameter(1, w.getId()).
                        setParameter(2, w.getWorkOrderStatusId().getId());
                if (q.getResultList() != null) {

                    if (!q.getResultList().isEmpty()) {
                        String time = (String) q.getResultList().get(0);
                        return time;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }

            } else {
                return null;
            }
        }
        return null;
    }

    public String getQueueTypeIds(String email) throws NercReportException {
        String query = String.format("select queue_type_id from right_template_queue_type where right_template_id = (select right_template_id from users where email = ?1) ");
        List<Integer> queueTypeIds = em.createNativeQuery(query).setParameter(1,
                email).getResultList();
        String values = queueTypeIds.stream().map(found -> String.valueOf(found)).collect(Collectors.joining(","));
        if (values == null) {
            throw new NercReportException("No queueTypes found ");
        }
        if (values.length() < 1) {
            throw new NercReportException("No queueTypes found ");
        }
        return values;
    }

    public String getAssignedTariffs(String email) {
        String query = String.format("select tariff from right_template_tariff where right_template_id = (select right_template_id from users where email = ?1) ");
        List<String> queueTypeIds = em.createNativeQuery(query).setParameter(1,
                email).getResultList();
        String values = queueTypeIds.stream().map(found -> "'".concat(found).concat("'")).collect(Collectors.joining(","));
        if (values == null) {
            values = "";
        }
        if (values.length() < 1) {
            values = "";
        }
        return values;
    }

    public Users findByEmail(String email) {
        String query = String.format("select * from users where email=?1 ");
        List<Users> users = em.createNativeQuery(query, Users.class).setParameter(1,
                email).getResultList();
        if ((users != null) && !users.isEmpty()) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public List<WorkOrder> getWorkOrderByParamsV2(String district, String from, String to, String queueTypeIds, String tariffs) {
        String qry = "select * from work_order where business_unit='%s' and date(create_time) >= date('%s') and date(create_time) <= date('%s') "
                + "and queue_type_id in (" + queueTypeIds + ")";
        if (tariffs.length() > 0) {
            qry.concat(" and customer_tariff in (" + tariffs + ")");
        }
        Query q = em.createNativeQuery(String.format(qry, district, from, to), WorkOrder.class);
        return q.getResultList();
    }

    public String[] getFirstAndLastNames(WorkOrder w) {
        String firstAndLastName = w.getCustomerName();

        String[] noname = null;
        if (w.getCustomerName() != null) {
            String[] splited = firstAndLastName.split("\\s+");

            return splited;
        } else {
            return noname;
        }
    }

    public String[] getAddresses(WorkOrder w) {
        String firstAndLastName = w.getAddressLine1();

        String[] splited = firstAndLastName.split("\\s+");

        return splited;
    }

//   public List<NercReport> getNercReport(String from, String to) {
//
////            em = emf.createEntityManager();
//            List<Object[]> list = em.createNativeQuery("SELECT customer_name AS CUSTOMER_FIRSTNAME, summary, "
//                    + "(select name from queue WHERE id = queue_id) AS queue,"
//                    + "(SELECT NAME FROM queue_type WHERE id = queue_type_id) AS queueType, "
//                    + "priority, business_unit AS region,customer_email AS email, "
//                    + "contact_number AS contactNumber, create_time AS createdTime,"
//                    + "(select firstname from users where id = created_by) AS createdby, "
//                    + "is_assigned AS isAssigned, channel, "
//                    + "(SELECT firstname FROM users WHERE id = (SELECT user_id FROM engineer WHERE id = engineer_id)) AS resource, "
//                    + "work_date AS workDate, current_status AS status FROM work_order WHERE create_time BETWEEN CAST(?1 AS DATE) AND CAST(?2 AS DATE) ORDER BY createdTime").
//                    setParameter(1, from).setParameter(2, to).getResultList();
//
//            return list.stream().map(e -> new ExtraDataDetails("3065412", 
//                    (String) e[1], (String) e[2], (String) e[3], "name", (String) e[5], (String) e[6],
//                    (String) e[7], (Date) e[8], (String) e[9], (short) e[10],
//                    (String) e[11], (String) e[12], (Date) e[13], (String) e[14])).collect(Collectors.toList());
//
//        
//    }
    public String getQueueName(int queueId) {
        List<String> resultList = em.createNativeQuery("select name from queue where id=?1 ").
                setParameter(1, queueId).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public String getWorkOrderExtraData(int ticketId) {
        List<String> resultList = em.createNativeQuery("select extra_data from work_order where ticket_id=?1 ").
                setParameter(1, ticketId).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<WorkOrder> getWorkOrdersforACustomer(String from, String to) {

        return em.createNativeQuery("select * FROM work_order WHERE create_time BETWEEN CAST(?1 AS DATE) "
                + "AND DATE_ADD(CAST(?2 AS DATE), INTERVAL 1 day)", WorkOrder.class).
                setParameter(1, from).setParameter(2, to)
                .getResultList();
    }

    public Users getAssigned(int engineerId) {
        List<Users> resultList = em.createNativeQuery("select * from users where id=(SELECT user_id from engineer where id =?1)", Users.class).
                setParameter(1, engineerId).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
        
//        List<Users> resultList = em.createNativeQuery("select * from users where is_active=1 and id=?1 and owner_id=?2 limit 1", Users.class).
//                    setParameter(1, userId).setParameter(2, 1).getResultList();
//            return resultList.isEmpty() ? null : resultList.get(0);
    }

    public Users getUpdatedBy(int userId) 
    {
        List<Users> resultList = em.createNativeQuery("select * from users where id =?1", Users.class).
                setParameter(1, userId).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
        
//        List<Users> resultList = em.createNativeQuery("select * from users where is_active=1 and id=?1 and owner_id=?2 limit 1", Users.class).
//                    setParameter(1, userId).setParameter(2, 1).getResultList();
//            return resultList.isEmpty() ? null : resultList.get(0);
    }
    public Integer getAssigneefromWorkOrderUpdate(int ticketId) {
        List<Integer> resultList = em.createNativeQuery("select updated_by from work_order_update where work_order_id = (select id from work_order where ticket_id=?1) ").
                setParameter(1, ticketId).
                getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
   
    }
}
