package org.yup.accountingledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AccountingLedgerApp {

    public static void main(String[] args) {

        getTransactions();

    }

    public static void getTransactions() {

        try {

            FileReader tReader = new FileReader("transactions.csv");
            BufferedReader tReaderBetter = new BufferedReader(tReader);

            tReaderBetter.readLine();
            for(String line = tReaderBetter.readLine(); line != null; line = tReaderBetter.readLine()) {

                String[] lineSegments = line.split("\\|");

                LocalDate date = LocalDate.parse(lineSegments[0]);
                LocalTime time = LocalTime.parse(lineSegments[1]);
                String description = lineSegments[2];
                String vendor = lineSegments[3];
                float amount = Float.parseFloat(lineSegments[4]);

                Transaction newTransaction = new Transaction(date, time, description, vendor, amount);

                System.out.println(newTransaction.asText());

            }

            tReaderBetter.close();
            tReader.close();

        } catch (IOException e) {

            System.out.println("file no workey.");

        }

    }

}
