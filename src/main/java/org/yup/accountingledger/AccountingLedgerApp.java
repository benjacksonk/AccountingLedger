package org.yup.accountingledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {

    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        transactions.addAll(getTransactions());
        homeMenu();
        System.out.println("Exiting application.");
    }

    private static ArrayList<Transaction> getTransactions() {

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

    private static void homeMenu() {
        for (boolean loop = true; loop; ) {
            System.out.println("HOME MENU");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            try {
                switch (promptUserLine().toUpperCase()) {
                    case "D" -> addDeposit();
                    case "P" -> makePayment();
                    case "L" -> ledgerMenu();
                    default  -> loop = false;
                }
            } catch (Exception e) {
                loop = false;
            }
        }
    }

    private static void addDeposit() {

        try {

            float amount = promptUserInt("Enter the deposit amount: ");
            if (amount > 0) {

                String description = promptUserLine("Enter the description: ");
                String vendor = promptUserLine("Enter the vendor: ");

                if (transactions.add(new Transaction(description, vendor, amount))) {
                    System.out.println("Deposit successful.");
                } else {
                    System.out.println("ERROR: Deposit could not be made.");
                }

            } else {
                System.out.println("ERROR: Deposit amount must be positive.");
            }

        } catch (Exception e) {
            System.out.println("ERROR: Deposit could not be made.");
        }

    }

    private static void makePayment() {

        try {

            float amount = promptUserFloat("Enter the payment amount");
            if (amount > 0) {

                String description = promptUserLine("Enter the description: ");
                String vendor = promptUserLine("Enter the vendor: ");

                if (transactions.add(new Transaction(description, vendor, -amount))) {
                    System.out.println("Payment successful.");
                } else {
                    System.out.println("ERROR: Payment could not be made.");
                }

            } else {
                System.out.println("ERROR: Payment amount must be positive.");
            }

        } catch (Exception e) {
            System.out.println("ERROR: Payment could not be made.");
        }

    }

    private static void ledgerMenu() {
        for (boolean loop = true; loop; ) {
            System.out.println("LEDGER MENU");
            System.out.println("A) View All");
            System.out.println("D) View Deposits");
            System.out.println("P) View Payments");
            System.out.println("R) View Reports");
            System.out.println("H) Home");
            try {
                switch (promptUserLine().toUpperCase()) {
                    case "A" -> viewAll();
                    case "D" -> viewDeposits();
                    case "P" -> viewPayments();
                    case "R" -> reportsMenu();
                    default  -> loop = false;
                }
            } catch (Exception e) {
                loop = false;
            }
        }
    }

    private static void viewAll() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction.asText());
        }
    }

    private static void viewDeposits() {
        for (Transaction transaction : transactions) {
            if (transaction.amount > 0) {
                System.out.println(transaction.asText());
            }
        }
    }

    private static void viewPayments() {
        for (Transaction transaction : transactions) {
            if (transaction.amount < 0) {
                System.out.println(transaction.asText());
            }
        }
    }

    private static void reportsMenu() {
        for (boolean loop = true; loop; ) {
            System.out.println("REPORTS MENU");
            System.out.println("1) Month to Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year to Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by vendor");
            System.out.println("0) Back");
            try {
                LocalDate now = LocalDate.now();
                LocalDate earliest;
                switch (promptUserInt()) {
                    case 1  -> viewReportsBetween(
                            now.withDayOfMonth(1),
                            now
                    );
                    case 2  -> viewReportsBetween(
                            earliest = now.withMonth(now.getMonthValue()-1).withDayOfMonth(1),
                            earliest.withDayOfMonth(earliest.lengthOfMonth())
                    );
                    case 3  -> viewReportsBetween(
                            now.withDayOfYear(1),
                            now
                    );
                    case 4  -> viewReportsBetween(
                            earliest = now.withYear(now.getYear()-1).withDayOfYear(1),
                            earliest.withDayOfYear(earliest.lengthOfYear())
                    );
                    case 5  -> System.out.println("Coming Soon™");
                    default -> loop = false;
                }
            } catch (Exception e) {
                loop = false;
            }
        }
    }

    private static void viewReportsBetween(LocalDate earliest, LocalDate latest) {
        for (Transaction transaction : transactions) {
            if (!transaction.date.isBefore(earliest) && !transaction.date.isAfter(latest)) {
                System.out.println(transaction.asText());
            }
        }
    }

    private static int promptUserInt() {
        int input = inputScanner.nextInt();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return input;
    }

    private static int promptUserInt(String prompt) {
        System.out.println(prompt);
        int input = inputScanner.nextInt();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return input;
    }

    private static float promptUserFloat() {
        float input = inputScanner.nextFloat();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return input;
    }

    private static float promptUserFloat(String prompt) {
        System.out.println(prompt);
        float input = inputScanner.nextFloat();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return input;
    }

    private static String promptUserLine() {
        return inputScanner.nextLine();
    }

    private static String promptUserLine(String prompt) {
        System.out.println(prompt);
        return inputScanner.nextLine();
    }

}
