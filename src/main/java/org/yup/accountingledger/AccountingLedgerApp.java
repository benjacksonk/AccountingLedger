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
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd,  hh:mm a");
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
        for (boolean loop = true; loop; ) {
            System.out.println("\nHOME MENU");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            try {
                switch (promptUserLine().toUpperCase()) {
                    case "D" -> addTransaction(false);
                    case "P" -> addTransaction(true);
                    case "L" -> showLedgerMenu();
                    case "X" -> loop = false;
                    default  -> System.out.println("ERROR: Reports menu, invalid input");
                }
            } catch (Exception e) {
                loop = false;
                System.out.println("ERROR: Home menu, exception occurred");
                e.printStackTrace();
            }
        }
    }

    private static void addTransaction(boolean isDebit) {

        String transactionType = isDebit ? "Debit" : "Deposit";

        try {

            float amount = promptUserFloat(String.format("%nEnter the %s amount: ", transactionType.toLowerCase()));

            if (amount > 0) {

                String description = promptUserLine("Enter the description: ");
                String vendor = promptUserLine("Enter the vendor: ");

                if (transactions.add(new Transaction(description, vendor, (isDebit ? -amount : amount)))) {
                    transactions.sort(comparing(Transaction::getDateTime).reversed());
                    System.out.printf("%s successful.%n", transactionType);
                } else {
                    System.out.printf("ERROR: %s could not be made.%n", transactionType);
                }

            } else {
                System.out.printf("ERROR: %s amount must be positive.%n", transactionType);
            }

        } catch (Exception e) {
            System.out.printf("ERROR: %s could not be made.", transactionType);
        }

    }

    private static void showLedgerMenu() {
        for (boolean loop = true; loop; ) {
            System.out.println("\nLEDGER MENU");
            System.out.println("A) View All");
            System.out.println("D) View Deposits");
            System.out.println("P) View Payments");
            System.out.println("R) View Reports");
            System.out.println("H) Home");
            try {
                switch (promptUserLine().toUpperCase()) {
                    case "A" -> showAllTransactions();
                    case "D" -> showDepositTransactions();
                    case "P" -> showDebitTransactions();
                    case "R" -> showReportsMenu();
                    case "H" -> loop = false;
                    default  -> System.out.println("ERROR: Ledger menu, invalid input");
                }
            } catch (Exception e) {
                loop = false;
                System.out.println("ERROR: Ledger menu, exception occurred");
                e.printStackTrace();
            }
        }
    }

    private static void showTransaction(Transaction transaction) {
        System.out.printf("%s  $ %13.2f,  %s,  %s%n",
                transaction.getDateTime().format(dateTimeFormatter),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getVendor()
        );
    }

    private static void showAllTransactions() {
        System.out.println();
        for (Transaction transaction : transactions) {
            showTransaction(transaction);
        }
    }

    private static void showDepositTransactions() {
        System.out.println();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                showTransaction(transaction);
            }
        }
    }

    private static void showDebitTransactions() {
        System.out.println();
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                showTransaction(transaction);
            }
        }
    }

    private static void showReportsMenu() {

        for (boolean loop = true; loop; ) {

            System.out.println("\nREPORTS MENU");
            System.out.println("1) Month to Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year to Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by vendor");
            System.out.println("0) Back");

            try {

                int userChoice = promptUserInt();

                LocalDate now = LocalDate.now();
                LocalDate earliest;

                switch (userChoice) {
                    case 1  -> showReportsByDate(
                            now.withDayOfMonth(1),
                            now
                    );
                    case 2  -> showReportsByDate(
                            earliest = now.withMonth(now.getMonthValue() - 1).withDayOfMonth(1),
                            earliest.withDayOfMonth(earliest.lengthOfMonth())
                    );
                    case 3  -> showReportsByDate(
                            now.withDayOfYear(1),
                            now
                    );
                    case 4  -> showReportsByDate(
                            earliest = now.withYear(now.getYear() - 1).withDayOfYear(1),
                            earliest.withDayOfYear(earliest.lengthOfYear())
                    );
                    case 5  -> showReportsByVendor(promptUserLine("Enter a vendor: "));
                    case 0  -> loop = false;
                    default -> System.out.println("ERROR: Reports menu, invalid input");
                }

            } catch (Exception e) {
                loop = false;
                System.out.println("ERROR: Reports menu, exception occurred");
                e.printStackTrace();
            }

        }

    }

    private static void showReportsByDate(LocalDate earliest, LocalDate latest) {

        System.out.printf("%nTRANSACTION REPORT (%s - %s):%n",
                earliest.format(dateFormatter),
                latest.format(dateFormatter)
        );

        float balance = 0;

        for (Transaction transaction : transactions) {
            if (!transaction.getDateTime().toLocalDate().isBefore(earliest) && !transaction.getDateTime().toLocalDate().isAfter(latest)) {
                balance += transaction.getAmount();
                showTransaction(transaction);
            }
        }

        System.out.printf("Balance: $ %.2f%n", balance);

    }

    private static void showReportsByVendor(String vendor) {

        System.out.printf("%nTRANSACTION REPORT (Vendor: %s):%n", vendor);

        float balance = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                showTransaction(transaction);
            }
        }

        System.out.printf("Balance: $ %.2f%n", balance);

    }



    private static int promptUserInt() {
        int userInput = inputScanner.nextInt();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return userInput;
    }

    private static int promptUserInt(String prompt) {
        System.out.print(prompt);
        int userInput = inputScanner.nextInt();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return userInput;
    }

    private static float promptUserFloat() {
        float userInput = inputScanner.nextFloat();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return userInput;
    }

    private static float promptUserFloat(String prompt) {
        System.out.print(prompt);
        float userInput = inputScanner.nextFloat();
        inputScanner.nextLine(); //consume leftover newline char to prevent headache
        return userInput;
    }

    private static String promptUserLine() {
        return inputScanner.nextLine();
    }

    private static String promptUserLine(String prompt) {
        System.out.print(prompt);
        return inputScanner.nextLine();
    }

}
