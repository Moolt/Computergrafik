/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RoadGenerator;

import Main.RoadTile;

/**
 *
 * @author Moolt
 */
public class TunnelState implements AutomatonState {

    private int possibleStates = 3;
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
