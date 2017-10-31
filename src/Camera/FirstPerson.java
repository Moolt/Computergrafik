package Camera;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Moolt
 */
public class FirstPerson extends Camera {

    public FirstPerson(Followable obj, int width, int height) {
        super(obj, width, height);
    }

    @Override
    public void look(GL2 gl) {
        GLU glu = GLU.createGLU(gl);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
//some comment
        glu.gluPerspective(45, width / height, 1.0, 100000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        //Position der Kamera
        float frontX = (float) Math.sin(Math.toRadians(obj.getDirection())) * (obj.getSpeed()+.5f) / 3;
        float frontZ = (float) Math.cos(Math.toRadians(obj.getDirection())) * (obj.getSpeed()+.5f) / 3;
        //Positon des zu betrachtenden Punkts
        float endX = (float) Math.sin(Math.toRadians(obj.getDirection())) * obj.getSpeed();
        float endZ = (float) Math.cos(Math.toRadians(obj.getDirection())) * obj.getSpeed();

        this.x = obj.getX() - frontX - 2;
        this.z = obj.getZ() - frontZ;
        this.y = obj.getY() + 70;

        glu.gluLookAt(x, y, z, this.obj.getX() + endX, this.obj.getY() + 70, this.obj.getZ() + endZ, 0.0, 1.0, 0.0);
    }

    @Override
    public void zoomIn(float factor) {

    }

    @Override
    public void zoomOut(float factor) {

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

    @Override
    public String getName() {
        return "FirstPerson";
    }

}
