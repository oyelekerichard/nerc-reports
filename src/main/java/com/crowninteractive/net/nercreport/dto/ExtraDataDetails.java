/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richard.oyeleke
 */
@XmlRootElement
public class ExtraDataDetails {

    private String serviceBand;
    private String complaintArea;
    private String complaintLga;
    private String customerType;
    private String customerFirstName;
    private String customerLastName;
    private int houseNumber;
    private String customerAdress;
    private String cutomerState;
    private String lga;
    private String area;

    public ExtraDataDetails() {
    }

    public ExtraDataDetails(String serviceBand, String complaintArea, String complaintLga, String customerType, String customerFirstName, String customerLastName, int houseNumber, String customerAdress, String cutomerState, String lga, String area) {
        this.serviceBand = serviceBand;
        this.complaintArea = complaintArea;
        this.complaintLga = complaintLga;
        this.customerType = customerType;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.houseNumber = houseNumber;
        this.customerAdress = customerAdress;
        this.cutomerState = cutomerState;
        this.lga = lga;
        this.area = area;
    }

    public String getServiceBand() {
        return serviceBand;
    }

    public void setServiceBand(String serviceBand) {
        this.serviceBand = serviceBand;
    }

    public String getComplaintArea() {
        return complaintArea;
    }

    public void setComplaintArea(String complaintArea) {
        this.complaintArea = complaintArea;
    }

    public String getComplaintLga() {
        return complaintLga;
    }

    public void setComplaintLga(String complaintLga) {
        this.complaintLga = complaintLga;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCustomerAdress() {
        return customerAdress;
    }

    public void setCustomerAdress(String customerAdress) {
        this.customerAdress = customerAdress;
    }

    public String getCutomerState() {
        return cutomerState;
    }

    public void setCutomerState(String cutomerState) {
        this.cutomerState = cutomerState;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "NercReport{" + "serviceBand=" + serviceBand + ", complaintArea=" + complaintArea + ", complaintLga=" + complaintLga + ", customerType=" + customerType + ", customerFirstName=" + customerFirstName + ", customerLastName=" + customerLastName + ", houseNumber=" + houseNumber + ", customerAdress=" + customerAdress + ", cutomerState=" + cutomerState + ", lga=" + lga + ", area=" + area + '}';
    }

}