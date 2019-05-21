/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author osita
 */
public class Config extends Properties {

    //this is an example of a Singleton class
    private static Config INSTANCE;

    private void loadProps() throws IOException {
        super.load(new FileInputStream("/var/config/wfm/config.properties"));
    }

    private Config() {
    }

    public static Config getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Config();
                INSTANCE.loadProps();

            } catch (Exception ex) {
                INSTANCE = null;
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return INSTANCE;
    }

    public String approvalEmailTemplateUrl() {
        return getProperty("email.approval");
    }

    public String rejectionEmailTemplateUrl() {
        return getProperty("email.rejection");
    }

    public String wfmFrontendUrl() {
        return getProperty("wfm.ui.url");
    }

    public String getEMCCGetCustomerDetailsURL() {
        return getProperty("emcc.customer.detail");
    }

    public String getEnumerationQueueName() {
        return getProperty("enumeration");
    }

    public String getEmailHostName() {
        return getProperty("email.host.name");
    }

    public String getEmailQueueName() {
        return getProperty("email.queue.name");
    }

    public String getEmailAddress() {
        return getProperty("email.address");
    }

    public String getEmailPort() {
        return getProperty("email.port");
    }

    public String getEmailPassword() {
        return getProperty("email.password");
    }

    public String getEmailSender() {
        return getProperty("email.sender");
    }
}
