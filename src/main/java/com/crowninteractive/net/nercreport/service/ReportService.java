/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author richard.oyeleke
 */
@Service
public class ReportService 
{
    private static Logger L = LoggerFactory.getLogger(ReportService.class);
    
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

}
