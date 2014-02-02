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
public class TunnelBeginState implements AutomatonState {

    private int possibleStates = 1;

    @Override
    public RoadTile getState() {
        return RoadTile.TUNNELBEGIN;
    }

    @Override
    public AutomatonState next(float random) {
        int state = Math.round(random * (possibleStates - 1));

        switch (state) {
            case 0:
                return new TunnelState();
            default:
                return new TunnelState();
        }
    }
}
