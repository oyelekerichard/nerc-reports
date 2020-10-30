/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.service;

import com.crowninteractive.net.nercreport.domainobject.Users;
import com.crowninteractive.net.nercreport.dto.ExtraDataDetails;
import com.crowninteractive.net.nercreport.repository.WorkOrderDao;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author richard.oyeleke
 */
@Service
public class ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private WorkOrderDao wdao;
    @Autowired
    private SendEmail emm;

    public ExtraDataDetails getComplaintDetails(int ticketId) {
        ExtraDataDetails report = new ExtraDataDetails();
        logger.info("to get extra data and spit it");
        String extraData = wdao.getWorkOrderExtraData(ticketId);

        JSONArray array = new JSONArray(extraData);

        logger.info("arrays >> " + array);

        JSONObject subObject1 = (JSONObject) array.get(0);

        String service = subObject1.optString("service_band");
        report.setServiceBand(service);
        JSONObject subObject2 = (JSONObject) array.get(1);
        String complaintlga = subObject2.optString("complaint_lga");
        report.setComplaintLga(complaintlga);
        String complaintarea = subObject2.optString("complaint_area");
        report.setComplaintArea(complaintarea);
        logger.info("SERVICE BAND >>" + service);
        logger.info("complaint_lga >>" + complaintlga);
        logger.info("complaint_area >>> " + complaintarea);

        JSONObject subObject3 = (JSONObject) array.get(2);

        logger.info("CUSTOMER DETAILS >>> " + subObject3.optJSONArray("customer_details"));

        JSONArray customerArray = subObject3.optJSONArray("customer_details");
        JSONObject subObject4 = (JSONObject) customerArray.get(0);
        String custormerType = subObject4.optString("customer_type");
        logger.info("custormerType >>" + custormerType);
        report.setCustomerType(custormerType);
        JSONObject subObject5 = (JSONObject) customerArray.get(1);
        String firstName = subObject5.optString("firstname");
        report.setCustomerFirstName(firstName);
        logger.info("firstName >>" + firstName);
        String lastName = subObject5.optString("lastname");
        report.setCustomerLastName(lastName);
        JSONObject subObject6 = (JSONObject) customerArray.get(2);
        String complaintAddress = subObject6.optString("address");

        logger.info("ADDRESS >>> " + complaintAddress);

        report.setCustomerAdress(complaintAddress);
        JSONObject subObject7 = (JSONObject) customerArray.get(3);
        String complaintState = subObject7.optString("state");
        report.setCutomerState(complaintState);
        JSONObject subObject8 = (JSONObject) customerArray.get(4);
        String lga = subObject8.optString("lga");
        report.setLga(lga);
        String area = subObject8.optString("area");
        report.setArea(area);

        report.setHouseNumber(getHouseNumber(complaintAddress));

        return report;
    }

    public ExtraDataDetails getComplaintDetailsV1(int ticketId) throws ParseException {
        ExtraDataDetails report = new ExtraDataDetails();
        logger.info("to get extra data and spit it");
        String extraData = wdao.getWorkOrderExtraData(ticketId);
        logger.info("INSDE EXTRA DATA DETAILS METHOD >>> " + ticketId);
        logger.info("INSDE EXTRA DATA DETAILS METHOD >>> " + extraData);

        if (extraData != null && !extraData.isEmpty()) {
            JSONObject subObject1 = new JSONObject(extraData);

            logger.info("INSDE EXTRA DATA DETAILS METHOD JSONOBJECT>>> " + subObject1);
//        JSONObject subObject1 = (JSONObject) extraData;

            JSONObject json = subObject1.optJSONObject("customer_details");
            if (json != null && !json.isEmpty()) {
                String service = json.optString("service_band");
                if (!service.isEmpty() && service != null) {
                    report.setServiceBand(service);
                } else {
                    report.setServiceBand("");
                }

                String complaintlga = json.optString("complaint_lga");
                if (!complaintlga.isEmpty() && complaintlga != null) {
                    report.setComplaintLga(complaintlga);
                } else {
                    report.setComplaintLga("");
                }
//            report.setComplaintLga(complaintlga);

                String complaintarea = json.optString("complaint_area");
                if (!complaintarea.isEmpty() && complaintarea != null) {
                    report.setComplaintArea(complaintarea);
                } else {
                    report.setComplaintArea("");
                }
//            report.setComplaintArea(complaintarea);
//            logger.info("SERVICE BAND >>" + service);
//            logger.info("complaint_lga >>" + complaintlga);
//            logger.info("complaint_area >>> " + complaintarea);
                String custormerType = json.optString("customer_type");

                logger.info("custormerType >>" + custormerType);
                if (!custormerType.isEmpty() && custormerType != null) {
                    report.setCustomerType(custormerType);
                } else {
                    report.setCustomerType("");
                }
//            report.setCustomerType(custormerType);

                String firstName = json.optString("firstname");
                if (!firstName.isEmpty() && firstName != null) {
                    report.setCustomerFirstName(firstName);
                } else {
                    report.setCustomerFirstName("");
                }
//            report.setCustomerFirstName(firstName);
//            logger.info("firstName >>" + firstName);
                String lastName = json.optString("lastname");
                if (!lastName.isEmpty() && lastName != null) {
                    report.setCustomerLastName(lastName);
                } else {
                    report.setCustomerLastName("");
                }
//            report.setCustomerLastName(lastName);

                String complaintAddress = json.optString("address");
                if (!complaintAddress.isEmpty() && complaintAddress != null) {
                    report.setCustomerAdress(complaintAddress);
                } else {
                    report.setCustomerAdress("");
                }

//            logger.info("ADDRESS >>> " + complaintAddress);
//            report.setCustomerAdress(complaintAddress);
                String complaintState = json.optString("state");
                if (!complaintState.isEmpty() && complaintState != null) {
                    report.setCutomerState(complaintState);
                } else {
                    report.setCutomerState("");
                }
//            report.setCutomerState(complaintState);

                String lga = json.optString("customer_lga");
                if (!lga.isEmpty() && lga != null) {
                    report.setLga(lga);
                } else {
                    report.setLga("");
                }
//            report.setLga(lga);
                String area = json.optString("customer_area");
                if (!area.isEmpty() && area != null) {
                    report.setArea(area);
                } else {
                    report.setArea("");
                }
//            report.setArea(area);

                report.setHouseNumber(getHouseNumberv2(complaintAddress));
            } else {
                report.setArea("");
                report.setComplaintArea("");
                report.setComplaintLga("");
                report.setCustomerAdress("");
                report.setCustomerFirstName(extraData);
                report.setCustomerLastName("");
                report.setCustomerType("");
                report.setCutomerState("");
                report.setHouseNumber(0);
                report.setLga("");
                report.setServiceBand("");
            }

        } else {
            report.setArea("");
            report.setComplaintArea("");
            report.setComplaintLga("");
            report.setCustomerAdress("");
            report.setCustomerFirstName(extraData);
            report.setCustomerLastName("");
            report.setCustomerType("");
            report.setCutomerState("");
            report.setHouseNumber(0);
            report.setLga("");
            report.setServiceBand("");
        }

        return report;
    }

    public static int getHouseNumber(String address) {
        logger.info("inside house number method >>> " + address);
        String houseNumber = address.replaceAll("[^0-9]", " ");
//        logger.info("numbers only >>> " + houseNumber);

        int value = Integer.parseInt(address.replaceAll("[^0-9]", ""));
        logger.info("numbers only vaue>>> " + value);
        if (address.isEmpty() || address.equalsIgnoreCase("")) {
            logger.info("numbers only house number>>> " + Integer.valueOf(""));
            return Integer.valueOf("");

        } else {
            return value;
        }
    }

    public static int getHouseNumberv2(String address) {
        logger.info("inside house number method V2>>> " + address);
        char[] chars = address.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        if (sb.toString() == null || sb.toString().isEmpty()) {
            logger.info("inside house number V222>>> " + address);
            return 0;
        } else {
            if (sb.toString().length() >= 4) {
                return Integer.valueOf(sb.toString().substring(0, 1));
            } else {
                return Integer.valueOf(sb.toString());
            }
        }
    }

    public String getAssigneeName(int userId) {
        logger.info("inside getAssigneeName method >>> " + userId);

//        String fullname = "";
        String firstName = "";
        String lastName = "";

        Users u = wdao.getAssigned(userId);
        logger.info("inside getAssigneeName method 22 >>> ");
        logger.info("inside getAssigneeName method 22 >>> "+u.getFirstname().toString());

        if (u.getFirstname() != null && !u.getFirstname().isEmpty()) {
            firstName = u.getFirstname();
        } else {
            firstName = " ";
        }

        if (u.getLastname() != null && !u.getLastname().isEmpty()) {
            lastName = u.getLastname();
        } else {
            firstName = " ";
        }
        logger.info("inside getAssigneeName method >>> " + firstName);
        logger.info("inside getAssigneeName method >>> " + lastName);

        String fullname = firstName + " " + lastName;
        logger.info("inside getAssigneeName method >>> " + fullname);
        return fullname;
    }

}
