
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class SkySphere implements GLEventListener {

    private Texture skyTexture;
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
        //GL2 gl = glad.getGL().getGL2();
        skyTexture.enable(gl);
        skyTexture.bind(gl);

        //create glu object and configure the sphere
        GLU glu = GLU.createGLU(gl);
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluQuadricOrientation(quad, GLU.GLU_INSIDE);        
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);
        gl.glPushMatrix();
        gl.glTranslatef(main.getCameraX(), main.getCameraZ(), main.getCameraY());
        gl.glRotatef(90, 1f, 0f, 0f);
        //draw the spehere
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
