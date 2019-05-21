/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.jms;

import com.crowninteractive.net.nercreport.domainobject.Users;
import com.crowninteractive.net.nercreport.domainobject.WorkOrder;
import com.crowninteractive.net.nercreport.exception.NercReportException;
import com.crowninteractive.net.nercreport.repository.WorkOrderDao;
import com.crowninteractive.net.nercreport.service.SendEmail;
import com.dyfferential.vyral.web.util.DateFormatter;
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
            info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
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
                info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
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

        String fileName = "nerc-reports" + from +"to"+ to+System.currentTimeMillis() + ".zip";
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

                Cell info1 = sheet.createRow(3).createCell(13);
                info1.setCellValue(DateFormatter.getSimpleDate(from) + " - " + DateFormatter.getSimpleDate(to));
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

        String fileName = "nerc-reports" + from +"to"+ to+System.currentTimeMillis() + ".zip";
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
    
}
