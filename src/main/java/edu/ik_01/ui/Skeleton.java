package edu.ik_01.ui;

import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

public class Skeleton extends Parent {
	
	private List<Joint> joints = new ArrayList<>();
	
	public void addChildren(Node node) {
		this.getChildren().add(node);
	}

	public List<Joint> getJoints() {
		return joints;
	}
	
	
}
