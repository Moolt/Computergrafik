
import java.awt.event.KeyEvent;
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
public class TowTruck implements GLEventListener {

    private Terrain terrain;
    private GLModel carModel; //das modell des autos
    private GLModel tireModel; //das modell eines reifens
    private float xPosition = 0f;
    private float yPosition = 0f;
    private float direction = 10;
    private float height = 0f;
    private int rotationY = 54;
    private float tireRotation = 0;
    private float tireTurn = 0f; //die auslenkung der reifen nach rechts/links    
    private float speed = 0f; //die momentane geschwindigkeit des autos
    private final float maxTurn = 20f; //die maximale auslenkung
    private final float maxSpeed = 15f; //die maximal zu erreichende geschwindigkeit
    private float tilt[] = new float[3]; //neigung des autos

    public TowTruck(Terrain terrain) {
        this.terrain = terrain;
        tilt[0] = 0f;
        tilt[1] = 0f;
        tilt[2] = 0f;
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
        this.direction += (speed * tireTurn) / 100;

        //Berechnung der Position aus Richtung und Geschwindigkeit
        this.yPosition += Math.cos(Math.toRadians(direction)) * speed / 3;
        this.xPosition += Math.sin(Math.toRadians(direction)) * speed / 3;
        
        
        //winkel vom auto wird aus der differenz der urspruenglichen hoehe und der neuen berechnet
        //damit die bewegung etwas weicher wirkt, wird der winkel mit aelteren interpoliert
        float prevHeight = height;
        tilt[0] = tilt[1];
        tilt[1] = tilt[2];
        this.height = this.terrain.getHeight(xPosition, yPosition);
        this.tilt[2] = (height - prevHeight) * 3 + tilt[1]/4 + tilt[0]/8;
    }

    private void drawTire(GL2 gl, float x, float y, float z, boolean front) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        if (front) {
            gl.glRotatef(tireTurn, 0f, 1f, 0f);
        }
        gl.glRotatef(-tireRotation, 0f, 0f, 1f);
        this.tireModel.opengldraw(gl);
        gl.glPopMatrix();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.carModel = ModelLoaderOBJ.LoadModel("./models/auto.obj",
                "pfad zu mtl", gl);
        this.tireModel = ModelLoaderOBJ.LoadModel("./models/reifen.obj",
                "pfad zu mtl", gl);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.update();
        //gl.glLoadIdentity();

        gl.glPushMatrix();

        gl.glTranslatef(xPosition, height + 20, yPosition);
        gl.glScalef(0.25f, 0.25f, 0.25f);
        gl.glRotatef(direction - 90, 0f, 1f, 0f);
        gl.glRotatef(tilt[2], 0f, 0f, 1f);
        this.carModel.opengldraw(gl);
        tireRotation += speed;
        this.drawTire(gl, 70, -45, -50, true);
        this.drawTire(gl, 70, -45, +50, true);
        this.drawTire(gl, -70, -45, -50, false);
        this.drawTire(gl, -70, -45, +50, false);
        gl.glPopMatrix();
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
    
    public float getTilt(){
        return tilt[2] + tilt[1]/2 + tilt[0] /4;
    }
}
