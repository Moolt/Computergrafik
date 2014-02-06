package Main;

import RoadGenerator.RoadAutomaton;
import com.jogamp.opengl.util.texture.Texture;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Diese Klasse ist zur Darstellung einer Strasse, die zufallsgeneriert wird
 *
 * @author Moolt
 */
public class Road implements GLEventListener {

    //Das Auto, dessen Position benoetigt wird
    private Car car;
    //Der Zustands-Automat, der fuer die Generierung ser Strasse zustaendig ist
    private RoadAutomaton roadAutomaton;
    //Modell der Leitpfosten seitlich der Strasse
    private Model poempelModel;
    //Model Renderer, zur Dartellung der Modelle
    private iModel3DRenderer modelRenderer;
    //Map, welche die Position und die Art des Strassenabschnitts jeweils als Paare speichert
    private List<Map.Entry<Integer, RoadTile>> roadTiles = new ArrayList<>();
    //Der Abschnitt, auf dessen hoehe sich das Auto befindet
    private int mainHeight = 0;
    //Der Mittelpunkt der Strasse (X-Koordinate)
    private float midOfRoad = 1000f;
    //Die maximale Anzahl von Strassenabschnitten, die gleichzeitig angezeigt werden
    //Bei groesserer Sichtweite muss diese Konstante erhoeht werden
    private final int numberOfTiles = 12;
    //Die Hoehe eines Strassenabschnitts
    private final float roadTileHeight = 600f;
    //Die Breite eines Strassenabschnitts
    private final float roadTileWidth = 220f;
    //Die Breite der Strassenkante
    private final float roadDropWidth = 20f;
    //Die Breite einer Grassflaeche neben der Strasse
    private final float grassTileWidth = 600f;
    //Die Breite des Seitenstreifens
    private final float sideStripeWidth = 40f;
    //Die Breite einer Wand eines Tunneleingangs
    private final float tunnelBeginWallWidth = 600f;
    //Die Hoehe einer Wand eines Tunneleingangs
    private final float tunnelWallHeight = 450f;
    //Die Breite einer Wasserflaeche neben/unter der Strasse
    private final float waterWidth = 1200f;
    //Die Hoehe des Wasserspiegels (normalerweise negativ)
    private final float waterHeight = -20f;
    //Die Verschiebung der Wasser-Textur auf der Flaeche
    //Aendert sich ueber Zeit, um eine Animation zu erzeugen
    private float waterOffset = 0f;
    //Die Asphalt Textur
    private Texture roadTexture;
    //Die Gras Textur
    private Texture grassTexture;
    //Die Textur der Seitenstreifen
    private Texture concreteTexture;
    //Die Textur fuer Tunnel
    private Texture brickTexture;
    //Die Wasser Textur
    private Texture waterTexture;

    //Tiling Parameter fuer die Strasse
    private float tileX;
    private float tileY;
    //Tiling Parameter fuer Strassenkante
    private float roadDropTile;
    //Tiling Parameter fuer die Grasflaechen
    private float grassTileX;
    private float grassTileY;
    //Tiling Parameter fuer die Seitenstreifen
    private float sideStripeTileX;
    private float sideStripeTileY;
    //Tiling Parameter fuer die Breite der Waender von Tunneleingaengen
    private float tunnelWallBeginTileX;
    //Tiling Parameter fuer die Waende der Tunnel
    private float tunnelWallTileX;
    private float tunnelWallTileY;
    //Tiling Parameter fuer Wasser
    private float waterTileX;
    private float waterTileY;
    private float waterEndGrassTileY;

    public Road(Car towTruck, float midOfRoad) {
        this.car = towTruck;
        this.midOfRoad = midOfRoad;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        try {
            GL2 gl = glad.getGL().getGL2();
            this.modelRenderer = DisplayListRenderer.getInstance();
            this.roadAutomaton = new RoadAutomaton();
            //Hier werden die Texturen geladen
            this.roadTexture = TextureLoader.loadTexture(gl, "asphalt.png");
            this.grassTexture = TextureLoader.loadTexture(gl, "grass.png");
            this.concreteTexture = TextureLoader.loadTexture(gl, "concrete.png");
            this.brickTexture = TextureLoader.loadTexture(gl, "brick.jpg");
            this.waterTexture = TextureLoader.loadTexture(gl, "water.png");
            //Landen des Leitpfosten Modells
            this.poempelModel = ModelFactory.createModel("./models/poempel.obj");
            this.poempelModel.setUseTexture(true);
            this.poempelModel.setUnitizeSize(false);
            poempelModel.centerModelOnPosition(true);
            poempelModel.setRenderModelBounds(false);

            //Berechnung der Tiling Werte
            tileX = roadTileWidth / roadTexture.getWidth();
            tileY = roadTileHeight / roadTexture.getHeight();
            roadDropTile = roadDropWidth / roadTexture.getWidth();
            grassTileX = grassTileWidth / grassTexture.getWidth();
            grassTileY = roadTileHeight / grassTexture.getHeight();
            sideStripeTileX = sideStripeWidth / concreteTexture.getWidth();
            sideStripeTileY = roadTileHeight / concreteTexture.getHeight();
            tunnelWallBeginTileX = tunnelBeginWallWidth / concreteTexture.getWidth();
            tunnelWallTileX = roadTileHeight / brickTexture.getWidth();
            tunnelWallTileY = tunnelWallHeight / brickTexture.getHeight();
            waterTileX = waterWidth / waterTexture.getWidth();
            waterTileY = roadTileHeight / waterTexture.getHeight();
            waterEndGrassTileY = waterHeight * -1 / grassTexture.getHeight();

            //Initialwerte der angezeigten Strassenabschnitte
            for (int i = 0; i < numberOfTiles; i++) {
                roadTiles.add(createRandomTile(i));
            }
        } catch (ModelLoadException ex) {
            Logger.getLogger(Road.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

    @Override
    public void display(GLAutoDrawable glad) {
        if ((int) car.getZ() / (int) roadTileHeight < roadTiles.size() / 2) {
            mainHeight = roadTiles.size() / 2;
        } else {
            mainHeight = (int) car.getZ() / (int) roadTileHeight;
        }
        this.update();

        GL2 gl = glad.getGL().getGL2();

        for (int i = 0; i < roadTiles.size(); i++) {
            //Auto befindet sich immer auf dem mittleren Strassenabschnitt
            int index = i - roadTiles.size() / 2;
            //Ermitteln, welcher Typ von Strassenabschnitt gezeichnet werden soll
            switch (roadTiles.get(i).getValue()) {
                case GRASS:
                    this.drawGrassTile(gl, index);
                    break;
                case TUNNELBEGIN:
                    this.drawTunnelBeginTile(gl, index);
                    break;
                case TUNNEL:
                    this.drawTunnelTile(gl, index);
                    break;
                case WASSER:
                    this.drawWaterTile(gl, index);
                    break;
                case WASSEREND:
                    this.drawWaterEndTile(gl, index);
                    break;
            }
        }
    }

    private void update() {
        //Berechnet das mittlere element der liste
        int midOfList = (int) Math.ceil(roadTiles.size() / 2);

        //Falls das Auto sich ueber dem mittleren Strassenabschnitt befindet
        //wird das hinterste geloescht und vorne ein neues eingefuegt
        if (roadTiles.get(midOfList).getKey() < mainHeight) {
            roadTiles.remove(0);
            //Neuster index + 1
            int newIndex = roadTiles.get(roadTiles.size() - 1).getKey() + 1;
            roadTiles.add(this.createRandomTile(newIndex));
        }
    }

    /**
     * Generiert anhand des Zustandsautomaten einen neuen Strassenabschnitt
     *
     * @param index Position des zu erzeugenden Strassenabschnitts
     * @return Ein Paar, das die Position und den Typen des erzeugten
     * Strassenabschnitts erhaelt
     */
    private Map.Entry<Integer, RoadTile> createRandomTile(int index) {
        return new AbstractMap.SimpleEntry<>(index, roadAutomaton.next());
    }

    /**
     * Zeichnet eine Strasse
     *
     * @param gl
     * @param i
     */
    private void drawRoad(GL2 gl, int i) {
        roadTexture.bind(gl);
        roadTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichne Strasse
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2, (mainHeight + i) * roadTileHeight, 0,
                midOfRoad + roadTileWidth / 2, (mainHeight + i + 1) * roadTileHeight, 0, tileX, tileY);
        //Zeichne uebergang von Strasse zu Gras
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2, (mainHeight + i + 1) * roadTileHeight, 0, roadDropTile, tileY);
        this.drawFloor(gl, midOfRoad + roadTileWidth / 2, (mainHeight + i) * roadTileHeight, 0,
                midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, roadDropTile, tileY);
        gl.glEnd();
        roadTexture.disable(gl);
    }

    /**
     * Zeichnet Seitenstreifen
     *
     * @param gl
     * @param i
     */
    private void drawSideStripe(GL2 gl, int i) {
        concreteTexture.bind(gl);
        concreteTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichne Seitenstreifen
        this.drawFloor(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, -10, sideStripeTileX, sideStripeTileY);
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, sideStripeTileX, sideStripeTileY);
        gl.glEnd();
        concreteTexture.disable(gl);
    }

    /**
     * Zeichnet die Waende eines Tunnel-Abschnitts
     *
     * @param gl
     * @param i
     */
    private void drawTunnelWalls(GL2 gl, int i) {
        brickTexture.enable(gl);
        brickTexture.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        this.drawWall(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, tunnelWallHeight, tunnelWallTileX, tunnelWallTileY);
        this.drawWall(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i) * roadTileHeight, tunnelWallHeight, tunnelWallTileX, tunnelWallTileY);
        gl.glEnd();
        brickTexture.disable(gl);
    }

    /**
     * Zeichnet den Gras-Strassenabschnitt mit Leitpfosten
     *
     * @param gl
     * @param i
     */
    private void drawGrassTile(GL2 gl, int i) {
        this.drawRoad(gl, i);

        //Zeichnet die Leitpfosten am Rande der Strasse
        for (int j = 0; j < 2; j++) {
            gl.glPushMatrix();
            gl.glTranslatef(midOfRoad + roadTileWidth / 2 + roadDropWidth + 10, 25f, (mainHeight + i) * roadTileHeight + (roadTileHeight / 2) * j + (roadTileHeight / 4));
            gl.glScalef(30f, 40f, 30f);
            modelRenderer.render(gl, poempelModel);
            gl.glPopMatrix();
            gl.glPushMatrix();
            gl.glTranslatef(midOfRoad - roadTileWidth / 2 - roadDropWidth - 10, 35f, (mainHeight + i) * roadTileHeight + (roadTileHeight / 2) * j + (roadTileHeight / 4));
            gl.glScalef(30f, 40f, 30f);
            modelRenderer.render(gl, poempelModel);
            gl.glPopMatrix();
        }

        grassTexture.bind(gl);
        grassTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichne Grasflaechen
        this.drawFloor(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + grassTileWidth, (mainHeight + i + 1) * roadTileHeight, -10, grassTileX, grassTileY);
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - grassTileWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, grassTileX, grassTileY);
        gl.glEnd();
        grassTexture.disable(gl);
    }

    /**
     * Zeichnet den Strassenabschnitt eines beginnenden Tunnels
     *
     * @param gl
     * @param i
     */
    private void drawTunnelBeginTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        this.drawTunnelWalls(gl, i);

        brickTexture.enable(gl);
        brickTexture.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Wand links
        this.drawWall(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i) * roadTileHeight, waterHeight,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth + tunnelBeginWallWidth, (mainHeight + i) * roadTileHeight, tunnelWallHeight, tunnelWallBeginTileX, tunnelWallTileY);
        //Wand rechts
        this.drawWall(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth - tunnelBeginWallWidth, (mainHeight + i) * roadTileHeight, waterHeight,
                midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i) * roadTileHeight, tunnelWallHeight, tunnelWallBeginTileX, tunnelWallTileY);
        gl.glEnd();
        brickTexture.disable(gl);
    }

    /**
     * Zeichnet den Strassenabschnitt eines Tunnels
     *
     * @param gl
     * @param i
     */
    private void drawTunnelTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        this.drawTunnelWalls(gl, i);
    }

    /**
     * Zeichnet den Strassenabschnitt im Wasser
     *
     * @param gl
     * @param i
     */
    private void drawWaterTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        //Verschiebung der Wasser Textur wird erhoeht
        this.waterOffset += .0006f;

        waterTexture.enable(gl);
        waterTexture.bind(gl);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_CONSTANT_COLOR);
        gl.glBlendEquation(GL2.GL_FUNC_ADD);
        //Ueberblend Funktion fuer einen Spiegel-Effekt
        gl.glBlendColor(1.000f, 0.812f, 0.812f, 0.620f);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichnen der eigentlichen Wasser Flaeche
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - waterWidth / 2, (mainHeight + i) * roadTileHeight, waterHeight + 1,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + waterWidth / 2, (mainHeight + i + 1) * roadTileHeight, waterHeight + 1, waterTileX, waterTileY, -waterOffset, -waterOffset / 5 - (float) Math.cos(waterOffset));
        gl.glEnd();
        waterTexture.disable(gl);
        gl.glDisable(GL2.GL_BLEND);
    }

    /**
     * Zeichnet einen Strassenabschnitt im Wasser vor einem Gras-Abschnitt
     *
     * @param gl
     * @param i
     */
    private void drawWaterEndTile(GL2 gl, int i) {
        this.drawWaterTile(gl, i);

        grassTexture.bind(gl);
        grassTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichne grasskante
        this.drawWall(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth - grassTileWidth, (mainHeight + i + 1) * roadTileHeight, waterHeight, grassTileX, waterEndGrassTileY);
        this.drawWall(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth + grassTileWidth, (mainHeight + i + 1) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, waterHeight, grassTileX, waterEndGrassTileY);
        gl.glEnd();
        grassTexture.disable(gl);
    }

    /**
     * Hilfsfunktion zum Zeichnen von Bodenflaechen
     *
     * @param gl
     * @param x0 Erster X-Wert der Flaeche
     * @param y0 Erster Y-Wert der Flaeche
     * @param z0 Erster Z-Wert der Flaeche
     * @param x1 Zweiter X-Wert der Flaeche
     * @param y1 Zweiter Y-Wert der Flaeche
     * @param z1 Zweiter Z-Wert der Flaeche
     * @param tileX Horizonale Wiederholung der Textur
     * @param tileY Vertikale Wiederholung der Textur
     */
    private void drawFloor(GL2 gl, float x0, float y0, float z0, float x1, float y1, float z1, float tileX, float tileY) {
        this.drawFloor(gl, x0, y0, z0, x1, y1, z1, tileX, tileY, 0, 0);
    }

    /**
     * Hilfsfunktion zum Zeichnen von Bodenflaechen mit Offset
     *
     * @param gl
     * @param x0 Erster X-Wert der Flaeche
     * @param y0 Erster Y-Wert der Flaeche
     * @param z0 Erster Z-Wert der Flaeche
     * @param x1 Erster X-Wert der Flaeche
     * @param y1 Erster Y-Wert der Flaeche
     * @param z1 Erster Z-Wert der Flaeche
     * @param tileX Horizonale Wiederholung der Textur
     * @param tileY Vertikale Wiederholung der Textur
     * @param xoffset Horizontale Verschiebung der Textur
     * @param yoffset Vertikale Verschiebung der Textur
     */
    private void drawFloor(GL2 gl, float x0, float y0, float z0, float x1, float y1, float z1, float tileX, float tileY, float xoffset, float yoffset) {
        gl.glTexCoord2f(xoffset, yoffset);
        gl.glVertex3f(x0, z0, y0);
        gl.glTexCoord2f(xoffset, tileY + yoffset);
        gl.glVertex3f(x0, z0, y1);
        gl.glTexCoord2f(tileX + xoffset, tileY + yoffset);
        gl.glVertex3f(x1, z1, y1);
        gl.glTexCoord2f(tileX + xoffset, yoffset);
        gl.glVertex3f(x1, z1, y0);
    }

    /**
     * Hilfsfunktion zum Zeichnen von Wandflaechen
     *
     * @param gl
     * @param x0 Erster X-Wert der Flaeche
     * @param y0 Erster Y-Wert der Flaeche
     * @param z0 Erster Z-Wert der Flaeche
     * @param x1 Zweiter X-Wert der Flaeche
     * @param y1 Zweiter Y-Wert der Flaeche
     * @param z1 Zweiter Z-Wert der Flaeche
     * @param tileX Horizonale Wiederholung der Textur
     * @param tileY Vertikale Wiederholung der Textur
     */
    private void drawWall(GL2 gl, float x0, float y0, float z0, float x1, float y1, float z1, float tileX, float tileY) {
        if (y0 == y1) {
            gl.glTexCoord2f(0, tileY);
            gl.glVertex3f(x0, z1, y0);
            gl.glTexCoord2f(tileX, tileY);
            gl.glVertex3f(x1, z1, y0);
            gl.glTexCoord2f(tileX, 0);
            gl.glVertex3f(x1, z0, y0);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(x0, z0, y0);
        } else {
            gl.glTexCoord2f(0, tileY);
            gl.glVertex3f(x0, z1, y0);
            gl.glTexCoord2f(tileX, tileY);
            gl.glVertex3f(x0, z1, y1);
            gl.glTexCoord2f(tileX, 0);
            gl.glVertex3f(x0, z0, y1);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(x0, z0, y0);
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }
}
