package Main;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Erleichtert die Verarbeitung von Mauseingaben
 * @author Moolt
 */
public class MouseInput implements MouseWheelListener {

    private Main main;

    public MouseInput(Main main) {
        this.main = main;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            this.main.zoomIn();
        } else {
            this.main.zoomOut();
        }
    }
}
