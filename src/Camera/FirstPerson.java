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
public class FirstPerson extends Camera {

    private float cameraX = 0f;
    private float cameraY = 400f;
    private float cameraZ = 0f;
    private float cameraDistance = 500f;
    private float cameraSmoothing = 10f;
    private boolean isSticky = false;

    public FirstPerson(Followable obj, int width, int height) {
        super(obj, width, height);
    }

    public FirstPerson(Followable obj, int width, int height, float cameraDistance, float cameraSmoothing, boolean isSticky) {
        super(obj, width, height);
        this.cameraDistance = cameraDistance;
        this.cameraSmoothing = cameraSmoothing;
        this.isSticky = isSticky;
    }

    @Override
    public void look(GL2 gl) {
        GLU glu = GLU.createGLU(gl);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, width / height, 1.0, 100000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        float frontX = (float) Math.sin(Math.toRadians(obj.getDirection())) * obj.getSpeed() /3;
        float frontZ = (float) Math.cos(Math.toRadians(obj.getDirection())) * obj.getSpeed() /3;

        float endX = (float) Math.sin(Math.toRadians(obj.getDirection())) * obj.getSpeed();
        float endZ = (float) Math.cos(Math.toRadians(obj.getDirection())) * obj.getSpeed();

        this.x = obj.getX() - frontX;
        this.z = obj.getZ() - frontZ;
        this.y = obj.getY() + 70;

        glu.gluLookAt(x, y, z, this.obj.getX() + endX, this.obj.getY() + 70, this.obj.getZ() + endZ, 0.0, 1.0, 0.0);
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
