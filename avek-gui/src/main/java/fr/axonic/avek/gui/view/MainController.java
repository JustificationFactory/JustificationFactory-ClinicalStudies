package fr.axonic.avek.gui.view;

import fr.axonic.avek.gui.model.BooleanExpEffect;
import fr.axonic.avek.gui.model.IExpEffect;
import fr.axonic.avek.gui.model.StringExpEffect;
import fr.axonic.avek.gui.view.expEffects.JellyBean;
import fr.axonic.avek.gui.view.expEffects.JellyBeansSelector;
import fr.axonic.avek.gui.view.expParameters.ExpParameters;
import fr.axonic.avek.gui.view.expSubject.ExpSubject;
import fr.axonic.avek.model.MonitoredSystem;
import fr.axonic.avek.model.base.ADate;
import fr.axonic.avek.model.base.ANumber;
import fr.axonic.avek.model.base.AString;
import fr.axonic.avek.model.base.AVar;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class MainController {

	@FXML
	private Button btnStrategy;
	@FXML
	private ExpParameters paneExpParameters;
	@FXML
	private ExpSubject expSubject;
	@FXML
	private JellyBeansSelector jellyBeansSelector;

	@FXML
	protected void initialize() {
		// TODO DEBUG ONLY ↓↓↓
			MonitoredSystem ms = new MonitoredSystem(42);
			ms.addCategory("Category 1");
			ms.addAVar("Category 1", new AString("a string", "strval1"));
			ms.addAVar("Category 1", new ANumber("an integer", 123456789));
			ms.addAVar("Category 1", new ANumber("a double", 12345.6789));
			ms.addAVar("Category 1", new ADate("a date", Calendar.getInstance().getTime()));

			ms.addCategory("Category 2");
			ms.addAVar("Category 2", new ANumber("an integer", 987654321));
			ms.addAVar("Category 2", new ANumber("a double", 98765.4321));

			List<IExpEffect> expEffects = new ArrayList<>();
			for (int i = 1; i <= 30; i++)
				expEffects.add(i%3==1?
						new StringExpEffect("SEffect " + i):
						new BooleanExpEffect("BEffect " + i));
		// end DEBUG ONLY ↑↑↑


		// Fill experiment subject informations
		expSubject.setData(ms);

		// Fill experiment effects list
		jellyBeansSelector.setJellyBeansChoice(FXCollections.observableArrayList(expEffects));
	}

	@FXML
	void onClicStrategyButton(ActionEvent event) {
		btnStrategy.setDisable(true);
	}
}

