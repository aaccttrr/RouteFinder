package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {

	@FXML TextField startField, destField;
	private TextField selected;

	@FXML
	private void test(ActionEvent e){
		Button btn = (Button) e.getSource();
		if(selected!=null) {
            if (selected.equals(startField)) startField.setText(btn.getText());
            else if (selected.equals(destField)) destField.setText(btn.getText());
        }
	}

	@FXML
	private void selectField(){
	    selected = (startField.isFocused())? startField : destField;
	}
}