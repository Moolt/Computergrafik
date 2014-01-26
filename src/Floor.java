
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class Floor implements GLEventListener {

    private float width, height, z;
    private Texture floorTexture;

    public Floor(float width, float height, float z) {
        this.width = width;
        this.height = height;
        this.z = z;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        try {
            floorTexture = TextureIO.newTexture(new File("./textures/kachel.jpg"), true);
            //Tiling
            floorTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            floorTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        } catch (Exception ex) {
            Logger.getLogger(Floor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        floorTexture.enable(gl);
        float repeath = width / 32;
        float repeatv = height / 32;
        //Flaeche zeichnen
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, repeatv);
        gl.glVertex3f(-width, z, height);
        gl.glTexCoord2f(repeath, repeatv);
        gl.glVertex3f(width, z, height);
        gl.glTexCoord2f(repeath, 0);
        gl.glVertex3f(width, z, -height);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3f(-width, z, -height);
        gl.glEnd();
        floorTexture.disable(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }
}
