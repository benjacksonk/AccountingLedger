package org.yup.accountingledger;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private float amount;

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
        return String.format("$ %13.2f,  %-17s  @  %-8s,  %s,  %s",
                this.amount,
                String.format("%d %s %02d",
                        this.date.getYear(),
                        this.date.getMonth(),
                        this.date.getDayOfMonth()
                ),
                this.time,
                this.description,
                this.vendor
        );
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
