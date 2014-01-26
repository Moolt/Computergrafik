
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Moolt
 */
public class MouseInput implements MouseWheelListener{
    private Main main;
    
    public MouseInput(Main main){
        this.main = main;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() < 0){
            this.main.zoomIn();
        } else {
            this.main.zoomOut();
        }
    }
}
