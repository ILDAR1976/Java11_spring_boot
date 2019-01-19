package edu.ik_01.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Group;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.paint.Color;

@SuppressWarnings({ "restriction", "unused" })
public final class Joint extends Group {
	private Logger logger = LoggerFactory.getLogger(Joint.class);
    private final float WIDTH = 10f;
	private String name;
    private Affine affine;
    private PhongMaterial material;
 
    public Joint() {
    	super();
    	this.affine = new Affine();
    	this.material = new PhongMaterial(Color.GRAY);
    	this.getTransforms().add(affine);
    	this.getChildren().add(new Sphere(10d));
    	//addMeshView();
    }
    
    public Joint(String name) {
        this();
        this.name = name;
    }

    public Joint(String name, Color color) {
        this(name);
        setColor(color);
    }

    public Joint(String name, Affine affine) {
        this(name);
        this.affine = affine;
        this.getTransforms().clear();
        this.getTransforms().add(this.affine);
    }

    public Joint(String name, Affine affine, Color color) {
        this(name, affine);
        this.getChildren().clear();
        setColor(color);
    }
    
	public Affine getAffine() {
		return affine;
	}

	public void setAffine(Affine affine) {
		this.affine = affine;
	}

	public TriangleMesh createCubeMesh() {

        float width = WIDTH;
        float points[] = {
                -width, -width, -width,
                width, -width, -width,
                width, width, -width,
                -width, width, -width,
                -width, -width, width,
                width, -width, width,
                width, width, width,
                -width, width, width};

        float texCoords[] = {0, 0, 1, 0, 1, 1, 0, 1};

        int faceSmoothingGroups[] = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        int faces[] = {
                0, 0, 2, 2, 1, 1,
                2, 2, 0, 0, 3, 3,
                1, 0, 6, 2, 5, 1,
                6, 2, 1, 0, 2, 3,
                5, 0, 7, 2, 4, 1,
                7, 2, 5, 0, 6, 3,
                4, 0, 3, 2, 0, 1,
                3, 2, 4, 0, 7, 3,
                3, 0, 6, 2, 2, 1,
                6, 2, 3, 0, 7, 3,
                4, 0, 1, 2, 5, 1,
                1, 2, 4, 0, 0, 3,
        };

        final TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);
        mesh.getFaceSmoothingGroups().setAll(faceSmoothingGroups);

        return mesh;
    }

    public void addMeshView() {
        final MeshView meshView = new MeshView(createCubeMesh());
		meshView.setMaterial(material);
		meshView.setDrawMode(DrawMode.FILL);
		meshView.setCullFace(CullFace.BACK);
        getChildren().add(meshView);
    }
   
    private void setColor(Color color) {
	    this.getChildren().clear();
	    this.material = new PhongMaterial(color);
	    
	    //addMeshView();
	    Sphere sphere = new Sphere(10d);
	    sphere.setMaterial(material);
	    this.getChildren().add(sphere);
    }

    @Override
    public String toString() {
    	
    	//return " Joint: " + this.name + " (x: " + this.affine.getTx() + " y: " + this.affine.getTy() + " z: " + this.affine.getTz() + ")";
    	return " Joint: " + this.name + " (x: " + Math.round(this.affine.getTx()) + " y: " + Math.round(this.affine.getTy()) + " z: " + Math.round(this.affine.getTz()) + ")";
    }
}
