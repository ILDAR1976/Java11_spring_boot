package edu.ik_01.ui;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

@SuppressWarnings("restriction")
public class ControlPanelController extends AnchorPane {
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(MainController.class);
	private ControlView controlView = new ControlView(); 
	
	@PostConstruct
	public void initialize() {
		this.getChildren().add(controlView);
	}

	public  ControlView getControlView() {
		return controlView;
	}

	
	
	
}
