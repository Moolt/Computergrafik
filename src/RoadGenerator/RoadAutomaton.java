package RoadGenerator;

import Main.RoadTile;

/**
 * Zustandsautomat, der zufaellige Strassenabschnitte erzeugt
 * @author Moolt
 */
public class RoadAutomaton {
    //Der aktuelle Zustand des Automaten
    private AutomatonState state;

    public RoadAutomaton() {
        this.state = new GrassState();
    }

    /**
     * Gibt den aktuellen Zustand zurueck und laesst den Automaten einen
     * neuen, zufaelligen Zustand annehmen
     * @return Der Aktuelle Zustand des Automaten
     */
    public RoadTile next() {
        RoadTile tile = state.getState();
        this.state = state.next((float) Math.random());
        System.err.println(tile.toString());
        return tile;
    }
}
