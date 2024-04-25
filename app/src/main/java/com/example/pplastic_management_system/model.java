package com.example.pplastic_management_system;

public class model {
    String name;

    String phone;
    String description;
    Double amount;
    String status;
    String userid;




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

    public model() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public model(String name, String phone, String description, Double amount, String status, String userid) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.amount = amount;
        this.userid = userid;
        this.status = status;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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
}
