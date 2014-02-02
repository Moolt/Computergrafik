package Main;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private GLModel treeModel;
    private List<Tree> trees;
    private float[][] heightmap;
    private int height, width;
    private Texture texture;
    private int grid = 4;
    private float factor = 2f;

    public Terrain(String heightmapPath, int grid, float factor) {
        this.trees = new ArrayList<>();
        this.grid = grid;
        this.factor = factor;

        try {
            BufferedImage bufferedImage = ImageIO.read(new File(heightmapPath));
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
        this.treeModel = ModelLoaderOBJ.LoadModel("./models/tree.obj", "", gl);
        //this.placeTrees(1000);
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
        this.drawTrees(gl);
        texture.enable(gl);
        texture.bind(gl);

        for (int w = 0; w < width - 1; w++) {
            gl.glBegin(GL2.GL_TRIANGLES);
            for (int h = 0; h < height - 1; h++) {
                gl.glTexCoord2f(0, 0);
                gl.glVertex3f(w * grid, (float) heightmap[w][h] / factor, h * grid);
                gl.glTexCoord2f(0, 1);
                gl.glVertex3f(w * grid, (float) heightmap[w][h + 1] / factor, (h + 1) * grid);
                gl.glTexCoord2f(1, 1);
                gl.glVertex3f((w + 1) * grid, (float) heightmap[w + 1][h + 1] / factor, (h + 1) * grid);

                gl.glTexCoord2f(0, 0);
                gl.glVertex3f(w * grid, (float) heightmap[w][h] / factor, h * grid);
                gl.glTexCoord2f(1, 1);
                gl.glVertex3f((w + 1) * grid, (float) heightmap[w + 1][h + 1] / factor, (h + 1) * grid);
                gl.glTexCoord2f(1, 0);
                gl.glVertex3f((w + 1) * grid, (float) heightmap[w + 1][h] / factor, h * grid);
            }
            gl.glEnd();
        }
        texture.disable(gl);
    }

    public float getHeight(float x, float z) {
        //determine which grid the instance is in
        int gridx = (int) Math.floor(x / grid);
        int gridy = (int) Math.floor(z / grid);

        //out of terrain
        if (gridx < 0 || gridy < 0 || gridx >= width || gridy >= height) {
            return 0f;
        }

        //determine where in the grid the instance is
        float offsetx = x - grid * gridx;
        float offsety = z - grid * gridy;

        //retrieve z values from grid map
        float z1 = heightmap[gridx][gridy];
        float z2 = heightmap[gridx + 1][gridy];
        float z3 = heightmap[gridx + 1][gridy + 1];
        float z4 = heightmap[gridx][gridy + 1];

//determine which triangle the instance is in
        if (offsetx > offsety) {
            return (z1 + ((z2 - z1) / (grid / Math.max(1, offsetx))) + ((z3 - z2) / (grid / Math.max(1, offsety)))) / factor;
        } else {
            return (z1 + ((z3 - z4) / (grid / Math.max(1, offsetx))) + ((z4 - z1) / (grid / Math.max(1, offsety)))) / factor;
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

    private void drawTrees(GL2 gl) {

       // gl.glPushMatrix();
        //gl.glLoadIdentity();
        for (Tree tree : trees) {
            gl.glTranslatef(tree.getX(), tree.getZ(), tree.getY());
            gl.glScalef(0.25f, 0.25f, 0.25f);
            gl.glRotatef(tree.getRotation(), 0f, 1f, 0f);
            this.treeModel.opengldraw(gl);
        }
       // gl.glPopMatrix();
    }

    public void placeTrees(int count) {
        for (int i = 0; i < count; i++) {
            float tx = (float) Math.random() * width;
            float ty = (float) Math.random() * height;
            float tz = this.getHeight(tx, ty);
            Tree t = new Tree(tx, ty, tz);
            this.trees.add(t);
        }
    }
}
