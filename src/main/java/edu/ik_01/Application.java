package edu.ik_01;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import javafx.scene.paint.PhongMaterial;
import edu.ik_01.ui.MainController;
import edu.ik_01.ui.Xform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.*;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;

@SuppressWarnings({ "restriction", "unused" })
@Lazy
@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {
	private Logger logger = LoggerFactory.getLogger(MainController.class);
	private final double ONE_FRAME = 1.0 / 24.0;
	private final double DELTA_MULTIPLIER = 200.0;
	private final double CONTROL_MULTIPLIER = 0.1;
	private final double SHIFT_MULTIPLIER = 0.1;
	private final double ALT_MULTIPLIER = 0.5;
	
	private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    
    private  Group root = new Group();
	private  Group axisGroup = new Group();
	
	private  Xform world = new Xform();
    private  PerspectiveCamera camera = new PerspectiveCamera(true);
    private  Xform cameraXform = new Xform();
    private  Xform cameraXform2 = new Xform();
    private  Xform cameraXform3 = new Xform();
    private  double cameraDistance = 1450;
    private  Xform moleculeGroup = new Xform();
	
    private Rotate xRotate;
    private Rotate yRotate;
	
	private double scenex, sceney = 0;
	private final DoubleProperty angleX = new SimpleDoubleProperty(0);
	private final DoubleProperty angleY = new SimpleDoubleProperty(0);
	private double anchorAngleX = 0;
	private double anchorAngleY = 0;

    @Value("JavaFX and spring application")
    private String windowTitle;
    
    @Value("Controll panel")
    private String controlPaneTitle;

    @Qualifier("mainView")
    @Autowired
    private ConfigurationControllers.View view;
    
    @Qualifier("controlPanelView")
    @Autowired
    private ConfigurationControllers.View controlView;

    @Override
    public void start(Stage stage) throws Exception {
    	buildScene();
    	buildCamera();
        buildAxes();
    	
    	Scene scene = new Scene(root, 1024, 768, true, SceneAntialiasing.BALANCED);
    	
    	scene.setFill(Color.GREY);
        handleMouse(scene, world);       
        handleKeyboard(scene, root);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setX(0);
        stage.setY(0);
        stage.show();
        
        
        Stage stageControl = new Stage();
        stageControl.setTitle(controlPaneTitle);
       
        stageControl.setScene(new Scene(controlView.getView(), 700, 440, true));
        stageControl.setX(600);
        stageControl.setResizable(true);
        stageControl.show();
        
        stage.setOnCloseRequest(event -> stageControl.close());
        
        scene.setCamera(camera);
    }

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
    	launchApp(Application.class, args);
    }

	public static void fugureRotate(Group figure, Scene scene, DoubleProperty angleX, DoubleProperty angleY,
			double anchorAngleX, double anchorAngleY, double scenex, double sceney) {
		Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
		Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
		figure.getTransforms().addAll(xRotate, yRotate);
		// Use Binding so your rotation doesn't have to be recreated
		xRotate.angleProperty().bind(angleX);
		yRotate.angleProperty().bind(angleY);
		// Start Tracking mouse movements only when a button is pressed
		scene.setOnMousePressed(event -> {
			event.getSceneX();
			event.getSceneY();
			angleX.get();
			angleY.get();
		});
		// Angle calculation will only change when the button has been pressed
		scene.setOnMouseDragged(event -> {
			angleX.set(anchorAngleX - (scenex - event.getSceneY()));
			angleY.set(anchorAngleY + sceney - event.getSceneX());
		});

	}
	
    private void buildScene() {
    	world.getChildren().add(view.getView());
        root.getChildren().add(world);
    }
	
    private void buildCamera() {
    	
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-cameraDistance);
        cameraXform.ry.setAngle(320.0);
        cameraXform.rx.setAngle(40);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(3240.0, 1, 1);
        final Box yAxis = new Box(1, 3240.0, 1);
        final Box zAxis = new Box(1, 1, 3240.0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        world.getChildren().addAll(axisGroup);
    }
    
    private void handleKeyboard(Scene scene, final Node root) {
        final boolean moveCamera = true;
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Duration currentTime;
                switch (event.getCode()) {
                    case T:
                        if (event.isShiftDown()) {
                            cameraXform.ry.setAngle(0.0);
                            cameraXform.rx.setAngle(0.0);
                            camera.setTranslateZ(-300.0);
                        }
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        break;
                    case Y:
                        if (event.isControlDown()) {
                            if (axisGroup.isVisible()) {
                                axisGroup.setVisible(false);
                            } else {
                                axisGroup.setVisible(true);
                            }
                        }
                        break;
                   
                    case UP:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() - 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() - 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - 2.0 * ALT_MULTIPLIER);
                        } else if (event.isShiftDown()) {
                            double z = camera.getTranslateZ();
                            double newZ = z + 5.0 * SHIFT_MULTIPLIER;
                            camera.setTranslateZ(newZ);
                        }
                        
                        
                        break;
                    case DOWN:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() + 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setY(cameraXform2.t.getY() + 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 2.0 * ALT_MULTIPLIER);
                        } else if (event.isShiftDown()) {
                            double z = camera.getTranslateZ();
                            double newZ = z - 5.0 * SHIFT_MULTIPLIER;
                            camera.setTranslateZ(newZ);
                        }
                        break;
                    case RIGHT:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() + 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() - 10.0 * ALT_MULTIPLIER);
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() + 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() - 2.0 * ALT_MULTIPLIER);
                        }
                        break;
                    case LEFT:
                        if (event.isControlDown() && event.isShiftDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() - 10.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown() && event.isShiftDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() + 10.0 * ALT_MULTIPLIER);  // -
                        } else if (event.isControlDown()) {
                            cameraXform2.t.setX(cameraXform2.t.getX() - 1.0 * CONTROL_MULTIPLIER);
                        } else if (event.isAltDown()) {
                            cameraXform.ry.setAngle(cameraXform.ry.getAngle() + 2.0 * ALT_MULTIPLIER);  // -
                        }
                        break;
                }
            }
        });
    }

    private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;
                double modifierFactor = 0.1;

                if (me.isControlDown()) {
                    modifier = 0.1;
                }
                if (me.isShiftDown()) {
                    modifier = 10.0;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0);  // +
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -
                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
            }
        });
    }

 
}
