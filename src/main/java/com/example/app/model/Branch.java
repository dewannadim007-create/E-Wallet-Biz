package com.example.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Branch entity representing bank branches
 */
@Document(collection = "branches")
public class Branch {

    // MongoDB ID (auto-generated)
    @Id
    private String id;

    
    @NotBlank(message = "Branch name is required")
    @Indexed
    private String name; // Branch name

    @NotBlank(message = "Branch type is required")
    private String type; // Branch type (Main, Sub-branch, ATM, etc.)

    @NotBlank(message = "Location is required")
    @Indexed
    private String location; // Branch location/address

    // Additional banking fields
    private String branchCode; // Unique branch code

    private String ifscCode; // IFSC code for Indian banks

    private String swiftCode; // SWIFT code for international transfers

    private String city; // City name

    private String state; // State name

    private String country; // Country name (default: Bangladesh)

    private String zipCode; // Postal/ZIP code

    private String phoneNumber; // Branch phone number

    private String email; // Branch email

    private String managerName; // Branch manager name

    private String openingHours; // e.g., "9:00 AM - 5:00 PM"

    private String closingHours;

    private Boolean isActive; // Is branch operational

    private Double latitude; // For GPS/map location

    private Double longitude;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public Branch() {
        this.isActive = true;
        this.country = "Bangladesh";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor matching your original (3 params)
    public Branch(String name, String type, String location) {
        this();
        this.name = name;
        this.type = type;
        this.location = location;
    }

    // Additional constructor with more details
    public Branch(String name, String type, String location, String branchCode, String city) {
        this(name, type, location);
        this.branchCode = branchCode;
        this.city = city;
    }

    // Getters and Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location; // FIXED: was setting to itself with wrong param name
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(String closingHours) {
        this.closingHours = closingHours;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "Branch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", city='" + city + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
