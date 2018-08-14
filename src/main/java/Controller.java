import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class Controller {

    @FXML private TextField branchIdFilter;



    @FXML
    public void handleAddBranch(){
    branchIdFilter.lengthProperty().intValue();
    }

    @FXML
    public void handleAddCustomer(){

    }

    @FXML
    public void handleAddTransaction(){

    }

    @FXML
    public void handleDeleteBranch(){

    }

    @FXML
    public void handleDeleteCustomer(){

    }

    @FXML
    public void handleDeleteTransaction(){

    }

    @FXML
    public void handleDateFilter(){

    }

    @FXML
    public void handleExit(){ Platform.exit(); }
}


/*
na poczatku logowanie do serwera finale zamienić na wprowadzanie danych
przyciski zgodnie ze schematem paint, pola tekstowe do wypełnienia id/nazwy/ zakresu na radiobutton
 czy checkbutton daty(transakcji) jedna na jeden dzień i druga na zakres dni
 */