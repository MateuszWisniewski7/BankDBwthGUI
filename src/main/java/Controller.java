import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class Controller {

    @FXML private BorderPane mainPane;
    @FXML private TextField branchIdFilter;
    @FXML private CheckBox dateFilter;
    @FXML private DatePicker fromPickedDate;
    @FXML private DatePicker toPickedDate;
    @FXML private Label branchesCounter;
    private short branchesCounterTxtLength;
    @FXML private Label customersCounter;
    private short customersCounterTxtLength;
    @FXML private Label transactionsCounter;
    private short transactionsCounterTxtLength;
    @FXML private Label transactionsSum;
    private short transactionsTxtLength;

    @FXML private TableView<BranchesTableModel> branchesTable;
    @FXML private TableColumn<BranchesTableModel,Integer> branchIdCol;
    @FXML private TableColumn<BranchesTableModel,String> branchNameCol;

    @FXML private TableView<CustomersTableModel> customersTable;
    @FXML private TableColumn<CustomersTableModel,Integer> customerIdCol;
    @FXML private TableColumn<CustomersTableModel,String> customerNameCol;

    @FXML private TableView<TransactionsTableModel> transactionsTable;
    @FXML private TableColumn<TransactionsTableModel,String> transactionDateCol;
    @FXML private TableColumn<TransactionsTableModel,String> transactionValueCol;

    private ObservableList<BranchesTableModel> branchesObvList;
    private ObservableList<CustomersTableModel> customersObvList;
    private ObservableList<TransactionsTableModel> transactionsObvList;



    public void initialize(){
        branchesObvList = DataSource.getInstance().loadBranches();
        branchIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        branchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        branchesTable.setItems(branchesObvList);
        branchNameCol.setSortType(TableColumn.SortType.ASCENDING);
        branchesTable.getSortOrder().add(branchNameCol);
        branchesTable.getSelectionModel().selectFirst();
        branchesCounterTxtLength=(short) branchesCounter.getText().length();
        customersCounterTxtLength=(short) customersCounter.getText().length();
        transactionsCounterTxtLength=(short) transactionsCounter.getText().length();
        transactionsTxtLength=(short) transactionsSum.getText().length();
        branchesCounter.setText(branchesCounter.getText()+branchesObvList.size());
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        transactionValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

    }

    @FXML
    public void handleShowCustomers(){
        branchesTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    try{
                        transactionsTable.setItems(null);
                        customersObvList=DataSource.getInstance().loadCustomers(branchesTable.getSelectionModel().getSelectedItem().getId());
                        customersTable.setItems(customersObvList);
                        customerNameCol.setSortType(TableColumn.SortType.ASCENDING);
                        customersTable.getSortOrder().add(customerNameCol);
                        customersTable.getSelectionModel().selectFirst();
                        customersCounter.setText(customersCounter.getText().substring(0,customersCounterTxtLength)+customersObvList.size());
                        transactionsCounter.setText(transactionsCounter.getText().substring(0,transactionsCounterTxtLength));
                        transactionsSum.setText(transactionsSum.getText().substring(0,transactionsTxtLength));
                        branchesTable.getSelectionModel().getSelectedItem().setSelected(true);
                        branchesTable.getItems().filtered(p->!p.equals(branchesTable.getSelectionModel().getSelectedItem())).forEach(notSelected->notSelected.setSelected(false));
                        cellUpdate(branchNameCol,branchesTable);
                        cellUpdate(branchIdCol,branchesTable);
                    }
                    catch (NullPointerException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @FXML
    public void handleShowTransactions(){

        customersTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    try{
                        transactionsObvList=DataSource.getInstance().loadTransactions(customersTable.getSelectionModel().getSelectedItem().getId());
                        transactionsTable.setItems(transactionsObvList);
                        transactionDateCol.setSortType(TableColumn.SortType.DESCENDING);
                        transactionsTable.getSortOrder().add(transactionDateCol);
                        transactionsCounter.setText(transactionsCounter.getText().substring(0,transactionsCounterTxtLength)+transactionsObvList.size());
                        double sum = 0.0;
                        for (TransactionsTableModel trans : transactionsObvList) {
                            sum += Double.parseDouble(trans.getValue().substring(0,trans.getValue().indexOf('P')));
                        }
                        transactionsSum.setText(transactionsSum.getText().substring(0,transactionsTxtLength)+String.format("%.2f",sum)+"PLN");
                        customersTable.getSelectionModel().getSelectedItem().setSelected(true);
                        customersTable.getItems().filtered(p->!p.equals(customersTable.getSelectionModel().getSelectedItem())).forEach(notSelected->notSelected.setSelected(false));
                        cellUpdate(customerNameCol,customersTable);
                        cellUpdate(customerIdCol,customersTable);
                    }
                    catch (NullPointerException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    @FXML
    public void handleAddBranch(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New branch");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("branchDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        BranchDialog controller = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (controller.areTextFieldsFilled()){
                BranchesTableModel newBranch = controller.create();
                if((branchesObvList.filtered(p->p.getId().equals(newBranch.getId())).isEmpty()&& branchesObvList.filtered(p->p.getName().equals(newBranch.getName())).isEmpty())){
                    branchesObvList.add(newBranch);
                    branchesTable.getSelectionModel().select(newBranch);
                    DataSource.getInstance().addBranch(newBranch);
                    branchesCounter.setText(branchesCounter.getText().substring(0,branchesCounterTxtLength)+branchesObvList.size());
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add branch");
                    alert.setHeaderText("Table already contains branch id/name");
                    alert.showAndWait();
                }
            }else{
                handleAddBranch();
            }
        }

    }

    @FXML
    public void handleAddCustomer(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New customer");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("customerDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        CustomerDialog controller = fxmlLoader.getController();
        controller.choiceBox.setItems(branchesObvList);
        controller.choiceBox.getSelectionModel().select(branchesTable.getSelectionModel().getSelectedItem());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (controller.areTextFieldsFilled()){
                CustomersTableModel newCustomer = controller.create(controller.choiceBox.getSelectionModel().getSelectedItem().getId());
                if((customersObvList.filtered(p->p.getId().equals(newCustomer.getId())).isEmpty() && customersObvList.filtered(p->p.getName().equals(newCustomer.getName())).isEmpty())){
                    customersObvList.add(newCustomer);
                    customersTable.getSelectionModel().select(newCustomer);
                    DataSource.getInstance().addCustomer(newCustomer,controller.getInitial());
                    customersCounter.setText(customersCounter.getText().substring(0,customersCounterTxtLength)+customersObvList.size());
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Add customer");
                    alert.setHeaderText("Table already contains customer's id/name");
                    alert.showAndWait();
                }
            }else{
                handleAddCustomer();
            }
        }
    }

    @FXML
    public void handleAddTransaction(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New transaction");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("transactionDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println(e.getMessage());
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        TransactionDialog controller = fxmlLoader.getController();
        customersObvList=DataSource.getInstance().loadAllCustomers();
        controller.choiceBox.setItems(customersObvList);
        if(!(customersTable.getSelectionModel().getSelectedItem()==null)){
            controller.choiceBox.getSelectionModel().select(customersTable.getSelectionModel().getSelectedItem());}
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (controller.areTextFieldsFilled()){
                TransactionsTableModel newTransaction = controller.create(controller.choiceBox.getSelectionModel().getSelectedItem().getId());
                if (!(customersTable.getSelectionModel().getSelectedItem()==null))
                if (controller.choiceBox.getSelectionModel().getSelectedItem().getId().equals(customersTable.getSelectionModel().getSelectedItem().getId()) && customersTable.getSelectionModel().getSelectedItem().isSelected())
                {transactionsObvList.add(newTransaction);
                transactionsTable.getSelectionModel().select(newTransaction);
                transactionsCounter.setText(transactionsCounter.getText().substring(0,transactionsCounterTxtLength)+transactionsObvList.size());
                double sum = 0.0;
                for (TransactionsTableModel trans : transactionsObvList) {
                    sum += Double.parseDouble(trans.getValue().substring(0,trans.getValue().indexOf('P')));
                }
                transactionsSum.setText(transactionsSum.getText().substring(0,transactionsTxtLength)+String.format("%.2f",sum)+"PLN");}
                DataSource.getInstance().addTransaction(newTransaction);
            }else{
                handleAddTransaction();
            }
        }
    }

    @FXML
    public void handleDeleteBranch(){
        BranchesTableModel item = branchesTable.getSelectionModel().getSelectedItem();
        if (item==null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete branch");
            alert.setHeaderText("Select any branch to delete");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete branch");
            alert.setHeaderText("Are you sure to delete " + "id: " +item.getId()+ ". name: " + item.getName());
            Optional<ButtonType> result= alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                item.setSelected(false);
                branchesObvList.remove(item);
                customersTable.setItems(null);
                transactionsTable.setItems(null);
                branchesTable.getSelectionModel().selectFirst();
                branchesCounter.setText(branchesCounter.getText().substring(0,branchesCounterTxtLength)+branchesObvList.size());
                DataSource.getInstance().deleteBranch(item.getId());
            }
        }
    }

    @FXML
    public void handleDeleteCustomer(){
        CustomersTableModel item = customersTable.getSelectionModel().getSelectedItem();
        if (item==null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete customer");
            alert.setHeaderText("Select any customer to delete");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete customer");
            alert.setHeaderText("Are you sure to delete " + "id: " +item.getId()+ ". name: " + item.getName());
            Optional<ButtonType> result= alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                item.setSelected(false);
                customersObvList.remove(item);
                transactionsTable.setItems(null);
                customersTable.getSelectionModel().selectFirst();
                customersCounter.setText(customersCounter.getText().substring(0,customersCounterTxtLength)+customersObvList.size());
                DataSource.getInstance().deleteCustomer(item.getId());
            }
        }
    }

    @FXML
    public void handleDeleteTransaction(){
        TransactionsTableModel item = transactionsTable.getSelectionModel().getSelectedItem();
        if (item==null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete transaction");
            alert.setHeaderText("Select any transaction to delete");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete transaction");
            alert.setHeaderText("Are you sure to delete " + "transaction from: " +item.getFormattedDate()+ " value: " + item.getValue());
            Optional<ButtonType> result= alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                transactionsObvList.remove(item);
                transactionsCounter.setText(transactionsCounter.getText().substring(0,transactionsCounterTxtLength)+transactionsObvList.size());
                double sum = 0.0;
                for (TransactionsTableModel trans : transactionsObvList) {
                    sum += Double.parseDouble(trans.getValue().substring(0,trans.getValue().indexOf('P')));
                }
                transactionsSum.setText(transactionsSum.getText().substring(0,transactionsTxtLength)+String.format("%.2f",sum)+"PLN");
                DataSource.getInstance().deleteTransaction(item.getDate());
            }
        }
    }

    @FXML
    public void handleDateFilter(){
        if (dateFilter.isSelected()){
            toPickedDate.setDisable(false);
        }
        else{
            toPickedDate.setDisable(true);
        }
    }

    @FXML
    public void handleExit(){ Platform.exit(); }

    private <S extends TableModel,T> void cellUpdate(TableColumn<S,T> column, TableView<S> table){
        column.setCellFactory(param -> {
            TableCell<S,T> cell = new TableCell<>(){
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }else {
                        if (item.getClass().equals(Integer.class)) {
                            setText(item.toString());
                            if (table.getSelectionModel().getSelectedItem().isSelected()) {
                                if (table.getSelectionModel().getSelectedItem().getId() == Integer.parseInt(item.toString())) {
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

}


//    private <S extends BranchesTableModel , T> void add(String title,String resource,Class T){
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
//        BranchDialog controller = fxmlLoader.getController();
//        Optional<ButtonType> result = dialog.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK){
//            if (controller.areTextFieldsFilled()){
//                TableModel newObj = controller.create();
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
