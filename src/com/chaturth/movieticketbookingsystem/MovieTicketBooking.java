package com.chaturth.movieticketbookingsystem;

import java.util.Scanner;

public class MovieTicketBooking {

    static boolean[][] seats = new boolean[10][10];
    static Scanner sc = new Scanner(System.in);

    static final int BALCONY = 450;
    static final int FIRST_CLASS = 350;
    static final int SECOND_CLASS = 200;

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n========================================");
            System.out.println("       MOVIE TICKET BOOKING SYSTEM");
            System.out.println("========================================");
            System.out.println("1. View Seats");
            System.out.println("2. Book Tickets");
            System.out.println("3. Cancel Ticket");
            System.out.println("4. View Offers");
            System.out.println("5. Movie Rating");
            System.out.println("6. Exit");
            System.out.println("----------------------------------------");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    showSeats();
                    break;

                case 2:
                    bookTickets();
                    break;

                case 3:
                    cancelTicket();
                    break;

                case 4:
                    showOffers();
                    break;

                case 5:
                    showRating();
                    break;

                case 6:
                    System.out.println("\nThank you for choosing our cinema!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 6);

        sc.close();
    }

    static void showSeats() {

        System.out.println("\n============== SEAT AVAILABILITY ==============");
        System.out.println("A = Available     B = Booked");
        System.out.println("Rows 1-2: Balcony (Rs. 450)");
        System.out.println("Rows 3-5: First Class (Rs. 350)");
        System.out.println("Rows 6-10: Second Class (Rs. 200)");
        System.out.println();

        for (int i = 0; i < 10; i++) {
            System.out.print("Row " + (i + 1) + "  : ");

            for (int j = 0; j < 10; j++) {
                System.out.print(seats[i][j] ? "B  " : "A  ");
            }

            if (i < 2) {
                System.out.print(" Balcony");
            } else if (i < 5) {
                System.out.print(" First Class");
            } else {
                System.out.print(" Second Class");
            }

            System.out.println();
        }

        System.out.println("================================================");
    }

    static void bookTickets() {

        System.out.println("\n============== BOOK TICKETS ==============");
        System.out.println("Rows 1-2  : Balcony - Rs. 450");
        System.out.println("Rows 3-5  : First Class - Rs. 350");
        System.out.println("Rows 6-10 : Second Class - Rs. 200");

        System.out.print("\nEnter number of tickets (1-10): ");
        int count = sc.nextInt();

        if (count < 1 || count > 10) {
            System.out.println("Please select between 1 and 10 tickets.");
            return;
        }

        int[] rows = new int[count];
        int[] numbers = new int[count];
        int total = 0;

        for (int i = 0; i < count; i++) {

            showSeats();

            System.out.println("\nSelect Seat " + (i + 1) + " of " + count);

            System.out.print("Enter row number (1-10): ");
            int row = sc.nextInt();

            System.out.print("Enter seat number (1-10): ");
            int seat = sc.nextInt();

            if (row < 1 || row > 10 || seat < 1 || seat > 10) {
                System.out.println("Invalid seat selection. Try again.");
                i--;
                continue;
            }

            if (seats[row - 1][seat - 1]) {
                System.out.println("Seat already booked. Select another seat.");
                i--;
                continue;
            }

            boolean duplicate = false;

            for (int j = 0; j < i; j++) {
                if (rows[j] == row && numbers[j] == seat) {
                    duplicate = true;
                    break;
                }
            }

            if (duplicate) {
                System.out.println("You already selected this seat. Try another.");
                i--;
                continue;
            }

            rows[i] = row;
            numbers[i] = seat;

            if (row <= 2) {
                total += BALCONY;
            } else if (row <= 5) {
                total += FIRST_CLASS;
            } else {
                total += SECOND_CLASS;
            }
        }

        for (int i = 0; i < count; i++) {
            seats[rows[i] - 1][numbers[i] - 1] = true;
        }

        System.out.println("\n========================================");
        System.out.println("           BOOKING CONFIRMED");
        System.out.println("========================================");
        System.out.println("Movie : KGF Chapter 2");
        System.out.println("Tickets: " + count);
        System.out.println("----------------------------------------");
        System.out.println("Selected Seats :");

        for (int i = 0; i < count; i++) {

            String className;
            int price;

            if (rows[i] <= 2) {
                className = "Balcony";
                price = BALCONY;
            } else if (rows[i] <= 5) {
                className = "First Class";
                price = FIRST_CLASS;
            } else {
                className = "Second Class";
                price = SECOND_CLASS;
            }

            System.out.println("Row " + rows[i] + ", Seat " + numbers[i]
                    + " - " + className + " - Rs. " + price);
        }

        System.out.println("----------------------------------------");
        System.out.println("Total Amount : Rs. " + total);
        System.out.println("========================================");
        System.out.println("   ENJOY YOUR MOVIE!");
        System.out.println("========================================");
    }
    static void cancelTicket() {

        System.out.println("\n============== CANCEL TICKET ==============");

        System.out.print("Enter row number (1-10): ");
        int row = sc.nextInt();

        System.out.print("Enter seat number (1-10): ");
        int seat = sc.nextInt();

        if (row < 1 || row > 10 || seat < 1 || seat > 10) {
            System.out.println("Invalid row or seat number!");
        } else if (!seats[row - 1][seat - 1]) {
            System.out.println("This seat is not booked!");
        } else {
            seats[row - 1][seat - 1] = false;

            System.out.println("\n========================================");
            System.out.println("          CANCELLATION SUCCESSFUL");
            System.out.println("========================================");
            System.out.println("Row Number  : " + row);
            System.out.println("Seat Number : " + seat);
            System.out.println("Status      : Ticket Cancelled");
            System.out.println("========================================");
        }
    }

    static void showOffers() {

        System.out.println("\n============== SPECIAL OFFERS ==============");
        System.out.println("Balcony     : Rs. 500 -> Rs. 450");
        System.out.println("First Class : Rs. 350 - No Offer");
        System.out.println("Second Class: Rs. 200 - No Offer");
        System.out.println("============================================");
    }

    static void showRating() {

        System.out.println("\n============== MOVIE RATING ==============");
        System.out.println("Movie          : KGF Chapter 2");
        System.out.println("Current Rating : 4.5 / 5");

        System.out.print("Enter your rating (1-5): ");
        int rating = sc.nextInt();

        if (rating >= 1 && rating <= 5) {
            System.out.println("\nThank you for rating the movie!");
            System.out.println("Your Rating: " + rating + " / 5");
        } else {
            System.out.println("Invalid rating. Enter a number from 1 to 5.");
        }
    }
}
