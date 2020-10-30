/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.domainobject;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author osita
 */
@Entity
@Table(name = "users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "token", nullable = false, length = 30)
    private String token;
    @Basic(optional = false)
    @Column(name = "owner_id", nullable = false)
    private int ownerId;
    @Basic(optional = false)
    @Column(name = "newpass", nullable = false)
    private int newpass;
    @Basic(optional = false)
    @Column(name = "level", nullable = false)
    private int level;
    @Basic(optional = false)
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    @Basic(optional = false)
    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;
    @Basic(optional = false)
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;
    @Column(name = "password", length = 200)
    private String password;
    @Basic(optional = false)
    @Column(name = "department", nullable = false, length = 100)
    private String department;
    @Basic(optional = false)
    @Column(name = "salt", nullable = false, length = 100)
    private String salt;
    @Basic(optional = false)
    @Column(name = "roles", nullable = false, length = 200)
    private String roles;
    @Basic(optional = false)
    @Column(name = "queues", nullable = false, length = 200)
    private String queues;
    @Basic(optional = false)
    @Column(name = "districts", nullable = false, length = 200)
    private String districts;
    @Column(name = "tariffs", length = 200)
    private String tariffs;
    @Column(name = "statuses", length = 300)
    private String statuses;
    @Basic(optional = false)
    @Column(name = "temp_password", nullable = false, length = 20)
    private String tempPassword;
    @Basic(optional = false)
    @Column(name = "sent_email", nullable = false)
    private int sentEmail;
    @Basic(optional = false)
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;
    @Column(name = "logon_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logonTime;
    @Column(name = "has_confirmed")
    private Integer hasConfirmed;
    @Column(name = "is_engineer")
    private Integer isEngineer;
    @Basic(optional = false)
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    @Basic(optional = false)
    @Column(name = "is_active", nullable = false)
    private int isActive;

    public Users() {
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getNewpass() {
        return newpass;
    }

    public void setNewpass(int newpass) {
        this.newpass = newpass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getQueues() {
        return queues;
    }

    public void setQueues(String queues) {
        this.queues = queues;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getTariffs() {
        return tariffs;
    }

    public void setTariffs(String tariffs) {
        this.tariffs = tariffs;
    }

    public String getStatuses() {
        return statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public int getSentEmail() {
        return sentEmail;
    }

    public void setSentEmail(int sentEmail) {
        this.sentEmail = sentEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getLogonTime() {
        return logonTime;
    }

    public void setLogonTime(Date logonTime) {
        this.logonTime = logonTime;
    }

    public Integer getHasConfirmed() {
        return hasConfirmed;
    }

    public void setHasConfirmed(Integer hasConfirmed) {
        this.hasConfirmed = hasConfirmed;
    }

    public Integer getIsEngineer() {
        return isEngineer;
    }

    public void setIsEngineer(Integer isEngineer) {
        this.isEngineer = isEngineer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Users{" + "id=" + id + ", token=" + token + ", ownerId=" + ownerId + ", newpass=" + newpass + ", level=" + level + ", email=" + email + ", firstname=" + firstname + ", lastname=" + lastname + ", password=" + password + ", department=" + department + ", salt=" + salt + ", roles=" + roles + ", queues=" + queues + ", districts=" + districts + ", tariffs=" + tariffs + ", statuses=" + statuses + ", tempPassword=" + tempPassword + ", sentEmail=" + sentEmail + ", phone=" + phone + ", logonTime=" + logonTime + ", hasConfirmed=" + hasConfirmed + ", isEngineer=" + isEngineer + ", createTime=" + createTime + ", updateTime=" + updateTime + ", isActive=" + isActive + '}';
    }

}
