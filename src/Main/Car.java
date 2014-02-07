package Main;

import Camera.Followable;
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

/**
 * Die Klasse Stellt ein Auto dar, das ueber Tastaturinteraktion bewegt werden kann
 * @author Moolt
 */
public class Car implements GLEventListener, Followable {

    //Das Modell des Autos
    private Model model;
    //Das Modell eines Reifens
    private Model tireModel;
    //Das Modell der Ruecklichter
    private Model backlightModel;
    //Der Modell-Renderer zur Darstellung der Modelle
    private iModel3DRenderer modelRenderer;
    //Die X-Position des Autos
    private float x = 0f;
    //Die Y-Position des Autos
    private float y = 0f;
    //Die Z-Position des Autos
    private float z = 0f;
    //Maximale x-Wert des Autos vorne
    private float maxXTop = 0f;
    //Minimale x-Wert des Autos vorne 
    private float minXTop = 0f;
    //Maximale x-Wert des Autos hinten
    private float maxXBot = 4.5518f;
    //Minimale x-Wert des Autos hinten
    private float minXBot = -4.0956f;
    //Die Richtung, in der das Auto faehrt
    private float direction = 10;
    //Die Drehung der Vorderraeder
    private float tireRotation = 0;
    //Die Auslenkung der Vorderraeder
    private float tireTurn = 0f;
    //Die Geschwindigkeit des Autos
    private float speed = 0f;
    //Traegheit beim Lenken
    private final float steering = 250f;
    //Die Beschleunigung
    private final float acceleration = 0.15f;
    //Maximale Auslenkung der Vorderraeder
    private final float maxTurn = 20f;
    //Maximale Geschwindigkeit des Autos
    private final float maxSpeed = 27f;
    //Ist der Rueckwaertsgang eingelegt?
    private boolean reverse = false;
    
    public Car(float xpos, float ypos) {
        this.x = xpos;
        this.z = ypos;
    }

    /**
     * Hier werden die Keyboard-Eingaben verarbeitet und die Bewegungen des
     * Autos berechnet
     */
    private void update() {
        //System.out.println("xMinTop: "+minXTop+" und xMaxTop: "+maxXTop);
        //System.out.println(" Linker Rand Bot: "+(minXBot)+" Rechter Rand Bot: "+(maxXBot)+ " direction: "+direction);
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
        } else if (speed < 0) {
            tireTurn += (speed * 0.01 * tireTurn);
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
            speed -= acceleration / 2;
            //Ausbremsen bei Rueckwaertsbewegung
        } else if (speed < 0) {
            speed += acceleration / 2;
        } 
        //Bremse beim Druecken der Leertaste
        if (KeyboardInput.isPressed(KeyEvent.VK_SPACE)) {
            speed -= speed / 30;
            reverse = true;
        }
        //Anpassen der Richtung anhand der Reifendrehung und Geschwindigkeit
        this.direction += (speed * tireTurn) / steering;
        if (direction>360 || direction <-360)
            this.direction=direction%360;
        //Berechnung der Position aus Richtung und Geschwindigkeit
        double tmpx = Math.sin(Math.toRadians(direction)) * speed / 3;
        if(maxXTop<=90.1 && minXTop<=82.1){
            this.x += tmpx;
            maxXTop += tmpx;
            minXTop -= tmpx;       
            this.z += Math.cos(Math.toRadians(direction)) * speed / 3;
        }else{
           if((maxXTop>90.1 && (direction<0||direction>180))||(minXTop>82.1 && (direction>0||direction<-180))){       
                if(maxXTop>90.1 && direction<0 && direction>-180){
                    this.x += tmpx;
                    maxXTop += tmpx;
                    minXTop -= tmpx;
                }else if( maxXTop>90.1 && direction<-180){
                    //Sliden
                }else if( maxXTop>90.1 && direction>180){
                    this.x += tmpx;
                    maxXTop += tmpx;
                    minXTop -= tmpx;
                }else if(minXTop>82.1 && direction>0 && direction <180){
                    this.x += tmpx;
                    maxXTop += tmpx;
                    minXTop -= tmpx;
                }else if(minXTop>82.1 && direction>180){
                    //Sliden
                }else if(minXTop>82.1 && direction<-180) {
                    this.x += tmpx;
                    maxXTop += tmpx;
                    minXTop -= tmpx;
                }             
                this.z += Math.cos(Math.toRadians(direction)) * speed / 3; 
           }else{
               this.z += Math.cos(Math.toRadians(direction)) * speed / 3;
           }
        }
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        this.modelRenderer = DisplayListRenderer.getInstance();

        //Hier werden die Modelle geladen
        //Die Texturen werden automatisch durch die ModelFactory Klasse nachgeladen
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
            if(path.equals("./models/car.obj")){
                maxXTop = model.getBounds().max.x;
                minXTop = model.getBounds().min.x;
            }
            return model;

        } catch (ModelLoadException ex) {
            Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
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
        gl.glTranslatef(x, y + 30, z);
        gl.glScalef(8.0025f, 8.0025f, 8.0025f);
        gl.glRotatef(direction, 0f, 1f, 0f);
        //Darstellung des Auto-Modells
        modelRenderer.render(gl, model);
        //Darstellung der Ruecklichter, wenn das Auto rueckwaerts faehrt
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

    /**
     * Zeichnet den Schein der Ruecklichter
     * @param gl 
     */
    private void drawBacklights(GL2 gl) {
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        modelRenderer.render(gl, backlightModel);
        gl.glDisable(GL2.GL_BLEND);
    }

    /**
     * Zeichnet ein Rad an der gegebenen Position mit Auslenkung (Falls Vorderrad)
     * @param gl
     * @param x X-Position des Rads
     * @param y Y-Position des Rads
     * @param z Z-Position des Rads
     * @param front Ist das Rad ein Vorderrad?
     */
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

    /**
     * 
     * @return Die Richtung, in der das Auto faehrt
     */
    public float getDirection() {
        return direction;
    }

    /**
     * 
     * @return Die Geschwindigkeit des Autos
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * 
     * @return Die maximale Geschwindigkeit des Autos
     */
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
