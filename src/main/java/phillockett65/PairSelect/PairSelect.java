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
 * PairSelect is a class that captures a connection between multiple pairs of 
 * letters.
 */
package phillockett65.PairSelect;


import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import phillockett65.Debug.Debug;


public class PairSelect extends AnchorPane {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private static final int PAIR_COUNT = 12;
    private static final int WIRE_COUNT = 10;

    private boolean onHandle = false;
    private boolean dragged = false;
    private int currentIndex = -1;
    private Line guide;

    private static double xCentre = 200.0;
    private static double yCentre = 200.0;
    private static double radius = 150.0;
    private static double labelRadius = radius + 25.0;

    private static double colIndent = 40.0;
    private static double rowIndent = 40.0;
    private static double colStep = 40.0;
    private static double rowStep = 100.0;

    private static final String GUIDECOL = "select-guide";
    private static final String UNGUIDECOL = "unselect-guide";
    private static final String LINECOL = "select-line";
    private static final String TEXTCOL = "select-text";

    private final Color guideCol = Color.SILVER;
    private final Color unguideCol = Color.MAROON;
    private final Color lineCol = Color.GREEN;
    private final Color textCol = Color.WHITE;


    private void setLineColours() {
 
        if (isCableAvailable()) {
            guide.setStroke(guideCol);
            guide.getStyleClass().remove(UNGUIDECOL);
        } else {
            guide.setStroke(unguideCol);
            if (!guide.getStyleClass().contains(UNGUIDECOL)) {
                guide.getStyleClass().add(UNGUIDECOL);
            }
        }
    }

    public double getMyWidth() { return xCentre * 2; }
    public double getMyHeight() { 
        return isPlugboard() ? (rowStep * 3) : (yCentre * 2) + rowIndent; 
    }

    public static final int STEPS = 13;
    public static final double pi = Math.acos(-1);
    public static final double STEP = (pi / STEPS);

    private ArrayList<Connect> pairList;

    private ArrayList<Plug> buttons;
    private ArrayList<Text> labels;
    private Point2D[] points;
    private boolean plugboard = false;


    public static int charToUpper(int v) { return Character.toUpperCase(v); }
    public static int charToIndex(int v) { return charToUpper(v) - 'A'; }
    public static int letterToIndex(String v) { return charToIndex(v.charAt(0)); }
    public static int indexToChar(int v) { return v + 'A'; }
    public static String indexToLetter(int v) { return "" + (char)indexToChar(v); }

    public static int charToInt(int v) { return charToIndex(v) + 1; }
    public static int intTochar(int v) { return indexToChar(v-1); }
    public static String intToString(int v) { return "" + (char)intTochar(v); }

    public static String indexToNumber(int v) { return Integer.toString(v+1); }
    public static int numberToIndex(String v) { return Integer.parseInt(v)-1; }

    public static int stringToIndex(String s) { return Character.isDigit(s.charAt(0)) ? numberToIndex(s) : letterToIndex(s); }

    public boolean hasPairs() { return !pairList.isEmpty(); }
    public int size() { return pairList.size(); }
    public String getText(int index) { return index < size() ? pairList.get(index).get() : ""; }

    public boolean isPlugboard() { return plugboard; }
    public boolean isReflector() { return !isPlugboard(); }
    
    private boolean isPairingDone() {
        if (isPlugboard())
            return false;

        if (pairList.size() >= PAIR_COUNT)
            return true;

        return false;
    }

    private boolean isCableAvailable() {
        if (!isPlugboard())
            return true;

        if (pairList.size() < WIRE_COUNT)
            return true;

        return false;
    }

    private void dragWire(Plug button, int index) {
        if (isPairingDone())
            return;

        dragged = true;
        currentIndex = index;
        button.stateChange(true, true);

        guide.setStartX(button.getCenterX());
        guide.setStartY(button.getCenterY());
        guide.setEndX(button.getCenterX());
        guide.setEndY(button.getCenterY());
        guide.setVisible(true);
        setLineColours();
    }

    private void dropWire(Plug button, int index) {
        dragged = false;
        button.stateChange(false, false);
        guide.setVisible(false);
    }

    private void pairWire(Plug button, int index) {
        dragged = false;
        button.stateChange(true, false);

        Plug currentButton = buttons.get(currentIndex);
        Connect pair = new Connect(currentButton, button);

        pairList.add(pair);
        Line line = pair.getLine();
        line.setStroke(lineCol);
        line.getStyleClass().add(LINECOL);
        this.getChildren().add(line);

        guide.setVisible(false);

        button.fireEvent(new PairEvent(PairEvent.LINK_CHANGE));
    }

    private void removeLink(Plug button, int index) {

        for (Connect pair : pairList)
        {
            int gotIndex = pair.gotIndex(index);
            if (gotIndex == 0)
                continue;

            int otherIndex = (gotIndex == 1) ? pair.getSecond() : pair.getFirst();

            Plug otherButton = buttons.get(otherIndex);
            otherButton.stateChange(false, false);
            button.stateChange(false, true);

            pair.resetTips();
            this.getChildren().remove(pair.getLine());
            pairList.remove(pair);

            setLineColours();
            button.fireEvent(new PairEvent(PairEvent.LINK_CHANGE));

            break;
        }
    }


    private void loadPlugboard() {
        final String[] plugOrder = { "QWERTZUIO", "ASDFGHJK", "PYXCVBNML" };
        points = new Point2D[26];

        int row = 0;
        for (String plug : plugOrder) {
            final int max = plug.length();
            for (int col = 0; col < max; ++col) {
                int index = charToIndex(plug.charAt(col));
                double x = colIndent + (col * colStep) + ((row % 2) * (colStep / 2));
                double y = rowIndent + (row * rowStep);
                points[index] = new Point2D(x, y);
            }
            ++row;
        }
    }

    private void positionReflectorButton(int index) {
        final double theta = STEP * index;
        final double s = Math.sin(theta);
        final double c = Math.cos(theta);

        final double x = (radius * s) + xCentre;
        final double y = (radius * c) + yCentre;
        Plug button = buttons.get(index);
        button.setCenterX(x);
        button.setCenterY(y);

        final double a = (labelRadius * s) + xCentre - 4;
        final double b = (labelRadius * c) + yCentre + 5;
        Text text = labels.get(index);
        text.setX(a);
        text.setY(b);
    }

    private void positionPlugboardButton(int index) {
        Point2D point = points[index];
        final double x = point.getX();
        final double y = point.getY();

        Plug button = buttons.get(index);
        button.setCentre(x, y);

        Text text = labels.get(index);
        text.setX(x-4);
        text.setY(y-20);
    }

    private void positionButtons() {
        if (isPlugboard()) {
            loadPlugboard();
            for (int index = 0; index < (STEPS*2); ++index) {
                positionPlugboardButton(index);
            }
        } else {
            for (int index = 0; index < (STEPS*2); ++index) {
                positionReflectorButton(index);
            }
        }
    }


    private void addButton(int index) {
        
        final String letter = indexToLetter(index);
        Plug button = new Plug(letter);

        Text text = new Text();
        text.setText(letter);
        text.setStroke(textCol);
        text.setFill(textCol);
        text.getStyleClass().add(TEXTCOL);
        text.setStrokeType(StrokeType.OUTSIDE);

        button.setOnMouseClicked(event -> {
            if (buttons.get(index).isUsed()) {
                removeLink(button, index);
            } else {
                if (dragged == true)
                    pairWire(button, index);
                else
                    dragWire(button, index);
            }
        });

        button.setOnMouseEntered(event -> {
            button.stateChange(buttons.get(index).isUsed(), true);
            onHandle = true;
        });

        button.setOnMouseExited(event -> {
            button.stateChange(buttons.get(index).isUsed(), false);
            onHandle = false;
        });

        this.getChildren().add(button);
        this.getChildren().add(text);

        buttons.add(button);
        labels.add(text);

    }
    private void addButtons() {
        for (int i = 0; i < (STEPS*2); ++i) {
            addButton(i);
        }

        positionButtons();
    }


    /**
     * Determine if the reflector (or plugboard) is valid.
     * @return true if the reflector is valid, false otherwise.
     */
    public boolean isValid() {
        // Check we have only 1 unconfigured pair.
        if ((isReflector()) && (pairList.size() < PAIR_COUNT)) {
            return false;
        }

        return true;
    }

    /**
     * Construct the Map represntation of the Pair set.
     * @return the Map represntation.
     */
    public int[] getMap() {
        int[] map = new int[26];

        for (int i = 0; i < map.length; ++i)
            map[i] = i;

        if (!isValid())
            return map;

        for (Connect pair : pairList) {
            final int a = pair.getFirst();
            final int b = pair.getSecond();
            map[a] = b;
            map[b] = a;
        }

        if (isPlugboard())
            return map;

        // Set up reflector unconfigured pair.
        int first = -1;
        for (int index = 0; index < buttons.size(); ++index) {
            if (buttons.get(index).isUsed())
                continue;

            if (first == -1) {
                first = index;
            } else {
                map[first] = index;
                map[index] = first;

                break;
            }
        }

        return map;
    }

    /**
     * Clear the state of all sub-objects.
     */
    public void clear() {
        for (Connect pair : pairList) {
            this.getChildren().remove(pair.getLine());
            pair.resetTips();
        }
        pairList.clear();

        for (Plug button : buttons) {
            button.stateChange(false, false);
        }
    }

    private void init() {

        pairList = new ArrayList<Connect>();
        buttons = new ArrayList<Plug>();
        labels = new ArrayList<Text>();

        guide = new Line();
        guide.setStroke(guideCol);
        guide.getStyleClass().add(GUIDECOL);
        guide.setStrokeWidth(5);
        guide.setStrokeLineCap(StrokeLineCap.ROUND);
        guide.setVisible(false);
        this.getChildren().add(guide);

        addButtons();

        this.setOnMouseClicked(event -> {
            if (onHandle == true)
                return;

            if (dragged == true) {
                Plug currrentButton = buttons.get(currentIndex);
                dropWire(currrentButton, currentIndex);
            }
        });

        this.setOnMouseMoved(event -> {
            if (dragged == false)
                return;

            guide.setEndX(event.getX());
            guide.setEndY(event.getY());

        });

        setPrefWidth(getMyWidth());
        setPrefHeight(getMyHeight());
    }


    /**
     * Construct a String represntation of the Pair set.
     * @return the String represntation.
     */
    public String getPairString() {
        Debug.trace(DD, "getPairString()");

        String to = "";
        final int MAX = size();
        for (int i = 0; i < MAX; ++i)
            to += getText(i) + " ";

        return to;
    }

    /**
     * Construct an ArrayList of Strings represntation of the Pair set.
     * @return the ArrayList of Strings represntation.
     */
    public ArrayList<String> getLinks() {
        Debug.trace(DD, "getList()");

        ArrayList<String> to = new ArrayList<String>();
        final int MAX = size();
        for (int i = 0; i < MAX; ++i)
            to.add(getText(i));

        return to;
    }

    private Connect addLink(int index1, int index2) {

        Plug button1 = buttons.get(index1);
        Plug button2 = buttons.get(index2);
        button1.stateChange(true, false);
        button2.stateChange(true, false);
        Connect pair = new Connect(button1, button2);

        pairList.add(pair);
        Line line = pair.getLine();
        line.setStroke(lineCol);
        line.getStyleClass().add(LINECOL);
        this.getChildren().add(line);

        return pair;
    }

    /**
     * Initialise pairList with the given ArrayList of Strings represntation.
     * @param links ArrayList of Strings.
     */
    public void setLinks(ArrayList<String> links) {
        Debug.trace(DD, "setLinks()");
        clear();
        if (links == null)
            return;

        for (String pairString : links) {
            if (pairString.length() != 2)
                continue;

            int index1 = charToIndex(pairString.charAt(0));
            int index2 = charToIndex(pairString.charAt(1));

            addLink(index1, index2);
        }
    }

    /**
     * Constructor.
     */
    public PairSelect(boolean isPlugboard) {
        super();
        plugboard = isPlugboard;
        init();
    }



    /**
     * PairSelect Event class.
     */
    public static class PairEvent extends Event {

        private static final long serialVersionUID = 202408191851L;
    
        /**
         * The only valid EventTypes for the SelectEvent.
         */
        public static final EventType<PairEvent> SELECT_LINK =
            new EventType<>(Event.ANY, "SELECT_LINK");
        public static final EventType<PairEvent> ANY = SELECT_LINK;
    
        public static final EventType<PairEvent> LINK_CHANGE =
            new EventType<>(PairEvent.ANY, "LINK_CHANGE");
    
        /**
         * Creates a new {@code PairEvent} with an event type of {@code ANY}.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         */
        public PairEvent() { super(ANY); }
    
        /**
         * Construct a new {@code PairEvent} with the specified event type.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param eventType this event represents.
         */
        public PairEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }
    
    
        @Override
        public PairEvent copyFor(Object newSource, EventTarget newTarget) {
            return (PairEvent) super.copyFor(newSource, newTarget);
        }
    
        @SuppressWarnings("unchecked")
        @Override
        public EventType<? extends PairEvent> getEventType() {
            return (EventType<? extends PairEvent>) super.getEventType();
        }
    
    }
    
}