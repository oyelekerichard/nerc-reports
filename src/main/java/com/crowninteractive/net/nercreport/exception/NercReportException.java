/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.exception;

/**
 *
 * @author johnson3yo
 */
public class NercReportException extends Exception {

    private String message;

    public NercReportException() {
    }

    public NercReportException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
