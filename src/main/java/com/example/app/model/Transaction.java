package com.example.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction entity representing banking transactions
 */
@Document(collection = "transactions")
public class Transaction {

    // MongoDB ID (auto-generated)
    @Id
    private String id;

    
    @NotBlank(message = "Receiver is required")
    @Indexed
    private String receiver; // Receiver account number

    @NotBlank(message = "Sender is required")
    @Indexed
    private String sender; // Sender account number

    private String date; // Transaction date (keeping as String for backward compatibility)

    @NotNull(message = "Amount is required")
    private double amount;

    @NotBlank(message = "Type is required")
    private String type; // Transaction type (DEPOSIT, WITHDRAWAL, TRANSFER, etc.)

    @Indexed(unique = true)
    private String ref; // Reference number (unique transaction ID)

    // Additional MongoDB fields
    private String status; // PENDING, COMPLETED, FAILED

    private String description; // Transaction description

    private LocalDateTime transactionDate; // For better date handling in MongoDB

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public Transaction() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.transactionDate = LocalDateTime.now();

        // Auto-generate date string for backward compatibility
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.date = this.transactionDate.format(formatter);
    }

    // Constructor matching your original (6 params)
    public Transaction(String receiver, String date, double amount, String type, String sender, String ref) {
        this();
        this.receiver = receiver;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.sender = sender;
        this.ref = ref;

        // Try to parse date string to LocalDateTime if provided
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.transactionDate = LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            this.transactionDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

        // Update transactionDate when date is set
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.transactionDate = LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            this.transactionDate = LocalDateTime.now();
        }
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;

        // Update date string for backward compatibility
        if (transactionDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.date = transactionDate.format(formatter);
        }
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
        return "Transaction{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", ref='" + ref + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
