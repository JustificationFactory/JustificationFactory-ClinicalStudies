package fr.axonic.avek.gui;

import fr.axonic.avek.gui.util.UtilForTests;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by Nathaël N on 11/08/16.
 */
public class TestMain extends ApplicationTest {
    static {
        UtilForTests.disableGraphics();
    }

    @Override
    public void start(Stage stage) throws Exception {
        new Main().start(stage);
    }

    @Ignore
    @Test
    public void test() {
        assertTrue("Application cannot be launched", true);
        clickOn("#btnStrategy");
        clickOn("#comboBox");
        type(KeyCode.DOWN, 3);
        type(KeyCode.ENTER);
        clickOn("#submit");
        clickOn("#btnStrategy");
    }
}
