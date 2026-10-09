/*  Enigma3 - a JavaFX based enigma machine simulator.
 *
 *  Copyright 2024 Philip Lockett.
 *
 *  This file is part of Enigma3.
 *
 *  Enigma3 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Enigma3 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Enigma3.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Boilerplate code responsible for launching the JavaFX application. 
 */
package phillockett65.Enigma;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import phillockett65.Debug.Debug;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    PrimaryController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Enigma 4.0.0");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.resizableProperty().setValue(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(scene);

        controller = fxmlLoader.getController();

        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                final KeyCode keyCode = event.getCode();
                Debug.trace(DD, "stage.addEventFilter(KeyEvent.KEY_PRESSED, '" + keyCode + "')");
                if (keyCode.isLetterKey())
                    controller.keyPress(keyCode);
            }
        });

        stage.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                final KeyCode keyCode = event.getCode();
                Debug.trace(DD, "stage.addEventFilter(KeyEvent.KEY_RELEASED, '" + keyCode + "')");
                if (keyCode.isLetterKey())
                    controller.keyRelease(keyCode);
            }
        });

        stage.show();

        controller.init(stage);
    }

    @Override
    public void stop() throws Exception {
        controller.saveState();
    }

    public static void main(String[] args) {
        launch();
    }

}