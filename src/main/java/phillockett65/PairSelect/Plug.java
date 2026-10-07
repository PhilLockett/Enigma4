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
 * Plug is a class that is associated with a letter ("label") and can either be
 * available or in use.
 */
package phillockett65.PairSelect;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Phil
 */
class Plug extends Circle {

    private static final String BUTTONCOL = "select-button";
    private static final String BUTTONSELCOL = "select-button-selected";
    private static final String BUTTONHOVCOL = "select-button-hover";
    private static final String BUTTONUSEDCOL = "select-button-hoverused";

    private final Color unusedCol = Color.GOLD;
    private final Color usedCol = Color.WHITE;
    private final Color availableCol = Color.LIME;
    private final Color unavailableCol = Color.RED;

    private static double buttonSize = 14.0;

    private final String label;
    private boolean used = false;
    private String style = "";
    private String replacementTip = "";

    public boolean isUsed() { return used; }


    /**
     * Change the state of the Plug. 
     * @param using the plug now.
     * @param hovering over the plug.
     */
    public void stateChange(boolean using, boolean hovering) {
        String newStyle;
        Color colour;
        if (hovering) {
            if (using) {
                colour = unavailableCol;
                newStyle = BUTTONUSEDCOL;
            } else {
                colour = availableCol;
                newStyle = BUTTONHOVCOL;
            }
        } else {
            if (using) {
                colour = usedCol;
                newStyle = BUTTONSELCOL;
            } else {
                colour = unusedCol;
                newStyle = BUTTONCOL;
            }
        }

        if (style != newStyle) {
            this.setFill(colour);
            getStyleClass().remove(style);
            style = newStyle;
            getStyleClass().add(style);
        }

        used = using;
    }

    /**
     * Position the plug.
     * @param x value of centre point.
     * @param y value of centre point.
     */
    public void setCentre(double x, double y) {
        this.setCenterX(x);
        this.setCenterY(y);
    }

    /**
     * Replace the default tooltip.
     * @param newTip Replacement tooltip
     */
    public void replaceTooltip(String newTip) {
        Tooltip tip = new Tooltip(label);
        Tooltip.uninstall(this, tip);
        
        replacementTip = newTip;
        tip = new Tooltip(replacementTip);
        Tooltip.install(this, tip);
    }

    /**
     * Set the tooltip to the default value.
     */
    public void resetTooltip() {
        Tooltip tip = new Tooltip(replacementTip);
        Tooltip.uninstall(this, tip);

        tip = new Tooltip(label);
        Tooltip.install(this, tip);
    }

    private void init() {

        this.setRadius(buttonSize);
        this.setFill(unusedCol);
        this.getStyleClass().add(BUTTONCOL);
        this.setStrokeWidth(0.0);
        
        used = false;
        this.setId(label);
        Tooltip tip = new Tooltip(label);
        Tooltip.install(this, tip);

        style = BUTTONCOL;
        getStyleClass().add(style);
    }

    /**
     * Constructor.
     */
    public Plug(String id) {
        super();
        
        label = id;
        setCentre(0.0, 0.0);
        init();
    }

}
