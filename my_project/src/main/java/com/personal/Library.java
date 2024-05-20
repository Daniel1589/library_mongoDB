package com.personal;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

public class Library {
    private MongoDatabase database;
    private static final int MAX_ATTEMPTS = 3;
    private static boolean validInput = false;
    private static int counter = 0;


    public Library() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("library");
        } catch (MongoException e) {
            System.out.println("Failed to connect to MongoDB server." + "\n" + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("An unknown error occurred. (Line 30)" + "\n" + e.getMessage());
        }
    }

    public void addBook(Scanner sc) {
        System.out.println("addBook()");
        while (isInputValid()) {
            try {
                System.out.print("Title: ");
                String title = sc.nextLine();
                System.out.print("Author: ");
                String author = sc.nextLine();
                System.out.print("Genre: ");
                String genre = sc.nextLine();
                System.out.print("Pages: ");
                int pages = sc.nextInt();
                sc.nextLine();
                System.out.print("Publish year: ");
                int publishYear = sc.nextInt();
                sc.nextLine();
                MongoCollection<Document> bookCollection = database.getCollection("books");
                Document bookDocument = new Document("title", title)
                .append("author", author)
                .append("genre", genre)
                .append("pages", pages)
                .append("publishYear", publishYear);
                bookCollection.insertOne(bookDocument);
                System.out.println("Book added.");
                validInput = true;
                } catch (InputMismatchException  e ) {
                    System.out.println("Invalid user format. Please enter a valid number.");
                    counter += 1;
                } catch (MongoException e ) {
                    System.out.println("An error occurred while trying to add a book to the database.");
                    return;
                } catch (Exception e) {
                    System.out.println("An unknown error occurred." + "\n" + e.getMessage());
                    return;
                }
        }
        maxAttemptsReached();
        resetState();
    }

    public void removeBook(Scanner sc) {
        System.out.print("Enter the book ID: ");
        String bookId = sc.nextLine();

        while (isInputValid()) {
            try {
                if (!ObjectId.isValid(bookId)) {
                    System.out.println("Invalid book ID format. Please enter a valid ObjectId.");
                    return;
                }
    
            MongoCollection<Document> bookCollection = database.getCollection("books");
    
            ObjectId objectId = new ObjectId(bookId);
            DeleteResult deleteResult = bookCollection.deleteOne(Filters.eq("_id", objectId));

            if (deleteResult.getDeletedCount() > 0) {
                System.out.println("Book with the ID of: " + bookId + " Has been successfully removed.");
                validInput = true;
            } else {
                System.out.println("No book with the ID of: " + bookId + " has been found. aborting...");
                return;
            }
    
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid bookID format. please enter a valid ObjectId.");
                counter += 1;
            } catch (MongoException e) {
                System.out.println("An error occurred while trying to remove a book from the database.");
                return;
            } catch (Exception e) {
                System.out.println("An unknown error occurred." + "\n" + e.getMessage());
                return;
            }
        }
        maxAttemptsReached();
        resetState();
    }

    public void printAllBooks(Scanner sc) {
        MongoCollection<Document> bookCollection = database.getCollection("books");
        Document projection = new Document("title", 1);
        MongoCursor<Document> cursor = bookCollection.find().projection(projection).iterator();

         while (isInputValid()) {
            try {
                System.out.println("printALlBooks()");
                System.out.println("Do you wish to print all the books stored?"
                +"\n1: Yes\t2: No");
                int intInput = sc.nextInt();
        
                switch (intInput) {
                    case 1:
                    if (cursor.hasNext()) {
                        while (cursor.hasNext()) {
                            Document book = cursor.next();
                            String title = book.getString("title");
                            System.out.println("Title: " + title);
                        }
                    } else {
                        System.out.println("The library is currently empty.");
                    }
                    validInput = true;
                    break;
                    case 2:
                    System.out.println("Exiting...");
                    validInput = true;
                    break;
                    default:
                    System.out.println("Invalid choice. Please enter 1 for YES or 2 for NO.");
                    counter += 1;
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid charecter, please use numerals.");
                counter += 1;
                sc.next();
            }
        }
        maxAttemptsReached();
        resetState();
        cursor.close();
    }
    public void resetState() {
        validInput = false;
        counter = 0;
    }
    public boolean isInputValid() {
        return !validInput && counter < MAX_ATTEMPTS;
    }
    public boolean maxAttemptsReached() {
        if (counter == MAX_ATTEMPTS) {
            System.out.println("Too many failed attempts, exiting...");
            return true;
        }
        return false;
    }
}
