package RoadGenerator;

import Main.RoadTile;

/**
 * Ein Zustand des Zustandautomaten
 * @author Moolt
 */
public interface AutomatonState {

    /**
     * @return Der aktuelle Zustand des Automaten
     */
    public RoadTile getState();

    /**
     * 
     * @param random Ein neuer, zufaelliger Zustand
     * @return Zufallszahl, die die Auswahl des naechsten Zustands beeinflusst
     */
    public AutomatonState next(float random);
}
