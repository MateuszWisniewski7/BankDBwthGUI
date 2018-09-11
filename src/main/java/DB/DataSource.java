package DB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.BranchesTableModel;
import models.CustomersTableModel;
import models.TransactionsTableModel;

import java.sql.*;
import java.time.LocalDateTime;


public class DataSource {

    private static DataSource instance = new DataSource();
    public static DataSource getInstance() {
        return instance;
    }

    private ObservableList<ObservableList> data;

    private static final String DB_NAME = "bank";
    private static final String SERVER = "localhost:3306";
    private static final String CONNECTION_STRING = "jdbc:mysql://" + SERVER + "/?useSSL=false&useTimezone=true&serverTimezone=Europe/Warsaw";
    private static final String USER = "student";
    private static final String PASSWORD = "student";

    private static final String TABLE_BRANCHES = "branches";
    private static final String COLUMN_BRANCH_ID = "_id";
    private static final String COLUMN_BRANCH_NAME = "name";

    private static final String TABLE_CUSTOMERS = "customers";
    private static final String COLUMN_CUSTOMER_ID = "_id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_BRANCH_ID = "branch";

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANSACTION_DATE = "_date";
    private static final String COLUMN_TRANSACTION_VALUE = "_value";
    private static final String COLUMN_TRANSACTION_CUSTOMER_ID = "customer";


    private Connection conn;

    // opening connection with server, creating database if it's not created and tables

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);

            String createDB = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            String createBranches = "CREATE TABLE IF NOT EXISTS " + TABLE_BRANCHES + " ("
                    + COLUMN_BRANCH_ID + " int NOT NULL AUTO_INCREMENT, "
                    + COLUMN_BRANCH_NAME + " text, "
                    + "PRIMARY KEY(" + COLUMN_BRANCH_ID + ")"
                    + ")";
            String createCustomers = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMERS + " ("
                    + COLUMN_CUSTOMER_ID + " int NOT NULL AUTO_INCREMENT, "
                    + COLUMN_CUSTOMER_NAME + " text, "
                    + COLUMN_CUSTOMER_BRANCH_ID + " int NOT NULL, "
                    + "PRIMARY KEY(" + COLUMN_CUSTOMER_ID + ")"
                    + ")";
            String createTransactions = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " ("
                    + COLUMN_TRANSACTION_DATE + " TIMESTAMP, "
                    + COLUMN_TRANSACTION_VALUE + " decimal(10,2), "
                    + COLUMN_TRANSACTION_CUSTOMER_ID + " int NOT NULL, "
                    + "PRIMARY KEY(" + COLUMN_TRANSACTION_DATE + ")"
                    + ")";

            conn.setCatalog(DB_NAME);
            Statement statement = conn.createStatement();
            statement.executeUpdate(createDB);
            statement.executeUpdate(createBranches);
            statement.executeUpdate(createCustomers);
            statement.executeUpdate(createTransactions);
            statement.close();


            System.out.println("Connection successfully opened.");
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection successfully closed.");
            }
        } catch (SQLException e) {
            System.out.println("Connection closing problem: " + e.getMessage());
        }
    }

    public void addBranch(BranchesTableModel branch) {
        String insertBranch = "INSERT INTO " + TABLE_BRANCHES + " ("
                + COLUMN_BRANCH_ID+ " , "
                + COLUMN_BRANCH_NAME + ") "
                + "VALUES ("+ branch.getId() +" , '" + branch.getName() + "')";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(insertBranch);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in addBranch method: " + e.getMessage());
        }
    }


    public void addCustomer(CustomersTableModel customer, double initial) {
        String insertCustomer = "INSERT INTO " + TABLE_CUSTOMERS + " ("
                + COLUMN_CUSTOMER_ID + " , "
                + COLUMN_CUSTOMER_NAME + " , "
                + COLUMN_CUSTOMER_BRANCH_ID + ") "
                + "VALUES ("+ customer.getId() + " , '" + customer.getName() + "' , " + customer.getBranchId() + ")";

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(insertCustomer);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in addCustomer method: " + e.getMessage());
        }
        addTransaction(new TransactionsTableModel(java.sql.Timestamp.valueOf(LocalDateTime.now()),Double.toString(initial),customer.getId()));

    }

    public void addTransaction(TransactionsTableModel transaction) {
        java.sql.Timestamp date = java.sql.Timestamp.valueOf(transaction.getDate());
        String insertTransaction = "INSERT INTO " + TABLE_TRANSACTIONS + " ("
                + COLUMN_TRANSACTION_DATE + " , "
                + COLUMN_TRANSACTION_VALUE + " , "
                + COLUMN_TRANSACTION_CUSTOMER_ID + ") "
                + "VALUES ('"
                + date + "' , '"
                + Double.parseDouble(transaction.getValue().substring(0,transaction.getValue().indexOf('P'))) + "' , "
                + transaction.getCustomerId() + ")";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(insertTransaction);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in addTransaction method: " + e.getMessage());
        }
    }

    public void deleteBranch(int id) {
        String deleteBranch = "DELETE FROM " + TABLE_BRANCHES + " WHERE "
                + COLUMN_BRANCH_ID+ " = "
                + id;
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(deleteBranch);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in deleteBranch method: " + e.getMessage());
        }
        deleteAllCustomers(id);
    }


    public void deleteCustomer(int id) {
        String deleteCustomer = "DELETE FROM " + TABLE_CUSTOMERS + " WHERE "
                + COLUMN_CUSTOMER_ID + " = "
                + id;
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(deleteCustomer);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in deleteCustomer method: " + e.getMessage());
        }
        deleteAllTransactions(id);
    }

    private void deleteAllCustomers(int id) {
        String deleteCustomer = "DELETE FROM " + TABLE_CUSTOMERS + " WHERE "
                + COLUMN_CUSTOMER_BRANCH_ID + " = "
                + id;
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(deleteCustomer);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in deleteCustomer method: " + e.getMessage());
        }
        deleteAllTransactions(id);
    }

    public void deleteTransaction(LocalDateTime dateLocal) {
//        String date = dateLocal.toString().replace('T',' ');
        java.sql.Timestamp date = Timestamp.valueOf(dateLocal);
        String deleteTransaction = "DELETE FROM " + TABLE_TRANSACTIONS + " WHERE "
                + COLUMN_TRANSACTION_DATE + " = "
                +"?";
        try {
            PreparedStatement preparedStatement=conn.prepareStatement(deleteTransaction);
            preparedStatement.setTimestamp(1,date);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("ERROR in deleteTransaction method: " + e.getMessage());
        }
    }

    private void deleteAllTransactions(int id) {
        String deleteTransaction = "DELETE FROM " + TABLE_TRANSACTIONS + " WHERE "
                + COLUMN_TRANSACTION_CUSTOMER_ID + " = "
                + id;
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(deleteTransaction);
            statement.close();
        } catch (SQLException e) {
            System.out.println("ERROR in deleteTransaction method: " + e.getMessage());
        }
    }

    public ObservableList<BranchesTableModel> loadBranches() {
        try {
            ObservableList<BranchesTableModel> branchesObvList = FXCollections.observableArrayList();
            String selectBranches = "SELECT * FROM " + TABLE_BRANCHES;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectBranches);
            while (resultSet.next()) {
                branchesObvList.add(new BranchesTableModel(resultSet.getInt(COLUMN_BRANCH_ID),resultSet.getString(COLUMN_BRANCH_NAME),false));
            }
            return branchesObvList;
        }
        catch (SQLException e){
            System.out.println("Error loading branches: " + e.getMessage());
        }
        return null;
    }

    public ObservableList<CustomersTableModel> loadCustomers(int branchId){
        try{
            ObservableList<CustomersTableModel> customersObvList = FXCollections.observableArrayList();
            String selectCustomers = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + COLUMN_CUSTOMER_BRANCH_ID + "=" + branchId;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectCustomers);
            while (resultSet.next()) {
                customersObvList.add(new CustomersTableModel(resultSet.getInt(COLUMN_CUSTOMER_ID),resultSet.getString(COLUMN_CUSTOMER_NAME),false,resultSet.getInt(COLUMN_CUSTOMER_BRANCH_ID)));
            }
            return customersObvList;
        }
        catch (SQLException e)   {
            System.out.println("Error loading customers: " + e.getMessage());
    }
        return null;
    }

    public ObservableList<CustomersTableModel> loadAllCustomers(){
        try{
            ObservableList<CustomersTableModel> customersObvList = FXCollections.observableArrayList();
            String selectCustomers = "SELECT * FROM " + TABLE_CUSTOMERS;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectCustomers);
            while (resultSet.next()) {
                customersObvList.add(new CustomersTableModel(resultSet.getInt(COLUMN_CUSTOMER_ID),resultSet.getString(COLUMN_CUSTOMER_NAME),false,resultSet.getInt(COLUMN_CUSTOMER_BRANCH_ID)));
            }
            return customersObvList;
        }
        catch (SQLException e)   {
            System.out.println("Error loading customers: " + e.getMessage());
        }
        return null;
    }


    public ObservableList<TransactionsTableModel> loadTransactions(int customerId){
        try{
            ObservableList<TransactionsTableModel> transactionsObvList = FXCollections.observableArrayList();
            String selectTransactions = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_TRANSACTION_CUSTOMER_ID + "=" + customerId;
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectTransactions);
            while (resultSet.next()) {
                transactionsObvList.add(new TransactionsTableModel(resultSet.getTimestamp(COLUMN_TRANSACTION_DATE),resultSet.getString(COLUMN_TRANSACTION_VALUE),resultSet.getInt(COLUMN_TRANSACTION_CUSTOMER_ID)));
            }
            return transactionsObvList;
        }
        catch (SQLException e)   {
            System.out.println("Error loading customers: " + e.getMessage());
        }
        return null;
    }
}













