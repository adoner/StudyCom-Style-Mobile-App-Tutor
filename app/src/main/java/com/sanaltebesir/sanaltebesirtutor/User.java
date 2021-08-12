package com.sanaltebesir.sanaltebesirtutor;

import java.util.Date;

public class User {
    String username;
    //String fullName;
    String userid;
    Date sessionExpiryDate;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    /*public void setFullName(String fullName) {
        this.fullName = fullName;
    }*/

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }

    /*public String getFullName() {
        return fullName;
    }*/

    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }
}