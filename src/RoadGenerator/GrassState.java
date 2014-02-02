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
public class GrassState implements AutomatonState {

    private int possibleStates = 3;

    @Override
    public RoadTile getState() {
        return RoadTile.GRASS;
    }

    @Override
    public AutomatonState next(float random) {
        float s1 = random * (possibleStates - 1f);
        int state = Math.round(s1);
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