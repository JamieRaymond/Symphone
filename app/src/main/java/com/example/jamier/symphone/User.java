package com.example.jamier.symphone;

public class User {
    public String email, firstName, lastName, dob;

    public User(){

    }

    public User(String email, String firstName, String lastName, String dob) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }
}