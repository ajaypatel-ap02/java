package com.Ajay.StudentInfo;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StudentInfo {
    int rollNo = 1;
    String name = null;
    String mobile = "1";

    // Database connection method
    Connection getConnect() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String url = "jdbc:oracle:thin:@localhost:1521";
            String user = "system";
            String pass = "ajay";
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Method to fetch and display student data
    public void accessData() {
        String access = "SELECT * FROM student ORDER BY id";
        try (Connection connection = getConnect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(access)) {

            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " : " + resultSet.getString(2) + " : " + resultSet.getString(3));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to insert new student data
    public void insertData() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello Student info : Insert the student data");

        try (Connection connection = getConnect()) {
            System.out.println("Enter your name");
            name = scanner.nextLine();

            // Enter mobile number, ensure it's a 10-digit number
            System.out.println("Enter mobile number (10 digits):");
            mobile = scanner.nextLine();

            while (mobile.length() != 10) {
                System.out.println("Mobile number must be 10 digits. Please enter again:");
                mobile = scanner.nextLine();
            }
            // Check if the mobile number already exists in the database
            String checkMobileQuery = "SELECT COUNT(*) FROM student WHERE mobile = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkMobileQuery)) {
                checkStmt.setString(1, mobile);
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        System.out.println("This mobile number already exists. Please enter a different number.");
                        return; // Exit the method and prompt the user again
                    }
                }
            }

            // Get the next roll number (based on existing data)
            String query = "SELECT MAX(id) FROM student";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    rollNo = resultSet.getInt(1) + 1; // Increment the max id found
                    System.out.println("Next Roll No: " + rollNo);
                }
            }

            // Insert the new student record
            String insert = "INSERT INTO student (id, name, mobile) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setInt(1, rollNo);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, mobile);
                preparedStatement.executeUpdate();
                System.out.println("Student data inserted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Not Connected: " + e.getMessage());
        }
//        scanner.close();
    }
    public void deleteData(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the roll no. of the student to delete the data");
        try {
            rollNo = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a numeric roll number."+e.getMessage());
            return;
        }
        String access = "SELECT * FROM student WHERE id = ?";
        String delete = "DELETE FROM student WHERE id = ?";

        try(Connection connection = getConnect();
                PreparedStatement selectstmt = connection.prepareStatement(access);
                PreparedStatement deletestmt = connection.prepareStatement(delete);){

            selectstmt.setInt(1,rollNo);
            ResultSet resultSet = selectstmt.executeQuery();
            if(resultSet.next()) {
                deletestmt.setInt(1,rollNo);
                int rowDeleted = deletestmt.executeUpdate();
                if(rowDeleted>0){
                    System.out.println("Row deleted successfully for roll no "+rollNo);
                }else{
                    System.out.println("Failed to delete the record");
                }
            }else{
                System.out.println("NO student with entered roll no ");
            }
        }catch (SQLException e){
            System.out.println("Error in deleting data "+e.getMessage());
        }
    }

    // Main operation loop
    public void operation() {
        Scanner scanner = new Scanner(System.in);
        try{
            while (true) {

                System.out.println("Welcome to Student Information");
                System.out.println("Enter the operation number:");
                System.out.println("1. Display data\n2. Insert data\n3. Delete\n4. Exit");

                int op;
                try {
                    op = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    scanner.next(); // Clear invalid input
                    continue;
                }
                switch (op) {
                    case 1:
                        accessData();
                        break;
                    case 2:
                        insertData();
                        break;
                    case 3:
                        deleteData();
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid input");
                }

            }
        }finally {
                scanner.close();
            System.out.println("Scanner Problem");
        }

    }


}
