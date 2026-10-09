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
 * DataStore is a class that serializes the settings data for saving and 
 * restoring to and from disc.
 */
package phillockett65.Enigma;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;

import phillockett65.Debug.Debug;

public class DataStore1 extends DataStore {
    private static final long serialVersionUID = 1L;

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private double mainX;
    private double mainY;

    private String reflectorChoice;
    private Boolean reconfigurable;
    private ArrayList<String> pairs;

    private Boolean fourthWheel;
    private Boolean show;

    private ArrayList<String> wheels;
    private ArrayList<Integer> ringSettings;
    private ArrayList<Integer> rotorOffsets;

    private ArrayList<String> plugs;



    /************************************************************************
     * Support code for the Initialization, getters and setters of DataStore1.
     */

     public DataStore1() {
        super();

        pairs = new ArrayList<String>();

        wheels = new ArrayList<String>();
        ringSettings = new ArrayList<Integer>();
        rotorOffsets = new ArrayList<Integer>();
    
        plugs = new ArrayList<String>();
    }

    /**
     * Data exchange from the model to this DataStore.
     * @param model contains the data.
     * @return true if data successfully pulled from the model, false otherwise.
     */
    public boolean pull() {
        Model model = Model.getInstance();
        boolean success = true;

        mainX = model.getMainXPos();
        mainY = model.getMainYPos();

        reflectorChoice = model.getReflectorChoice();
        reconfigurable = model.isReconfigurable();

        pairs = model.getPairList();

        fourthWheel = model.isFourthWheel();
        show = model.isShow();

        final int rotorStateCount = model.getRotorStateCount();
        for (int i = 0; i < rotorStateCount; ++i) {
            wheels.add(model.getWheelChoice(i));
            ringSettings.add(model.getRingIndex(i));
            rotorOffsets.add(model.getRotorIndex(i));
        }

        plugs = model.getPlugList();

        return success;
    }

    /**
     * Data exchange from this DataStore to the model.
     * @param model contains the data.
     * @return true if data successfully pushed to the model, false otherwise.
     */
    public boolean push() {
        Model model = Model.getInstance();
        boolean success = true;

        model.setMainPos(mainX, mainY);

        model.initReflectorChoice(reflectorChoice);
        model.setReconfigurable(reconfigurable);

        model.initPairList(pairs);

        model.initFourthWheel(fourthWheel);
        model.setShow(show);

        final int rotorStateCount = wheels.size();
        for (int i = 0; i < rotorStateCount; ++i) {
            model.setRotorState(i, wheels.get(i), ringSettings.get(i), rotorOffsets.get(i));
        }

        model.initPlugList(plugs);

        return success;
    }



    /************************************************************************
     * Support code for static public interface.
     */

    /**
     * Static method that instantiates a DataStore, populates it from the 
     * model and writes it to disc.
     * @return true if data successfully written to disc, false otherwise.
     */
    public static boolean writeData() {
        boolean success = false;

        DataStore1 store = new DataStore1();
        store.pull();
        store.dump();

        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(Model.DATAFILE));

            objectOutputStream.writeObject(store);
            success = true;
        } catch (IOException e) {
            // e.printStackTrace();
            Debug.critical(DD, e.getMessage());
        }

        return success;
    }

    /**
     * Static method that instantiates a DataStore, populates it from disc 
     * and writes it to the model.
     * @return true if data successfully read from disc, false otherwise.
     */
    public static boolean readData() {
        boolean success = false;

        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(Model.DATAFILE));

            DataStore base = (DataStore)objectInputStream.readObject();
            long SVUID = ObjectStreamClass.lookup(base.getClass()).getSerialVersionUID();
 
            DataStore1 store = null;
            if (SVUID == 1) {
                store = (DataStore1)base;
                success = store.push();
                store.dump();
            }

        } catch (IOException e) {
            Debug.critical(DD, e.getMessage());
        } catch (ClassNotFoundException e) {
            Debug.critical(DD, e.getMessage());
        }

        return success;
    }



    /************************************************************************
     * Support code for debug stuff.
     */

    /**
      * Print data store on the command line.
      */
    private void dump() {
        Debug.info(DD, "");
        Debug.info(DD, "mainX = " + mainX);
        Debug.info(DD, "mainY = " + mainY);
        Debug.info(DD, "");
        Debug.info(DD, "reflectorChoice = " + reflectorChoice);
        Debug.info(DD, "reconfigurable = " + reconfigurable);
        Debug.info(DD, "pairs = " + pairs);
        Debug.info(DD, "");
        Debug.info(DD, "fourthWheel = " + fourthWheel);
        Debug.info(DD, "");
        Debug.info(DD, "wheels = " + wheels);
        Debug.info(DD, "ringSettings = " + ringSettings);
        Debug.info(DD, "rotorOffsets = " + rotorOffsets);
        Debug.info(DD, "");
        Debug.info(DD, "plugs = " + plugs);
        Debug.info(DD, "");
        Debug.info(DD, "show = " + show);
        Debug.info(DD, "");
    }

}

