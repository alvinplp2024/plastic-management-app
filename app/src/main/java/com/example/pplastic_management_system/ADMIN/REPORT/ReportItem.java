package com.example.pplastic_management_system.ADMIN.REPORT;

public class ReportItem {
    String id;
    String name;
    String plastic;
    String phone;
    String description;
    Double amount;
    String documentId;
    String status;
    String content;


    public ReportItem() {

    }
    // Constructor with documentId
    public ReportItem(String name, String phone, String description, Double amount, String plastic, String documentId,String status,String content,String id) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.amount = amount;
        this.plastic = plastic;
        this.documentId = documentId;
        this.status = status;
        this.content=content;
        this.id =id;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlastic() {
        return plastic;
    }

    public void setPlastic(String plastic) {
        this.plastic = plastic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
