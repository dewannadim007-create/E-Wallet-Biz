package com.example.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * User entity representing a bank customer
 */
@Document(collection = "users")
public class User {

    // MongoDB ID (auto-generated)
    @Id
    private String id;

    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Indexed(unique = true)
    private String mobile;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String DOB; // Date of Birth

    @Indexed(unique = true)
    private String account; // Account number

    @Indexed(unique = true)
    private String nid; // National ID

    private String image; // Profile image path/URL

    private String address;

    // Additional banking fields
    private Double balance; // Bank Account Balance (Physical Bank)

    private Double walletBalance; // Online Wallet Balance (App)

    private String accountType; // SAVINGS, CURRENT, etc.

    private String userRole; // USER, ADMIN

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public User() {
        this.balance = 0.0;
        this.walletBalance = 0.0;
        this.isActive = true;
        this.userRole = "USER";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor matching your original (6 params)
    public User(String name, String mobile, String email, String DOB, String account, String nid) {
        this();
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.DOB = DOB;
        this.account = account;
        this.nid = nid;
    }

    // Constructor matching your original (7 params with password)
    public User(String name, String mobile, String email, String password, String DOB, String account, String nid) {
        this();
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.DOB = DOB;
        this.account = account;
        this.nid = nid;
    }

    // Constructor for cheque table (as per your comment)
    public User(String name, String id, String email, String address, String nid) {
        this();
        this.name = name;
        this.id = id;
        this.email = email;
        this.address = address;
        this.nid = nid;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id; // FIXED: was setting to itself
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address; // FIXED: was setting to itself
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", account='" + account + '\'' +
                ", nid='" + nid + '\'' +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                ", userRole='" + userRole + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
