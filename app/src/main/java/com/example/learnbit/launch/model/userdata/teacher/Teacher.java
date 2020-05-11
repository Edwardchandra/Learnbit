package com.example.learnbit.launch.model.userdata.teacher;

public class Teacher {

   private double rating;
   private long balance;
   private String description;

    public Teacher(long balance, String description, double rating) {
        this.balance = balance;
        this.description = description;
        this.rating = rating;
    }

    public Teacher() {}

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
