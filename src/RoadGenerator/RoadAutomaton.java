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
public class RoadAutomaton {
    private AutomatonState state;
    
    public RoadAutomaton(){
        this.state = new GrassState();
    }
    
    public RoadTile next(){
        RoadTile tile = state.getState();
        this.state = state.next((float)Math.random());
        System.err.println(tile.toString());
        return tile;
    }
}
