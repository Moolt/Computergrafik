package Main;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Zeichnet einen Skysphere an der Position der Kamera
 *
 * @author Moolt
 */
public class SkySphere implements GLEventListener {

    //Die Textur des Skysphere
    private Texture skyTexture;
    //Eine Referenz zur Hauptklasse zur Ermittlung der Kameraposition
    private Main main;

    public SkySphere(Main main) {
        this.main = main;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        try {
            GL2 gl = glad.getGL().getGL2();
            this.skyTexture = TextureIO.newTexture(new File("./textures/skysphere.jpg"), true);
        } catch (Exception ex) {
            Logger.getLogger(SkySphere.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {

    }

    public void draw(GL2 gl) {
        skyTexture.enable(gl);
        skyTexture.bind(gl);

        //GLU Instanz erzeugen und den quadratic-Koerper konfigurieren
        GLU glu = GLU.createGLU(gl);
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluQuadricOrientation(quad, GLU.GLU_INSIDE);
        //Tiefenueberpruefung ausschalten
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);
        gl.glPushMatrix();
        gl.glTranslatef(main.getCameraX(), main.getCameraY(), main.getCameraZ());
        gl.glRotatef(90, 1f, 0f, 0f);
        //Zeichnen des Skysphere
        glu.gluSphere(quad, 400, 10, 15);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glDepthMask(true);
        gl.glEnable(GL.GL_DEPTH_TEST);
        skyTexture.disable(gl);
        glu.gluDeleteQuadric(quad);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

}
