/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import javax.media.opengl.GL2;

/**
 *
 * @author Moolt
 */
public abstract class Camera {

    protected Followable obj;
    protected int width, height;
    protected float x = 0f;
    protected float y = 0f;
    protected float z = 0f;

    public Camera(Followable obj, int width, int height) {
        this.obj = obj;
        this.width = width;
        this.height = height;
    }

    public abstract void look(GL2 gl);

    public abstract void zoomIn(float factor);

    public abstract void zoomOut(float factor);

    public abstract float getX();

    public abstract float getY();

    public abstract float getZ();
}
