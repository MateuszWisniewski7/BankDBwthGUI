package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.BranchesTableModel;

import java.util.regex.Pattern;

public class BranchDialogCtlr {

    @FXML
    TextField id;
    @FXML
    TextField name;
    @FXML
    Label tip;
    private final Pattern pattern = Pattern.compile("\\d*");

    public BranchesTableModel create() {
        return new BranchesTableModel(Integer.parseInt(id.getText()), name.getText(), false);
    }

    @FXML
    public boolean areTextFieldsFilled() {
        if (!pattern.matcher(id.getText()).matches()) {
            id.setText(id.getText(0,
                                  id.getLength() - 1));
            id.positionCaret(id.getLength());
            tip.setVisible(true);
        } else {
            tip.setVisible(false);
        }
        return !id.getText().isEmpty()
               && !name.getText().trim().isEmpty();
    }
}
