package com.example.learnbit.launch.teacher.profile.withdraw.model;

public class Withdraw {

    private String bankName;
    private int bankNumber;
    private String destinationName;
    private Boolean isSent;
    private int amount;
    private String userUid;

    public Withdraw(String bankName, int bankNumber, String destinationName, Boolean isSent, int amount, String userUid) {
        this.bankName = bankName;
        this.bankNumber = bankNumber;
        this.destinationName = destinationName;
        this.isSent = isSent;
        this.amount = amount;
        this.userUid = userUid;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(int bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public Boolean getSent() {
        return isSent;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
