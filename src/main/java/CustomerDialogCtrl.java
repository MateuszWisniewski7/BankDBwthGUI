import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class CustomerDialogCtrl {

    @FXML TextField id;
    @FXML TextField name;
    @FXML TextField initial;
    @FXML Label tip;
    @FXML ChoiceBox<BranchesTableModel> choiceBox;
    private final Pattern pattern = Pattern.compile("\\d*");
    private final Pattern patternInitial = Pattern.compile("\\d*(\\.?\\d{0,2})");

    public CustomersTableModel create(Integer branchId){
        return new CustomersTableModel(Integer.parseInt(id.getText()),name.getText().trim(),false, branchId);
    }

    public double getInitial(){
        return Double.parseDouble(initial.getText());
    }

    @FXML
    public boolean areTextFieldsFilled(){
        if (!pattern.matcher(id.getText()).matches()){
            id.setText(id.getText(0,id.getLength()-1));
            id.positionCaret(id.getLength());
            tip.setText("Use only digits in id field");
            tip.setVisible(true);
        }else{
            tip.setVisible(false);
        }
        if (!patternInitial.matcher(initial.getText()).matches()){
            initial.setText(initial.getText(0,id.getLength()-1));
            initial.positionCaret(initial.getLength());
            tip.setText("Use only double form in initial transaction field");
            tip.setVisible(true);
        }else{
            tip.setVisible(false);
        }
        return !id.getText().isEmpty() && !name.getText().trim().isEmpty() && !initial.getText().trim().isEmpty() && !choiceBox.getSelectionModel().isEmpty();
    }

}
