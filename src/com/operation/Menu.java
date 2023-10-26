package com.operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Menu {
    
    static Scanner sc = new Scanner(System.in);
    
    protected static void addNewTransactionData() throws IOException {
        FileWriter dbWriter;
        BufferedWriter dbBufferedWriter;
        
        try {
            // argumen "true" maksudnya file yg di write di append (ditambahin) ke -
            // bkn ngapus semuanya trus nulis ulang
            dbWriter = new FileWriter("transaction-data.csv", true);
            dbBufferedWriter = new BufferedWriter(dbWriter);
        } catch (IOException e) {
            System.err.println("Database not found\nPlease Add Database First!");
            return;
        }
        
        // Input
        String tgl, name, food, paymentMethod;
        boolean isInputEmpty;

        do {
            System.out.print("Transaction date (dd mon yyyy): ");
            tgl = getDateFormat();
            System.out.print("Name: ");
            name = sc.nextLine();
            System.out.print("Food: ");
            food = sc.nextLine();
            System.out.print("Payment method: ");
            paymentMethod = sc.nextLine();
            if (tgl.isEmpty() || name.isEmpty() || food.isEmpty() || paymentMethod.isEmpty()) {
                isInputEmpty = true;
                System.out.println("All data should be filled\n");
            } else {
                isInputEmpty = false;
            }
        } while (isInputEmpty);

        System.out.println("\n-----------------------");
        System.out.println("Transaction Data Added!");
        System.out.println("-----------------------\n");

        // masukkin input ke database
        dbBufferedWriter.write(tgl+","+name+","+food+","+paymentMethod);
        dbBufferedWriter.flush();
        dbBufferedWriter.newLine();

        // close buffer
        dbBufferedWriter.close();
    }   


    protected static void deleteTransactionData() throws IOException {
        // Instasiasi database
        File db = new File("transaction-data.csv");
        // Pake reader aja soalnya emg cuma buat read
        FileReader dbReader = new FileReader(db);
        BufferedReader dbBufferedReader = new BufferedReader(dbReader);

        // Instasiasi tmp database yg bakal di write
        File tempDB = new File("temp-db.csv");
        /* 
        * 1. pake writer buat "hapus" data tmp, caranya dengan nge copy (write) isi database asal
        *    terus nge skip baris yg mau dihapus sehingga baris yg mau dihapus ga dipindahin ke database tmp
        * 2. database asal di delete (ada fungsi delete di objek File, makanya dipake), trus database tmp
        *    di rename (salah satu fungsi File jg) jadi nama database asal
        */
        FileWriter dbWriter = new FileWriter(tempDB);
        BufferedWriter dBufferedWriter = new BufferedWriter(dbWriter);

        // kasih liat databasenya
        showAllDatabase();

        // take user input
        System.out.print("number of data you want to delete: ");
        //Scanner sc = new Scanner(System.in);
        while (!sc.hasNextInt()) {
            System.out.println("input must be a number: ");
            sc.next();
        }

        int numberToDelete = sc.nextInt();

        Utility.clearScreen();

        // loop buat misahin data yg mau dihapus
        boolean isFound = false; // assign false dlu karena blom nyari
        int dataNumber = 0; // variabel nomor data

        String dataReader = dbBufferedReader.readLine(); // read data baris pertama
        Utility.printHeaderOutput(); // header output biar bagus

        while (dataReader != null) {
            // type safety
            if (dataReader.isBlank() || dataReader == null) {
                Utility.printFooterOutput();
                System.out.println("there is empty line of data, exitting program...\n");
                System.exit(1);
            }

            boolean isDelete = false;
            dataNumber++;
            // kalo nomor datanya cocok sama input, tampilin baris datanya, trus tanya mau hapus apa ga
            if (numberToDelete == dataNumber) {
                isFound = true;

                System.out.printf("|%-2d|", dataNumber);
                showDataList(dataReader);
                Utility.printFooterOutput(); // footer biar bagus

                isDelete = Utility.getYesOrNo("are you sure you want to delete this data (y/n): ");
            }

            // kalo yes (mau hapus, lakukan ini (skip datanya))
            if (isDelete) {
                // Sysout aja, soalnya emg datanya didiemin
                System.out.printf("data number %d has been deleted\n\n", dataNumber);
            } else {
                // kalo no (gajadi hapus data) ya tulis lg aja datanya
                // else ini jg kondisi buat baris data yg lain yg nomornya gacocok sama nomor yg mau di delete
                dBufferedWriter.write(dataReader);
                dBufferedWriter.flush();
                dBufferedWriter.newLine();
            }
            
            dataReader = dbBufferedReader.readLine(); // read baris selanjutnya
        }

        if (!isFound) {
            System.out.println("data is not found");
        }

        // delete databse asal
        db.delete();

        // rename tmp database jadi database asal
        tempDB.renameTo(db);

        dbBufferedReader.close(); dBufferedWriter.close();
    }


    private static String getDateFormat() {
        // bikin format tanggal buat si String tgl
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        boolean isDateFormatValid;
        
        String tgl = sc.nextLine();

        do {
            isDateFormatValid = false;
            try { // nyoba parse (ubah si string jadi tanggal, cocok ama formatnya apa engga)
                df.parse(tgl); // kalo cannot parse, di catch di bawah
                isDateFormatValid = true;
            } catch (ParseException e) {
                System.err.println("invalid date format");
                System.out.print("Transaction date (dd MMM yyyy): ");
                tgl = sc.nextLine();
            }
        } while (!isDateFormatValid);

        return tgl;
    }


    private static void searchByKeywords(String[] keywords) throws IOException {
        FileReader dbReader;
        BufferedReader dbBufferedReader;

        // handle exception biar program jalan walaupun database kaga ada
        try {
            dbReader = new FileReader("transaction-data.csv");
            dbBufferedReader = new BufferedReader(dbReader);
        } catch (IOException e) {
            System.err.println("Database not found\nPlease Add Database First!");
            //biar buffer dibawahnya ga cacing merah (bilangnya blom initialized)
            return;
        }
        
        // print header
        Utility.printHeaderOutput();

        // read baris pertama
        String dataReader = dbBufferedReader.readLine(); // read baris pertama
        int dataNumber = 0;
        
        // Coba cari datanya selama masih ada
        while (dataReader != null) { // di atas tadi udh di read, diliat null apa ga, kalo ga null, lanjut
            // type safety
            if (dataReader.isBlank() || dataReader == null) {
                Utility.printFooterOutput();
                System.out.println("there is empty line of data, exitting program...\n");
                System.exit(1);
            }
            
            // assign true ke var bool
            boolean isExist = true;
            
            // looping per kata (word)
            for (String word : keywords) {
                /*
                kalo setiap kata cocok sama dataReader, berarti dia bakal return true && true
                * contoh: keywordsnya 03 agustus dan itu ada di database baris ke 3
                * Read baris pertama (yg ga ke print)
                * looping 1 (kata pertama) -> "03", isExist = true && dataReadernya ga cocok/false (karena baris pertama itu tanggal 01)
                * looping 2 (kata kedua) -> "agustus", isExist = false (gara2 looping kata pertama tadi) && dataReader cocok/true
                * karena false && true -> hasilnya false -> data ga ke print -> read selanjutnya
                
                * (lgsg skip) baris ketiga (yg ke print)
                * looping 1 (kata pertama) -> "03", isExist = true && dataReadernya cocok/true
                * looping 2 (kata kedua) -> "agustus", isExist = true && dataReader cocok/true
                * true && true -> masuk kondisi if -> ke print
                **/
                isExist = isExist && dataReader.toLowerCase().contains(word.toLowerCase());
            }

            if (isExist) {
                dataNumber++;
                
                System.out.printf("|%-2d|", dataNumber);
                showDataList(dataReader);
            }

            // read baris selanjutnya
            dataReader = dbBufferedReader.readLine();
        }   

        // footer
        Utility.printFooterOutput();

        dbBufferedReader.close();
    }


    protected static void searchTransactionKeywords() throws IOException {
        //Scanner sc = new Scanner(System.in);
        
        System.out.print("Search transaction by keywords: ");

        // scan keywords (bisa beberapa kata)
        String strFinder = sc.nextLine();

        // split jadi perkata
        String[] keywords = strFinder.split("\\s+"); //split di whitespace

        // masuk fungsi bawahnya
        searchByKeywords(keywords);
    }


    protected static void showAllDatabase() throws IOException {
        FileReader dbReader;
        BufferedReader dbBufferedReader;

        // handle exception biar program jalan walaupun database kaga ada
        try {
            dbReader = new FileReader("transaction-data.csv");
            dbBufferedReader = new BufferedReader(dbReader);
        } catch (IOException e) {
            System.err.println("Database not found\nPlease Add Database First!");
            //biar buffer dibawahnya ga cacing merah (bilangnya blom initialized)
            return;
        }

        // header output
        Utility.printHeaderOutput();

        String dataReader = dbBufferedReader.readLine();
        int dataNumber = 0;
        
        while (dataReader != null) {
            if (dataReader.isBlank() || dataReader == null) {
                Utility.printFooterOutput();
                System.out.println("there is empty line of data, exitting program...\n");
                System.exit(1);
            }

            dataNumber++;

            System.out.printf("|%-2d|", dataNumber);
            showDataList(dataReader);
            
            dataReader = dbBufferedReader.readLine();
        }

        Utility.printFooterOutput();

        dbBufferedReader.close();
    }


    private static void showDataList(String dataReader) throws IOException {
        StringTokenizer strToken = new StringTokenizer(dataReader, ",");
        try {
            //tgl
            System.out.printf("%-16s| ", strToken.nextToken());
            //--------------------------------------------------------
            System.out.printf("%-10s|", strToken.nextToken());
            System.out.printf("%-15s|", strToken.nextToken());
            System.out.printf("%-10s|", strToken.nextToken());
            System.out.println();
        } catch (NoSuchElementException e) {
            System.err.println("null element");
            return;
        }
    }


    // protected static void updateTransactionData() throws IOException {
    //     // Instasiasi database
    //     File db = new File("-");
    //     // Pake reader aja soalnya emg cuma buat read
    //     FileReader dbReader = new FileReader(db);
    //     BufferedReader dbBufferedReader = new BufferedReader(dbReader);

    //     // Instasiasi tmp database yg bakal di write
    //     File tempDB = new File("temp_-");
    //     /*
    //     * Mirip sama delete data, cmn kalo nomor datanya cocok, datanya diubah terus masukin ke tmp db
    //     * tmp db nerima data baru yg diupdate sama data lain yg ga diupdate
    //     * trus jadiin tmp db jadi database asal (kaya delete data)
    //     */
    //     FileWriter dbWriter = new FileWriter(tempDB);
    //     BufferedWriter dBufferedWriter = new BufferedWriter(dbWriter);

    //     // kasih liat databasenya
    //     showAllDatabase();

    //     // take user input
    //     System.out.print("number of data you want to update: ");
    //    // Scanner sc = new Scanner(System.in);
    //     while (!sc.hasNextInt()) {
    //         System.out.println("input must be a number: ");
    //         sc.next();
    //     }

    //     int numberToUpdate = sc.nextInt();

    //     Utility.clearScreen();
        
    //     Utility.printHeaderOutput(); // header output biar bagus
        
    //     String dataReader = dbBufferedReader.readLine();
    //     int dataNumber = 0;
    //     while (dataReader != null) {
    //         // type safety
    //         if (dataReader.isBlank() || dataReader == null) {
    //             Utility.printFooterOutput();
    //             System.out.println("there is empty line of data, exitting program...\n");
    //             System.exit(1);
    //         }

    //         boolean isUpdate = false;
    //         dataNumber++;
    //         // kalo nomor datanya cocok sama input, tampilin baris datanya
    //         if (numberToUpdate == dataNumber) {
    //             System.out.printf("|%-2d|", dataNumber);
    //             showDataList(dataReader);
    //             Utility.printFooterOutput(); // footer biar bagus

    //             String[] dataColumn = {"transaction date", "name", "food", "payment"};
    //             String[] tmpDataColumn = new String[dataColumn.length];
    //             StringTokenizer strToken = new StringTokenizer(dataReader, ",");
    //             String formerData;

    //             for (int i = 0; i < dataColumn.length; i++) {
    //                 isUpdate = Utility.getYesOrNo("Do you want to update "+dataColumn[i]+" data (y/n): ");
    //                 formerData = strToken.nextToken();

    //                 if (isUpdate) {
    //                     System.out.print("Input new "+dataColumn[i]+" data: ");
    //                     sc = new Scanner(System.in);

    //                     if (i == 0) { // kolom[0] -> tanggal
    //                         tmpDataColumn[0] = getDateFormat();
    //                         continue;
    //                     }
    //                     tmpDataColumn[i] = sc.nextLine();
    //                 } else {
    //                     tmpDataColumn[i] = formerData;
    //                 }
    //             }

    //             // reset token
    //             strToken = new StringTokenizer(dataReader, ",");
    //             //tgl
    //             System.out.printf("%-16s -> %-16s\n", strToken.nextToken(), tmpDataColumn[0]);
    //             //--------------------------------------------------------
    //             System.out.printf("%-16s -> %-16s\n", strToken.nextToken(), tmpDataColumn[1]);
    //             System.out.printf("%-16s -> %-16s\n", strToken.nextToken(), tmpDataColumn[2]);
    //             System.out.printf("%-16s -> %-16s\n", strToken.nextToken(), tmpDataColumn[3]);
    //         } else {
    //             dBufferedWriter.write(dataReader);
    //         }
            
    //     }

    //     dbBufferedReader.close(); dBufferedWriter.close();
    // }

}
