package com.example.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "utility")
public class Utility {

    @Id
    private String id;
    private String account;
    private String type;
    private String provider;
    private Double balance;

    public Utility() {
    }

    public Utility(String account, String type, String provider, Double balance) {
        this.account = account;
        this.type = type;
        this.provider = provider;
        this.balance = balance;
    }

    public Utility(String provider, String type, String account) {
        this.provider = provider;
        this.type = type;
        this.account = account;
        this.balance = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
