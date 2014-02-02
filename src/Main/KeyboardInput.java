package Main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Moolt
 */
public class KeyboardInput implements KeyListener {

    private static HashMap<Integer, KeyState> keyMap = new HashMap<>();

    @Override
    public void keyTyped(KeyEvent e) {
        keyMap.put(e.getKeyCode(), KeyState.TYPED);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyMap.put(e.getKeyCode(), KeyState.PRESSED);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyMap.put(e.getKeyCode(), KeyState.RELEASED);
    }

    public void update() {
        for (Map.Entry<Integer, KeyState> entry : keyMap.entrySet()) {
            if (entry.getValue() == KeyState.RELEASED) {
                entry.setValue(KeyState.NONE);
            }
        }
    }

    public static boolean isPressed(int event) {
        if (keyMap.containsKey(event) && keyMap.get(event) == KeyState.PRESSED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isReleased(int event) {
        if (keyMap.containsKey(event) && keyMap.get(event) == KeyState.RELEASED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTyped(int event) {
        if (keyMap.containsKey(event) && keyMap.get(event) == KeyState.TYPED) {
            return true;
        } else {
            return false;
        }
    }
}