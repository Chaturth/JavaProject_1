package com.chaturth.hositalappointment;

public class Patient {

    private int patientID;
    private String name;
    private int age;
    private String contactNumber;

    public Patient(int patientID, String name, int age, String contactNumber) {
        this.patientID = patientID;
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
    }

    public int getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void displayPatient() {
        System.out.println("Patient ID: " + patientID);
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Contact Number: " + contactNumber);
    }


}

