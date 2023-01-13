package com.example.multiproject;

public class Users {
    private String name;
    private String job;
    private String phone;
    public Users(){}


    public Users(String name, String job, String phone) {
        this.name = name;
        this.job = job;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) { this.phone = phone; }
}
