package com.chaturth.movieticketbookingsystem;

public class MovieTicketBooking {

    public static void main(String[] args) {

        int[][] seats = new int[5][5];

        for (int[] row : seats) {
            for (int seat : row) {
                System.out.print(seat + " ");
            }
            System.out.println();
        }

        }
    }
}

