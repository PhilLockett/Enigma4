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
 * Implementation of a button custom control. 
 */
package phillockett65.Enigma;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class CustomControl extends HBox {

    private static final String STYLESHEET = "CustomControl.css";
    private static final String CONTROL = "custom-control";
    private static final String HOVER = "control-hover";
    private static final String THUMB = "control-thumb";

    // List of Nodes that are re-styled based on mouse hovers.
    private ObservableList<Node> hoverList = FXCollections.observableArrayList();

    protected Rectangle thumb;

    // Dimensions for the track and thumb.
    protected final double height = 22.0;
    protected final double padding = 2.0;
    protected final double size = height - (2.0 * padding);


    /**
     * Action to perform when this control clicked.
     */
    private void action() {
        fireEvent(new ControlEvent(id));
    }

    /**
     * Mouse has entered the control.
     */
    private void entered() {
        for (Node node : hoverList) {
            if (!node.getStyleClass().contains(HOVER)) {
                node.getStyleClass().add(HOVER);
            }
        }
    }

    /**
     * Mouse has exited the control.
     */
    private void exited() {
        for (Node node : hoverList) {
            node.getStyleClass().remove(HOVER);
        }
    }


    /************************************************************************
     * Public interface.
     */

    protected int id = 0;
    protected boolean state = false;

    public void setIdent(int ident) { id = ident; }
    public int getIdent() { return id; }

    public void setSelected(boolean newState) { state = newState; }
    public boolean isSelected() { return state; }

    public void setTooltip(String label) {
        Tooltip tip = new Tooltip(label);
        Tooltip.install(this, tip);
    }


    /**
     * Constructor.
     * @param text string for the label.
     * @param labelLeft true if label should be left of the button, false for right.
     * @param aspectRation for the track size.
     */
    public CustomControl(String text, boolean labelLeft, double aspectRation) {
        super();

        this.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());
        this.getStyleClass().add(CONTROL);
        this.setAlignment(Pos.CENTER_LEFT);

        this.setOnMouseClicked(event -> action());
        this.setOnMouseEntered(event -> entered());
        this.setOnMouseExited(event -> exited());

        // Build the track.
        final double width = height * aspectRation;
        Rectangle track = new Rectangle(0, 0, width, height);
        track.getStyleClass().add(CONTROL);
        hoverList.add(track);

        // Build the thumb.
        thumb = new Rectangle(padding, padding, size, size);
        thumb.getStyleClass().add(THUMB);

        // Group track and thumb together.
        Group group = new Group(track, thumb);

        // Build the label.
        Label label = new Label(text);
        label.getStyleClass().add(CONTROL);
        hoverList.add(label);

        if (labelLeft) {
            this.getChildren().addAll(label, group);
        } else {
            this.getChildren().addAll(group, label);
        }
    }



    /************************************************************************
     * ButtonControl Event class.
     */

    public static class ControlEvent extends Event {

        private static final long serialVersionUID = 202611101657L;

        /**
         * The only valid EventTypes for the ButtonEvent.
         */
        public static final EventType<ControlEvent> CONTROL_BASE =
            new EventType<>(Event.ANY, "CONTROL_BASE");
        public static final EventType<ControlEvent> ANY = CONTROL_BASE;
        public static final EventType<ControlEvent> CONTROL_PRESSED =
            new EventType<>(ControlEvent.ANY, "CONTROL_PRESSED");
        public static final EventType<ControlEvent> CONTROL_TOGGLED =
            new EventType<>(ControlEvent.ANY, "CONTROL_TOGGLED");

        private final int id;
        private final boolean on;

        public int getIdent() { return id; }
        public boolean isOn() { return on; }

        /**
         * Creates a new {@code ControlEvent} with an event type of {@code ANY}.
         * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
         */
        public ControlEvent() { super(ANY); id = 0; on = false; }

        /**
         * Construct a new {@code ControlEvent} with a pressed event type and a
         * state of true.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param ident of the source of this event.
         */
        public ControlEvent(int ident) {
            super(ControlEvent.CONTROL_PRESSED);
            id = ident;
            on = true;
        }

        /**
         * Construct a new {@code ControlEvent} with a toggled event type and a
         * specified state.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param ident of the source of this event.
         * @param state of the control.
         */
        public ControlEvent(int ident, boolean state) {
            super(ControlEvent.CONTROL_TOGGLED);
            id = ident;
            on = state;
        }

        @Override
        public ControlEvent copyFor(Object newSource, EventTarget newTarget) {
            return (ControlEvent) super.copyFor(newSource, newTarget);
        }

        @SuppressWarnings("unchecked")
        @Override
        public EventType<? extends ControlEvent> getEventType() {
            return (EventType<? extends ControlEvent>) super.getEventType();
        }
    }

}
