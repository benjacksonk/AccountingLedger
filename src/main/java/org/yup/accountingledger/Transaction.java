package org.yup.accountingledger;

import java.time.LocalDateTime;

public class Transaction {

    private LocalDateTime dateTime;
    private String description;
    private String vendor;
    private float amount;



    public Transaction(LocalDateTime dateTime, String description, String vendor, float amount) {
        this.dateTime = dateTime;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transaction(String description, String vendor, float amount) {
        this.dateTime = LocalDateTime.now();
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }



    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public float getAmount() {
        return amount;
    }

}
