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
public class ThirdPersonCamera extends Camera {

    private float cameraX = 0f;
    private float cameraY = 400f;
    private float cameraZ = 0f;
    private float cameraDistance = 500f;
    private float cameraSmoothing = 10f;
    private boolean isSticky = false;

    public ThirdPersonCamera(Followable obj, int width, int height) {
        super(obj, width, height);
    }

    public ThirdPersonCamera(Followable obj, int width, int height, float cameraDistance, float cameraSmoothing, boolean isSticky) {
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

        float cZ = (float) Math.cos(Math.toRadians(obj.getDirection()))
                * cameraDistance * (1 + 0.5f * obj.getSpeed() / obj.getMaxSpeed());
        float cX = (float) Math.sin(Math.toRadians(obj.getDirection()))
                * cameraDistance * (1 + 0.5f * obj.getSpeed() / obj.getMaxSpeed());
        
        
        //Die Kamera nimmt nicht die berechneten werte an, sondern "haengt immer etwas hinterher"        
        cameraX -= (cameraX - cX) / cameraSmoothing;
        cameraZ -= (cameraZ - cZ) / cameraSmoothing;
        //Kamera guckt auf das Auto

        if (!isSticky) {
            this.x = obj.getX();
            this.z = obj.getZ() - cameraDistance;
        } else {
            this.x = +obj.getX() - cameraX;
            this.z = +obj.getZ() - cameraZ;
        }
        this.y = this.obj.getY() + cameraY * (1 - 0.1f * obj.getSpeed() / obj.getMaxSpeed());

        glu.gluLookAt(x, y, z, this.obj.getX(), this.obj.getY(), this.obj.getZ(), 0.0, 1.0, 0.0);
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
