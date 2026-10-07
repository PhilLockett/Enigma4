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
 * Embedded control to capture a rotors settings. This includes the rotor 
 * name, the ring setting and the rotor offset. 
 */
package phillockett65.Enigma;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import phillockett65.Debug.Debug;


public class RotorControl extends VBox {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private final static String[] names = { "fourth", "left", "middle", "right" };

    private ChoiceBox<String> wheelChoicebox;
    private Spinner<String> ringSettingSpinner;
    private Spinner<String> rotorOffsetSpinner;

    private ObservableList<String> wheelList = FXCollections.observableArrayList();
    private ObservableList<String> ringList = FXCollections.observableArrayList();
    private int id = 0;

    private SpinnerValueFactory<String> ringSettingSVF;
    private SpinnerValueFactory<String> rotorOffsetSVF;

    private String padding(int index) {
        if (index < 9)
            return "   ";

        return "  ";
    }

    /**
     * Constructor.
     */
    public RotorControl() {
        super();
        Debug.trace(DD, "CustomRotorControl()");

        setSpacing(8);
        wheelChoicebox = new ChoiceBox<String>();
        ringSettingSpinner = new Spinner<String>();
        rotorOffsetSpinner = new Spinner<String>();

        getChildren().addAll(wheelChoicebox, ringSettingSpinner, rotorOffsetSpinner);

        // Initialize "ringList" ObservableList.
        for (int i = 0; i < 26; ++i) {
            final String display = Rotor.indexToLetter(i) + padding(i) + String.valueOf(i + 1);
            ringList.add(display);
        }
    }

    /**
     * Initialise the rotor.
     * @param i rotor identifier.
     * @param wheelList list of rotor names.
     */
    public void init(int i, ObservableList<String> wheelList) {
        Debug.trace(DD, "init()");

        id = i;
        String name = names[id];
        this.wheelList.setAll(wheelList);

        // Initialize "Rotor Selection" choice box.
        wheelChoicebox.setItems(wheelList);
        wheelChoicebox.setValue(wheelList.get(0));
        wheelChoicebox.setTooltip(new Tooltip("Select the " + name + " Rotor"));

        // Initialize "Ring Settings" spinner.
        ringSettingSVF = new SpinnerValueFactory.ListSpinnerValueFactory<String>(ringList);
        ringSettingSpinner.setValueFactory(ringSettingSVF);
        ringSettingSpinner.getValueFactory().wrapAroundProperty().set(true);
        ringSettingSpinner.setTooltip(new Tooltip("Select Ring Setting for the " + name + " Rotor"));

        // Initialize "Rotor Offsets" spinner.
        rotorOffsetSVF = new SpinnerValueFactory.ListSpinnerValueFactory<String>(ringList);
        rotorOffsetSpinner.setValueFactory(rotorOffsetSVF);
        rotorOffsetSpinner.getValueFactory().wrapAroundProperty().set(true);
        rotorOffsetSpinner.setTooltip(new Tooltip("Select offset for the " + name + " Rotor"));
    }

    /**
     * Initialise the change event listeners.
     */
    public void initListeners() {
        Debug.trace(DD, "initListeners()");

        wheelChoicebox.valueProperty().addListener( (v, oldValue, newValue) -> {
            Debug.trace(DD, "wheelChoicebox[" + id + "] = " + newValue);
            wheelChoicebox.fireEvent(new RotorEvent(RotorEvent.WHEEL_CHOICE, id));
        });

        ringSettingSpinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            Debug.trace(DD, "ringSettingSpinner[" + id + "] = " + newValue);
            ringSettingSpinner.fireEvent(new RotorEvent(RotorEvent.RING_SETTING, id));
        });

        rotorOffsetSpinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            Debug.trace(DD, "rotorOffsetSpinner[" + id + "] = " + newValue);
            rotorOffsetSpinner.fireEvent(new RotorEvent(RotorEvent.ROTOR_OFFSET, id));
        });
    }

    private int valueToIndex(String s) { return Mapper.letterToIndex(s); }

    public String getWheelChoice() { return wheelChoicebox.getValue(); }
    public void setWheelChoice(String value) { wheelChoicebox.setValue(value); }

    private String getRingValue() { return ringSettingSVF.getValue(); }
    public int getRingIndex() { return valueToIndex(getRingValue()); }
    private void setRingValue(String value) { ringSettingSVF.setValue(value); }
    public void setRingIndex(int index) { setRingValue(ringList.get(index % 26)); }

    private String getRotorValue() { return rotorOffsetSVF.getValue(); }
    public int getRotorIndex() { return valueToIndex(getRotorValue()); }
    private void setRotorValue(String value) { rotorOffsetSVF.setValue(value); }
    public void setRotorIndex(int index) { setRotorValue(ringList.get(index % 26)); }
    public void increment(int steps) { rotorOffsetSVF.increment(steps); }
    
    /**
     * Set up the custom controller
     * @param wheel selected.
     * @param ringIndex for the ring setting.
     * @param rotorIndex for the rotor start position.
     */
    public void set(String wheel, int ringIndex, int rotorIndex) {
        setWheelChoice(wheel);
        setRingIndex(ringIndex);
        setRotorIndex(rotorIndex);
    }



    /**
     * RotorControl Event class.
     */
    public static class RotorEvent extends Event {

        private static final long serialVersionUID = 202409111243L;

        private final int id;

        public int getId() { return id; }

        /**
         * The only valid EventTypes for the RotorEvent.
         */
        public static final EventType<RotorEvent> SELECT_ROTOR =
            new EventType<>(Event.ANY, "SELECT_ROTOR");
        public static final EventType<RotorEvent> ANY = SELECT_ROTOR;

        public static final EventType<RotorEvent> WHEEL_CHOICE =
            new EventType<>(RotorEvent.ANY, "WHEEL_CHOICE");
        public static final EventType<RotorEvent> RING_SETTING =
            new EventType<>(RotorEvent.ANY, "RING_SETTING");
        public static final EventType<RotorEvent> ROTOR_OFFSET =
            new EventType<>(RotorEvent.ANY, "ROTOR_OFFSET");

        /**
         * Creates a new {@code RotorEvent} with an event type of {@code ANY}.
         * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
         */
        public RotorEvent() {
            super(ANY);
            id = 0;
        }

        /**
         * Construct a new {@code RotorEvent} with the specified event type and 
         * rotor id.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param eventType this event represents.
         * @param id        of the rotor.
         */
        public RotorEvent(EventType<? extends Event> eventType, int id) {
            super(eventType);
            this.id = id;
        }


        @Override
        public RotorEvent copyFor(Object newSource, EventTarget newTarget) {
            return (RotorEvent) super.copyFor(newSource, newTarget);
        }

        @SuppressWarnings("unchecked")
        @Override
        public EventType<? extends RotorEvent> getEventType() {
            return (EventType<? extends RotorEvent>) super.getEventType();
        }

    }

}