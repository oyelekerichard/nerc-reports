/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.controller;

import com.crowninteractive.net.nercreport.dto.BaseResponse;
import com.crowninteractive.net.nercreport.exception.NercReportException;
import com.crowninteractive.net.nercreport.jms.ReportReceiver;
import com.crowninteractive.net.nercreport.service.ReportService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author johnson3yo
 */
@RequestMapping("nerc")
@RestController
public class NERCController {

    @Autowired
    private ReportReceiver reportReceiver;
    @Autowired
    private ReportService reportService;
        
    BaseResponse awe;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NERCController.class);

    @GetMapping("test")
    public String getTest() {
        return "nerc is running ";
    }
//
//    @GetMapping("start_report")
//    public ResponseEntity startReport(@RequestParam("from") String from,
//            @RequestParam("to") String to,
//            @RequestParam("email") String email) {
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    reportReceiver.processWriteV3(from, to, email);
//                } catch (IOException | NercReportException | EmailException ex) {
//                    Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }).start();
//
//        return new ResponseEntity("NERC Report Request Received And Is Been Processed", HttpStatus.OK);
//    }

    @GetMapping("start_report_v1")
    public String startReportV1(@RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("email") String email) {
        try {
            reportReceiver.processWriteV4(from, to, email);
            awe = new BaseResponse(0, "success");
        } catch (IOException | NercReportException | EmailException ex) {
            Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
        }

//        return new ResponseEntity("NERC Report Request Received And Is Been Processed", HttpStatus.OK);
        return new Gson().toJson(awe);
    }
    
    @GetMapping("start_report_v2")
    public String startReportV2(@RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("email") String email) throws FileNotFoundException, ParseException, java.text.ParseException {
        try {
            reportReceiver.processWriteV5(from, to, email);
            awe = new BaseResponse(0, "success");
        } catch (IOException | NercReportException | EmailException ex) {
            Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
        }

//        return new ResponseEntity("NERC Report Request Received And Is Been Processed", HttpStatus.OK);
        return new Gson().toJson(awe);
    }

    @GetMapping("extra_data")
    public ResponseEntity extraData(@RequestParam("ticketId") int ticketId) throws ParseException {
        try {
            reportService.getComplaintDetailsV1(ticketId);
            logger.info("house number version 2 >>>> "+reportService.getHouseNumberv2("my name is senator"));
        } catch (Exception ex) {
            Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ResponseEntity(reportService.getComplaintDetailsV1(ticketId), HttpStatus.OK);
    }

    @GetMapping("user")
    public ResponseEntity getUser(@RequestParam("userId") int userId) throws ParseException {
        try {
            reportService.getEngineerAssigned(userId);
            logger.info("USERSSSSSSSS >>>> "+reportService.getEngineerAssigned(userId));
        } catch (Exception ex) {
            Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ResponseEntity(reportService.getComplaintDetailsV1(userId), HttpStatus.OK);
    }
//    (reportService.getAssigneeName(w.getEngineerId()) != null){
}
