package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.List;

public class Controller {

	@FXML Pane settlementPane, routePane;
	@FXML TextField startField, destField, maxRoutesText, waypointField,
			exclusionField, addSettlementName, addSettlementXPos, addSettlementYPos,
			addRouteStart, addRouteEnd, addRouteDist;
	@FXML VBox menuPane, settingsPane;
	@FXML Slider maxRoutesSlider, addRouteDiff, addRouteDanger;
	@FXML RadioButton radioShortest, radioEasiest, radioSafest;
	@FXML ToggleGroup priorityGroup;
	@FXML ListView waypointsView, exclusionsView;
	@FXML HBox routeWarningBox;
	@FXML Button updateRoute;
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
	private void addRoute(ActionEvent e){
		Settlement start = Map.lookupSettlement(addRouteStart.getText());
		Settlement end = Map.lookupSettlement(addRouteEnd.getText());
		if(start==null) {
			addRouteStart.setText("Settlement Not Found!");
			return;
		}
		if(end==null){
			addRouteEnd.setText("Settlement Not Found!");
			return;
		}
		if(start==end){
			addRouteStart.setText("Start and End Cannot");
			addRouteEnd.setText("Be The Same!");
		}
		try{
			double distance = Double.parseDouble(addRouteDist.getText());
			int difficulty = (int) addRouteDiff.getValue();
			int danger = (int) addRouteDanger.getValue();
			if(Map.routeExists(start, end)){
				if(e.getSource()==updateRoute){
					Map.addRoute(start, end, distance, difficulty, danger);
					closeWarning();
					showRoutes();
				}
				else {
					routeWarningBox.setVisible(true);
				}
			}
			else{
				Map.addRoute(start, end, distance, difficulty, danger);
				showRoutes();
			}
		}
		catch (Exception ex){
			addRouteDist.setText("Invalid Distance!");
		}
	}

	@FXML
	private void closeWarning(){
		routeWarningBox.setVisible(false);
	}

	@FXML
	private void saveMapData(){
		InputOutput.writeSettlements();
		InputOutput.writeRoutes();
	}

	@FXML
	private void toggleView(){
		menuPane.setVisible(!menuPane.isVisible());
		settingsPane.setVisible(!settingsPane.isVisible());
	}

	private void showSettlements(){
		Settlement[] settlements = Map.getSettlements();
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
			settlementPane.getChildren().add(b);
		}
	}

	private void showRoutes(){
		Object[][] routes = Map.getRoutes();
		for(Object[] r:routes){
			drawRoute((Settlement)r[0],(Settlement)r[1]);
		}
	}

	private void drawRoute(Settlement start, Settlement end){
		List<Node> settlementButtons = settlementPane.getChildren();
		Button startButton=null;
		Button endButton=null;
		for(Node b:settlementButtons){
			if(start.getPlacename().equals(((Button)b).getText())){
				startButton = (Button) b;
			}
			else if(end.getPlacename().equals(((Button)b).getText())){
				endButton = (Button) b;
			}
		}
		Line line = new Line(startButton.getLayoutX()+8,startButton.getLayoutY()+8,endButton.getLayoutX()+8,endButton.getLayoutY()+8);
		line.setStrokeWidth(3);
		routePane.getChildren().add(line);
	}

	@FXML
	private void initialize(){
		showSettlements();
		showRoutes();
	}
}