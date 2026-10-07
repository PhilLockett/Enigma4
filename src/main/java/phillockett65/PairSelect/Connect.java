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
 * Connect is a class that captures a connection between two letters.
 */
package phillockett65.PairSelect;

import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Connect {
    private final int first;
    private final int second;
    private Line line;
    private Plug button1;
    private Plug button2;


    public String get() { return PairSelect.indexToLetter(first) + PairSelect.indexToLetter(second); }
    public String getTip() { return PairSelect.indexToLetter(first) + "-" + PairSelect.indexToLetter(second); }
    public int getFirst() { return first; }
    public int getSecond() { return second; }

    /**
     * Check if the Pair includes the specified index (Plug). 
     * @param index to check for.
     * @return The "position" of the index if found or 0 otherwise.
     */
    public int gotIndex(int index) {
        if (getFirst() == index)
            return 1;
        if (getSecond() == index)
            return 2;

        return 0;
    }

    public Line getLine() { return line; }

    private static Line buildLine(Plug b1, Plug b2, double width) {
        Line line = new Line(b1.getCenterX(), b1.getCenterY(), b2.getCenterX(), b2.getCenterY());
        line.setStrokeWidth(width);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        return line;
    }

    /**
     * Reset the tooltips for both letters to the default values.
     */
    public void resetTips() {
        button1.resetTooltip();
        button2.resetTooltip();
    }

    /**
     * Set the tooltips for both letters to the link value.
     */
    public void setTips() {
        final String tip = getTip();
        button1.replaceTooltip(tip);
        button2.replaceTooltip(tip);
    }

    /**
     * Constructor.
     */
    public Connect(Plug b1, Plug b2) {

        final int v1 = PairSelect.letterToIndex(b1.getId());
        final int v2 = PairSelect.letterToIndex(b2.getId());
        if (v1 < v2) {
            first = v1;
            second = v2;
            button1 = b1;
            button2 = b2;
        } else {
            first = v2;
            second = v1;
            button1 = b2;
            button2 = b1;
        }

        setTips();

        line = buildLine(b1, b2, 7);
    }

}
