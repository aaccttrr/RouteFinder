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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Controller {

	@FXML Pane mapPane;
	@FXML TextField startField, destField, maxRoutesText, waypointField,
			exclusionField, addSettlementName, addSettlementXPos, addSettlementYPos,
			addRouteStart, addRouteEnd, addRouteDist;
	@FXML VBox menuPane, settingsPane;
	@FXML Slider maxRoutesSlider, addRouteDiff, addRouteDanger;
	@FXML ToggleGroup priorityGroup;
	@FXML ListView waypointsView, exclusionsView;
	@FXML HBox routeWarningBox;
	@FXML Button updateRoute, removeWaypointButton, removeExclusionButton;
	@FXML ToggleButton showRoutesButton;
	private TextField waitingForMapClick;


	@FXML
	private void selectLocation(Event e){ //allows for selection of settlement buttons or map coordinates
		if(waitingForMapClick!=null) {
			if (waitingForMapClick.equals(addSettlementXPos) || waitingForMapClick.equals(addSettlementYPos)) {
				addSettlementXPos.setText(String.valueOf(((MouseEvent) e).getX()));
				addSettlementYPos.setText(String.valueOf(((MouseEvent) e).getY()));
			} else {
				if (e instanceof ActionEvent) {
					String placename = ((Button) e.getSource()).getText();
					if (waitingForMapClick != null && waitingForMapClick.isFocused()) {
						waitingForMapClick.setText(placename);
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
		List<Node> children = mapPane.getChildren();
		int i=0;
		while(children.get(i) instanceof Line){ //clear previous route lines
			while(children.get(i) instanceof Line &&
					((Line) children.get(i)).getStroke().equals(Color.BLACK)){
				children.remove(i);
			}
			i++;
		}
		if(startField.getText().equals(destField.getText())){ //prevent routes between same settlement
			destField.setText("Invalid Route!");
			return;
		}
		GraphNode<Settlement> start = Map.lookupNode(startField.getText());
		Settlement end = Map.lookupSettlement(destField.getText());
		if(start==null){
			startField.setText("Settlement Not Found!");
			return;
		}
		if(end==null){
			destField.setText("Settlement Not Found!");
			return;
		}
		if(((RadioButton)priorityGroup.getSelectedToggle()).getText().equals("Any Valid Route")){
			List<List<GraphNode<Settlement>>> paths = Map.findValidPaths(start, end);
			if(paths==null || paths.size()==0){
				startField.setText("No Route Found!");
				destField.setText("");
				return;
			}
			for(List<GraphNode<Settlement>> path:paths) {
				for (i=0;i<path.size()-1;i++) {
					Object[] routeInfo = {path.get(i).getData(), path.get(i+1).getData(),
							Map.getRoute(path.get(i).getData(), path.get(i + 1).getData())};  //route info for drawing routes {startPoint, endPoint, routeObject}
					drawRoute(routeInfo, Color.BLACK);
				}
			}
		}
		else {
			Settlement[] path = Map.findCheapestPath(start, end);
			if (path==null) {
				startField.setText("No Route Found!");
				destField.setText("");
				return;
			}
			for (i=0;i<path.length-1;i++) {
				Object[] routeInfo = {path[i], path[i + 1], Map.getRoute(path[i], path[i + 1])};
				drawRoute(routeInfo, Color.BLACK);
			}
		}
	}

	@FXML
	private void updateMaxRoutes(Event e){ //update maximum routes to be found by findValidPaths()
		int maxRoutes;
		if(e.getSource()==maxRoutesSlider){
			maxRoutes= (int) maxRoutesSlider.getValue();
			maxRoutesText.setText(Integer.toString(maxRoutes));
			Map.setMaxValidPaths(maxRoutes);
		}
		else{
			try {
				maxRoutes = Integer.parseInt(maxRoutesText.getText());
				if(maxRoutes<maxRoutesSlider.getMin() || maxRoutes>maxRoutesSlider.getMax()) throw new Exception();
				maxRoutesSlider.setValue(maxRoutes);
				Map.setMaxValidPaths(maxRoutes);
			}
			catch (Exception ex){
				maxRoutesText.setText("!");
			}
		}
	}

	@FXML
	private void updateRoutePriority(){ //update route priority, i.e. type of route weight, to be used by findCheapestPath()
		String buttonPriority = ((RadioButton)priorityGroup.getSelectedToggle()).getText();
		String priority="";
		switch (buttonPriority){
			case "Any Valid Route": priority="valid";
			break;
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
			GraphNode<Settlement> node = Map.lookupNode(placename);
			if (node == null) {
				waypointField.setText("Not a Settlement!");
			} else {
				if(!(Map.getWaypoints().contains(node) || Map.getExclusions().contains(node))) {
					Map.addWaypoint(node);
					waypointsView.setItems(FXCollections.observableList(Map.getWaypoints()));
					removeWaypointButton.setDisable(false);
					waypointField.clear();
				}
				else if(Map.getWaypoints().contains(node)){
					waypointField.setText("Waypoint Exists!");
				}
				else{
					waypointField.setText("Exclusion!");
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
			GraphNode<Settlement> node = Map.lookupNode(placename);
			if (node == null) {
				exclusionField.setText("Not a Settlement!");
			} else {
				if(!(Map.getExclusions().contains(node) || Map.getWaypoints().contains(node))) {
					Map.addExclusion(node);
					exclusionsView.setItems(FXCollections.observableList(Map.getExclusions()));
					removeExclusionButton.setDisable(false);
					exclusionField.clear();
				}
				else if(Map.getExclusions().contains(node)){
					exclusionField.setText("Exclusion Exists!");
				}
				else{
					exclusionField.setText("Waypoint!");
				}
			}
		}
		catch (Exception e){
			exclusionField.requestFocus();
		}
	}

	@FXML
	private void removeWaypoint(){
		GraphNode<Settlement> selected = (GraphNode<Settlement>) waypointsView.getSelectionModel().getSelectedItem();
		if(selected!=null){
			Map.removeWaypoint(selected);
			waypointsView.setItems(FXCollections.observableList(Map.getWaypoints()));
			if(waypointsView.getItems().size()==0) removeWaypointButton.setDisable(true);
		}
	}

	@FXML
	private void removeExclusion(){
		GraphNode<Settlement> selected = (GraphNode<Settlement>) exclusionsView.getSelectionModel().getSelectedItem();
		if(selected!=null){
			Map.removeExclusion(selected);
			exclusionsView.setItems(FXCollections.observableList(Map.getExclusions()));
			if(exclusionsView.getItems().size()==0) removeExclusionButton.setDisable(true);
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
		for(Settlement s:Map.getSettlements()){ //check if location already has an existing settlement
			if(s.getXPos()-10<XPos && s.getXPos()+10>XPos && s.getYPos()-10<YPos && s.getYPos()+10>YPos){
				addSettlementXPos.setText("Already");
				addSettlementYPos.setText("Occupied!");
				return;
			}
		}
		if(placename!=null && XPos!=-1 && YPos!=-1){
			Map.addSettlement(new Settlement(placename, XPos, YPos));
			showSettlements();
			addSettlementName.setText("Added!");
			addSettlementXPos.clear();
			addSettlementYPos.clear();
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
		if(start.equals(end)){
			addRouteStart.setText("Start and End Cannot");
			addRouteEnd.setText("Be The Same!");
		}
		try{
			int distance = Integer.parseInt(addRouteDist.getText());
			int difficulty = (int) addRouteDiff.getValue();
			int danger = (int) addRouteDanger.getValue();
			if(Map.getRoute(start, end)!=null){ //check if route already exists between settlements
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
		InputOutput.writeExclusions();
	}

	@FXML
	private void toggleView(){ //toggle between menu and settings
		menuPane.setVisible(!menuPane.isVisible());
		settingsPane.setVisible(!settingsPane.isVisible());
	}

	@FXML
	private void showRoutes(){ //show all routes on map
		if(showRoutesButton.isSelected()) {
			Object[][] routes = Map.getRoutes();
			for (Object[] r : routes) {
				drawRoute(r, new Color(0.5, 0.5, 0.5, 0.5));
			}
		}
		else{
			List<Node> children = mapPane.getChildren();
			int i=0;
			while(children.get(i) instanceof Line){
				while(children.get(i) instanceof Line &&
						((Line) children.get(i)).getStroke().equals(new Color(0.5, 0.5, 0.5, 0.5))){
					children.remove(i);
				}
				i++;
			}
		}
	}

	private void showSettlements(){ //place buttons for all settlements
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
			mapPane.getChildren().add(b);
		}
	}

	private void drawRoute(Object[] routeInfo, Paint colour){ //draw route with routeInfo={start, end, routeObject} and colour
		Settlement start = (Settlement) routeInfo[0];
		Settlement end = (Settlement) routeInfo[1];
		List<Node> children = mapPane.getChildren();
		List<Node> settlementButtons = children.subList(children.size()-Map.getNumSettlements(),children.size());
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
		Line line = new Line(startButton.getLayoutX()+8,startButton.getLayoutY()+8,
				endButton.getLayoutX()+8,endButton.getLayoutY()+8);
		line.setStrokeWidth(3);
		line.setStroke(colour);
		Route r = (Route) routeInfo[2];
		Tooltip t = new Tooltip("Distance: "+r.getDistance()+"km\nDifficulty: "+r.getDifficulty()+"\nDanger: "+r.getDanger());
		Tooltip.install(line, t);
		mapPane.getChildren().add(0,line);
	}

	@FXML
	private void initialize(){
		showSettlements();
		waitingForMapClick = startField;
		exclusionsView.setItems(FXCollections.observableList(Map.getExclusions()));
		if(exclusionsView.getItems().size()!=0) removeExclusionButton.setDisable(false);
	}
}