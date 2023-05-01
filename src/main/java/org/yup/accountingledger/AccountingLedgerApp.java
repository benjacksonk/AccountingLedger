package org.yup.accountingledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {

    public static ArrayList<Transaction> transactions = new ArrayList<>();
    public static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {

        transactions.addAll(getTransactions());

        for(boolean running = homeMenu(); running; running = homeMenu()){
            System.out.println();
        }

    }

    public static ArrayList<Transaction> getTransactions() {

        ArrayList<Transaction> transactions = new ArrayList<>();

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
                transactions.add(newTransaction);

            }

            tReaderBetter.close();
            tReader.close();

        } catch (IOException e) {

            System.out.println("file no workey.");

        }

        return transactions;

    }

    public static boolean homeMenu() {
        System.out.println("HOME MENU");
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment (Debit)");
        System.out.println("L) Ledger");
        System.out.println("X) Exit");
        switch (inputScanner.nextLine().toUpperCase()) {
            case "D":
                addDeposit();
                return true;
            case "P":
                System.out.println("This is where I would run the makePayment() method, IF I HAD ONE!");
                return true;
            case "L":
                ledgerMenu();
                return true;
            default:
                System.out.println("Exiting application.");
                return false;
        }
    }

    public static void addDeposit() {

        System.out.print("Enter an amount: ");
        float amount = inputScanner.nextFloat();

        if (amount > 0) {

            System.out.print("Enter a description: ");
            String description = inputScanner.nextLine();

            System.out.print("Enter a vendor: ");
            String vendor = inputScanner.nextLine();

            if (transactions.add(new Transaction(description, vendor, amount))) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("ERROR: Deposit could not be made.");
            }

        } else {
            System.out.println("ERROR: Deposit amount must be positive.");
        }

    }

    public static void makePayment() {



    }

    public static void ledgerMenu() {
        System.out.println("LEDGER MENU");
        System.out.println("A) View All");
        System.out.println("D) View Deposits");
        System.out.println("P) View Payments");
        System.out.println("R) View Reports");
        System.out.println("H) Home");
        switch (inputScanner.nextLine().toUpperCase()) {
            case "A":
                viewAll();
                break;
            case "D":
                System.out.println("This is where I would run the viewDeposits() method, IF I HAD ONE!");
                break;
            case "P":
                System.out.println("This is where I would run the viewPayments() method, IF I HAD ONE!");
                break;
            case "R":
                System.out.println("This is where I would run the viewReports() method, IF I HAD ONE!");
                break;
            default:
                System.out.println("Returning to home menu.");
                break;
        }
    }

    public static void viewAll() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction.asText());
        }
    }

    public static void reportsMenu() {

        // 1 - month to date
        // 2 - previous month
        // 3 - year to date
        // 4 - previous year
        // 5 - search by vendor
        // 0 - back

    }

}
