package fr.axonic.avek.gui.components.jellybeans;

import fr.axonic.avek.gui.model.GUIEffect;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cduffau on 02/07/16.
 */
public class JellyBeanSelector extends VBox {
    private static final Logger LOGGER = LoggerFactory.getLogger(JellyBeanSelector.class);
    private static final URL FXML
            = JellyBeanSelector.class.getClassLoader().getResource("fr/axonic/avek/gui/components/jellybeans/JellyBeansSelector.fxml");
    private static final String CSS = "fr/axonic/avek/gui/components/jellybeans/jellyBeanSelector.css";

    @FXML
    private Button newExpEffectButton;
    @FXML
    private JellyBeanPane jellyBeanPane;
    @FXML
    private ComboBox<JellyBeanItem> comboBoxJellyBean;

    private GUIEffect jellyBeanChoice;

    // should be public
    public JellyBeanSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(FXML);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        LOGGER.info("Loading JellyBeanSelector... (fxml)");
        try {
            fxmlLoader.load();
            LOGGER.debug("JellyBeanSelector loaded.");
        } catch (IOException e) {
            LOGGER.error("Impossible to load FXML for JellyBeanSelector", e);
        }

        this.getStylesheets().add(CSS);
        LOGGER.info("Added css for JellyBeanSelector");
    }

    @FXML
    protected void initialize() {
        jellyBeanPane.onRemoveJellyBean(this::onRemoveJellyBean);
        jellyBeanPane.setAllJellyBeansEditable(true);
        updateJellyBeanChoice();

        comboBoxJellyBean.setConverter(
                new StringConverter<JellyBeanItem>() {
                    private final Map<String, JellyBeanItem> values = new HashMap<>();

                    @Override
                    public String toString(JellyBeanItem jellyBeanItem) {
                        if (jellyBeanItem == null) {
                            return "";
                        } else {
                            values.put(jellyBeanItem.getLabel(), jellyBeanItem);
                            return jellyBeanItem.getLabel();
                        }
                    }

                    @Override
                    public JellyBeanItem fromString(String s) {
                        try {
                            return values.get(s);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                });
    }

    @FXML
    void onSelectorHiding(Event event) {
        Platform.runLater(this::addJellyBean);
    }

    private void addJellyBean() {
        // Verify if JellyBean already created (this result is already selected)
        JellyBeanItem choice = comboBoxJellyBean.getValue();
        if (choice == null) {
            LOGGER.warn("Choice is null");
            return;
        }
        if (jellyBeanPane.contains(choice)) {
            LOGGER.warn("Choice already added: " + choice.getLinkedObject());
            return;
        }
        jellyBeanPane.addJellyBean(choice);
        jellyBeanChoice.add(choice);
        updateJellyBeanChoice();
    }

    private void onRemoveJellyBean(JellyBean effectName) {
        jellyBeanPane.remove(effectName);
        jellyBeanChoice.remove(effectName.getJellyBeanItem());
        updateJellyBeanChoice();
    }

    /**
     * Set Combo box entries for already selected sample to 'selectedResult' style, and others to 'notSelectedResult'
     * (typically, set selected sample entries in a grey color, and let the others black)
     */
    private void updateJellyBeanChoice() {
        comboBoxJellyBean.setCellFactory(
                new Callback<ListView<JellyBeanItem>, ListCell<JellyBeanItem>>() {
                    @Override
                    public ListCell<JellyBeanItem> call(ListView<JellyBeanItem> param) {
                        return new ListCell<JellyBeanItem>() {
                            @Override
                            public void updateItem(JellyBeanItem item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item.getLabel());
                                    getStyleClass().remove("selectedResult");
                                    getStyleClass().remove("notSelectedResult");
                                    getStyleClass().add(jellyBeanPane.contains(item) ? "selectedResult" : "notSelectedResult");
                                }
                            }
                        };
                    }
                });
    }

    public void setJellyBeansChoice(GUIEffect<?> exprMap) {
        jellyBeanChoice = exprMap;
        this.comboBoxJellyBean.setItems(
                FXCollections.observableArrayList(exprMap.getJellyBeanItemList()));
    }

    JellyBeanPane getJellyBeanPane() {
        return jellyBeanPane;
    }
}
