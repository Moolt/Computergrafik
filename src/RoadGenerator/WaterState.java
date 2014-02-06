package RoadGenerator;

import Main.RoadTile;

/**
 *
 * @author Moolt
 */
public class WaterState implements AutomatonState {

    //Die Anzahl der moeglichen Folgezustaende
    private int possibleStates = 3;
    //Die minimale Wiederholung des Zustands
    private static int minRepeat;

    public WaterState() {
        if (minRepeat == 0) {
            minRepeat = (int) (Math.random() * 10) + 4;
        }
    }

    @Override
    public RoadTile getState() {
        return RoadTile.WASSER;
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
                return new WaterState();
            case 1:
                return new EndWaterState();
            case 2:
                return new TunnelBeginState();
            default:
                return new WaterState();
        }
    }
}
