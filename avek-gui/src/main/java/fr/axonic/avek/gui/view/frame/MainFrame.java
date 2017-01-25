package fr.axonic.avek.gui.view.frame;

import fr.axonic.avek.engine.WrongEvidenceException;
import fr.axonic.avek.engine.provider.MockedArgumentationSystem;
import fr.axonic.avek.graph.ArgumentationDiagram;
import fr.axonic.avek.gui.api.GUIAPIImpl;
import fr.axonic.avek.gui.view.AbstractView;
import fr.axonic.validation.exception.VerificationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Nathaël N on 26/07/16.
 */
public class MainFrame extends BorderPane {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    private static final URL FXML = MainFrame.class.getClassLoader()
            .getResource("fr/axonic/avek/gui/view/frame/MainFrame.fxml");

    @FXML
    private Button btnStrategy;

    public MainFrame() {
        FXMLLoader fxmlLoader = new FXMLLoader(FXML);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        LOGGER.info("Loading MainFrame... (fxml)");
        try {
            fxmlLoader.load();
            LOGGER.debug("MainFrame loaded.");
        } catch (IOException e) {
            LOGGER.fatal("Impossible to load FXML for MainFrame", e);
        }
    }

    @FXML
    private void onClickStrategyButton(ActionEvent event) {
        GUIAPIImpl.getInstance().onStrategySubmitted();
    }

    // TODO ↓↓↓↓ >>>> TEMPORARY <<<< ↓↓↓↓
    @FXML
    private void onClickGraphButton(ActionEvent event) {
        try {
            ArgumentationDiagram.show(MockedArgumentationSystem.getAXONICArgumentationSystem().getSteps());
        } catch (VerificationException | WrongEvidenceException e) {
            LOGGER.error("Cannot show graph now");
        }
    }
    // TODO ↑↑↑↑ >>>> TEMPORARY <<<< ↑↑↑↑


    public void setView(AbstractView av) {
        if(!av.isLoaded())
            av.load();

        // remove abstract view currently loaded
        setCenter(av);
    }

    public void setStrategyButtonLabel(String strategyButtonLabel) {
        btnStrategy.setVisible(true);
        btnStrategy.setText(strategyButtonLabel);
    }

    public void hideStrategyButton() {
        btnStrategy.setVisible(false);
    }
}
