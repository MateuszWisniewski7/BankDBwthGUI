package controllers;

import DB.DataSource;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import models.BranchesTableModel;
import models.CustomersTableModel;
import models.TableModel;
import models.TransactionsTableModel;

import java.io.IOException;
import java.time.chrono.ChronoLocalDate;
import java.util.Optional;
import java.util.regex.Pattern;


public class Controller {

    @FXML
    private BorderPane mainPane;
    @FXML
    private TextField branchIdFilter;
    @FXML
    private TextField customerIdFilter;
    @FXML
    private TextField branchNameFilter;
    @FXML
    private TextField customerNameFilter;
    @FXML
    private CheckBox dateFilter;
    @FXML
    private DatePicker fromPickedDate;
    @FXML
    private DatePicker toPickedDate;
    @FXML
    private Label branchesCounter;
    private short branchesCounterTxtLength;
    @FXML
    private Label customersCounter;
    private short customersCounterTxtLength;
    @FXML
    private Label transactionsCounter;
    private short transactionsCounterTxtLength;
    @FXML
    private Label transactionsSum;
    private short transactionsTxtLength;

    @FXML
    private TableView<BranchesTableModel> branchesTable;
    @FXML
    private TableColumn<BranchesTableModel, Integer> branchIdCol;
    @FXML
    private TableColumn<BranchesTableModel, String> branchNameCol;

    @FXML
    private TableView<CustomersTableModel> customersTable;
    @FXML
    private TableColumn<CustomersTableModel, Integer> customerIdCol;
    @FXML
    private TableColumn<CustomersTableModel, String> customerNameCol;

    @FXML
    private TableView<TransactionsTableModel> transactionsTable;
    @FXML
    private TableColumn<TransactionsTableModel, String> transactionDateCol;
    @FXML
    private TableColumn<TransactionsTableModel, String> transactionValueCol;

    private ObservableList<BranchesTableModel> branchesObvList;
    private ObservableList<CustomersTableModel> customersObvList;
    private ObservableList<TransactionsTableModel> transactionsObvList;

    private ObservableList<BranchesTableModel> filteredBranchesObvList;
    private ObservableList<CustomersTableModel> filteredCustomersObvList;
    private ObservableList<TransactionsTableModel> filteredTransactionsObvList;


    public void initialize() {
        branchesObvList = DataSource.getInstance().loadBranches();
        filteredBranchesObvList = branchesObvList;
        branchIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        branchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        branchesTable.setItems(filteredBranchesObvList);
        branchNameCol.setSortType(TableColumn.SortType.ASCENDING);
        branchesTable.getSortOrder().add(branchNameCol);
        branchesTable.getSelectionModel().selectFirst();
        branchesCounterTxtLength = (short) branchesCounter.getText().length();
        customersCounterTxtLength = (short) customersCounter.getText().length();
        transactionsCounterTxtLength = (short) transactionsCounter.getText().length();
        transactionsTxtLength = (short) transactionsSum.getText().length();
        branchesCounter.setText(branchesCounter.getText()
                                + branchesObvList.size());
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        transactionValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

    }

    @FXML
    public void handleShowCustomers() {
        branchesTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount()
                    == 2) {
                    try {
                        transactionsTable.setItems(null);
                        toPickedDate.getEditor().clear();
                        fromPickedDate.getEditor().clear();
                        customersObvList = DataSource.getInstance()
                                                     .loadCustomers(branchesTable.getSelectionModel()
                                                                                 .getSelectedItem()
                                                                                 .getId());
                        filteredCustomersObvList = customersObvList;
                        customersTable.setItems(filteredCustomersObvList);
                        customerNameCol.setSortType(TableColumn.SortType.ASCENDING);
                        customersTable.getSortOrder().add(customerNameCol);
                        customersTable.getSelectionModel()
                                      .selectFirst();
                        refreshCCounters();
                        refreshTCounters();
                        branchesTable.getSelectionModel()
                                     .getSelectedItem()
                                     .setSelected(true);
                        branchesTable.getItems()
                                     .filtered(p -> !p.equals(branchesTable.getSelectionModel()
                                                                           .getSelectedItem()))
                                     .forEach(notSelected -> notSelected.setSelected(false));
                        cellUpdate(branchNameCol, branchesTable);
                        cellUpdate(branchIdCol, branchesTable);
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @FXML
    public void handleShowTransactions() {

        customersTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount()
                    == 2) {
                    try {
                        transactionsObvList = DataSource.getInstance()
                                                        .loadTransactions(customersTable.getSelectionModel()
                                                                                        .getSelectedItem()
                                                                                        .getId());
                        toPickedDate.getEditor().clear();
                        fromPickedDate.getEditor().clear();
                        transactionsTable.setItems(transactionsObvList);
                        transactionDateCol.setSortType(TableColumn.SortType.DESCENDING);
                        transactionsTable.getSortOrder().add(transactionDateCol);
                        refreshTCounters();
                        customersTable.getSelectionModel()
                                      .getSelectedItem()
                                      .setSelected(true);
                        customersTable.getItems()
                                      .filtered(p -> !p.equals(customersTable.getSelectionModel()
                                                                             .getSelectedItem()))
                                      .forEach(notSelected -> notSelected.setSelected(false));
                        cellUpdate(customerNameCol, customersTable);
                        cellUpdate(customerIdCol, customersTable);
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @FXML
    public void handleAddBranch() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New branch");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("branchDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        BranchDialogCtlr controller = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()
            && result.get()
               == ButtonType.OK) {
            if (controller.areTextFieldsFilled()) {
                BranchesTableModel newBranch = controller.create();
                if ((branchesObvList.filtered(p -> p.getId().equals(newBranch.getId())).isEmpty()
                     && branchesObvList.filtered(p -> p.getName().equals(newBranch.getName())).isEmpty())) {
                    branchesObvList.add(newBranch);
                    branchesTable.getSelectionModel().select(newBranch);
                    DataSource.getInstance().addBranch(newBranch);
                    refreshBCounters();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add branch");
                    alert.setHeaderText("Table already contains branch id/name");
                    alert.showAndWait();
                }
            } else {
                handleAddBranch();
            }
        }

    }

    @FXML
    public void handleAddCustomer() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New customer");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("customerDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        CustomerDialogCtrl controller = fxmlLoader.getController();
        controller.choiceBox.setItems(branchesObvList);
        controller.choiceBox.getSelectionModel().select(branchesTable.getSelectionModel().getSelectedItem());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()
            && result.get()
               == ButtonType.OK) {
            if (controller.areTextFieldsFilled()) {
                CustomersTableModel newCustomer = controller.create(controller.choiceBox.getSelectionModel()
                                                                                        .getSelectedItem()
                                                                                        .getId());
                if ((customersObvList.filtered(p -> p.getId().equals(newCustomer.getId())).isEmpty()
                     && customersObvList
                             .filtered(p -> p.getName().equals(newCustomer.getName()))
                             .isEmpty())) {
                    customersObvList.add(newCustomer);
                    customersTable.getSelectionModel().select(newCustomer);
                    DataSource.getInstance().addCustomer(newCustomer, controller.getInitial());
                    refreshCCounters();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add customer");
                    alert.setHeaderText("Table already contains customer's id/name");
                    alert.showAndWait();
                }
            } else {
                handleAddCustomer();
            }
        }
    }

    @FXML
    public void handleAddTransaction() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New transaction");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("transactionDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        TransactionDialogCtrl controller = fxmlLoader.getController();
        ObservableList<CustomersTableModel> allCustomers = DataSource.getInstance().loadAllCustomers();
        controller.choiceBox.setItems(allCustomers);
        if (!(customersTable.getSelectionModel().getSelectedItem()
              == null)) {
            controller.choiceBox.getItems()
                                .filtered(item -> item.getId()
                                                      .equals(customersTable.getSelectionModel()
                                                                            .getSelectedItem()
                                                                            .getId()))
                                .forEach(item -> controller.choiceBox.getSelectionModel().select(item));
        }
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()
            && result.get()
               == ButtonType.OK) {
            if (controller.areTextFieldsFilled()) {
                TransactionsTableModel newTransaction = controller.create(controller.choiceBox.getSelectionModel()
                                                                                              .getSelectedItem()
                                                                                              .getId());
                if (!(customersTable.getSelectionModel().getSelectedItem()
                      == null))
                    if (controller.choiceBox.getSelectionModel()
                                            .getSelectedItem()
                                            .getId()
                                            .equals(customersTable.getSelectionModel()
                                                                  .getSelectedItem()
                                                                  .getId())
                        && customersTable.getSelectionModel()
                                         .getSelectedItem()
                                         .isSelected()) {
                        transactionsObvList.add(newTransaction);
                        transactionsTable.getSelectionModel().select(newTransaction);
                        refreshTCounters();
                    }
                DataSource.getInstance().addTransaction(newTransaction);
            } else {
                handleAddTransaction();
            }
        }
    }

    @FXML
    public void handleDeleteBranch() {
        BranchesTableModel item = branchesTable.getSelectionModel().getSelectedItem();
        if (item
            == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete branch");
            alert.setHeaderText("Select any branch to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete branch");
            alert.setHeaderText("Are you sure to delete "
                                + "id: "
                                + item.getId()
                                + ". name: "
                                + item.getName());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()
                && result.get()
                   == ButtonType.OK) {
                item.setSelected(false);
                branchesObvList.remove(item);
                customersTable.setItems(null);
                transactionsTable.setItems(null);
                branchesTable.getSelectionModel().selectFirst();
                refreshBCounters();
                refreshCCounters();
                refreshTCounters();
                DataSource.getInstance().deleteBranch(item.getId());
            }
        }
    }

    @FXML
    public void handleDeleteCustomer() {
        CustomersTableModel item = customersTable.getSelectionModel().getSelectedItem();
        if (item
            == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete customer");
            alert.setHeaderText("Select any customer to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete customer");
            alert.setHeaderText("Are you sure to delete "
                                + "id: "
                                + item.getId()
                                + ". name: "
                                + item.getName());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()
                && result.get()
                   == ButtonType.OK) {
                item.setSelected(false);
                customersObvList.remove(item);
                transactionsTable.setItems(null);
                customersTable.getSelectionModel().selectFirst();
                refreshCCounters();
                refreshTCounters();
                DataSource.getInstance().deleteCustomer(item.getId());
            }
        }
    }

    @FXML
    public void handleDeleteTransaction() {
        TransactionsTableModel item = transactionsTable.getSelectionModel().getSelectedItem();
        if (item
            == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete transaction");
            alert.setHeaderText("Select any transaction to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete transaction");
            alert.setHeaderText("Are you sure to delete "
                                + "transaction from: "
                                + item.getFormattedDate()
                                + " value: "
                                + item
                                        .getValue());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()
                && result.get()
                   == ButtonType.OK) {
                transactionsObvList.remove(item);
                refreshTCounters();
                DataSource.getInstance().deleteTransaction(item.getDate());
            }
        }
    }

    @FXML
    public void handleClearDates() {
        transactionsTable.setItems(transactionsObvList);
        toPickedDate.getEditor().clear();
        fromPickedDate.getEditor().clear();
        refreshTCounters();
    }

    @FXML
    public void handleRange() {
        if (dateFilter.isSelected()) {
            toPickedDate.setDisable(false);
        } else {
            toPickedDate.setDisable(true);
            toPickedDate.getEditor().setText("");
        }
    }

    @FXML
    public void handleDateFilter() {
        if (!(customersTable.getItems().isEmpty())) {
            if (!(fromPickedDate.getEditor().getText().isEmpty())
                && toPickedDate.getEditor().getText().isEmpty()) {
                if (toPickedDate.isDisabled()) {
                    transactionsTable.setItems(transactionsObvList.filtered(item -> ChronoLocalDate.from(item.getDate())
                                                                                                   .compareTo(
                                                                                                           ChronoLocalDate
                                                                                                                   .from(fromPickedDate
                                                                                                                                 .getValue()))
                                                                                    == 0));
                } else {
                    transactionsTable.setItems(transactionsObvList.filtered(item -> ChronoLocalDate.from(item.getDate())
                                                                                                   .compareTo(
                                                                                                           ChronoLocalDate
                                                                                                                   .from(fromPickedDate
                                                                                                                                 .getValue()))
                                                                                    >= 0));
                }
            } else if ((!(fromPickedDate.getEditor().getText().isEmpty())
                        && !(toPickedDate.getEditor()
                                         .getText()
                                         .isEmpty()))) {
                transactionsTable.setItems(transactionsObvList.filtered(item -> (ChronoLocalDate.from(item.getDate())
                                                                                                .compareTo(
                                                                                                        ChronoLocalDate
                                                                                                                .from(fromPickedDate
                                                                                                                              .getValue()))
                                                                                 >= 0)
                                                                                && (ChronoLocalDate
                                                                                            .from(item.getDate())
                                                                                            .compareTo(
                                                                                                    ChronoLocalDate
                                                                                                            .from(
                                                                                                                    toPickedDate
                                                                                                                            .getValue()))
                                                                                    <= 0)));
            } else {
                transactionsTable.setItems(transactionsObvList.filtered(item -> ChronoLocalDate.from(item.getDate())
                                                                                               .compareTo(
                                                                                                       ChronoLocalDate
                                                                                                               .from(toPickedDate
                                                                                                                             .getValue()))
                                                                                <= 0));
            }
            refreshTCounters();
        }
    }

    @FXML
    public void handleCustomerFilter() {
        final Pattern pattern = Pattern.compile("\\d*");
        ObservableList<CustomersTableModel> temp;
        if (pattern.matcher(customerIdFilter.getText()).matches()) {
            if (!(customersObvList
                  == null
                  || customersObvList.isEmpty())) {
                if (!(customerNameFilter.getText().trim().isEmpty())) {
                    temp = customersObvList.filtered(customer -> customer.getName()
                                                                         .length()
                                                                 >= customerNameFilter.getText()
                                                                                      .trim()
                                                                                      .length());
                    temp = temp.filtered(customer -> customer.getName()
                                                             .substring(0, customerNameFilter.getText().trim().length())
                                                             .equalsIgnoreCase(customerNameFilter.getText().trim()));
                    if (temp.isEmpty()) {
                        customersTable.setItems(null);
                        transactionsTable.setItems(null);
                        customersObvList.forEach(item -> item.setSelected(false));
                        refreshCCounters();
                        refreshTCounters();
                        return;
                    } else {
                        filteredCustomersObvList = temp;
                    }
                } else {
                    filteredCustomersObvList = customersObvList;
                }
                if (!(customerIdFilter.getText().isEmpty())) {
                    temp = filteredCustomersObvList.filtered(customer -> customer.getId()
                                                                                 .equals(Integer.parseInt(
                                                                                         customerIdFilter
                                                                                                 .getText())));
                    filteredCustomersObvList = temp;
                }
                if (filteredCustomersObvList.filtered(p -> p.isSelected()).isEmpty()) {
                    customersObvList.forEach(item -> item.setSelected(false));
                    transactionsTable.setItems(null);
                }
                customersTable.setItems(filteredCustomersObvList);
                customersTable.getSelectionModel().selectFirst();
                refreshCCounters();
                refreshTCounters();
            }
        } else {
            customerIdFilter.setText(customerIdFilter.getText(0,
                                                              customerIdFilter.getLength()
                                                              - 1));
            customerIdFilter.positionCaret(customerIdFilter.getLength());
        }
    }


    @FXML
    public void handleBranchFilter() {
        final Pattern pattern = Pattern.compile("\\d*");
        ObservableList<BranchesTableModel> temp;
        if (pattern.matcher(branchIdFilter.getText()).matches()) {
            if (!(branchesObvList
                  == null
                  || branchesObvList.isEmpty())) {
                if (!(branchNameFilter.getText().trim().isEmpty())) {
                    temp = branchesObvList.filtered(branch -> branch.getName().length()
                                                              >= branchNameFilter.getText()
                                                                                 .trim()
                                                                                 .length());
                    temp = temp.filtered(branch -> branch.getName()
                                                         .substring(0, branchNameFilter.getText().trim().length())
                                                         .equalsIgnoreCase(branchNameFilter.getText().trim()));
                    if (temp.isEmpty()) {
                        branchesTable.setItems(null);
                        customersTable.setItems(null);
                        transactionsTable.setItems(null);
                        branchesObvList.forEach(item -> item.setSelected(false));
                        customersObvList.forEach(item -> item.setSelected(false));
                        refreshBCounters();
                        refreshCCounters();
                        refreshTCounters();
                        return;
                    } else {
                        filteredBranchesObvList = temp;
                    }
                } else {
                    filteredBranchesObvList = branchesObvList;
                }
                if (!(branchIdFilter.getText().isEmpty())) {
                    temp = filteredBranchesObvList.filtered(branch -> branch.getId()
                                                                            .equals(Integer.parseInt(
                                                                                    branchIdFilter.getText())));
                    filteredBranchesObvList = temp;
                }
                if (filteredBranchesObvList.filtered(p -> p.isSelected()).isEmpty()) {
                    branchesObvList.forEach(item -> item.setSelected(false));
                    customersObvList.forEach(item -> item.setSelected(false));
                    customersTable.setItems(null);
                    transactionsTable.setItems(null);
                }
                branchesTable.setItems(filteredBranchesObvList);
                branchesTable.getSelectionModel().selectFirst();
                refreshBCounters();
                refreshCCounters();
                refreshTCounters();
            }
        } else {
            branchIdFilter.setText(branchIdFilter.getText(0,
                                                          branchIdFilter.getLength()
                                                          - 1));
            branchIdFilter.positionCaret(branchIdFilter.getLength());
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

    private <S extends TableModel, T> void cellUpdate(TableColumn<S, T> column, TableView<S> table) {
        column.setCellFactory(param -> {
            TableCell<S, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if (item.getClass().equals(Integer.class)) {
                            setText(item.toString());
                            if (table.getSelectionModel().getSelectedItem().isSelected()) {
                                if (table.getSelectionModel()
                                         .getSelectedItem()
                                         .getId()
                                    == Integer.parseInt(item.toString())) {
                                    setTextFill(Color.RED);
                                } else {
                                    setTextFill(Color.BLACK);
                                }
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        } else {
                            setText(item.toString());
                            if (table.getSelectionModel().getSelectedItem().getName().equals(item)) {
                                if (table.getSelectionModel().getSelectedItem().isSelected()) {
                                    setTextFill(Color.RED);
                                } else {
                                    setTextFill(Color.BLACK);
                                }

                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                }
            };
            return cell;
        });

    }

    private void refreshTCounters() {
        if (transactionsTable.getItems()
            == null
            || transactionsObvList.isEmpty()) {
            transactionsCounter.setText(transactionsCounter.getText().substring(0, transactionsCounterTxtLength)
                                        + 0);
            transactionsSum.setText(transactionsSum.getText().substring(0, transactionsTxtLength)
                                    + "0PLN");
        } else {
            transactionsCounter.setText(transactionsCounter.getText()
                                                           .substring(0, transactionsCounterTxtLength)
                                        + transactionsTable
                                                .getItems()
                                                .size());
            double sum = 0.0;
            for (TransactionsTableModel trans : transactionsTable.getItems()) {
                sum += Double.parseDouble(trans.getValue().substring(0, trans.getValue().indexOf('P')));
            }
            transactionsSum.setText(transactionsSum.getText()
                                                   .substring(0, transactionsTxtLength)
                                    + String.format("%.2f", sum)
                                    + "PLN");
        }
    }

    private void refreshCCounters() {
        if (customersTable.getItems()
            == null
            || customersObvList.isEmpty()) {
            customersCounter.setText(customersCounter.getText().substring(0, customersCounterTxtLength)
                                     + 0);
        } else {
            customersCounter.setText(customersCounter.getText().substring(0, customersCounterTxtLength)
                                     + customersTable
                                             .getItems()
                                             .size());
        }
    }

    private void refreshBCounters() {
        if (branchesTable.getItems()
            == null
            || branchesObvList.isEmpty()) {
            branchesCounter.setText(branchesCounter.getText().substring(0, branchesCounterTxtLength)
                                    + 0);
        } else {
            branchesCounter.setText(branchesCounter.getText()
                                                   .substring(0, branchesCounterTxtLength)
                                    + branchesTable.getItems()
                                                   .size());
        }
    }
}


//    private <S extends models.BranchesTableModel , T> void add(String title,String resource,Class T){
//        Dialog<ButtonType> dialog = new Dialog<>();
//        dialog.initOwner(mainPane.getScene().getWindow());
//        dialog.setTitle(title);
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource(resource));
//        try{
//            dialog.getDialogPane().setContent(fxmlLoader.load());
//        }catch (IOException e){
//            System.out.println(e.getMessage());
//            return;
//        }
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//        controllers.BranchDialogCtlr controller = fxmlLoader.getController();
//        Optional<ButtonType> result = dialog.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK){
//            if (controller.areTextFieldsFilled()){
//                models.TableModel newObj = controller.create();
//                if(!branchesObvList.contains(newObj)){
//                    branchesObvList.add(branchesObvList.get(0).getClass().cast(newObj));
//                    branchesTable.getSelectionModel().select(branchesObvList..getComponentType().cast(newObj));
//                    branchesCounter.setText(branchesCounter.getText().substring(0,branchesCounterTxtLength)+branchesObvList.size());
//                }else{
//
//                }
//            }else{
//                handleAddBranch();
//            }
//        }
//    }
