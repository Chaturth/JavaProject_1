package com.chaturth.hositalappointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Scanner;

public class HospitalAppointmentSystem {

    static HashMap<Integer, Patient> patients = new HashMap<>();
    static HashMap<Integer, Doctor> doctors = new HashMap<>();
    static HashMap<Integer, Appointment> appointments = new HashMap<>();

    static Scanner sc = new Scanner(System.in);

    static int appointmentCounter = 1;

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== Hospital Appointment Management System =====");
            System.out.println("1. Register Patient");
            System.out.println("2. Register Doctor");
            System.out.println("3. Book Appointment");
            System.out.println("4. View All Appointments");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerPatient();
                    break;

                case 2:
                    registerDoctor();
                    break;

                case 3:
                    bookAppointment();
                    break;

                case 4:
                    viewAppointments();
                    break;

                case 5:
                    System.out.println("Thank you for using the system.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 5);
    }

    static void registerPatient() {

        System.out.print("Enter Patient ID: ");
        int patientID = sc.nextInt();
        sc.nextLine();

        if (patients.containsKey(patientID)) {
            System.out.println("Patient ID already exists.");
            return;
        }

        System.out.print("Enter Patient Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Patient Age: ");
        int age = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Contact Number: ");
        String contactNumber = sc.nextLine();

        Patient patient = new Patient(patientID, name, age, contactNumber);
        patients.put(patientID, patient);

        System.out.println("Patient registered successfully.");
    }

    static void registerDoctor() {

        System.out.print("Enter Doctor ID: ");
        int doctorID = sc.nextInt();
        sc.nextLine();

        if (doctors.containsKey(doctorID)) {
            System.out.println("Doctor ID already exists.");
            return;
        }

        System.out.print("Enter Doctor Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Specialization: ");
        String specialization = sc.nextLine();

        Doctor doctor = new Doctor(doctorID, name, specialization);
        doctors.put(doctorID, doctor);

        System.out.println("Doctor registered successfully.");
    }

    static void bookAppointment() {

        if (patients.isEmpty() || doctors.isEmpty()) {
            System.out.println("Please register at least one patient and one doctor first.");
            return;
        }

        System.out.println("\nAvailable Patients:");

        for (Patient patient : patients.values()) {
            patient.displayPatient();
            System.out.println("----------------------");
        }

        System.out.print("Enter Patient ID: ");
        int patientID = sc.nextInt();
        sc.nextLine();

        Patient patient = patients.get(patientID);

        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.println("\nAvailable Doctors:");

        for (Doctor doctor : doctors.values()) {
            doctor.displayDoctor();
            System.out.println("----------------------");
        }

        System.out.print("Enter Doctor ID: ");
        int doctorID = sc.nextInt();
        sc.nextLine();

        Doctor doctor = doctors.get(doctorID);

        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        if (date.isBefore(LocalDate.now())) {
            System.out.println("Appointment date cannot be in the past.");
            return;
        }

        System.out.print("Enter Appointment Time (HH:MM, 24-hour format): ");
        LocalTime time = LocalTime.parse(sc.nextLine());

        for (Appointment appointment : appointments.values()) {

            if (appointment.getDoctor().getDoctorId() == doctorID
                    && appointment.getDate().equals(date)
                    && appointment.getTime().equals(time)
                    && appointment.getStatus().equals("BOOKED")) {

                System.out.println("This doctor's time slot is already booked.");
                return;
            }
        }

        Appointment appointment = new Appointment(
                appointmentCounter++, patient, doctor, date, time);

        appointments.put(appointment.getAppointmentId(), appointment);

        System.out.println("\nAppointment booked successfully!");
        appointment.displayAppointment();
    }

    static void viewAppointments() {

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\n========== Appointment Details ==========");

        for (Appointment appointment : appointments.values()) {
            appointment.displayAppointment();
            System.out.println("-----------------------------------------");
        }
    }
}




