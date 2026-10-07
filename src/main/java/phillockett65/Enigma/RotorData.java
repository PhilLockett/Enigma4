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
 * Rotor is a class that extends Mapper and captures the static rotor information 
 * including the cipher string and the turnover points.
 */
package phillockett65.Enigma;

import java.util.Arrays;

import phillockett65.Debug.Debug;

public class RotorData extends Mapper {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private final String cipher;
    private final String date;
    private final String name;
    private final String turnovers;
    

    /************************************************************************
     * Initialization support code.
     */

    /**
     * Constructor.
     * @param id of this mapping.
     * @param cipher String representation of the mapping.
     * @param date rotor was introduced (for reference purposes).
     * @param name of rotor group (for reference purposes).
     * @param turnover list of letters
     */
    public RotorData(String id, String cipher, String date, String name, String turnover) {
        super(id, cipher);
        this.cipher = cipher;
        this.date = date;
        this.name = name;
        this.turnovers = turnover;
    }


    /************************************************************************
     * Getters support code.
     */

    public String getCipher()	{ return cipher; }
    public String getDate()		{ return date; }
    public String getName()		{ return name; }
    public String getTurnovers() { return turnovers; }


    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Rotor [" + 
            "id=" + getId() + 
            ", map=" + Arrays.toString(getMap()) + 
            ", cipher=" + cipher + 
            ", name=" + name + 
            ", reflect=" + isReflector() + 
            ", date=" + date + 
            ", turnovers=" + turnovers + 
            "]";
    }

    public void dumpCipher() { Debug.info(DD, cipher); }

}
