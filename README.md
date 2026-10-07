# Enigma4

`Enigma4` is a JavaFX application that simulates the Enigma machine.

**USE AT OWN RISK.**

## Overview
`Enigma4` is set up as a Maven project that uses JavaFX, FXML and CSS to render 
the GUI. 
Maven can be run from the command line as shown below.
Maven resolves dependencies and builds the application independently of an IDE.

This application simulates the [Enigma Machine](https://en.wikipedia.org/wiki/Enigma_machine). 

**Note:** _there are no restrictions placed on the Rotor selection, as such some 
Rotor combinations can be made that may not be available on the real machine._

**Note:** _the turnover points for the Commercial, Rocket and SwissK Rotors are 
guesses and may be incorrect._

## Dependencies
`Enigma4` is dependent on the following:

  * Java 15.0.1
  * Apache Maven 3.6.3

The source code is structured as a standard Maven project which requires Maven 
and a JDK to be installed. A quick web search will help, alternatively
[Oracle](https://www.java.com/en/download/) and 
[Apache](https://maven.apache.org/install.html) should guide you through the
install.

Also [OpenJFX](https://openjfx.io/openjfx-docs/) can help set up your 
favourite IDE to be JavaFX compatible, however, Maven does not require this.

## Cloning
The following commands clone the code:

	git clone https://github.com/PhilLockett/Enigma4.git
	cd Enigma4/

## Running
Once cloned the following command executes the code:

	mvn clean javafx:run

## User Guide
Selected settings and states will be persisted from one session to the next.
This means that all settings are saved to "Settings.dat" when the application 
is shutdown.
The next time `Enigma4` is executed, these settings are loaded ready to continue 
from where it left off.

### Reflector Set-Up
The choice box allows standard fixed configuration reflectors to be selected. 

Alternatively, select 'CONFIGURABLE' to use a reconfigurable reflector.
Twelve loop-back wired pairs must be defined using the pair-select pop-up 
window after clicking the 'Edit' buttton. 

**Note:** _the thirteenth pair is assumed from the two remaining unused 
letters._

### Rotor Set-Up
By default, `Enigma4` functions as a 3 Rotor machine, allowing the Left, Middle 
and Right Rotors to be configured.
When selected, the 'Use Fourth Rotor' toggle switch, brings in the Fourth Rotor.

#### Rotor Selection
The 'Rotor' choice box allows different Rotors to be selected for each of the 
rotor positions.
No restrictions are placed on the selection so combinations can be selected 
that may not be available on the real machine.

#### Ring Settings
The 'Ring Setting' spinners set the ring setting for each rotor.

#### Rotor Offsets
These rotor 'Offset' spinners set up the initial rotor positions. 

The Rotors advance in a predefined orderly manner with each key press before 
translation.

### Plugboard Connections
Zero or more swap-over pairs can be configured (typically ten are set). 
The pairs are defined using the pair-select pop-up window after clicking the 
'Edit' buttton. 

### Translation
This area shows the key presses and the translated values using the current 
configuration settings.

#### Show Translation
When selected, the 'Show Translation' toggle switch displays all the individual 
translation steps on the command line.

#### Default Settings
The 'Default Settings' choice box allows pre-configured settings to be 
selected from the Luftwaffe Enigma key list number 649. 
The details of these settings can be found [here](https://en.wikipedia.org/wiki/Enigma_machine#Details). 

Selecting one of these entries will override the: 'Wheel order' (rotor), 'Ring 
settings', 'Plugboard connections', 'Reconfigurable reflector wiring' and the 
'Indicator groups' (using the first indicator group for the rotor offset).

## Points of interest
This code has the following points of interest:

  * `Enigma4` simulates the behaviour of the [Enigma](https://en.wikipedia.org/wiki/Enigma_machine) machine.
  * `Enigma4` is an updated version of [Enigma2](https://github.com/PhilLockett/Enigma3).
  * `Enigma4` is a Maven project that uses JavaFX, FXML and CSS.
  * Uses a custom controller, "RotorControl", to represent rotor settings.
  * Uses a custom controller, "PairSelect", to represent reflector and plugboard settings.
  * Uses a custom controller, "SwitchControl", to represent a toggle switch.
  * Uses subtle linear and radial colour gradients on controls.
  * Uses setShape() to create circular text fields.
  * Uses custom top-bars on all windows.
  * Rotor combinations can be selected that may not be available on the real machine.
  * Data is persisted from one session to the next.
  * A `static` Debug object helps control diagnostic output.
