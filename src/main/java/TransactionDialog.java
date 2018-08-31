import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class TransactionDialog {

    @FXML TextField value;
    @FXML Label tip;
    @FXML ChoiceBox<CustomersTableModel> choiceBox;
    private final Pattern pattern = Pattern.compile("([-]?\\d*(\\.?\\d{0,2}))");
    private LocalDateTime dateTime = LocalDateTime.now();

    public TransactionsTableModel create(Integer customerId){
        if(!(value.getText().indexOf('.')<0) && !(Double.toString(Double.parseDouble(value.getText())).substring(Double.toString(Double.parseDouble(value.getText())).indexOf('.')).length()<3))
        return new TransactionsTableModel(Timestamp.valueOf(dateTime),Double.toString(Double.parseDouble(value.getText())),customerId);
        else
        return new TransactionsTableModel(Timestamp.valueOf(dateTime),Double.toString(Double.parseDouble(value.getText()))+"0",customerId);

    }

    @FXML
    public boolean areTextFieldsFilled(){
        if (!pattern.matcher(value.getText()).matches()){
            value.setText(value.getText(0,value.getLength()-1));
            value.positionCaret(value.getLength());
            tip.setVisible(true);
        }else{
            tip.setVisible(false);
        }
        return !value.getText().isEmpty() && !choiceBox.getSelectionModel().isEmpty();
    }

}

