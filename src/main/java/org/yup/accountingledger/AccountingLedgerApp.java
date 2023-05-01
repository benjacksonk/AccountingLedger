package org.yup.accountingledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class AccountingLedgerApp {

    public static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {

        getTransactions();

        for(boolean running = homeMenu(); running; running = homeMenu()){
            System.out.println();
        }

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

//                System.out.println(newTransaction.asText());

            }

            tReaderBetter.close();
            tReader.close();

        } catch (IOException e) {

            System.out.println("file no workey.");

        }

    }

    public static boolean homeMenu() {
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment (Debit)");
        System.out.println("L) Ledger");
        System.out.println("X) Exit");
        switch (inputScanner.nextLine().toUpperCase()) {
            case "D":
                //add deposit
                System.out.println("This is where I would run the addDeposit() method, IF I HAD ONE!");
                return true;
            case "P":
                //make payment a.k.a. debit
                System.out.println("This is where I would run the makePayment() method, IF I HAD ONE!");
                return true;
            case "L":
                //ledgerMenu()
                System.out.println("This is where I would run the ledgerMenu() method, IF I HAD ONE!");
                return true;
            default:
                // X - exit
                System.out.println("Exiting application.");
                return false;
        }
    }

}
