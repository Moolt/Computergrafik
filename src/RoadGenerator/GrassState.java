package RoadGenerator;

import Main.RoadTile;

/**
 *
 * @author Moolt
 */
public class GrassState implements AutomatonState {

    //Die Anzahl der moeglichen Folgezustaende
    private int possibleStates = 3;
    //Die minimale Wiederholung des Zustands
    private static int minRepeat;

    public GrassState() {
        if (minRepeat == 0) {
            minRepeat = (int) (Math.random() * 10) + 6;
        }
    }

    @Override
    public RoadTile getState() {
        return RoadTile.GRASS;
    }

    @Override
    public AutomatonState next(float random) {
        float s1 = random * (possibleStates - 1f);
        int state = Math.round(s1);

        if (minRepeat > 0) {
            minRepeat--;
            return this;
        }

        switch (state) {
            case 0:
                return new WaterState();
            case 1:
                return new GrassState();
            case 2:
                return new TunnelBeginState();
            default:
                return new GrassState();
        }
    }
}
