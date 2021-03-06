package fr.axonic.avek.gui.components;

import fr.axonic.avek.gui.components.filelist.FileListView;
import fr.axonic.avek.gui.util.ConcurrentTaskManager;
import fr.axonic.avek.gui.util.UtilForTests;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nathaël N on 25/07/16.
 */
public class TestFileList extends ApplicationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestFileList.class);

    static {
        UtilForTests.disableGraphics();
    }

    private FileListView flv;

    @Override
    public void start(Stage stage) throws Exception {
        this.flv = new FileListView();

        Scene scene = new Scene(flv, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void before() throws IOException {
        UtilForTests.deleteFileArchi();
        UtilForTests.createFileArchi();
    }

    @After
    public void after() {
        UtilForTests.deleteFileArchi();
    }

    @Ignore
    @Test
    public void testAddFiles() throws Exception {
        ConcurrentTaskManager ctm = new ConcurrentTaskManager();

        // Get method FileListView.onAddFiles(File) with authorized use access
        Method method = FileListView.class.getDeclaredMethod("onAddFiles", List.class);
        method.setAccessible(true);

        assertEquals(0, flv.getFileList().getItems().size());

        ctm.runNowOnPlatform(() ->
                method.invoke(flv,
                        Collections.singletonList(
                                new File("./temp/toto/titi.txt"))));
        assertEquals(1, flv.getFileList().getItems().size());

        ctm.runNowOnPlatform(() ->
                method.invoke(flv,
                        Collections.singletonList(
                                new File("./temp/toto/tonton.txt"))));
        assertEquals(2, flv.getFileList().getItems().size());

        // File already added
        LOGGER.warn("[NOT A WARNING] A deliberate action should show a WARN message\n" +
                "    \"FileListView - File already added\" should following this (Tests)");
        ctm.runNowOnPlatform(() ->
                method.invoke(flv,
                        Collections.singletonList(
                                new File("./temp/toto/titi.txt"))));
        assertEquals("Folder contains: "+flv.getFileList().getItems(), 2, flv.getFileList().getItems().size());

        // File not found
        LOGGER.error("[NOT AN ERROR] A deliberate action should show a ERROR message\n" +
                "    \"FileListView - File not found\" should following this (Tests)");
        ctm.runNowOnPlatform(() ->
                method.invoke(flv,
                        Collections.singletonList(
                                new File("./temp/toto/ThisIsNotAFile.txt"))));
        assertEquals(2, flv.getFileList().getItems().size());
    }

/* TODO: Not working drag and drop tests
    @Test
	public void testDragAndDrop() throws InterruptedException {
		assertEquals(0, flv.getFileList().getItems().size());

		this.dragTo(new File("./temp/toto/titi.txt"), flv);
		//assertEquals(1, flv.getFileList().getItems().size());

		this.dragTo(new File("./temp/toto/tonton.txt"), flv);
		//assertEquals(2, flv.getFileList().getItems().size());

		// Already present so should not be added
		this.dragTo(new File("./temp/toto/titi.txt"), flv);
		//assertEquals(2, flv.getFileList().getItems().size());

		assertTrue(true);
	}

	private void dragTo(File inputFile, Node victim) throws InterruptedException {
		this.moveTo(0,0);

		final CountDownLatch countDownLatch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(inputFile));
				Clipboard.getSystemClipboard().setContent(content);
			} finally {
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
		this.press(MouseButton.PRIMARY);
		moveTo(victim);
		moveTo(0, 0);
		moveTo(victim);
		sleep(1000);
		this.release(MouseButton.PRIMARY);
	}*/
}
