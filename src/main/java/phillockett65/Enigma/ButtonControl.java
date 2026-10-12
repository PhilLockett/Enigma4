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
 * Implementation of a stateless button custom control. 
 */
package phillockett65.Enigma;

import javafx.animation.TranslateTransition;

public class ButtonControl extends CustomControl {

    private void syncUI() {
        final double dist = b;

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
        fireEvent(new ControlEvent(id));
    }



    /************************************************************************
     * Public interface.
     */


    /**
     * Constructor.
     * @param text string for the label.
     * @param labelLeft true if label should be left of the button, false for right.
     */
    public ButtonControl(String text, boolean labelLeft) {
        super(text, labelLeft, 1.0);

        this.setOnMouseClicked(event -> pressButton());
    }

}
