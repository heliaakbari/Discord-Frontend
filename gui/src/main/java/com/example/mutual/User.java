package com.example.mutual;

import java.io.Serializable;

/**
 * holds the user's information including : username, phone number, password, email, status, profile photo - file and format-
 */
public class User implements Serializable {
    private static final long serialVersionUID = 944266325658983L;
    private String username;
    private String password;
    private String phoneNum;
    private String email;
    private Enum<Status> status;
    private byte[] profilePhoto;
    private String profilePhotoFormat;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Enum<Status> getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null) {
            this.status = null;
        } else {
            this.status = Status.valueOf(status);
        }
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public String getProfilePhotoFormat() {
        return profilePhotoFormat;
    }

    public void setProfilePhoto(byte[] profilePhoto, String format) {
        this.profilePhoto = profilePhoto;
        this.profilePhotoFormat = format;
    }

    /**
     * @return a string of user's info including username. phone number and status to be used in printing and ...
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("======================================\n");
        stringBuilder.append("username : ").append(username).append("\n");
        if (phoneNum != null && !phoneNum.equalsIgnoreCase("NULL"))
            stringBuilder.append("phone number : ").append(phoneNum).append("\n");
        if (status != null)
            stringBuilder.append("status : ").append(status).append("\n");
        stringBuilder.append("======================================\n");

        return stringBuilder.toString();
    }
}
