package edu.ik_01.ui;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import edu.ik_01.ConfigurationControllers;
import javax.annotation.PostConstruct;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

@SuppressWarnings({ "restriction", "unused" })

public class MainController {
	private Logger logger = LoggerFactory.getLogger(MainController.class);
	@FXML
	private AnchorPane mainAnchor;


	@Qualifier("controlPanelView")
	@Autowired
	private ConfigurationControllers.View controlView;

	/*
	 * A working variables
	 */
	private final int N = 3 + 1;
	private final double TOLIRANCE = .1d;
	private Joint target;
	private Joint b;
	private Joint[] p = new Joint[N];
	private Bone[] l = new Bone[N - 1];

	private double distance;
	private double distanceBreak;
	private double DIFa;
	private double[] d = new double[N - 1];
	private double[] r = new double[N - 1];
	private double[] lambda = new double[N];

	@FXML
	public void initialize() {
	}

	@SuppressWarnings({})
	@PostConstruct
	public void init() {
		Group grp = new Group();
		ControlView control = ((ControlPanelController) controlView.getController()).getControlView();
		target = new Joint("target", new Affine(new Translate(-50d, 50d, 50d)), Color.TRANSPARENT);
		int i = 0;
		p[1] = new Joint("joint_" + ++i, new Affine(new Translate(50d, 50d, 50d)), Color.BLUE);
		p[2] = new Joint("joint_" + ++i, new Affine(new Translate(-50d, 50d, 50d)), Color.BLUE);
		p[3] = new Joint("joint_" + ++i, new Affine(new Translate(-150d, 50d, 50d)), Color.GREEN);

		l[1] = new Bone(new Point3D(50d, 50d, 50d), new Point3D(-50d, 50d, 50d), Color.BLUE);
		l[2] = new Bone(new Point3D(-50d, 50d, 50d), new Point3D(-150d, 50d, 50d), Color.BLUE);

		for (i = 1; i <= (N - 2); i++) {
			d[i] = getDistance(p[i], p[i + 1]);
			distanceBreak += d[i];
		}

		bind2(target, control);

		grp.getChildren().addAll( p[1], p[2], p[3], l[1], l[2]);

		mainAnchor.getChildren().addAll(grp);
	}

	private void recalculation() {
		b = new Joint("buffer");
		// Дистанция между корнем и целью
		distance = getDistance(p[1], target);
		// Проверяем достижимость цели
		if (distance > distanceBreak) {
			// цель недостижима
			for (int i = 1; i<=(N-2); i++) {
				// Найдем дистанцию r[i] между целью t и узлом p[i]
				r[i] = getDistance(p[i], target);
				lambda[i] = d[i] / r[i];
				// Находим новую позицию узла p[i]
				calcNewPosition(p[i], target, lambda[i], true);
			}
		} else {
			// Цель достижима; т.о. b будет новой позицией узла p[1]
			setPosition(b, p[1]);
			// Проверяем, не выше ли дистанция между конечным узлом p[n] и
			// целевой позицией t значения терпимости (tolerance)
			DIFa = getDistance(p[N - 1], target);
			double oldDIFa = 0d;
			
			while (DIFa > TOLIRANCE) {
				// Этап 1 : прямое следование
				// Устанавливаем конечный узел p[n] в качестве цели (вероятно, имелось ввиду
				// "ставим на позицию цели" - прим. перев.)
				setPosition(p[N - 1], target);
				
				for (int j = (N - 2); j >= 1; j--) {
					// Получаем расстояние r[i] между узлом p[j] и новой позицией p[j+1]
					r[j] = getDistance(p[j + 1], p[j]);
					lambda[j] = d[j] / r[j];
					// Вычисляем новую позицию узла p[j]
					calcNewPosition(p[j + 1], p[j], lambda[j], false);
				}
				
				if ( (DIFa - oldDIFa >= -.1d) && (DIFa - oldDIFa <= .1d) ) break; else oldDIFa = DIFa;
				// Этап 2: обратное следование
				// Устанавливаем корневому элементу p[1] начальную позицию
				setPosition(p[1], b);
				for (int i = 1; i < (N - 1); i++) {
					// Получаем дистанцию r[i] между узлом p[i+1] и позицией p[i]
					r[i] = getDistance(p[i + 1], p[i]);
					lambda[i] = d[i] / r[i];
					// Получаем новую позицию узла p[i+1]
					calcNewPosition(p[i], p[i + 1], lambda[i], false);
				}

				DIFa = getDistance(p[N - 1], target);
			}
		}
	}

	private Point3D scalarMul(Point3D inp, double scalar) {
		return new Point3D(scalar * inp.getX(), scalar * inp.getY(), scalar * inp.getZ());
	}

	private synchronized Joint calcNewPosition(Joint a, Joint t, double scalar, boolean flag) {

		Point3D point = scalarMul(new Point3D(a.getAffine().getTx(), a.getAffine().getTy(), a.getAffine().getTz()),
				1 - scalar)
						.add(scalarMul(new Point3D(t.getAffine().getTx(), t.getAffine().getTy(), t.getAffine().getTz()),
								scalar));

		if (flag) {
			a.getAffine().setTx(point.getX());
			a.getAffine().setTy(point.getY());
			a.getAffine().setTz(point.getZ());
			return a;
		} else {
			t.getAffine().setTx(point.getX());
			t.getAffine().setTy(point.getY());
			t.getAffine().setTz(point.getZ());
			return t;
		}
	}

	private synchronized Joint calcNewPosition(Joint a, Joint t, Joint c, double scalar) {
		Joint joint = new Joint("");
		joint = calcNewPosition(a, t, scalar, true);
		c.getAffine().setTx(joint.getAffine().getTx());
		c.getAffine().setTy(joint.getAffine().getTy());
		c.getAffine().setTz(joint.getAffine().getTz());
		return c;
	}

	private double getDistance(Joint a, Joint b) {
		return (new Point3D(a.getAffine().getTx(), a.getAffine().getTy(), a.getAffine().getTz()))
				.distance((new Point3D(b.getAffine().getTx(), b.getAffine().getTy(), b.getAffine().getTz())));
	}

	private void bind(Joint target, ControlView control) {
		target.getAffine().txProperty().bind(control.getS_mxx().getValue());
		target.getAffine().tyProperty().bind(control.getS_mxy().getValue());
		target.getAffine().tzProperty().bind(control.getS_mxz().getValue());

		target.getAffine().txProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});
		target.getAffine().tyProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});
		target.getAffine().tzProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});

		l[1].getPointAX().bind(p[1].getAffine().txProperty());
		l[1].getPointAY().bind(p[1].getAffine().tyProperty());
		l[1].getPointAZ().bind(p[1].getAffine().tzProperty());

		l[1].getPointBX().bind(p[2].getAffine().txProperty());
		l[1].getPointBY().bind(p[2].getAffine().tyProperty());
		l[1].getPointBZ().bind(p[2].getAffine().tzProperty());

		l[2].getPointAX().bind(p[2].getAffine().txProperty());
		l[2].getPointAY().bind(p[2].getAffine().tyProperty());
		l[2].getPointAZ().bind(p[2].getAffine().tzProperty());

		l[2].getPointBX().bind(p[3].getAffine().txProperty());
		l[2].getPointBY().bind(p[3].getAffine().tyProperty());
		l[2].getPointBZ().bind(p[3].getAffine().tzProperty());

		l[3].getPointAX().bind(p[3].getAffine().txProperty());
		l[3].getPointAY().bind(p[3].getAffine().tyProperty());
		l[3].getPointAZ().bind(p[3].getAffine().tzProperty());

		l[3].getPointBX().bind(p[4].getAffine().txProperty());
		l[3].getPointBY().bind(p[4].getAffine().tyProperty());
		l[3].getPointBZ().bind(p[4].getAffine().tzProperty());

		l[4].getPointAX().bind(p[4].getAffine().txProperty());
		l[4].getPointAY().bind(p[4].getAffine().tyProperty());
		l[4].getPointAZ().bind(p[4].getAffine().tzProperty());

		l[4].getPointBX().bind(p[5].getAffine().txProperty());
		l[4].getPointBY().bind(p[5].getAffine().tyProperty());
		l[4].getPointBZ().bind(p[5].getAffine().tzProperty());
	}

	private void bind2(Joint target, ControlView control) {
		target.getAffine().txProperty().bind(control.getS_mxx().getValue());
		target.getAffine().tyProperty().bind(control.getS_mxy().getValue());
		target.getAffine().tzProperty().bind(control.getS_mxz().getValue());

		target.getAffine().txProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});
		target.getAffine().tyProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});
		target.getAffine().tzProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number new_val, Number old_val) {
				listener();
			}
		});

		l[1].getPointAX().bind(p[1].getAffine().txProperty());
		l[1].getPointAY().bind(p[1].getAffine().tyProperty());
		l[1].getPointAZ().bind(p[1].getAffine().tzProperty());

		l[1].getPointBX().bind(p[2].getAffine().txProperty());
		l[1].getPointBY().bind(p[2].getAffine().tyProperty());
		l[1].getPointBZ().bind(p[2].getAffine().tzProperty());

		l[2].getPointAX().bind(p[2].getAffine().txProperty());
		l[2].getPointAY().bind(p[2].getAffine().tyProperty());
		l[2].getPointAZ().bind(p[2].getAffine().tzProperty());

		l[2].getPointBX().bind(p[3].getAffine().txProperty());
		l[2].getPointBY().bind(p[3].getAffine().tyProperty());
		l[2].getPointBZ().bind(p[3].getAffine().tzProperty());

	}

	private synchronized void listener() {
		recalculation();
	}

	private void setPosition(Joint a, Joint b) {
		a.getAffine().setTx(b.getAffine().getTx());
		a.getAffine().setTy(b.getAffine().getTy());
		a.getAffine().setTz(b.getAffine().getTz());
	}

}
