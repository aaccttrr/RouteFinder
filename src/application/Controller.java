package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.LinkedList;

public class Controller {

	@FXML Pane mapPane;
	@FXML TextField startField, destField, maxRoutesText, waypointField,
			exclusionField, addSettlementName, addSettlementXPos, addSettlementYPos;
	@FXML VBox menuPane, settingsPane;
	@FXML Slider maxRoutesSlider;
	@FXML RadioButton radioShortest, radioEasiest, radioSafest;
	@FXML ToggleGroup priorityGroup;
	@FXML ListView waypointsView, exclusionsView;
	private TextField waitingForMapClick;

	@FXML
	private void selectLocation(Event e){
		if(waitingForMapClick!=null) {
			if (waitingForMapClick.equals(addSettlementXPos) || waitingForMapClick.equals(addSettlementYPos)) {
				if (e instanceof MouseEvent) {
					addSettlementXPos.setText(String.valueOf(((MouseEvent) e).getX()));
					addSettlementYPos.setText(String.valueOf(((MouseEvent) e).getY()));
				} else {
					addSettlementXPos.setText("Already");
					addSettlementYPos.setText("Occupied!");
				}
			} else {
				if (e instanceof ActionEvent) {
					String placename = ((Button) e.getSource()).getText();
					if (waitingForMapClick != null && waitingForMapClick.isFocused()) {
						if(waitingForMapClick.equals(startField)){
							waitingForMapClick.setText((destField.getText().equals(placename))? "Invalid Route!" : placename);
						}
						else if(waitingForMapClick.equals(destField)){
							waitingForMapClick.setText((startField.getText().equals(placename))? "Invalid Route!" : placename);
						}
						else{
							waitingForMapClick.setText(placename);
						}
					}
				}
			}
		}
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
	private void addExclusion(){
		try {
			String placename = exclusionField.getText();
			Settlement settlement = Map.lookupSettlement(placename);
			if (settlement == null) {
				exclusionField.setText("Settlement Not Found!");
			} else {
				LinkedList<Settlement> list = Map.getExclusions();
				if(!list.contains(settlement)) {
					Map.addExclusion(settlement);
					exclusionsView.setItems(FXCollections.observableList(list));
					exclusionField.clear();
				}
				else{
					exclusionField.setText("Exclusion Already Exists!");
				}
			}
		}
		catch (Exception e){
			exclusionField.requestFocus();
		}
	}

	@FXML
	private void addSettlement(){
		String placename = addSettlementName.getText();
		double XPos = -1;
		double YPos = -1;
		try {
			XPos = Double.parseDouble(addSettlementXPos.getText());
		}
		catch (Exception e) {
			addSettlementXPos.setText("!");
		}
		try {
			YPos = Double.parseDouble(addSettlementYPos.getText());
		}
		catch (Exception e){
			addSettlementYPos.setText("!");
		}
		if(placename!=null && XPos!=-1 && YPos!=-1){
			Map.addSettlement(new Settlement(placename, XPos, YPos));
		}
	}

	@FXML
	private void addRoute(){
		InputOutput.readSettlements();
		Settlement[] settlements = Map.getSettlements();
		for(Settlement s:settlements){
			System.out.println(s.getPlacename());
		}
	}

	@FXML
	private void saveMapData(){
		InputOutput.writeSettlements();
	}

	@FXML
	private void toggleView(){
		menuPane.setVisible(!menuPane.isVisible());
		settingsPane.setVisible(!settingsPane.isVisible());
	}

	private void updateSettlements(){
		Settlement[] settlements = Map.getSettlements();
		ObservableList<Node> children = mapPane.getChildren();
		for(Settlement s:settlements){
			Button b = new Button(s.getPlacename());
			b.setLayoutX(s.getXPos()-8);
			b.setLayoutY(s.getYPos()-7);
			b.setPrefWidth(10);
			b.setPrefHeight(10);
			b.setTooltip(new Tooltip(s.getPlacename()));
			b.getStyleClass().add("button-transparent");
			b.setOnAction(this::selectLocation);
			b.setFocusTraversable(false);
			children.add(b);
		}
		children.get(children.size()-1).setFocusTraversable(true);
	}

	@FXML
	private void initialize(){
		updateSettlements();
	}
}