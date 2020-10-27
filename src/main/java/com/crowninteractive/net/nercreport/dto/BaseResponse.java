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
public class BaseResponse {

    private int resp;
    private String desc;
    private Object object;

    public BaseResponse() {

    }

    public BaseResponse(int resp, String desc) {
        this.resp = resp;
        this.desc = desc;
    }

    public BaseResponse(int resp, String desc, Object object) {
        this.resp = resp;
        this.desc = desc;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getResp() {
        return resp;
    }

    public void setResp(int resp) {
        this.resp = resp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Awesome{" + "resp=" + resp + ", desc=" + desc + ", object=" + object + '}';
    }

}
