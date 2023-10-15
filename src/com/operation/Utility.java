package com.operation;

import java.io.IOException;
import java.util.Scanner;


public class Utility {

    static Scanner sc = new Scanner(System.in);

    protected static void clearScreen() {
        System.out.print(String.format("\033[H\033[2J"));
    }


    protected static boolean getYesOrNo(String message) {
        System.out.print(message);
        String userInput = sc.next();

        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.err.print("invalid input, try again (y/n): ");
            userInput = sc.next();
        }

        /* 
        kalo input y, getYesOrNo bakal return true dan program tetep jalan
        sebaliknya kalo n, yg di return false dan program berhenti 
        ? gatau knp kalo scannernya di close, jadinya error pas mau input buat y/n
        * kemungkinan kalo scanner di close, scanner gabisa dipake buat loop selanjutnya (kalo program masih jalan)
        */
        return userInput.equalsIgnoreCase("y");
    }


    protected static void printFooterOutput() {
        System.out.println("------------------------------------------------------------");
    }


    protected static void printHeaderOutput() {
        // header output
        System.out.println("============================================================");
        System.out.printf("|%-58s|\n", "DATABASE");
        System.out.println("============================================================");
        System.out.printf("|%-2s|%-16s|%-11s|%-15s|%-10s|\n", "No", "Tgl", "Nama", "Food", "Payment");
        System.out.println("|----------------------------------------------------------|");
    }


    public static void printMainMenu() throws IOException {
        Scanner sc = new Scanner(System.in);
        String userInput;
        boolean isContinue;

        do {
            Utility.clearScreen();
            System.out.println("================================");
            System.out.printf("|%-30s|\n", "Cathering App");
            System.out.println("================================");
            System.out.printf("|%-30s|\n", "1. View Transaction Data");
            System.out.printf("|%-30s|\n","2. Search Transaction Data");
            System.out.printf("|%-30s|\n","3. Add New Transaction Data");
            System.out.printf("|%-30s|\n","4. Remove Transactions Data");
            System.out.printf("|%-30s|\n","5. Update Transactions Data");
            System.out.printf("|%-30s|\n","6. Exit");
            System.out.println("================================");
            System.out.print("choose menu > ");

            // validate input must be int
            while (!sc.hasNextInt()) { // masukin input, sama liat ada int apa ga
                System.out.print("input must be integer: ");
                // input yg udh dimasuki ke scanner diliat (sc.next) dia integer apa bukan, trus cek lg ke kondisi loop
                sc.next();
            }

            // kalo input dh bener integer, menuChoosenya == int yg udh dipilih tadi 
            // sc.nextInt() berarti dia ngebaca integer yg udh input di atas
            userInput = sc.next();

            // clear screen biar menu yg ditampilin bersih
            Utility.clearScreen();

            switch (userInput) {
                case "1":
                    Menu.showAllDatabase();
                    break;

                case "2":
                    Menu.searchTransactionKeywords();
                    break;

                case "3":
                    Menu.addNewTransactionData();
                    break;

                case "4":
                    Menu.deleteTransactionData();
                    break;

                // case "5":
                //     Menu.updateTransactionData();
                //     break;

                case "6":
                    System.out.println("App closed");
                    System.exit(0);

                default:
                    System.out.println("invalid input");
                    break;
            }

            // setiap abis kelar sama pilihan menu (break), masuk ke sini buat nanya mau lanjut apa ga
            isContinue = Utility.getYesOrNo("Do you want to continue (y/n): ");
            // kalo lanjut -> isContinue = true dan kondisi while masih berlaku
            
        } while (!userInput.equals("6") && isContinue);

        sc.close();
    }

}