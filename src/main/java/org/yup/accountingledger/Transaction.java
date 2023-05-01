package org.yup.accountingledger;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {

    LocalDate date;
    LocalTime time;
    String description;
    String vendor;
    float amount;

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, float amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transaction(String description, String vendor, float amount) {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public String asText() {
        return  date.getYear()
                + " "
                + date.getMonth()
                + " "
                + date.getDayOfMonth()
                + " @ "
                + time.toString()
                + ", "
                + description
                + ", "
                + vendor
                + ", $ "
                + String.format("%.2f", amount);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
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
