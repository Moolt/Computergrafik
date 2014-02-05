/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Moolt
 */
public class Topdown extends Camera {

    private float cameraDistance = 500f;
    private float cameraSmoothing = 10f;

    public Topdown(Followable obj, int width, int height) {
        super(obj, width, height);
    }

    public Topdown(Followable obj, int width, int height, float cameraDistance) {
        super(obj, width, height);
        this.cameraDistance = cameraDistance;
    }

    @Override
    public void look(GL2 gl) {
        GLU glu = GLU.createGLU(gl);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, width / height, 1.0, 100000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        this.x = obj.getX();
        this.z = obj.getZ() - 1;
        this.y = this.obj.getY() + cameraDistance * (1 + 0.3f * obj.getSpeed() / obj.getMaxSpeed());

        glu.gluLookAt(x, y, z, this.obj.getX(), this.obj.getY(), this.obj.getZ(), 0.0, 0.0, 1.0);
    }

    @Override
    public void zoomIn(float factor) {
        this.cameraDistance -= factor;
    }

    @Override
    public void zoomOut(float factor) {
        this.cameraDistance += factor;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getZ() {
        return this.z;
    }

}
