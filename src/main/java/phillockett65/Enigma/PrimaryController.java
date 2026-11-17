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
 * PrimaryController is the class that is responsible for centralizing control.
 * It is instantiated by the FXML loader creates the Model.
 * 
 * Each handler should always update the model with the changed data first, 
 * then act on the new state of the model.
 * 
 * The order of the code should match the order of the layout when possible.
 */
package phillockett65.Enigma;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import phillockett65.Debug.Debug;
import phillockett65.Enigma.CustomControl.ControlEvent;

public class PrimaryController {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private Model model;



    /************************************************************************
     * General support code.
     */

    private static final String TOPBARICON = "top-bar-icon";
    private static final String ICONHOVER = "top-bar-icon-hover";

    // List of Nodes that are re-styled based on mouse hovers.
    private ObservableList<Node> hoverList = FXCollections.observableArrayList();


    /************************************************************************
     * Support code for the Initialization of the Controller.
     */

    private Stage stage;


    /**
     * Constructor.
     */
    public PrimaryController() {
        Debug.trace(DD, "PrimaryController constructed.");
        model = Model.getInstance();
    }

    /**
     * Called by the FXML mechanism to initialize the controller. Called after 
     * the constructor to initialise all the controls.
     */
    @FXML public void initialize() {
        Debug.trace(DD, "PrimaryController initialized.");
        model.initialize();

        initializeTopBar();
        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();
    }

    /**
     * Called by Application after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init(Stage stage) {
        Debug.trace(DD, "PrimaryController init.");
        this.stage = stage;
        model.init(stage);
        syncUI();
        if (!model.isDefaulted()) {
            stage.setX(model.getMainXPos());
            stage.setY(model.getMainYPos());
        }

        headingLabel.setText(model.getTitle());

        Scene scene = stage.getScene();
        scene.setOnKeyPressed(event -> keyPress(event.getCode()));
        scene.setOnKeyReleased(event -> keyRelease(event.getCode()));
    }

    /**
     * Called by Application on shutdown.
     */
    public void saveState() {
        // Only save window position just before saving state.
        model.setMainPos(stage.getX(), stage.getY());

        // Write current state to disc using the latest version of DataStore.
        DataStore1.writeData();
    }

    /**
     * Synchronise all controls with the model. This should be the last step 
     * in the initialisation.
     */
    public void syncUI() {
        reflectorChoicebox.setValue(model.getReflectorChoice());
        setReconfigurable();
        configureControl.setSelected(model.isReconfigurable());

        displayPairs();

        fourthWheelControl.setSelected(model.isFourthWheel());
        showStepsControl.setSelected(model.isShow());

        displayPlugs();
    }


    /************************************************************************
     * Non production code for "Cancel" symbol SVG path generation.
     */

    // private double round(double value) {
    //     final double SCALE = 1000.0;
    //     long i = Math.round(value * SCALE);

    //     return (double)i / SCALE;
    // }

    // private String coord(double x, double y) {
    //     return String.valueOf(round(x)) + "," + String.valueOf(round(y)) + " ";
    // }

    // private String genSVGPath(double iconSize) {
    //     final double centre = iconSize / 2;
    //     final double radius = centre * 0.7;

    //     final double a = centre - (radius * 1.2);
    //     final String line = "M" + coord(centre, a) + "L" + coord(centre, centre);

    //     final double x = centre;
    //     final double y = centre - radius;
    //     final double dx = radius * 0.5;         // sin(15)*cos(15) = 0.5
    //     final double dy = radius * 0.13397;     // sin(15)*sin(15) = 0.13397

    //     final String arc =  "M" + coord(x-dx, y+dy) + "A" + coord(radius, radius) + "0 1 0 " + coord(x+dx, y+dy);

    //     System.out.println(line + arc);
    //     return line + arc;
    // }


    /************************************************************************
     * Support code for "Top Bar" panel.
     */

    private double x = 0.0;
    private double y = 0.0;

    @FXML
    private HBox topBar;

    @FXML
    private Label headingLabel;

    @FXML
    void topBarOnMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void topBarOnMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
 
     /**
     * Mouse has entered the control.
     */
    private void entered() {
        for (Node node : hoverList) {
            if (!node.getStyleClass().contains(ICONHOVER)) {
                node.getStyleClass().add(ICONHOVER);
            }
        }
    }

    /**
     * Mouse has exited the control.
     */
    private void exited() {
        for (Node node : hoverList) {
            node.getStyleClass().remove(ICONHOVER);
        }
    }

    /**
     * Builds the cancel button as a Pane and includes the mouse click handler.
     * @return the Pane that represents the cancel button.
     */
    private AnchorPane buildCancel() {
        final double iconSize = 28.0;

        AnchorPane cancel = new AnchorPane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add(TOPBARICON);
        hoverList.add(cancel);

        SVGPath svg = new SVGPath();
        svg.setContent("M14.0,2.24 L14.0,14.0 M9.1,5.513 A9.8,9.8 0 1 0 18.9,5.513");
        // svg.setContent(genSVGPath(iconSize));
        svg.getStyleClass().add(TOPBARICON);
        hoverList.add(svg);

        cancel.getChildren().add(svg);

        cancel.setOnMouseClicked(event -> stage.close());
        cancel.setOnMouseEntered(event -> entered());
        cancel.setOnMouseExited(event -> exited());

        return cancel;
    }


    /**
     * Initialize "Reflector" panel.
     */
    private void initializeTopBar() {
        topBar.getChildren().add(buildCancel());
    }


     /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    @FXML
    private ChoiceBox<String> reflectorChoicebox;

    @FXML
    private HBox configureHBox;

    private SwitchControl configureControl;

    @FXML
    private HBox reflectorHBox;

    private ButtonControl reflectorEditControl;

    @FXML
    private TextField pairList;

    private void displayPairs() {
        pairList.setText(model.getPairString());
    }


    /**
     * Switch between being able to select a hard-wired reflector or set-up a 
     * reconfigurable reflector depending on isReconfigurable().
     */
    private void setReconfigurable() {
        final boolean reconfigurable = model.isReconfigurable();

        reflectorChoicebox.setDisable(reconfigurable);
        reflectorEditControl.setDisable(!reconfigurable);
        pairList.setDisable(!reconfigurable);
    }

    /**
     * Initialize "Reflector" panel.
     */
    private void initializeReflector() {
        reflectorChoicebox.setItems(model.getReflectorList());
        reflectorChoicebox.setTooltip(new Tooltip("Select a Reflector"));

        reflectorChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setReflectorChoice(newValue);
            setReconfigurable();
        });

        configureControl = new SwitchControl("Use Configurable Reflector", true);
        configureControl.addEventFilter(ControlEvent.ANY, this::handleConfigureEvent);
        configureControl.setTooltip("Select to use a configurable Reflector");

        configureHBox.getChildren().add(configureControl);

        reflectorEditControl = new ButtonControl("Edit", true);
        reflectorEditControl.addEventFilter(ControlEvent.ANY, this::handleReflectorEditEvent);
        reflectorEditControl.setTooltip("Edit the configurable Reflector");

        reflectorHBox.getChildren().add(reflectorEditControl);

        pairList.setTooltip(new Tooltip("Current Reflector configuration"));

        displayPairs();
        setReconfigurable();
    }

    private void handleReflectorEditEvent(ControlEvent event) {
        Debug.trace(DD, "handleReflectorEditEvent()");

        if (event.getEventType() == ControlEvent.CONTROL_PRESSED) {
            if (model.launchReflector()) {
                displayPairs();
            }
        }
    }

    private void handleConfigureEvent(ControlEvent event) {
        Debug.trace(DD, "handleConfigureEvent() " + event.isOn());

        if (event.getEventType() == ControlEvent.CONTROL_TOGGLED) {
            model.setReconfigurable(event.isOn());
            setReconfigurable();
        }
    }


    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    @FXML
    private HBox rotorSetUpHBox;

    @FXML
    private HBox fourthWheelHBox;

    private SwitchControl fourthWheelControl;

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        fourthWheelControl = new SwitchControl("Use Fourth Rotor", false);
        fourthWheelControl.addEventFilter(ControlEvent.ANY, this::handleFourthWheelEvent);
        fourthWheelControl.setTooltip("Select to use a fourth Rotor");

        fourthWheelHBox.getChildren().add(fourthWheelControl);

        rotorSetUpHBox.getChildren().addAll(model.getRotorControls());
    }

    private void handleFourthWheelEvent(ControlEvent event) {
        Debug.trace(DD, "handleFourthWheelEvent() " + event.isOn());

        if (event.getEventType() == ControlEvent.CONTROL_TOGGLED) {
            model.setFourthWheel(event.isOn());
        }
    }


    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */

    @FXML
    private HBox plugboardHBox;

    private ButtonControl plugboardEditControl;

    @FXML
    private TextField plugList;

    private void displayPlugs() {
        plugList.setText(model.getPlugString());
    }


    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {
        plugboardEditControl = new ButtonControl("Edit", true);
        plugboardEditControl.addEventFilter(ControlEvent.ANY, this::handlePlugboardEditEvent);
        plugboardEditControl.setTooltip("Edit the Plugboard");

        plugboardHBox.getChildren().add(plugboardEditControl);

        plugList.setTooltip(new Tooltip("Current Plugboard connections"));

        displayPlugs();
    }

    private void handlePlugboardEditEvent(ControlEvent event) {
        Debug.trace(DD, "handlePlugboardEditEvent()");

        if (event.getEventType() == ControlEvent.CONTROL_PRESSED) {
            if (model.launchPlugboard()) {
                displayPlugs();
            }
        }
    }


    /************************************************************************
     * Support code for "Translation" panel.
     */

    private int currentKey = -1;

    @FXML
    private HBox showStepsHBox;

    private SwitchControl showStepsControl;

    @FXML
    private ChoiceBox<Integer> settingsChoicebox;

    @FXML
    private HBox mainIO;

    @FXML
    private TextField keyIO;

    @FXML
    private Label labelIO;

    @FXML
    private TextField lampIO;


    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        showStepsControl = new SwitchControl("Show Translation", false);
        showStepsControl.addEventFilter(ControlEvent.ANY, this::handleShowStepsEvent);
        showStepsControl.setTooltip("Select to show each translation step on the command line");

        showStepsHBox.getChildren().add(showStepsControl);

        settingsChoicebox.setItems(model.getSettingsList());
        settingsChoicebox.setTooltip(new Tooltip("Select a settings entry from the Luftwaffe Enigma key list number 649"));

        settingsChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.dailySettings(newValue);
            syncUI();
        });

        final char arrow = '\u2799';
        labelIO.setText("" + arrow);

        Circle circle = new Circle(25);
        keyIO.setShape(circle);
        keyIO.setTooltip(new Tooltip("Key pressed"));

        lampIO.setShape(circle);
        lampIO.setTooltip(new Tooltip("Translated result"));
    }


    /**
     * Called by the scene when a key is pressed.
     * @param keyCode to be processed
     */
    private void keyPress(KeyCode keyCode) {
        if (keyCode.isLetterKey() == false) {
            return;
        }

        if (currentKey == -1) {
            currentKey = Mapper.letterToIndex(keyCode.getChar());
            final int index = model.translate(currentKey);

            keyIO.setText(keyCode.getChar());
            lampIO.setText(Mapper.indexToLetter(index));
        }
    }

    /**
     * Called by the scene when a key is released.
     * @param keyCode to be processed
     */
    private void keyRelease(KeyCode keyCode) {
        if (keyCode.isLetterKey() == false) {
            return;
        }

        final int index = Mapper.letterToIndex(keyCode.getChar());
        if (currentKey == index) {
            currentKey = -1;
            keyIO.setText("");
            lampIO.setText("");
        }
    }

    private void handleShowStepsEvent(ControlEvent event) {
        Debug.trace(DD, "handleShowStepsEvent() " + event.isOn());

        if (event.getEventType() == ControlEvent.CONTROL_TOGGLED) {
            model.setShow(event.isOn());
        }
    }

}
