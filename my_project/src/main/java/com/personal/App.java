package com.personal;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    private static final int MAX_ATTEMPTS = 3;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library library = new Library();
        int attempts = 0;
    
        System.out.println("Welcome to the library.");
        System.out.println("=======================");
    
        int intInput= 0;
        while (attempts < MAX_ATTEMPTS) {
            try {
                    System.out.println("Please make a selection:");
                    System.out.println("(1): Search books   (2): Add a book   (3): Remove a book   (4): Exit");
                    intInput = sc.nextInt();
                    sc.nextLine();
                    switch (intInput) {
                        case 1:
                            library.printAllBooks(sc);
                            break;
                        case 2:
                            library.addBook(sc);
                            break;
                        case 3:
                            library.removeBook(sc);
                            break;
                        case 4:
                            System.out.println("Exiting...");
                            return;
                        default:
                            System.out.println("Invalid option. Please select again.");
                    }
            } catch (InputMismatchException e) {
                System.out.println("Invalid charecter, please enter 1,2,3 or 4.");
                attempts += 1;
                System.out.println(attempts);
                sc.next();
            }
        }
    if (attempts == MAX_ATTEMPTS) {
        System.out.println("Too many failed attempts, exiting the program.");
    }
    }
}
