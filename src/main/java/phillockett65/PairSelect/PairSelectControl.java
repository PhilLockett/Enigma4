/*  CustomRotorController - a JavaFX based Custom Controller representing a Rotor.
 *
 *  Copyright 2024 Philip Lockett.
 *
 *  This file is part of CustomRotorController.
 *
 *  CustomRotorController is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CustomRotorController is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CustomRotorController.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Pop-up window that contains control buttons for the cancel, done and clear 
 * buttons and the PairSelect object.
 */
package phillockett65.PairSelect;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import phillockett65.Enigma.ButtonControl;
import phillockett65.Enigma.CustomControl.ControlEvent;
import phillockett65.Enigma.Model;
import phillockett65.PairSelect.PairSelect.PairEvent;

/**
 *
 * @author Phil
 */
public class PairSelectControl extends Stage {


    private Scene scene;
    private Model model;

    private VBox root;
    private TitledPane pane;
    private PairSelect pairSelect;
    private TextField field;

    private ButtonControl clearControl;
    private ButtonControl abortControl;
    private ButtonControl doneControl;

    private boolean plugboard = false;
    private boolean result = false;


    private void setPos() {
        final double dw = (model.getMainWidth() - this.getWidth()) / 2.0;
        final double x = model.getMainXPos() + dw;
        this.setX(x);

        final double y = model.getMainYPos() + 32.0;
        this.setY(y);
    }

    private void syncDoneButton() {
        boolean disabled = !plugboard;
        if (!plugboard) {
            disabled = pairSelect.size() < 12;
        }
        doneControl.setDisable(disabled);
    }


    private void syncPairs() {
        field.setText(getPairString());
    }

    private void syncUI() {
        syncDoneButton();
        syncPairs();
    }



    /**
     * Builds the options buttons as a HBox and includes the action event 
     * handlers for both the done and clear buttons.
     * @return the HBox that represents the options buttons.
     */
    private HBox buildOptions() {
        HBox options = new HBox();
        options.setSpacing(20.0);

        clearControl = new ButtonControl("Clear", false);
        Region region = new Region();
        abortControl = new ButtonControl("Abort", true);
        doneControl = new ButtonControl("Apply", true);
        
        clearControl.addEventFilter(ControlEvent.ANY, this::handleClearEvent);
        abortControl.addEventFilter(ControlEvent.ANY, this::handleAbortEvent);
        doneControl.addEventFilter(ControlEvent.ANY, this::handleDoneEvent);

        clearControl.setTooltip("Click to delete all entered pairs");
        abortControl.setTooltip("Click to discard changes and exit");
        doneControl.setTooltip("Click when all connections have been entered");

        options.getChildren().add(clearControl);
        options.getChildren().add(region);
        HBox.setHgrow(region, Priority.ALWAYS);
        options.getChildren().add(abortControl);
        options.getChildren().add(doneControl);

        return options;
    }

    private void handleClearEvent(ControlEvent event) {
        if (event.getEventType() == ControlEvent.CONTROL_PRESSED) {
            clear();
            syncUI();
        }
    }
 
    private void handleAbortEvent(ControlEvent event) {
        if (event.getEventType() == ControlEvent.CONTROL_PRESSED) {
            restoreSnapshot();
            result = false;
            close();
        }
    }
 
    private void handleDoneEvent(ControlEvent event) {
        if (event.getEventType() == ControlEvent.CONTROL_PRESSED) {
            result = true;
            close();
        }
    }
 
    /**
     * Builds the selected pairs display as a HBox.
     * @return the HBox that represents the selected pairs display.
     */
    private HBox buildSelected() {
        HBox selection = new HBox();

        field = new TextField();
        field.setEditable(false);
        field.setFocusTraversable(false);

        HBox.setHgrow(field, Priority.ALWAYS);
        selection.getChildren().add(field);

        return selection;
    }

    /**
     * Builds the User controls as a VBox.
     * @return the VBox that captures the User controls.
     */
    private VBox buildControlPanel() {
        VBox panel = new VBox();

        panel.setSpacing(10);
        panel.setPadding(new Insets(10.0));

        pairSelect = new PairSelect(plugboard);

        panel.getChildren().add(pairSelect);
        panel.getChildren().add(buildSelected());
        panel.getChildren().add(buildOptions());

        return panel;
    }

    private void init(boolean isPlugboard, String title) {
        model = Model.getInstance();
        plugboard = isPlugboard;

        this.resizableProperty().setValue(false);
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);

        this.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                setPos();
            }
        });

        pane = new TitledPane(title, buildControlPanel());
        pane.setCollapsible(false);
        
        root = new VBox();
        root.addEventHandler(PairEvent.LINK_CHANGE, event -> syncUI());
        root.getChildren().add(pane);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("PairSelect.css").toExternalForm());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                restoreSnapshot();
                result = false;
                close();
            }
        });

        this.setScene(scene);
    }


    /**
     * Construct a String represntation of the Pair set.
     * @return the String represntation.
     */
    public String getPairString() {
        return pairSelect.getPairString();
    }

    public ArrayList<String> getLinks() { return pairSelect.getLinks(); }
    public int[] getMap() { return pairSelect.getMap(); }
    public void clear() { pairSelect.clear(); }
    public void setLinks(ArrayList<String> links) { pairSelect.setLinks(links); }

    /**
     * Fabricate default pair values.
     */
    public void defaultSettings() {
        ArrayList<String> links = new ArrayList<String>();
        for (int i = 0; i  < 12; ++i) {
            final int a = i * 2;
            String pair = PairSelect.indexToLetter(a) + PairSelect.indexToLetter(a+1);
            links.add(pair);
        }
        setLinks(links);
    }

    private ArrayList<String> snapshot = new ArrayList<String>();

    private void takeSnapshot() {
        snapshot.clear();
        ArrayList<String> links = getLinks();

        for (String link : links) {
            snapshot.add(link);
        }
    }

    private void restoreSnapshot() {
        setLinks(snapshot);
    }

    /**
     * Constructor.
     */
    public PairSelectControl(boolean isPlugboard, String title) {
        super();
        init(isPlugboard, title);
    }


    /**
     * Launch the Pair Select Control and wait for user input.
     * @return true if the control was updated, false if cancelled.
     */
    public boolean showControl() {
        takeSnapshot();
        syncUI();
        this.showAndWait();

        return result;
    }

}
