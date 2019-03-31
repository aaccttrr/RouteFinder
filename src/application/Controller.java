package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedList;

public class Controller {

	private TextField waitingForMapClick;
	@FXML TextField startField, destField, maxRoutesText, waypointField;
	@FXML VBox menuPane, settingsPane;
	@FXML Slider maxRoutesSlider;
	@FXML RadioButton radioShortest, radioEasiest, radioSafest;
	@FXML ToggleGroup priorityGroup;
	@FXML ListView waypointsView;


	@FXML
	private void selectSettlement(ActionEvent e){
		String placename = ((Button)e.getSource()).getText();
		if(waitingForMapClick!=null && waitingForMapClick.isFocused()) waitingForMapClick.setText(placename);
	}

	@FXML
	private void selectField(MouseEvent e){
		waitingForMapClick = (TextField) e.getSource();
	}

	@FXML
	private void findRoute(){

	}

	@FXML
	private void updateMaxRoutes(Event e){
		int maxRoutes;
		if(e.getSource()==maxRoutesSlider){
			maxRoutes= (int) maxRoutesSlider.getValue();
			maxRoutesText.setText(Integer.toString(maxRoutes));
		}
		else{
			try {
				maxRoutes = Integer.parseInt(maxRoutesText.getText());
				maxRoutesSlider.setValue(maxRoutes);
			}
			catch (Exception ex){
				maxRoutesText.setText("!");
			}
		}
	}

	@FXML
	private void updateRoutePriority(){
		String buttonPriority = ((RadioButton)priorityGroup.getSelectedToggle()).getText();
		String priority="";
		switch (buttonPriority){
			case "Shortest Route": priority="distance";
			break;
			case "Easiest Route": priority="difficulty";
			break;
			case "Safest Route": priority="danger";
			break;
		}
		Map.setRoutePriority(priority);
	}

	@FXML
	private void addWaypoint(){
		try {
			String placename = waypointField.getText();
			Settlement settlement = Map.lookupSettlement(placename);
			if (settlement == null) {
				waypointField.setText("Settlement Not Found!");
			} else {
				LinkedList<Settlement> list = Map.getWaypoints();
				if(!list.contains(settlement)) {
					Map.addWaypoint(settlement);
					waypointsView.setItems(FXCollections.observableList(list));
					waypointField.clear();
				}
				else{
					waypointField.setText("Waypoint Already Exists!");
				}
			}
		}
		catch (Exception e){
			waypointField.requestFocus();
		}
	}

	@FXML
	private void addRouteInfo(){
		InputOutput.readSettlements();
		Settlement[] settlements = Map.getSettlements();
		for(Settlement s:settlements){
			System.out.println(s.getPlacename());
		}
	}

	@FXML
	private void toggleView(){
		menuPane.setVisible(!menuPane.isVisible());
		settingsPane.setVisible(!settingsPane.isVisible());
	}
}