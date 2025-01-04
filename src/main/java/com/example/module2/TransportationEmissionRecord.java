package com.example.module2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "transportation_emission_records")
public class TransportationEmissionRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String transportationType;
    private double quantity;
    private double co2Emission;
    private String date; // The date field

    // Constructor including all fields
    public TransportationEmissionRecord(String transportationType, double quantity, double co2Emission, String date) {
        this.transportationType = transportationType;
        this.quantity = quantity;
        this.co2Emission = co2Emission;
        this.date = date;
    }

    // Getter and Setter for id (generated automatically by Room)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for transportationType
    public String getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(String transportationType) {
        this.transportationType = transportationType;
    }

    // Getter and Setter for quantity
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for co2Emission
    public double getCo2Emission() {
        return co2Emission;
    }

    public void setCo2Emission(double co2Emission) {
        this.co2Emission = co2Emission;
    }

    // Getter and Setter for date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Method to get the day of the week from the date (0 = Sunday, 1 = Monday, ...)
    public int getDay() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); // Adjusted for dd/MM/yyyy format
        try {
            Date parsedDate = format.parse(date);
            SimpleDateFormat dayFormat = new SimpleDateFormat("u"); // 'u' gives day of the week (1 = Monday, 7 = Sunday)
            String dayString = dayFormat.format(parsedDate);
            return Integer.parseInt(dayString) - 1; // Adjust for your needs (0 = Sunday, 6 = Saturday)
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }
    }

    // Method to get the CO2 emission amount for this record
    public double getEmissionAmount() {
        return co2Emission * quantity; // CO2 emission depends on quantity
    }

    // Method to get the month from the date (0 = January, 1 = February, ...)
    public int getMonth() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); // Adjusted for dd/MM/yyyy format
        try {
            Date parsedDate = format.parse(date);
            SimpleDateFormat monthFormat = new SimpleDateFormat("M"); // 'M' gives the month (1 = January, 12 = December)
            String monthString = monthFormat.format(parsedDate);
            return Integer.parseInt(monthString) - 1; // Adjust for your needs (0 = January, 11 = December)
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }
    }

    @Override
    public String toString() {
        return "TransportationEmissionRecord{" +
                "id=" + id +
                ", transportationType='" + transportationType + '\'' +
                ", quantity=" + quantity +
                ", co2Emission=" + co2Emission +
                ", date='" + date + '\'' +
                '}';
    }
}

