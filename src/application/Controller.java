package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

	@FXML
	private void test(ActionEvent e){
		Button btn = (Button) e.getSource();
		System.out.println(btn.getLayoutX()+", "+btn.getLayoutY());
	}
}