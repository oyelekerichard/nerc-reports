/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.jms;

import com.crowninteractive.net.nercreport.domainobject.Users;
import com.crowninteractive.net.nercreport.domainobject.WorkOrder;
import com.crowninteractive.net.nercreport.dto.BaseResponse;
import com.crowninteractive.net.nercreport.dto.ExtraDataDetails;
import com.crowninteractive.net.nercreport.exception.NercReportException;
import com.crowninteractive.net.nercreport.repository.WorkOrderDao;
import com.crowninteractive.net.nercreport.service.SendEmail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.Message;
import javax.jms.TextMessage;
import org.apache.commons.mail.EmailException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author johnson3yo
 */
@Component
public class ReportReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ReportReceiver.class);

    @Autowired
    private WorkOrderDao wdao;
    @Autowired
    private SendEmail emm;

    BaseResponse awe;

    @JmsListener(destination = "${nerc.report.queue}")
    public void handleMessage(Message message) {

        if (message instanceof TextMessage) {
            try {
                TextMessage textMessage = (TextMessage) message;
                String[] txt = textMessage.getText().split(",");
                processWriteV3(txt[0], txt[1], txt[2]);
            } catch (Exception ex) {
                logger.error("-----------------------Exception occured --------------------------");
                logger.error("-----------------------Processing file ---------------------------- ");
                ex.printStackTrace();
            }
        }

    }

    private String getBillingId(WorkOrder w) {

        try {
            return (w.getReferenceType() != null && w.getReferenceTypeData() != null && w.getReferenceType().equals("Billing ID") && !w.getReferenceTypeData().equals("Not Applicable")) ? String.valueOf(w.getReferenceTypeData()) : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getResolution(WorkOrder w) {
        switch (w.getCurrentStatus().toLowerCase()) {
            case "closed":
            case "completed":
            case "resolved":
                return w.getCurrentStatus();
            default:
                return "PENDING";
        }
    }

    public void processWriteV3(String from, String to, String email) throws FileNotFoundException, IOException, NercReportException, EmailException {
        long started = System.currentTimeMillis();
        logger.info("--------------Nerc report started ---------");
        logger.info("--------------Email-------------" + email);
        Users u = wdao.findByEmail(email);
        if (u.getDistricts() == null) {
            logger.info("-----------No district for user ---------" + email);
            return;
        }
        String commaSeparated = u.getDistricts();

        String queueTypeId = wdao.getQueueTypeIds(email);
        String tariffs = wdao.getAssignedTariffs(email);

        List<String> districts = new ArrayList<String>(Arrays.asList(commaSeparated.split("\\s*,\\s*")));
        logger.info("----------districts----------------" + districts);

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short) 200);

        CellStyle h = workbook.createCellStyle();
        Font f = workbook.createFont();
        f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        f.setFontHeight((short) 300);
        h.setFont(f);
        h.setAlignment(CellStyle.ALIGN_CENTER);

        headerStyle.setFont(font);

        for (int i = 0; i < districts.size(); i++) {

            List<WorkOrder> lwListt = wdao.getWorkOrderByParams(districts.get(i), from, to, queueTypeId, tariffs);

            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(districts.get(i));

            Cell info0 = sheet.createRow(2).createCell(0);
            info0.setCellStyle(h);
            info0.setCellValue("EKO ELECTRICITY DISTRIBUTION COMPANY (EKEDP)");
            sheet.addMergedRegion(new CellRangeAddress(
                    2, //first row (0-based)
                    2, //last row  (0-based)
                    0, //first column (0-based)
                    13 //last column  (0-based)
            ));

            Cell info1 = sheet.createRow(3).createCell(13);
//            info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
            info1.setCellValue(from + " - " + to);
            info1.setCellStyle(headerStyle);
            Cell info2 = sheet.getRow(3).createCell(12);
            info2.setCellValue("Month : ");
            info2.setCellStyle(headerStyle);

            Row xheader = sheet.createRow(5);
            String[] header = {"S/N", "Ticket ID",
                "Customer Name",
                "Customer's Address",
                "Account Number",
                "Phone No", "Tariff Class", "B/Unit",
                "Nature of Complaint", "Date Received",
                "Action Taken", "Date Resolved", "Resolution", "Category"};
            for (int col = 0; col < 14; col++) {
                Cell cell = xheader.createCell(col);
                cell.setCellValue(header[col]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(col);
            }

            int rownum = 6;
            double count = 1;

            logger.info(" ----- Processing Sheet --------------" + districts.get(i));
            for (WorkOrder w : lwListt) {

                Row row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(count++);

                row.createCell(1).setCellValue(w.getTicketId());

                if (w.getCustomerName() != null && !w.getCustomerName().isEmpty()) {
                    row.createCell(2).setCellValue(w.getCustomerName());
                } else {
                    row.createCell(2).setCellValue(Cell.CELL_TYPE_BLANK);
                }

                row.createCell(3).setCellValue(w.getAddressLine1() + ", " + w.getCity());
                row.createCell(4).setCellValue(getBillingId(w));
                row.createCell(5).setCellValue(String.valueOf(w.getContactNumber()));
                row.createCell(6).setCellValue(String.valueOf(w.getCustomerTariff()));
                row.createCell(7).setCellValue(String.valueOf(w.getBusinessUnit()));
                row.createCell(8).setCellValue(String.valueOf(w.getSummary()));
                row.createCell(9).setCellValue(String.valueOf(w.getCreateTime()));
                String status = "";
                if (w.getIsClosed() != null) {
                    if (w.getCurrentStatus() != null) {
                        status = w.getIsClosed() == 0 ? w.getCurrentStatus() : "CLOSED";
                    }
                }
                row.createCell(10).setCellValue(status);
                row.createCell(11).setCellValue(wdao.getDateResolved(w));
                row.createCell(12).setCellValue(getResolution(w));
                row.createCell(13).setCellValue(w.getQueueType().getName());

            }
        }

        String localfile = "generated_" + System.currentTimeMillis() + ".xlsx";
        String report = "/var/files/email/" + localfile;
        FileOutputStream outputStream = new FileOutputStream(report);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

        logger.info("file is " + report);
        long ended = System.currentTimeMillis();

        logger.info("------------Total processing time ------------" + ((ended - started) / 1000));
        emm.sendAnEmail("a12wq_minions", "NERC customer care report", email, "Your report is now ready. Please find attached the requested report", localfile, null);

    }

    public void processWriteV2(String from, String to, String email) throws FileNotFoundException, IOException, NercReportException, EmailException {
        long started = System.currentTimeMillis();
        logger.info("--------------Nerc report started ---------");
        logger.info("--------------Email-------------" + email);

        Users u = wdao.findByEmail(email);
        if (u.getDistricts() == null) {
            logger.info("-----------No district for user ---------" + email);
            return;
        }
        String commaSeparated = u.getDistricts();

        String queueTypeId = wdao.getQueueTypeIds(email);
        String tariffs = wdao.getAssignedTariffs(email);

        List<String> districts = new ArrayList<String>(Arrays.asList(commaSeparated.split("\\s*,\\s*")));

        logger.info("----------districts----------------" + districts);

        List<String> files = new ArrayList();

        for (int i = 0; i < districts.size(); i++) {

            XSSFWorkbook workbook = new XSSFWorkbook();

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontHeight((short) 200);

            CellStyle h = workbook.createCellStyle();
            Font f = workbook.createFont();
            f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            f.setFontHeight((short) 300);
            h.setFont(f);
            h.setAlignment(CellStyle.ALIGN_CENTER);

            headerStyle.setFont(font);

            List<WorkOrder> lwListt = wdao.getWorkOrderByParams(districts.get(i), from, to, queueTypeId, tariffs);

            XSSFSheet sheet = (XSSFSheet) workbook.createSheet(districts.get(i));

            logger.info(" ----- Processing Sheet --------------" + districts.get(i));
            int rownum = 6;
            double count = 1;
            for (WorkOrder w : lwListt) {
                Cell info0 = sheet.createRow(2).createCell(0);
                info0.setCellStyle(h);
                info0.setCellValue("EKO ELECTRICITY DISTRIBUTION COMPANY (EKEDP)");
                sheet.addMergedRegion(new CellRangeAddress(
                        2, //first row (0-based)
                        2, //last row  (0-based)
                        0, //first column (0-based)
                        13 //last column  (0-based)
                ));

                Cell info1 = sheet.createRow(3).createCell(13);
//                info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
                info1.setCellValue(from + " - " + to);
                info1.setCellStyle(headerStyle);
                Cell info2 = sheet.getRow(3).createCell(12);
                info2.setCellValue("Month : ");
                info2.setCellStyle(headerStyle);

                Row xheader = sheet.createRow(5);
                String[] header = {"S/N", "Ticket ID",
                    "Customer Name",
                    "Customer's Address",
                    "Account Number",
                    "Phone No", "Tariff Class", "B/Unit",
                    "Nature of Complaint", "Date Received",
                    "Action Taken", "Date Resolved", "Resolution", "Category"};
                for (int col = 0; col < 14; col++) {
                    Cell cell = xheader.createCell(col);
                    cell.setCellValue(header[col]);
                    cell.setCellStyle(headerStyle);
                    //  sheet.autoSizeColumn(col);
                }

                Row row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(count++);

                row.createCell(1).setCellValue(w.getTicketId());

                if (w.getCustomerName() != null && !w.getCustomerName().isEmpty()) {
                    row.createCell(2).setCellValue(w.getCustomerName());
                } else {
                    row.createCell(2).setCellValue(Cell.CELL_TYPE_BLANK);
                }

                row.createCell(3).setCellValue(w.getAddressLine1() + ", " + w.getCity());
                row.createCell(4).setCellValue(getBillingId(w));
                row.createCell(5).setCellValue(String.valueOf(w.getContactNumber()));
                row.createCell(6).setCellValue(String.valueOf(w.getCustomerTariff()));
                row.createCell(7).setCellValue(String.valueOf(w.getBusinessUnit()));
                row.createCell(8).setCellValue(String.valueOf(w.getSummary()));
                row.createCell(9).setCellValue(String.valueOf(w.getCreateTime()));
                String status = "";
                if (w.getIsClosed() != null) {
                    if (w.getCurrentStatus() != null) {
                        status = w.getIsClosed() == 0 ? w.getCurrentStatus() : "CLOSED";
                    }
                }
                row.createCell(10).setCellValue(status);
                row.createCell(11).setCellValue(wdao.getDateResolved(w));
                row.createCell(12).setCellValue(getResolution(w));
                row.createCell(13).setCellValue(w.getQueueType().getName());

            }

            String localfile = districts.get(i) + "_" + System.currentTimeMillis() + ".xlsx";
            files.add(localfile);
            String report = "/var/files/email/" + localfile;
            FileOutputStream outputStream = new FileOutputStream(report);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            logger.info("file is " + report);

        }

        String fileName = "nerc-reports" + from + "to" + to + System.currentTimeMillis() + ".zip";
        FileOutputStream fos = new FileOutputStream("/var/files/email/" + fileName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : files) {
            File fileToZip = new File("/var/files/email/" + srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
        long ended = System.currentTimeMillis();

        for (String f : files) {
            File file = new File("/var/files/email/" + f);
            file.delete();
        }

        logger.info("------------Total processing time ------------" + ((ended - started) / 1000));
        emm.sendAnEmail("a12wq_minions", "NERC customer care report", email, "Your report is now ready. Please find attached the requested report", fileName, null);

    }

    public void processWriteV4(String from, String to, String email) throws FileNotFoundException, IOException, NercReportException, EmailException {
        long started = System.currentTimeMillis();
        logger.info("--------------New Nerc report template ---------");
        logger.info("--------------Email-------------" + email);

        Users u = wdao.findByEmail(email);
        if (u.getDistricts() == null) {
            logger.info("-----------No district for user ---------" + email);
            return;
        }
        String commaSeparated = u.getDistricts();

        String queueTypeId = wdao.getQueueTypeIds(email);
        String tariffs = wdao.getAssignedTariffs(email);

        List<String> districts = new ArrayList<String>(Arrays.asList(commaSeparated.split("\\s*,\\s*")));

        logger.info("----------districts----------------" + districts);

        List<String> files = new ArrayList();

        for (int i = 0; i < districts.size(); i++) {

            XSSFWorkbook workbook = new XSSFWorkbook();

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontHeight((short) 200);

            CellStyle h = workbook.createCellStyle();
            Font f = workbook.createFont();
            f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            f.setFontHeight((short) 300);
            h.setFont(f);
            h.setAlignment(CellStyle.ALIGN_CENTER);

            headerStyle.setFont(font);

            List<WorkOrder> lwListt = wdao.getWorkOrderByParamsV2(districts.get(i), from, to, queueTypeId, tariffs);

            XSSFSheet sheet = (XSSFSheet) workbook.createSheet(districts.get(i));

            logger.info(" ----- Processing Sheet --------------" + districts.get(i));
            int rownum = 6;
            double count = 1;
            for (WorkOrder w : lwListt) {
                Cell info0 = sheet.createRow(2).createCell(0);
                info0.setCellStyle(h);
                info0.setCellValue("EKO ELECTRICITY DISTRIBUTION COMPANY (EKEDP)");
                sheet.addMergedRegion(new CellRangeAddress(
                        2, //first row (0-based)
                        2, //last row  (0-based)
                        0, //first column (0-based)
                        13 //last column  (0-based)
                ));

                Row xheader = sheet.createRow(4);
                String[] header = {"DISCO_REF_NO", "CUSTOMER_FIRSTNAME", "CUSTOMER_LASTNAME", "CUSTOMER_HOUSENO",
                    "CUSTOMER_STREETNAME", "CUSTOMER_STATE_OF_RESIDENCE", "CUSTOMER_Local_Government_Area",
                    "CUSTOMER_AREA", "PHONE NUMBER", "EMAIL", "ACCOUNT_NUMBER", "DISCO_CODE", "Complaint Category",
                    "Complaint Sub Category", "BUSINESS UNIT", "STATE", "FORUM NAME", "COMPLAINT_HOUSENO",
                    "COMPLAINT_STREETNAME", "COMPLAINT_Local_Government_Area", "COMPLAINT_AREA", "CIN", "METER NUMBER",
                    "CUSTOMER CLASS", "COMPLAINT DESCRIPTION", "COMPLAINT DATE", "ASSIGNED NAME", "RESOLVE DATE",
                    "STATUS", "REMARK"};
                for (int col = 0; col < 30; col++) {
                    Cell cell = xheader.createCell(col);
                    cell.setCellValue(header[col]);
                    cell.setCellStyle(headerStyle);
//                    cell.setCellStyle(font.getBoldweight());
                    //  sheet.autoSizeColumn(col);/
                }

                Row row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellValue("3065412");

                logger.info("EXTRA DATA IS >>>> " + wdao.getWorkOrderExtraData(w.getTicketId()));
                String extraData = wdao.getWorkOrderExtraData(w.getTicketId());
                if (extraData != null) {

                    ExtraDataDetails customerDetails = getComplaintDetails(w.getTicketId());

                    logger.info("EXTRA DATA IS >>>> " + extraData);
//                String[] names = wdao.getFirstAndLastNames(w);
                    if (customerDetails.getCustomerFirstName() != null && !customerDetails.getCustomerFirstName().isEmpty()) {
                        row.createCell(1).setCellValue(customerDetails.getCustomerFirstName());
                    } else {
                        row.createCell(1).setCellValue(Cell.CELL_TYPE_BLANK);
                    }
//                row.createCell(1).setCellValue(getFirstName(w.getCustomerName()));

                    if (customerDetails.getCustomerLastName() != null && !customerDetails.getCustomerLastName().isEmpty()) {
                        row.createCell(2).setCellValue(customerDetails.getCustomerLastName());
                    } else {
                        row.createCell(2).setCellValue(Cell.CELL_TYPE_BLANK);
                    }

                    row.createCell(3).setCellValue(w.getAddressLine1());
                    row.createCell(4).setCellValue("");
                    row.createCell(5).setCellValue(w.getState());
                    row.createCell(6).setCellValue(customerDetails.getLga());
                    row.createCell(7).setCellValue(customerDetails.getArea());
                    row.createCell(8).setCellValue(w.getContactNumber());
                    row.createCell(9).setCellValue(w.getCustomerEmail());
                    row.createCell(10).setCellValue(w.getReferenceTypeData());
                    row.createCell(11).setCellValue(" EKEDP ");
                    row.createCell(12).setCellValue(w.getQueue().getName());
                    row.createCell(13).setCellValue(w.getQueueType().getName());
                    row.createCell(14).setCellValue(w.getBusinessUnit());
                    row.createCell(15).setCellValue(w.getState());
                    row.createCell(16).setCellValue(" EKO FORUM ");
                    row.createCell(17).setCellValue(customerDetails.getHouseNumber());
                    row.createCell(18).setCellValue(customerDetails.getCustomerAdress());
                    row.createCell(19).setCellValue(customerDetails.getComplaintLga());
                    row.createCell(20).setCellValue(w.getCity());
                    row.createCell(21).setCellValue("CIN");
                    row.createCell(22).setCellValue(w.getReferenceTypeData());
                    row.createCell(23).setCellValue(w.getCustomerTariff());
                    row.createCell(24).setCellValue(w.getDescription());
                    row.createCell(25).setCellValue(w.getCustomerTariff());
                    row.createCell(26).setCellValue(w.getAgentName());
                    row.createCell(27).setCellValue(wdao.getDateResolved(w));
                    String status = "";
                    if (w.getIsClosed() != null) {
                        if (w.getCurrentStatus() != null) {
                            status = w.getIsClosed() == 0 ? w.getCurrentStatus() : "CLOSED";
                        }
                    }
                    row.createCell(28).setCellValue(status);

                    row.createCell(29).setCellValue(w.getSummary());

                }
                logger.info("No work order found for given params");
            }

            String localfile = districts.get(i) + "_" + System.currentTimeMillis() + ".xlsx";
            files.add(localfile);
            String report = "/var/files/email/" + localfile;
            FileOutputStream outputStream = new FileOutputStream(report);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            logger.info("file is " + report);

        }

        String fileName = "nerc-reports" + from + "to" + to + System.currentTimeMillis() + ".zip";
        FileOutputStream fos = new FileOutputStream("/var/files/email/" + fileName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : files) {
            File fileToZip = new File("/var/files/email/" + srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();
        long ended = System.currentTimeMillis();

        for (String f : files) {
            File file = new File("/var/files/email/" + f);
            file.delete();
        }

        logger.info("------------Total processing time ------------" + ((ended - started) / 1000));
        emm.sendAnEmail("a12wq_minions", "NERC customer care report", email, "Your report is now ready. Please find attached the requested report", fileName, null);

    }

    public void processWriteV5(String from, String to, String email) throws FileNotFoundException, IOException, NercReportException, EmailException, ParseException {
        long started = System.currentTimeMillis();
        logger.info("--------------New Nerc report template ---------");
        logger.info("--------------Email-------------" + email);

        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short) 200);

        CellStyle h = workbook.createCellStyle();
        Font f = workbook.createFont();
        f.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        f.setFontHeight((short) 300);
        h.setFont(f);
        h.setAlignment(CellStyle.ALIGN_CENTER);

        headerStyle.setFont(font);

//            List<WorkOrder> lwListt = wdao.getWorkOrderByParamsV2(districts.get(i), from, to, queueTypeId, tariffs);
        List<WorkOrder> lwListt = wdao.getWorkOrdersforACustomer(from, to);

        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(from + "" + to);

        int rownum = 6;
        double count = 1;
        for (WorkOrder w : lwListt) {
            Cell info0 = sheet.createRow(2).createCell(0);
            info0.setCellStyle(h);
            info0.setCellValue("EKO ELECTRICITY DISTRIBUTION COMPANY (EKEDP)");
            sheet.addMergedRegion(new CellRangeAddress(
                    2, //first row (0-based)
                    2, //last row  (0-based)
                    0, //first column (0-based)
                    13 //last column  (0-based)
            ));

            logger.info("WORKORDER >>> " + w);
//                Cell info1 = sheet.createRow(3).createCell(13);
//                info1.setCellValue(from + " - " + to);
//                info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
//                info1.setCellStyle(headerStyle);
//                Cell info2 = sheet.getRow(3).createCell(12);
//                info2.setCellValue("Month : ");
//                info2.setCellStyle(headerStyle);
            Row xheader = sheet.createRow(4);
            String[] header = {"DISCO_REF_NO", "CUSTOMER_FIRSTNAME",
                "CUSTOMER_LASTNAME",
                "CUSTOMER_HOUSENO",
                "CUSTOMER_STREETNAME",
                "CUSTOMER_STATE_OF_RESIDENCE", "CUSTOMER_Local_Government_Area", "CUSTOMER_AREA",
                "PHONE NUMBER", "EMAIL",
                "ACCOUNT_NUMBER", "DISCO_CODE", "Complaint Category", "Complaint Sub Category",
                "BUSINESS UNIT", "STATE", "FORUM NAME", "COMPLAINT_HOUSENO", "COMPLAINT_STREETNAME",
                "COMPLAINT_Local_Government_Area", "COMPLAINT_AREA", "CIN", "METER NUMBER",
                "CUSTOMER CLASS", "COMPLAINT DESCRIPTION", "COMPLAINT DATE", "ASSIGNED NAME",
                "RESOLVE DATE", "STATUS", "REMARK"};
            for (int col = 0; col < 30; col++) {
                Cell cell = xheader.createCell(col);
                cell.setCellValue(header[col]);
                cell.setCellStyle(headerStyle);
//                    cell.setCellStyle(font.getBoldweight());
                //  sheet.autoSizeColumn(col);/
            }

            Row row = sheet.createRow(rownum++);
            Cell cell = row.createCell(0);
            cell.setCellValue("3065412");

//            logger.info("EXTRA DATA IS >>>> " + wdao.getWorkOrderExtraData(w.getTicketId()));
//            logger.info("EXTRA DATA IS >>>> " + wdao.getWorkOrderExtraData(w.getTicketId()));
//            String extraData = wdao.getWorkOrderExtraData(w.getTicketId());
            ExtraDataDetails customerDetails = getComplaintDetailsV1(w.getTicketId());

//            logger.info("EXTRA DATA IS >>>> " + extraData);
//                String[] names = wdao.getFirstAndLastNames(w);
            if (customerDetails.getCustomerFirstName() != null && !customerDetails.getCustomerFirstName().isEmpty()) {
                row.createCell(1).setCellValue(customerDetails.getCustomerFirstName());
            } else {
                row.createCell(1).setCellValue(Cell.CELL_TYPE_BLANK);
            }
//                row.createCell(1).setCellValue(getFirstName(w.getCustomerName()));

            if (customerDetails.getCustomerLastName() != null && !customerDetails.getCustomerLastName().isEmpty()) {
                row.createCell(2).setCellValue(customerDetails.getCustomerLastName());
            } else {
                row.createCell(2).setCellValue(Cell.CELL_TYPE_BLANK);
            }

            row.createCell(3).setCellValue(w.getAddressLine1());
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(w.getState());
            row.createCell(6).setCellValue(customerDetails.getLga());
            row.createCell(7).setCellValue(customerDetails.getArea());
            row.createCell(8).setCellValue(w.getContactNumber());
            row.createCell(9).setCellValue(w.getCustomerEmail());
            row.createCell(10).setCellValue(w.getReferenceTypeData());
            row.createCell(11).setCellValue(" EKEDP ");
            row.createCell(12).setCellValue(w.getQueue().getName());
            row.createCell(13).setCellValue(w.getQueueType().getName());
            row.createCell(14).setCellValue(w.getBusinessUnit());
            row.createCell(15).setCellValue(customerDetails.getCutomerState());
            row.createCell(16).setCellValue(" EKO FORUM ");
            row.createCell(17).setCellValue(customerDetails.getHouseNumber());
            row.createCell(18).setCellValue("");
            row.createCell(19).setCellValue(customerDetails.getComplaintLga());
            row.createCell(20).setCellValue(customerDetails.getComplaintArea());
            row.createCell(21).setCellValue("CIN");
            row.createCell(22).setCellValue(w.getReferenceTypeData());
            row.createCell(23).setCellValue(w.getCustomerTariff());
            row.createCell(24).setCellValue(w.getDescription());
            row.createCell(25).setCellValue(w.getCreateTime());
            row.createCell(26).setCellValue(w.getAgentName());
            row.createCell(27).setCellValue(wdao.getDateResolved(w));
            String status = "";
            if (w.getIsClosed() != null) {
                if (w.getCurrentStatus() != null) {
                    status = w.getIsClosed() == 0 ? w.getCurrentStatus() : "CLOSED";
                }
            }
            row.createCell(28).setCellValue(status);

            row.createCell(29).setCellValue(w.getSummary());
        }

//        String localfile = "NercReports" + "_" + System.currentTimeMillis() + ".xlsx";
        String localfile = "nerc-reports" + from + "to" + to + System.currentTimeMillis() + ".xlsx";
//        files.add(localfile);
        String report = "/var/files/email/" + localfile;
        FileOutputStream outputStream = new FileOutputStream(report);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        logger.info("file is " + report);

        logger.info("------------Total processing time ------------");
        emm.sendAnEmail("a12wq_minions", "NERC customer care report", email, "Your report is now ready. Please find attached the requested report", localfile, null);

    }

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
}
