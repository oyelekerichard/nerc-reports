/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.controller;

import com.crowninteractive.net.nercreport.exception.NercReportException;
import com.crowninteractive.net.nercreport.jms.ReportReceiver;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("test")
    public String getTest() {
        return "nerc is running ";
    }

    @GetMapping("start_report")
    public ResponseEntity startReport(@RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("email") String email) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    reportReceiver.processWriteV3(from, to, email);
                } catch (IOException | NercReportException | EmailException ex) {
                    Logger.getLogger(NERCController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        return new ResponseEntity("NERC Report Request Received And Is Been Processed", HttpStatus.OK);
    }

}
