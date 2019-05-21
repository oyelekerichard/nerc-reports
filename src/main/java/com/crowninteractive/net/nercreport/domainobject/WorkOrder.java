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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author john
 */
@Entity
@Table(name = "work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "ticket_id")
    private int ticketId;
    @Basic(optional = false)
    @Column(name = "token", nullable = false, length = 30)
    private String token;
    @Basic(optional = false)
    @Column(name = "owner_id", nullable = false)
    private int ownerId;
    @Basic(optional = false)
    @Column(name = "summary", nullable = false, length = 500)
    private String summary;
    @Lob
    @Column(name = "description", length = 65535)
    private String description;
    @Basic(optional = false)
    @Column(name = "contact_number", nullable = false, length = 30)
    private String contactNumber;
    @Basic(optional = false)
    @Column(name = "reference_type", nullable = false, length = 20)
    private String referenceType;
    @Column(name = "reference_type_data", length = 40)
    private String referenceTypeData;
    @Basic(optional = false)
    @Column(name = "address_line_1", nullable = false, length = 200)
    private String addressLine1;
    @Column(name = "address_line_2", length = 200)
    private String addressLine2;
    @Basic(optional = false)
    @Column(name = "city", nullable = false, length = 50)
    private String city;
    @Basic(optional = false)
    @Column(name = "state", nullable = false, length = 50)
    private String state;
    @Basic(optional = false)
    @Column(name = "business_unit", nullable = false, length = 50)
    private String businessUnit;
    @Column(name = "customer_tariff", length = 50)
    private String customerTariff;
    @Basic(optional = false)
    @Column(name = "priority", nullable = false, length = 10)
    private String priority;
    @Basic(optional = false)
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "is_closed")
    private Short isClosed;
    @Column(name = "closed_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedTime;
    @Column(name = "is_assigned")
    private Short isAssigned;
    @Column(name = "date_assigned")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAssigned;
    @Column(name = "channel", length = 15)
    private String channel;
    @Basic(optional = false)
    @Column(name = "is_active", nullable = false)
    private int isActive;
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    @Column(name = "current_status", length = 50)
    private String currentStatus;
    @Column(name = "inventory_description", length = 150)
    private String inventoryDescription;
    @Column(name = "reported_by", length = 150)
    private String reportedBy;
    @Column(name = "inventory_ref", length = 150)
    private String inventoryRef;
    @Column(name = "requested_inventory", length = 500)
    private String requestedInventory;
    @Column(name = "date_requested_inventory")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRequestedInventory;
    @Column(name = "approved_inventory", length = 500)
    private String approvedInventory;
    @Column(name = "inventory_approved_by")
    private Integer inventoryApprovedBy;
    @Column(name = "date_approved_inventory")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateApprovedInventory;
    @Column(name = "inventory_approved")
    private Short inventoryApproved;
    @Column(name = "work_date")
    @Temporal(TemporalType.DATE)
    private Date workDate;
    @Column(name = "slot", length = 15)
    private String slot;
    @Column(name = "agent_name", length = 100)
    private String agentName;
    @Column(name = "customer_name", length = 60)
    private String customerName;
    @Column(name = "debt_balance_amount")
    private Double debtBalanceAmount;
    @Column(name = "current_bill")
    private Double currentBill;
    @Column(name = "last_payment_amount")
    private Double lastPaymentAmount;
    @Column(name = "last_payment_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastPaymentDate;
    @Column(name = "purpose", length = 256)
    private String purpose;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "previous_outstanding")
    private Double previousOutstanding;
    @Column(name = "due_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dueDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "queue_id")
    private Queue queue;
    @ManyToOne(optional = false)
    @JoinColumn(name = "queue_type_id")
    private QueueType queueType;
    @JoinColumn(name = "work_order_status_id")
    @ManyToOne
    private WorkOrderStatus workOrderStatusId;

    public WorkOrder() {
    }

    public WorkOrder(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceTypeData() {
        return referenceTypeData;
    }

    public void setReferenceTypeData(String referenceTypeData) {
        this.referenceTypeData = referenceTypeData;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getCustomerTariff() {
        return customerTariff;
    }

    public void setCustomerTariff(String customerTariff) {
        this.customerTariff = customerTariff;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Short getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Short isClosed) {
        this.isClosed = isClosed;
    }

    public Date getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(Date closedTime) {
        this.closedTime = closedTime;
    }

    public Short getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(Short isAssigned) {
        this.isAssigned = isAssigned;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getInventoryDescription() {
        return inventoryDescription;
    }

    public void setInventoryDescription(String inventoryDescription) {
        this.inventoryDescription = inventoryDescription;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getInventoryRef() {
        return inventoryRef;
    }

    public void setInventoryRef(String inventoryRef) {
        this.inventoryRef = inventoryRef;
    }

    public String getRequestedInventory() {
        return requestedInventory;
    }

    public void setRequestedInventory(String requestedInventory) {
        this.requestedInventory = requestedInventory;
    }

    public Date getDateRequestedInventory() {
        return dateRequestedInventory;
    }

    public void setDateRequestedInventory(Date dateRequestedInventory) {
        this.dateRequestedInventory = dateRequestedInventory;
    }

    public String getApprovedInventory() {
        return approvedInventory;
    }

    public void setApprovedInventory(String approvedInventory) {
        this.approvedInventory = approvedInventory;
    }

    public Integer getInventoryApprovedBy() {
        return inventoryApprovedBy;
    }

    public void setInventoryApprovedBy(Integer inventoryApprovedBy) {
        this.inventoryApprovedBy = inventoryApprovedBy;
    }

    public Date getDateApprovedInventory() {
        return dateApprovedInventory;
    }

    public void setDateApprovedInventory(Date dateApprovedInventory) {
        this.dateApprovedInventory = dateApprovedInventory;
    }

    public Short getInventoryApproved() {
        return inventoryApproved;
    }

    public void setInventoryApproved(Short inventoryApproved) {
        this.inventoryApproved = inventoryApproved;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public QueueType getQueueType() {
        return queueType;
    }

    public void setQueueType(QueueType queueType) {
        this.queueType = queueType;
    }

    public WorkOrderStatus getWorkOrderStatusId() {
        return workOrderStatusId;
    }

    public void setWorkOrderStatusId(WorkOrderStatus workOrderStatusId) {
        this.workOrderStatusId = workOrderStatusId;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkOrder)) {
            return false;
        }
        WorkOrder other = (WorkOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Double getDebtBalanceAmount() {
        return debtBalanceAmount;
    }

    public void setDebtBalanceAmount(Double debtBalanceAmount) {
        this.debtBalanceAmount = debtBalanceAmount;
    }

    public Double getCurrentBill() {
        return currentBill;
    }

    public void setCurrentBill(Double currentBill) {
        this.currentBill = currentBill;
    }

    public Double getLastPaymentAmount() {
        return lastPaymentAmount;
    }

    public void setLastPaymentAmount(Double lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPreviousOutstanding() {
        return previousOutstanding;
    }

    public void setPreviousOutstanding(Double previousOutstanding) {
        this.previousOutstanding = previousOutstanding;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}
