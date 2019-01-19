package edu.ik_01.ui;

import javafx.scene.Group;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.Node;
import javafx.beans.property.SimpleDoubleProperty;

@SuppressWarnings("restriction")
public class ControlView extends Group{
	private final int CELL_ROW_SIZE = 100;
	private final int CELL_COLUMN_SIZE = 5;
	private final double STEP = 30d;
	
	private double x = 30d;
	private double y = 30d;
	private double x2 = 180;
	private double y2 = 100d;
	
	private NiceSlider s_mxx = new NiceSlider(x,y        ,"target x: ", -1000d, 1000d, -50d);
	private NiceSlider s_mxy = new NiceSlider(x,y += STEP,"target y: ", -1000d, 1000d, 50d);
	private NiceSlider s_mxz = new NiceSlider(x,y += STEP,"target z: ", -1000d, 1000d, 50d);

	private NiceSlider s_myx = new NiceSlider(x,y += STEP,"myx: ", -1000d, 1000d, -50d);
	private NiceSlider s_myy = new NiceSlider(x,y += STEP,"myy: ", -1000d, 1000d, 50d);
	private NiceSlider s_myz = new NiceSlider(x,y += STEP,"myz: ", -1000d, 1000d, 50d);

	private NiceSlider s_mzx = new NiceSlider(x,y += STEP,"mzx: ", -1000d, 1000d, 50d);
	private NiceSlider s_mzy = new NiceSlider(x,y += STEP,"mzy: ", -1000d, 1000d, 50d);
	private NiceSlider s_mzz = new NiceSlider(x,y += STEP,"mzz: ", -1000d, 1000d, -50d);

	private NiceSlider s_tx = new NiceSlider(x,y += STEP,"tx: ", -1000d, 1000d, 50d);
	private NiceSlider s_ty = new NiceSlider(x,y += STEP,"ty: ", -1000d, 1000d, 50d);
	private NiceSlider s_tz = new NiceSlider(x,y += STEP,"tz: ", -1000d, 1000d, 50d);


	private DoubleProperty rotateX = new SimpleDoubleProperty(0d);
	private DoubleProperty rotateY = new SimpleDoubleProperty(0d);
	private DoubleProperty rotateZ = new SimpleDoubleProperty(0d);
	
	private Label[][] label = new Label[4][3];
	private GridPane gridPane = new GridPane();
	
	public ControlView() {
		this.getChildren().addAll(s_mxx, s_mxy, s_mxz);
		this.getChildren().addAll(s_myx, s_myy, s_myz);
		this.getChildren().addAll(s_mzx, s_mzy, s_mzz);
		this.getChildren().addAll(s_tx,  s_ty,  s_tz);
	}
	
	public Label[][] getLabel() {
		return label;
	}
	
	public DoubleProperty getRotateX() {
		return rotateX;
	}

	public DoubleProperty getRotateY() {
		return rotateY;
	}

	public DoubleProperty getRotateZ() {
		return rotateZ;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public NiceSlider getS_mxx() {
		return s_mxx;
	}

	public NiceSlider getS_mxy() {
		return s_mxy;
	}

	public NiceSlider getS_mxz() {
		return s_mxz;
	}

	public NiceSlider getS_myx() {
		return s_myx;
	}

	public NiceSlider getS_myy() {
		return s_myy;
	}

	public NiceSlider getS_myz() {
		return s_myz;
	}

	public NiceSlider getS_mzx() {
		return s_mzx;
	}

	public NiceSlider getS_mzy() {
		return s_mzy;
	}

	public NiceSlider getS_mzz() {
		return s_mzz;
	}

	public NiceSlider getS_tx() {
		return s_tx;
	}

	public NiceSlider getS_ty() {
		return s_ty;
	}

	public NiceSlider getS_tz() {
		return s_tz;
	}

	public static class NiceSlider extends Group {

		private final double DIAS = 3d;
		private final double MULTIPLIER = 6d;
		private final double LDX_DEFAULT = 0d;
		private final double LDY_DEFAULT = -15d;
		private final double VDX_DEFAULT = 150d;
		private final double VDY_DEFAULT = -15d;
		
		private Slider slider;
		private Label label;
		private Label valueLabel;
		private double width = 450;
		private double height = 20;
		private double value = 1d;

		private NumberBinding sumlX;
		private NumberBinding sumlY;
		private NumberBinding sumvX;
		private NumberBinding sumvY;

		private SimpleDoubleProperty ldX = new SimpleDoubleProperty(LDX_DEFAULT);
		private SimpleDoubleProperty ldY = new SimpleDoubleProperty(LDY_DEFAULT);

		private SimpleDoubleProperty vdX = new SimpleDoubleProperty(VDX_DEFAULT);
		private SimpleDoubleProperty vdY = new SimpleDoubleProperty(VDY_DEFAULT);

		public NiceSlider(double x, double y, String label, double min, double max, double value) {
			this(x, y, label);
			this.slider.setMin(min);
			this.slider.setMax(max);
			this.slider.setValue(value);
		}
	
		public NiceSlider(double x, double y, String label) {
			slider = new Slider();
			slider.layoutXProperty().set(x);
			slider.layoutYProperty().set(y);
			slider.setPrefWidth(width);
			slider.setMaxHeight(height);
			slider.valueProperty().set(this.value);
			
			slider.setShowTickMarks(true);
			
			this.label = new Label(label);

			sumlX = slider.layoutXProperty().add(ldX);
			this.label.layoutXProperty().bind(sumlX);

			sumlY = slider.layoutYProperty().add(ldY);
			this.label.layoutYProperty().bind(sumlY);

			this.valueLabel = new Label();

			vdX.set( label.length() * MULTIPLIER + DIAS);
			sumvX = slider.layoutXProperty().add(vdX);
			this.valueLabel.layoutXProperty().bind(sumvX);

			sumvY = slider.layoutYProperty().add(vdY);
			this.valueLabel.layoutYProperty().bind(sumvY);

			this.valueLabel.textProperty().bind(slider.valueProperty().asString());

			getChildren().addAll(this.label, valueLabel, slider);
		}

		public DoubleProperty getValue() {
			return this.slider.valueProperty();
		}

	}

}
