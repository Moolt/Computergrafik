
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class Terrain implements GLEventListener {

    private float[][] heightmap;
    private int height, width;
    private Texture texture;

    public Terrain(String heightmap) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(heightmap));
            this.height = bufferedImage.getHeight();
            this.width = bufferedImage.getWidth();

            this.heightmap = new float[width][height];
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    Color color = new Color(bufferedImage.getRGB(w, h));
                    float grayTone = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                    this.heightmap[w][h] = grayTone;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        try {
            this.texture = TextureIO.newTexture(new File("./textures/dirt.jpg"), true);
            texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        } catch (Exception ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        texture.enable(gl);
        texture.bind(gl);
        int scale = 7; //1 to 10 pixel
        float factor = 2.1f;

        for (int w = 0; w < width - 1; w++) {
            gl.glBegin(GL2.GL_TRIANGLES);
            for (int h = 0; h < height - 1; h++) {
                gl.glTexCoord2f(0, 0);
                gl.glVertex3f(w * scale, (float) heightmap[w][h] / factor, h * scale);
                gl.glTexCoord2f(0, 1);
                gl.glVertex3f(w * scale, (float) heightmap[w][h + 1] / factor, (h + 1) * scale);
                gl.glTexCoord2f(1, 1);
                gl.glVertex3f((w + 1) * scale, (float) heightmap[w + 1][h + 1] / factor, (h + 1) * scale);

                gl.glTexCoord2f(0, 0);
                gl.glVertex3f(w * scale, (float) heightmap[w][h] / factor, h * scale);
                gl.glTexCoord2f(1, 1);
                gl.glVertex3f((w + 1) * scale, (float) heightmap[w + 1][h + 1] / factor, (h + 1) * scale);
                gl.glTexCoord2f(1, 0);
                gl.glVertex3f((w + 1) * scale, (float) heightmap[w + 1][h] / factor, h * scale);
            }
            gl.glEnd();
        }

        texture.disable(gl);

    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

}
