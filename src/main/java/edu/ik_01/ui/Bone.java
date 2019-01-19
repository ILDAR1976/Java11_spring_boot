package edu.ik_01.ui;

import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Text;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.beans.property.SimpleDoubleProperty;

@SuppressWarnings({"restriction", "unused"})
public class Bone extends Group {
	private Logger logger = LoggerFactory.getLogger(Bone.class);
	
	private DoubleProperty pointAX;
	private DoubleProperty pointAY;
	private DoubleProperty pointAZ;
	
	private DoubleProperty pointBX;
	private DoubleProperty pointBY;
	private DoubleProperty pointBZ;
	
	private Color color;
	
	public Bone(Point3D pointA, Point3D pointB, Color color) {
	    this.color = color;
	    pointAX = new SimpleDoubleProperty(pointA.getX());
	    pointAY = new SimpleDoubleProperty(pointA.getY());
	    pointAZ = new SimpleDoubleProperty(pointA.getZ());
	    
	    pointBX = new SimpleDoubleProperty(pointB.getX());
	    pointBY = new SimpleDoubleProperty(pointB.getY());
	    pointBZ = new SimpleDoubleProperty(pointB.getZ());

	    draw();
	    bind();
	}
	
	public DoubleProperty getPointAX() {
		return pointAX;
	}

	public DoubleProperty getPointAY() {
		return pointAY;
	}

	public DoubleProperty getPointAZ() {
		return pointAZ;
	}

	public DoubleProperty getPointBX() {
		return pointBX;
	}

	public DoubleProperty getPointBY() {
		return pointBY;
	}

	public DoubleProperty getPointBZ() {
		return pointBZ;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void bind() {
		pointAX.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
		pointAY.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
		pointAZ.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
		pointBX.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
		pointBY.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
		pointBZ.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				draw();
			}
		});
	}
	
	public synchronized void draw() {
	    Point3D pointA = new Point3D(pointAX.get(), pointAY.get(), pointAZ.get());
	    Point3D pointB = new Point3D(pointBX.get(), pointBY.get(), pointBZ.get());
	    Point3D temp = pointA.subtract(pointB);
	    double Y = temp.getX() != 0 || temp.getZ() != 0 ? pointB.getY() : pointB.getY() > pointA.getY() ? pointB.getY() : pointA.getY();
	    Point3D dir = pointA.subtract(pointB).crossProduct(new Point3D(0, -1, 0));
	    double angle = Math.acos(pointA.subtract(pointB).normalize().dotProduct(new Point3D(0, -1, 0)));
	    double h1 = pointA.distance(pointB);
	   
	    PyramidMesh pyramid1 = new PyramidMesh(h1 - 20d,30d);
	    PyramidMesh pyramid2 = new PyramidMesh(-h1 + 90 ,30d);
	    
	    
	    final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
	    pyramid1.setMaterial(material);
	    pyramid1.getTransforms().addAll(new Translate(pointB.getX(), Y - h1 , pointB.getZ()),
	            new Rotate(-Math.toDegrees(angle), 0d, h1 , 0d, new Point3D(dir.getX(), -dir.getY(), dir.getZ())));
	    pyramid2.setMaterial(material);
	    pyramid2.getTransforms().addAll(new Translate(pointB.getX(), Y - h1 / 10, pointB.getZ()),
	            new Rotate(-Math.toDegrees(angle), 0d, h1 / 10, 0d, new Point3D(dir.getX(), -dir.getY(), dir.getZ())));
	    this.getChildren().clear();
	    this.getChildren().addAll(pyramid1, pyramid2);
	}
}
