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

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import phillockett65.Debug.Debug;
import phillockett65.Enigma.ButtonControl.ButtonEvent;
import phillockett65.Enigma.SwitchControl.SwitchEvent;

public class PrimaryController {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private Model model;



    /************************************************************************
     * General support code.
     */

    private static final String TOPBARICON = "top-bar-icon";



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
     * Builds the cancel button as a Pane and includes the mouse click handler.
     * @return the Pane that represents the cancel button.
     */
    private Pane buildCancel() {
        final double iconSize = 28.0;
        final double cancelStroke = 2.5;

        Pane cancel = new Pane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add(TOPBARICON);

        double centre = iconSize / 2;
        double radius = centre * 0.7;

        Arc arc = new Arc(centre, centre, radius, radius, 120.0, 300.0);
        arc.setStroke(Color.WHITE);
        arc.getStyleClass().add(TOPBARICON);
        arc.setStrokeWidth(cancelStroke);
        arc.setFill(Color.TRANSPARENT);
        arc.setType(ArcType.OPEN);

        double a = centre - (radius * 1.2);
        Line line = new Line(centre, a, centre, centre);
        line.setStroke(Color.WHITE);
        line.getStyleClass().add(TOPBARICON);
        line.setStrokeWidth(cancelStroke);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        cancel.getChildren().addAll(arc, line);

        cancel.setOnMouseClicked(event -> {
            stage.close();
        });

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
    private TitledPane reflectorSetUpTitledPane;

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
        // reflectorSetUpTitledPane.setTooltip(new Tooltip("Select which Reflector (reversing drum) to use"));

        reflectorChoicebox.setItems(model.getReflectorList());
        reflectorChoicebox.setTooltip(new Tooltip("Select a Reflector"));

        reflectorChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setReflectorChoice(newValue);
            setReconfigurable();
        });

        configureControl = new SwitchControl("Configure", false);
        configureControl.addEventFilter(SwitchEvent.ANY, this::handleConfigureEvent);
        configureControl.setTooltip("Select to use a configurable Reflector");

        configureHBox.getChildren().add(configureControl);

        reflectorEditControl = new ButtonControl("Edit", true);
        reflectorEditControl.addEventFilter(ButtonEvent.ANY, this::handleReflectorEditEvent);
        reflectorEditControl.setTooltip("Edit the configurable Reflector");

        pairList.setTooltip(new Tooltip("Current Reflector configuration"));

        reflectorHBox.getChildren().add(reflectorEditControl);
        displayPairs();

        setReconfigurable();
    }

    private void handleReflectorEditEvent(ButtonEvent event) {
        Debug.trace(DD, "handleReflectorEditEvent()");

        if (event.getEventType() == ButtonEvent.BUTTON_PRESSED) {
            if (model.launchReflector()) {
                displayPairs();
            }
        }
    }

    private void handleConfigureEvent(SwitchEvent event) {
        Debug.trace(DD, "handleConfigureEvent() " + event.isOn());

        if (event.getEventType() == SwitchEvent.SWITCH_TOGGLED) {
            model.setReconfigurable(event.isOn());
            setReconfigurable();
        }
    }


    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    @FXML
    private TitledPane rotorSetUpTitledPane;
 
    @FXML
    private HBox rotorSetUpHBox;

    @FXML
    private HBox fourthWheelHBox;

    private SwitchControl fourthWheelControl;

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        // rotorSetUpTitledPane.setTooltip(new Tooltip("Select and set up the Rotors (wheels / drums)"));

        fourthWheelControl = new SwitchControl("Use Fourth Rotor", false);
        fourthWheelControl.addEventFilter(SwitchEvent.ANY, this::handleFourthWheelEvent);

        fourthWheelHBox.getChildren().add(fourthWheelControl);

        rotorSetUpHBox.getChildren().addAll(model.getRotorControls());

        fourthWheelControl.setTooltip("Select to use a fourth Rotor");
    }

    private void handleFourthWheelEvent(SwitchEvent event) {
        Debug.trace(DD, "handleFourthWheelEvent() " + event.isOn());

        if (event.getEventType() == SwitchEvent.SWITCH_TOGGLED) {
            model.setFourthWheel(event.isOn());
        }
    }


    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */

    @FXML
    private TitledPane plugboardConnectionsTitledPane;

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
        // plugboardConnectionsTitledPane.setTooltip(new Tooltip("Configure the Plugboard using unique wiring pairs"));

        plugboardEditControl = new ButtonControl("Edit", true);
        plugboardEditControl.addEventFilter(ButtonEvent.ANY, this::handlePlugboardEditEvent);
        plugboardEditControl.setTooltip("Edit the Plugboard");

        plugboardHBox.getChildren().add(plugboardEditControl);

        plugList.setTooltip(new Tooltip("Current Plugboard connections"));

        displayPlugs();
    }

    private void handlePlugboardEditEvent(ButtonEvent event) {
        Debug.trace(DD, "handlePlugboardEditEvent()");

        if (event.getEventType() == ButtonEvent.BUTTON_PRESSED) {
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
        showStepsControl.addEventFilter(SwitchEvent.ANY, this::handleShowStepsEvent);
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
     * Called by the Application when a new key is pressed.
     * @param keyCode key to be processed
     */
    public void keyPress(KeyCode keyCode) {
        if (currentKey == -1) {
            currentKey = Mapper.letterToIndex(keyCode.getChar());
            final int index = model.translate(currentKey);

            keyIO.setText(keyCode.getChar());
            lampIO.setText(Mapper.indexToLetter(index));
        }
    }

    /**
     * Called by the Application when a current key is released.
     * @param keyCode key to be processed
     */
    public void keyRelease(KeyCode keyCode) {
        final int index = Mapper.letterToIndex(keyCode.getChar());
        if (currentKey == index) {
            currentKey = -1;
            keyIO.setText("");
            lampIO.setText("");
        }
    }

    private void handleShowStepsEvent(SwitchEvent event) {
        Debug.trace(DD, "handleShowStepsEvent() " + event.isOn());

        if (event.getEventType() == SwitchEvent.SWITCH_TOGGLED) {
            model.setShow(event.isOn());
        }
    }

}
