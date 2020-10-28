/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.service;

import com.crowninteractive.net.nercreport.domainobject.WorkOrder;
import com.crowninteractive.net.nercreport.dto.BaseResponse;
import com.crowninteractive.net.nercreport.dto.ExtraDataDetails;
import com.crowninteractive.net.nercreport.jms.ReportReceiver;
import com.crowninteractive.net.nercreport.repository.WorkOrderDao;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private WorkOrderDao wdao;
    @Autowired
    private SendEmail emm;

    BaseResponse awe;

    public void sendWorkOrderFileV1(String from, String to, String emailAddress) {
//        List<String> err = validateEnumWorkOrder(from, to);

//        if (!from.isEmpty() && !to.isEmpty()) {
//            final List<WorkOrderReport> workOrders = wdao.getWorkOrdersReport(from, to);
//
//            if (!workOrders.isEmpty()) {
//                sendWorkOrderEmailAttachmentV1(emailAddress, workOrders);
//            } else {
//                L.info("No work order found for given params");
//            }
//        } else {
//            L.info("Errors in validating to {} and from {}", to, from);
//        }
    }

//    private void sendWorkOrderEmailAttachmentV1(final String emailAddress, final List<WorkOrderReport> data) {
//        L.info("Preparing to send WorkOrder email to ::  {}", emailAddress);
//
//        final File excelFileFor = createExcelFileForV1(WorkOrderReport.class, data, true);
//        if (excelFileFor != null) {
//            try {
//                sendEmailTo(emailAddress, null, "Your work order download file", "Please find "
//                        + "attached your WorkOrder download file", excelFileFor);
//                L.info("Successfully created and sent file to {}", emailAddress);
//            } catch (Exception ex) {
//                L.error("An error occurred while trying to send workorder file to " + emailAddress,
//                        ex);
//            }
//        } else {
//            L.warn("Couldn't create excel file to send to {}", emailAddress);
//        }
//    }
//    
//    
//    private <T> File createExcelFileForV1(Class<T> clazz, List<T> data, boolean forEmail) {
//        try {
//            L.info("Creating file with {} data rows", data.size());
//
//            L.info("workorder file sent :: " + data.toString());
//
//            final String fileName = "work_order_request" + System.currentTimeMillis() + ".xls";
//            final String filePath;
//
//            if (forEmail) {
//                filePath = "/data/var/files/wfm/" + fileName;
////                filePath = "C:\\var\\config\\wfm\\" + fileName;
//            } else {
//                filePath = "/var/wfm/downloads/" + fileName;
//            }
//
//            final FileOutputStream fileOutputStream = new FileOutputStream(filePath);
//            final Workbook workbook = new SXSSFWorkbook(data.size());
//            final Sheet sheet = workbook.createSheet();
//
//            final Field[] declaredFields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isAnnotationPresent(ExcludeForExcel.class)).toArray(Field[]::new);
//
//            final Row headRow = sheet.createRow(0);
//
//            for (int j = 0; j < declaredFields.length; j++) {
//                final Cell cellInHeadRow = headRow.createCell(j);
//                cellInHeadRow.setCellValue(declaredFields[j].getName().toUpperCase());
//            }
//
//            int rowCount = 2;
//
//            for (T datum : data) {
//                final Row row = sheet.createRow(rowCount);
//                for (int k = 0; k < declaredFields.length; k++) {
//                    final Cell cell = row.createCell(k);
//                    final Field declaredField = declaredFields[k];
//                    declaredField.setAccessible(true);
//                    final Object value = declaredField.get(datum);
//                    cell.setCellValue(value != null ? value.toString() : " ");
//                }
//
//                rowCount++;
//            }
//            workbook.write(fileOutputStream);
//            fileOutputStream.close();
//            return new File(filePath);
//
//        } catch (Exception ex) {
//            L.error("An error occurred while trying to createWorkOrderFile", ex);
//            return null;
//        }
//    }
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

        logger.info("INSDE EXTRA DATA DETAILS METHOD >>> " + extraData);

        if (extraData != null && !extraData.isEmpty()) {
            JSONObject subObject1 = new JSONObject(extraData);

            logger.info("INSDE EXTRA DATA DETAILS METHOD JSONOBJECT>>> " + subObject1);
//        JSONObject subObject1 = (JSONObject) extraData;

            JSONObject json = subObject1.optJSONObject("customer_details");
            String service = json.optString("service_band");
            report.setServiceBand(service);
            String complaintlga = json.optString("complaint_lga");
            report.setComplaintLga(complaintlga);
            String complaintarea = json.optString("complaint_area");
            report.setComplaintArea(complaintarea);
            logger.info("SERVICE BAND >>" + service);
            logger.info("complaint_lga >>" + complaintlga);
            logger.info("complaint_area >>> " + complaintarea);
            String custormerType = json.optString("customer_type");
            logger.info("custormerType >>" + custormerType);
            report.setCustomerType(custormerType);

            String firstName = json.optString("firstname");
            report.setCustomerFirstName(firstName);
            logger.info("firstName >>" + firstName);
            String lastName = json.optString("lastname");
            report.setCustomerLastName(lastName);

            String complaintAddress = json.optString("address");

            logger.info("ADDRESS >>> " + complaintAddress);

            report.setCustomerAdress(complaintAddress);
            String complaintState = json.optString("state");
            report.setCutomerState(complaintState);

            String lga = json.optString("customer_lga");
            report.setLga(lga);
            String area = json.optString("customer_area");
            report.setArea(area);

            report.setHouseNumber(getHouseNumber(complaintAddress));
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
        String houseNumber = address.replaceAll("[^0-9]", " ");

        int value = Integer.parseInt(address.replaceAll("[^0-9]", ""));

        logger.info("numbers only >>> " + houseNumber);

        return value;
    }

    
    public String getBillingId(WorkOrder w) {

        try {
            return (w.getReferenceType() != null && w.getReferenceTypeData() != null && w.getReferenceType().equals("Billing ID") && !w.getReferenceTypeData().equals("Not Applicable")) ? String.valueOf(w.getReferenceTypeData()) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getResolution(WorkOrder w) {
        switch (w.getCurrentStatus().toLowerCase()) {
            case "closed":
            case "completed":
            case "resolved":
                return w.getCurrentStatus();
            default:
                return "PENDING";
        }
    }
}
