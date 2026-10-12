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
 * Implementation of a stateful toggle switch custom control. 
 */
package phillockett65.Enigma;

import javafx.animation.TranslateTransition;

public class SwitchControl extends CustomControl {

    private void syncUI() {
        final double dist = ((h - b) * 2.0) - s;

        setStyleClass(state, label, SELECTED);

        TranslateTransition translate = new TranslateTransition();
        translate.setNode(thumb);
        translate.setDuration(duration);
        if (!state) {
            translate.setByX(-dist);
        } else {
            translate.setByX(dist);
        }
        translate.play();
    }

    private void toggleSwitch() {
        state = !state;
        syncUI();
        fireEvent(new ControlEvent(id, state));
    }


    /************************************************************************
     * Public interface.
     */

    @Override
    public void setSelected(boolean newState) {
        if (newState == state) {
            return;
        }

        toggleSwitch();
    }


    /**
     * Constructor.
     * @param text string for the label.
     * @param labelLeft true if label should be left of the switch, false for right.
     */
    public SwitchControl(String text, boolean labelLeft) {
        super(text, labelLeft, 2.0);

        this.setOnMouseClicked(event -> toggleSwitch());
    }


}
