/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author johnson3yo
 */
@Configuration
@ComponentScan({"com.crowninteractive.net.nercreport"})
@PropertySource(value = {"file:/var/config/nerc-reports/application.properties"})
public class ApplicationConfiguration {
    
}
