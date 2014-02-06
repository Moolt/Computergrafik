package RoadGenerator;

import Main.RoadTile;

/**
 *
 * @author Moolt
 */
public class TunnelState implements AutomatonState {

    //Die Anzahl der moeglichen Folgezustaende
    private int possibleStates = 3;
    //Die minimale Wiederholung des Zustands
    private static int minRepeat;

    public TunnelState() {
        if (minRepeat == 0) {
            minRepeat = (int) (Math.random() * 4) + 4;
        }
    }

    @Override
    public RoadTile getState() {
        return RoadTile.TUNNEL;
    }

    @Override
    public AutomatonState next(float random) {
        int state = Math.round(random * (possibleStates - 1));

        if (minRepeat > 0) {
            minRepeat--;
            return this;
        }

        switch (state) {
            case 0:
                return new TunnelState();
            case 1:
                return new WaterState();
            case 2:
                return new GrassState();
            default:
                return new TunnelState();
        }
    }
}
