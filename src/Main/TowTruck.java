package Main;

import Camera.Followable;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import net.java.joglutils.model.DisplayListRenderer;
import net.java.joglutils.model.ModelFactory;
import net.java.joglutils.model.ModelLoadException;
import net.java.joglutils.model.geometry.Model;
import net.java.joglutils.model.iModel3DRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class TowTruck implements GLEventListener, Followable {

    //private Terrain terrain;
    private Model model; //das modell des autos
    private Model tireModel; //das modell eines reifens
    private Model backlightModel; //das modell der hinterlicher
    private iModel3DRenderer modelRenderer;
    private Texture texture;
    private float x = 0f;
    private float y = 0f;
    private float z = 0f;
    private float direction = 10;
    private int rotationY = 54;
    private float tireRotation = 0;
    private float tireTurn = 0f; //die auslenkung der reifen nach rechts/links    
    private float speed = 0f; //die momentane geschwindigkeit des autos
    private final float steering = 250f; //lenktraegigkeit
    private final float acceleration = 0.15f; //beschleunigung
    private final float maxTurn = 20f; //die maximale auslenkung
    private final float maxSpeed = 27f; //die maximal zu erreichende geschwindigkeit
    private boolean reverse = false;

    //private float tilt[] = new float[3]; //neigung des autos
    public TowTruck(float xpos, float ypos) {
        this.x = xpos;
        this.z = ypos;
    }

    /**
     * Hier werden die Keyboard-Eingaben verarbeitet und die Bewegungen des
     * Autos berechnet
     */
    private void update() {
        reverse = false;
        //Drehung der Vorderraeder nach rechts
        if (KeyboardInput.isPressed(KeyEvent.VK_D)) {
            if (tireTurn > -maxTurn) {
                tireTurn -= 1;
            }
            //Drehung der Vorderraeder nach links
        } else if (KeyboardInput.isPressed(KeyEvent.VK_A)) {
            if (tireTurn < maxTurn) {
                tireTurn += 1;
            }
            //Zurueckdrehen der Vorderraeder bei Bewegung
        } else if (speed > 0) {
            tireTurn -= (speed * 0.01 * tireTurn);
        }

        //Vorwaertsbewegung beim Druecken von W
        if (KeyboardInput.isPressed(KeyEvent.VK_W)) {
            if (speed < maxSpeed) {
                speed += acceleration;                
            }
            //Rueckwaertsbewegung beim Druecken von S
        } else if (KeyboardInput.isPressed(KeyEvent.VK_S)) {
            if (speed > -maxSpeed / 2) {
                speed -= acceleration;
                reverse = true;
            }
            //Ausbremsen bei Vorwaertsbewegung
        } else if (speed > 0) {
            speed -= acceleration/2;            
            //Ausbremsen bei Rueckwaertsbewegung
        } else if (speed < 0) {
            speed += acceleration/2;                 
        }

        //Bremse beim Druecken der Leertaste
        if (KeyboardInput.isPressed(KeyEvent.VK_SPACE)) {
            speed -= speed / 30;
            reverse = true;
        }

        //Anpassen der Richtung anhand der Reifendrehung und Geschwindigkeit
        this.direction += (speed * tireTurn) / steering;

        //Berechnung der Position aus Richtung und Geschwindigkeit
        this.x += Math.sin(Math.toRadians(direction)) * speed / 3;
        this.z += Math.cos(Math.toRadians(direction)) * speed / 3;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.modelRenderer = DisplayListRenderer.getInstance();

        this.model = this.loadModel("./models/car.obj", true);
        this.tireModel = this.loadModel("./models/tire.obj", true);
        this.backlightModel = this.loadModel("./models/backlights.obj", false);

    }

    private Model loadModel(String path, boolean center) {
        try {
            Model model = ModelFactory.createModel(path);
            model.centerModelOnPosition(center);
            model.setUseTexture(true);
            model.setRenderModelBounds(false);
            model.setUnitizeSize(false);
            return model;

        } catch (ModelLoadException ex) {
            Logger.getLogger(TowTruck.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.update();

        gl.glPushMatrix();
        gl.glTranslatef(x, y + 25, z);
        gl.glScalef(8.0025f, 8.0025f, 8.0025f);
        gl.glRotatef(direction, 0f, 1f, 0f);
        modelRenderer.render(gl, model);
        if (reverse) {
            this.drawBacklights(gl);
        }
        tireRotation += speed;
        this.drawTire(gl, +3.5f, -2, -5.8f, false);
        this.drawTire(gl, +3.5f, -2, +6.5f, true);
        this.drawTire(gl, -3.5f, -2, -5.8f, false);
        this.drawTire(gl, -3.5f, -2, +6.5f, true);
        gl.glPopMatrix();
    }

    private void drawBacklights(GL2 gl) {
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        modelRenderer.render(gl, backlightModel);
        gl.glDisable(GL2.GL_BLEND);
    }

    private void drawTire(GL2 gl, float x, float y, float z, boolean front) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        if (front) {
            gl.glRotatef(tireTurn * 1.5f, 0f, 1f, 0f);
        }
        gl.glRotatef(+tireRotation, 1f, 0f, 0f);
        this.modelRenderer.render(gl, tireModel);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

    public float getDirection() {
        return direction;
    }

    public float getSpeed() {
        return speed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
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
