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

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CustomControl extends HBox {

    private static final String STYLESHEET = "CustomControl.css";
    protected static final String SELECTED = "control-selected";
    private static final String HOVER = "control-hover";

    private static final Paint TRACK = Color.SILVER;
    private static final Paint TRACKHOVER = Color.WHITE;

    private Model model;

    private Rectangle track;
    protected Rectangle thumb;
    protected Label label;

    // Dimensions for the track and thumb.
    protected final double h = 22.0;
    protected final double b = 2.0;
    protected final double s = h - (2.0 * b);

    protected final Duration duration = Duration.millis(100);


    /**
     * @param state to adjust the appearance to or from.
     * @param field to adjust the appearance of.
     * @param style to adjust the appearance to or from.
     */
    protected void setStyleClass(boolean state, Control field, String style) {
        if (!state) {
            field.getStyleClass().remove(style);
        } else {
            if (!field.getStyleClass().contains(style))
                field.getStyleClass().add(style);
        }
    }

    private void action() {
        fireEvent(new ControlEvent(id));
    }

    private void hoverButton(boolean hovering) {
        setStyleClass(hovering, label, HOVER);
        if (hovering) {
            track.setStroke(TRACKHOVER);
        } else {
            track.setStroke(TRACK);
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
        model = Model.getInstance();

        this.setSpacing(model.getSeparation());
        this.setAlignment(Pos.CENTER_LEFT);

        // Build the track.
        final double w = h * aspectRation;
        track = new Rectangle(0, 0, w, h);
        Stop[] trackStops = new Stop[]{
            new Stop(0, Color.web("#444")),
            new Stop(1, Color.web("#888"))
        };
        LinearGradient trackLG = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, trackStops);

        final double p = model.getRoundness();
        final double a1 = h * p;
        final double a2 = s * p;

        track.setStrokeWidth(2.0);
        track.setStroke(TRACK);
        track.setFill(trackLG);
        track.setArcWidth(a1);
        track.setArcHeight(a1);

        // Build the thumb.
        thumb = new Rectangle(b, b, s, s);
        Stop[] thumbStops = new Stop[]{
            new Stop(0, Color.web("#fff")),
            new Stop(1, Color.web("#999"))
        };
        RadialGradient thumbRG = new RadialGradient(0, 0, 0.5, 0.25, 0.5, true, CycleMethod.NO_CYCLE, thumbStops);

        thumb.setFill(thumbRG);
        thumb.setArcWidth(a2);
        thumb.setArcHeight(a2);

        // Group track and thumb together.
        Group group = new Group(track, thumb);

        // Build the label.
        label = new Label(text);
        label.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());

        if (labelLeft) {
            this.getChildren().addAll(label, group);
        } else {
            this.getChildren().addAll(group, label);
        }

        setStyleClass(true, label, SELECTED);

        this.setOnMouseClicked(event -> action());
        this.setOnMouseEntered(event -> hoverButton(true));
        this.setOnMouseExited(event -> hoverButton(false));
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
