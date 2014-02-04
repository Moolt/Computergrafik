package Main;

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
public class TowTruck implements GLEventListener {

    //private Terrain terrain;
    private Model carModel; //das modell des autos
    private Model tireModel; //das modell eines reifens
    private iModel3DRenderer modelRenderer;
    private Texture texture;
    private float xPosition = 0f;
    private float yPosition = 0f;
    private float direction = 10;
    private float height = 0f;
    private int rotationY = 54;
    private float tireRotation = 0;
    private float tireTurn = 0f; //die auslenkung der reifen nach rechts/links    
    private float speed = 0f; //die momentane geschwindigkeit des autos
    private float steering = 250f;
    private final float maxTurn = 20f; //die maximale auslenkung
    private final float maxSpeed = 20f; //die maximal zu erreichende geschwindigkeit

    //private float tilt[] = new float[3]; //neigung des autos
    public TowTruck(float xpos, float ypos) {
        this.xPosition = xpos;
        this.yPosition = ypos;
    }

    /**
     * Hier werden die Keyboard-Eingaben verarbeitet und die Bewegungen des
     * Autos berechnet
     */
    private void update() {
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
                speed += 0.1f;
            }
            //Rueckwaertsbewegung beim Druecken von S
        } else if (KeyboardInput.isPressed(KeyEvent.VK_S)) {
            if (speed > -maxSpeed / 2) {
                speed -= 0.1f;
            }
            //Ausbremsen bei Vorwaertsbewegung
        } else if (speed > 0) {
            speed -= 0.05f;
            //Ausbremsen bei Rueckwaertsbewegung
        } else if (speed < 0) {
            speed += 0.05f;
        }

        //Bremse beim Druecken der Leertaste
        if (KeyboardInput.isPressed(KeyEvent.VK_SPACE)) {
            speed -= speed / 30;
        }

        //Anpassen der Richtung anhand der Reifendrehung und Geschwindigkeit
        this.direction += (speed * tireTurn) / steering;

        //Berechnung der Position aus Richtung und Geschwindigkeit
        this.yPosition += Math.cos(Math.toRadians(direction)) * speed / 3;
        this.xPosition += Math.sin(Math.toRadians(direction)) * speed / 3;
    }

    private void drawTire(GL2 gl, float x, float y, float z, boolean front) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        if (front) {
            gl.glRotatef(tireTurn, 0f, 1f, 0f);
        }
        gl.glRotatef(-tireRotation, 1f, 0f, 0f);
        this.modelRenderer.render(gl, tireModel);
        gl.glPopMatrix();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.modelRenderer = DisplayListRenderer.getInstance();
        try {
            this.carModel = ModelFactory.createModel("./models/car.obj");
            this.tireModel = ModelFactory.createModel("./models/tire.obj");
            // When loading the model, adjust the center to the boundary center
            this.carModel.centerModelOnPosition(true);
            this.tireModel.centerModelOnPosition(true);
            this.carModel.setUseTexture(true);
            this.tireModel.setUseTexture(true);
            this.carModel.setRenderModelBounds(false);
            this.tireModel.setRenderModelBounds(false);
            //carModel.setUseLighting(true);
            this.carModel.setUnitizeSize(false);
            this.tireModel.setUnitizeSize(false);

        } catch (ModelLoadException ex) {
            Logger.getLogger(TowTruck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.update();
        //gl.glLoadIdentity();

        //texture.enable(gl);
        //texture.bind(gl);
        gl.glPushMatrix();

        gl.glTranslatef(xPosition, height + 25, yPosition);
        gl.glScalef(8.0025f, 8.0025f, 8.0025f);
        gl.glRotatef(direction, 0f, 1f, 0f);
        modelRenderer.render(gl, carModel);
        tireRotation += speed;
        this.drawTire(gl, +3.5f, -2, -5.8f, false);
        this.drawTire(gl, +3.5f, -2, +6.5f, true);
        this.drawTire(gl, -3.5f, -2, -5.8f, false);
        this.drawTire(gl, -3.5f, -2, +6.5f, true);
        gl.glPopMatrix();

        //texture.disable(gl);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }
}
