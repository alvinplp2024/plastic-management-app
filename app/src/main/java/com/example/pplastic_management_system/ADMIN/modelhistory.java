package com.example.pplastic_management_system.ADMIN;

public class modelhistory {
    String name;
    String plastic;
    String phone;
    String description;
    Double amount;
    String documentId;
    String status;




    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public modelhistory() {

    }

    public String getPlastic() {
        return plastic;
    }

    public void setPlastic(String plastic) {
        this.plastic = plastic;
    }


    // Constructor without documentId
    public modelhistory(String name, String phone, String description, Double amount, String plastic) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.amount = amount;
        this.plastic = plastic;
    }

    // Constructor with documentId
    public modelhistory(String name, String phone, String description, Double amount, String plastic, String documentId,String status) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.amount = amount;
        this.plastic = plastic;
        this.documentId = documentId;
        this.status = status; // Initialize status field
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Add getter and setter methods for document ID
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
