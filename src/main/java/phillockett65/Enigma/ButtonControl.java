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

import javafx.animation.TranslateTransition;
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

public class ButtonControl extends HBox {

    private static final String STYLESHEET = "SwitchControl.css";
    private static final String SELECTED = "myswitch-selected";
    private static final String HOVER = "myswitch-hover";

    private static final Paint TRACK = Color.SILVER;
    private static final Paint TRACKHOVER = Color.WHITE;

    private Model model;
    private int id = 0;

    private Rectangle track;
    private Rectangle thumb;
    private Label label;

    // Dimensions for the track and thumb.
    private final double h = 22.0;
    private final double w = h * 1.0;
    private final double b = 2.0;
    private final double s = h - (2.0 * b);
    private final double x = b;
    private final double dist = b;
    private final double y = b;

    private final Duration duration = Duration.millis(100);


    /**
     * Use CSS to change the style of a Control.
     * @param state to adjust the appearance to or from.
     * @param field to adjust the appearance of.
     * @param style to adjust the appearance to or from.
     */
    private void setStyleClass(boolean state, Control field, String style) {
        if (!state) {
            field.getStyleClass().remove(style);
        } else {
            if (!field.getStyleClass().contains(style))
                field.getStyleClass().add(style);
        }
    }

    private void syncUI() {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(thumb);
        translate.setDuration(duration);
        translate.setCycleCount(2);
        translate.setAutoReverse(true);
        translate.setByY(dist);
        translate.play();
    }

    private void pressButton() {
        syncUI();
        fireEvent(new ButtonEvent(ButtonEvent.BUTTON_PRESSED, id));
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

    public void setTooltip(String label) {
        Tooltip tip = new Tooltip(label);
        Tooltip.install(this, tip);
    }

    public void setSelected(boolean newState) {
        pressButton();
    }

    public void setIdent(int ident) { id = ident; }
    public int getIdent() { return id; }


    /**
     * Constructor.
     * @param text string for the label.
     * @param labelLeft true if label should be left of the button, false for right.
     */
    public ButtonControl(String text, boolean labelLeft) {
        super();
        model = Model.getInstance();

        this.setSpacing(model.getSeparation());
        this.setAlignment(Pos.CENTER_LEFT);

        // Build the track.
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
        thumb = new Rectangle(x, y, s, s);
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

        this.setOnMouseClicked(event -> pressButton());
        this.setOnMouseEntered(event -> hoverButton(true));
        this.setOnMouseExited(event -> hoverButton(false));
    }



    /************************************************************************
     * ButtonControl Event class.
     */

    public static class ButtonEvent extends Event {

        private static final long serialVersionUID = 202610071201L;

        /**
         * The only valid EventTypes for the ButtonEvent.
         */
        public static final EventType<ButtonEvent> BUTTON_BASE =
            new EventType<>(Event.ANY, "BUTTON_BASE");
        public static final EventType<ButtonEvent> ANY = BUTTON_BASE;
        public static final EventType<ButtonEvent> BUTTON_PRESSED =
            new EventType<>(ButtonEvent.ANY, "BUTTON_PRESSED");

        private final int id;

        public int getIdent() { return id; }

        /**
         * Creates a new {@code ButtonEvent} with an event type of {@code ANY}.
         * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
         */
        public ButtonEvent() { super(ANY); id = 0; }

        /**
         * Construct a new {@code ButtonEvent} with the specified event type and 
         * selected colour.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param eventType this event represents.
         * @param ident     of the source of this event.
         */
        public ButtonEvent(EventType<? extends Event> eventType, int ident) {
            super(eventType);
            id = ident;
        }

        @Override
        public ButtonEvent copyFor(Object newSource, EventTarget newTarget) {
            return (ButtonEvent) super.copyFor(newSource, newTarget);
        }

        @SuppressWarnings("unchecked")
        @Override
        public EventType<? extends ButtonEvent> getEventType() {
            return (EventType<? extends ButtonEvent>) super.getEventType();
        }
    }

}
