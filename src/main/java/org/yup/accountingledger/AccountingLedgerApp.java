package org.yup.accountingledger;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static java.util.Comparator.comparing;

public class AccountingLedgerApp {

    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        transactions.addAll(readTransactionsFile());
        homeMenu();
        writeTransactionsFile(transactions);
        System.out.println("Exiting application.");
    }

    private static ArrayList<Transaction> readTransactionsFile() {

        ArrayList<Transaction> transactions = new ArrayList<>();

        try {

            FileReader ledgerFileReader = new FileReader("transactions.csv");
            BufferedReader ledgerFileReaderButBetter = new BufferedReader(ledgerFileReader);

            ledgerFileReaderButBetter.readLine();
            for(String line = ledgerFileReaderButBetter.readLine(); line != null; line = ledgerFileReaderButBetter.readLine()) {

                String[] lineSegments = line.split("\\|");

                LocalDate date = LocalDate.parse(lineSegments[0]);
                LocalTime time = LocalTime.parse(lineSegments[1]);
                String description = lineSegments[2];
                String vendor = lineSegments[3];
                float amount = Float.parseFloat(lineSegments[4]);

                Transaction newTransaction = new Transaction(date, time, description, vendor, amount);
                transactions.add(newTransaction);
                transactions.sort(comparing(Transaction::getDate, Collections.reverseOrder()));

            }

            ledgerFileReaderButBetter.close();
            ledgerFileReader.close();

        } catch (IOException e) {

            System.out.println("file no workey.");

        }

        return transactions;

    }

    private static void writeTransactionsFile(ArrayList<Transaction> transactions) {

        try {

            FileWriter ledgerFileWriter = new FileWriter("ledger.csv");
            BufferedWriter ledgerFileWriterButBetter = new BufferedWriter(ledgerFileWriter);

            ledgerFileWriterButBetter.write("date|time|description|vendor|amount");
            //EXAMPLE: '2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50'
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                ledgerFileWriterButBetter.write(
                    String.format(
                        "%n%s|%5s|%s|%s|%.2f",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount()
                    )
                );
            }

            ledgerFileWriterButBetter.close();
            ledgerFileWriter.close();

        } catch (IOException e) {
        }

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
                    case "D" -> addTransaction(false);
                    case "P" -> addTransaction(true);
                    case "L" -> ledgerMenu();
                    case "X" -> loop = false;
                    default  -> System.out.println("ERROR.\nRepeating menu...");
                }
            } catch (Exception e) {
                System.out.println("ERROR.\nRepeating menu...");
            }
        }
    }

    private static void addTransaction(boolean isDebit) {

        try {

            float amount = promptUserFloat("Enter the transaction amount: ");

            if (amount > 0) {

                String description = promptUserLine("Enter the description: ");
                String vendor = promptUserLine("Enter the vendor: ");

                if (transactions.add(new Transaction(description, vendor, (isDebit ? -amount : amount)))) {
                    System.out.println("Transaction successful.");
                } else {
                    System.out.println("ERROR: Transaction could not be made.");
                }

            } else {
                System.out.println("ERROR: Transaction amount must be positive.");
            }

        } catch (Exception e) {
            System.out.println("ERROR: Transaction could not be made.");
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
                    case "H" -> loop = false;
                    default  -> System.out.println("ERROR.\nRepeating menu...");
                }
            } catch (Exception e) {
                System.out.println("ERROR.\nRepeating menu...");
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
            if (transaction.getAmount() > 0) {
                System.out.println(transaction.asText());
            }
        }
    }

    private static void viewPayments() {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
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
                    case 5  -> viewReportsByVendor(promptUserLine("Enter a vendor: "));
                    case 0  -> loop = false;
                    default -> System.out.println("ERROR.\nRepeating menu...");
                }
            } catch (Exception e) {
                System.out.println("ERROR.\nRepeating menu...");
            }
        }
    }

    private static void viewReportsBetween(LocalDate earliest, LocalDate latest) {
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(earliest) && !transaction.getDate().isAfter(latest)) {
                System.out.println(transaction.asText());
            }
        }
    }

    private static void viewReportsByVendor(String vendor) {
        for (Transaction transaction : transactions) {
            if (transaction.getVendor() == vendor) {
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
