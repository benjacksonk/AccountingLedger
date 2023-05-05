package org.yup.accountingledger;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import static java.util.Comparator.comparing;

public class AccountingLedgerApp {

    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static Scanner inputScanner = new Scanner(System.in);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd    hh:mm a");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd");



    public static void main(String[] args) {

        String transactionsFileName = "ledger.csv";

        transactions.addAll(readTransactionsFile(transactionsFileName));

        showHomeMenu();

        writeTransactionsFile(transactionsFileName);

        System.out.println("Exiting application.");

    }



    private static ArrayList<Transaction> readTransactionsFile(String fileName) {

        ArrayList<Transaction> transactions = new ArrayList<>();

        try {

            FileReader ledgerFileReader = new FileReader(fileName);
            BufferedReader ledgerFileBufferedReader = new BufferedReader(ledgerFileReader);

            ledgerFileBufferedReader.readLine(); //skip beyond the header line

            String line;
            while((line = ledgerFileBufferedReader.readLine()) != null) {

                String[] lineSegments = line.split("\\|");

                LocalDate date = LocalDate.parse(lineSegments[0]);
                LocalTime time = LocalTime.parse(lineSegments[1]);
                String description = lineSegments[2];
                String vendor = lineSegments[3];
                float amount = Float.parseFloat(lineSegments[4]);

                Transaction newTransaction = new Transaction(LocalDateTime.of(date, time), description, vendor, amount);
                transactions.add(newTransaction);

            }

            transactions.sort(comparing(Transaction::getDateTime).reversed());

            ledgerFileBufferedReader.close();
            ledgerFileReader.close();

        } catch (IOException e) {
            System.out.println("ERROR while trying to read file");
            e.printStackTrace();
        }

        return transactions;

    }

    private static void writeTransactionsFile(String fileName) {

        try {

            FileWriter ledgerFileWriter = new FileWriter(fileName);
            BufferedWriter ledgerBufferedWriter = new BufferedWriter(ledgerFileWriter);

            //EXAMPLE: '2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50'

            ledgerBufferedWriter.write("date|time|description|vendor|amount");

            for (Transaction transaction : transactions) {
                ledgerBufferedWriter.write(
                        String.format("%n%s|%s|%s|%s|%.2f",
                                transaction.getDateTime().toLocalDate(),
                                transaction.getDateTime().toLocalTime(),
                                transaction.getDescription(),
                                transaction.getVendor(),
                                transaction.getAmount()
                        )
                );
            }

            ledgerBufferedWriter.close();
            ledgerFileWriter.close();

        } catch (IOException e) {
            System.out.println("ERROR while trying to write file");
            e.printStackTrace();
        }

    }



    private static void showHomeMenu() {
        boolean loop = true;
        while (loop) {

            System.out.println("\nHOME");
            System.out.println("D)  Make Deposit");
            System.out.println("P)  Make Payment");
            System.out.println("L)  View Ledger");
            System.out.println("X)  Exit");

            try {

                switch (promptUserLine().toUpperCase()) {
                    case "D" -> addTransaction(true);
                    case "P" -> addTransaction(false);
                    case "L" -> showLedgerMenu();
                    case "X" -> loop = false;
                    default -> System.out.println("INPUT ERROR");
                }

            } catch (Exception e) {
                System.out.println("INPUT ERROR");
            }
        }
    }

    private static void addTransaction(boolean isDeposit) {
        try {

            String transactionType = isDeposit ? "Deposit" : "Payment";

            System.out.printf("%nMAKE %s%n", transactionType.toUpperCase());

            float amount = promptUserFloat("Value: $ ");
            if (amount > 0) {

                String vendor = promptUserLine(String.format("Paid %s: ", isDeposit ? "by" : "to"));
                String description = promptUserLine("Purpose: ");

                transactions.add(new Transaction(description, vendor, (isDeposit ? amount : -amount)));
                transactions.sort(comparing(Transaction::getDateTime).reversed());

                System.out.printf("%s successful.%n", transactionType);

            } else {
                System.out.printf("ERROR: %s amount must be positive.%n", transactionType);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Transaction could not be made.");
        }
    }

    private static void showLedgerMenu() {
        boolean loop = true;
        while (loop) {

            System.out.println("\nLEDGER");
            System.out.println("A)  All Transactions");
            System.out.println("D)  View Deposits");
            System.out.println("P)  View Payments");
            System.out.println("R)  View Reports");
            System.out.println("H)  Home");

            try {

                switch (promptUserLine().toUpperCase()) {
                    case "A" -> showAllTransactions();
                    case "D" -> showDepositTransactions();
                    case "P" -> showDebitTransactions();
                    case "R" -> showReportsMenu();
                    case "H" -> loop = false;
                    default -> System.out.println("INPUT ERROR");
                }

            } catch (Exception e) {
                System.out.println("INPUT ERROR");
            }
        }
    }

    private static void showTransaction(Transaction transaction) {
        System.out.printf("%s    $%11.2f  %s  %s (%s)%n",
                transaction.getDateTime().format(dateTimeFormatter),
                transaction.getAmount(),
                (transaction.getAmount() < 0 ? "→" : "←"),
                transaction.getVendor(),
                transaction.getDescription()
        );
    }

    private static void showAllTransactions() {
        System.out.println("\nTRANSACTIONS");
        for (Transaction transaction : transactions) {
            showTransaction(transaction);
        }
    }

    private static void showDepositTransactions() {
        System.out.println("\nDEPOSITS");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                showTransaction(transaction);
            }
        }
    }

    private static void showDebitTransactions() {
        System.out.println("\nDEBITS");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                showTransaction(transaction);
            }
        }
    }

    private static void showReportsMenu() {
        boolean loop = true;
        while (loop) {

            System.out.println("\nTRANSACTION REPORTS");
            System.out.println("1)  Month to Date");
            System.out.println("2)  Previous Month");
            System.out.println("3)  Year to Date");
            System.out.println("4)  Previous Year");
            System.out.println("5)  Search by vendor");
            System.out.println("0)  Back");

            try {

                int userChoice = promptUserInt();

                LocalDate now = LocalDate.now();
                LocalDate earliest;

                switch (userChoice) {
                    case 1 -> showReportsByDate(
                            now.withDayOfMonth(1),
                            now
                    );
                    case 2 -> showReportsByDate(
                            earliest = now.withMonth(now.getMonthValue() - 1).withDayOfMonth(1),
                            earliest.withDayOfMonth(earliest.lengthOfMonth())
                    );
                    case 3 -> showReportsByDate(
                            now.withDayOfYear(1),
                            now
                    );
                    case 4 -> showReportsByDate(
                            earliest = now.withYear(now.getYear() - 1).withDayOfYear(1),
                            earliest.withDayOfYear(earliest.lengthOfYear())
                    );
                    case 5 -> showReportsByVendor();
                    case 0 -> loop = false;
                    default -> System.out.println("INPUT ERROR");
                }
            } catch (Exception e) {
                System.out.println("INPUT ERROR");
            }
        }
    }

    private static void showReportsByDate(LocalDate earliest, LocalDate latest) {
        try {

            System.out.printf("%nTRANSACTION REPORT (%s - %s)%n",
                    earliest.format(dateFormatter),
                    latest.format(dateFormatter)
            );

            float balance = 0;

            for (Transaction transaction : transactions) {
                if (!transaction.getDateTime().toLocalDate().isBefore(earliest) && !transaction.getDateTime().toLocalDate().isAfter(latest)) {
                    showTransaction(transaction);
                    balance += transaction.getAmount();
                }
            }

            System.out.printf("-----------    BALANCE:    $%11.2f%n", balance);

        } catch (Exception e) {
            System.out.println("ERROR: Show reports by date, exception occurred");
        }

    }

    private static void showReportsByVendor() {
        try {

            String vendor = promptUserLine("\nSEARCH TRANSACTIONS\nEnter a vendor: ");

            System.out.printf("%nTRANSACTION REPORT (to/from %s)%n", vendor);

            float balance = 0;

            for (Transaction transaction : transactions) {
                if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                    showTransaction(transaction);
                    balance += transaction.getAmount();
                }
            }

            System.out.printf("-----------    BALANCE:    $%11.2f%n", balance);

        } catch (Exception e) {
            System.out.println("ERROR: Could not search reports by vendor");
        }
    }



    private static int promptUserInt() {
        return Integer.parseInt(inputScanner.nextLine());
    }

    private static int promptUserInt(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(inputScanner.nextLine());
    }

    private static float promptUserFloat() {
        return Float.parseFloat(inputScanner.nextLine());
    }

    private static float promptUserFloat(String prompt) {
        System.out.print(prompt);
        return Float.parseFloat(inputScanner.nextLine());
    }

    private static String promptUserLine() {
        return inputScanner.nextLine();
    }

    private static String promptUserLine(String prompt) {
        System.out.print(prompt);
        return inputScanner.nextLine();
    }

}
