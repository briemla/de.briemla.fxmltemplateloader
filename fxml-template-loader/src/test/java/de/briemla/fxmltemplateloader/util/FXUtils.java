package de.briemla.fxmltemplateloader.util;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class FXUtils {

	private static final CountDownLatch startLatch = new CountDownLatch(1);

	public static void startFxApplicationThread() throws InterruptedException {
		new Thread("TestApplication-Launcher") {
			@Override
			public void run() {
				super.run();
				Application.launch(TestApplication.class);
			}
		}.start();
		startLatch.await();
	}

	public static class TestApplication extends Application {

		@Override
		public void start(Stage primaryStage) throws Exception {
			startLatch.countDown();
		}

	}

}
