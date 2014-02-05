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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Moolt
 */
public class Road implements GLEventListener {

    private TowTruck towTruck;
    private RoadAutomaton roadAutomaton;
    private Model poempelModel;
    private iModel3DRenderer modelRenderer;
    private List<Map.Entry<Integer, RoadTile>> roadTiles = new ArrayList<>();
    private int mainHeight = 0;
    private float midOfRoad = 1000f;
    private final int numberOfTiles = 12;
    private final float roadTileHeight = 600f;
    private final float roadTileWidth = 220f;
    private final float roadDropWidth = 20f;
    private final float grassTileWidth = 600f;
    private final float sideStripeWidth = 40f;
    private final float tunnelBeginWallWidth = 600f;
    private final float tunnelWallHeight = 450f;
    private final float waterWidth = 1200f;
    private final float waterHeight = -20f;
    private float waterOffset = 0f;
    private Texture roadTexture;
    private Texture grassTexture;
    private Texture concreteTexture;
    private Texture brickTexture;
    private Texture waterTexture;
    private Texture cloudTexture;
    private Texture middleSolidTexture;

    //tiling parameter fuer texturen
    private float tileX;
    private float tileY;
    private float roadDropTile;
    private float grassTileX;
    private float grassTileY;
    private float sideStripeTileX;
    private float sideStripeTileY;
    private float tunnelWallBeginTileX;
    private float tunnelWallTileX;
    private float tunnelWallTileY;
    private float waterTileX;
    private float waterTileY;
    private float waterEndGrassTileY;

    public Road(TowTruck towTruck, float midOfRoad) {
        this.towTruck = towTruck;
        this.midOfRoad = midOfRoad;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        try {
            GL2 gl = glad.getGL().getGL2();
            this.modelRenderer = DisplayListRenderer.getInstance();
            this.roadAutomaton = new RoadAutomaton();
            this.roadTexture = TextureLoader.loadTexture(gl, "asphalt.png");
            this.grassTexture = TextureLoader.loadTexture(gl, "grass.png");
            this.concreteTexture = TextureLoader.loadTexture(gl, "concrete.png");
            this.brickTexture = TextureLoader.loadTexture(gl, "brick.jpg");
            this.waterTexture = TextureLoader.loadTexture(gl, "water.png");
            this.cloudTexture = TextureLoader.loadTexture(gl, "clouds.jpg");
            this.middleSolidTexture = TextureLoader.loadTexture(gl, "middlesolid.png");

            this.poempelModel = ModelFactory.createModel("./models/poempel.obj");
            this.poempelModel.setUseTexture(true);
            this.poempelModel.setUnitizeSize(false);
            poempelModel.centerModelOnPosition(true);
            poempelModel.setRenderModelBounds(false);

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
        if ((int) towTruck.getyPosition() / (int) roadTileHeight < roadTiles.size() / 2) {
            mainHeight = roadTiles.size() / 2;
        } else {
            mainHeight = (int) towTruck.getyPosition() / (int) roadTileHeight;
        }
        this.update();

        GL2 gl = glad.getGL().getGL2();

        for (int i = 0; i < roadTiles.size(); i++) {
            //auto befindet sich immer auf dem mittleren tile
            int index = i - roadTiles.size() / 2;
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
        //berechnet das mittlere element der liste
        int midOfList = (int) Math.ceil(roadTiles.size() / 2);

        //falls das auto sich ueber dem mittleren tile befindet
        //wird das hinterste geloescht und vorne ein neues eingefuegt
        if (roadTiles.get(midOfList).getKey() < mainHeight) {
            roadTiles.remove(0);
            //neuster index + 1
            int newIndex = roadTiles.get(roadTiles.size() - 1).getKey() + 1;
            roadTiles.add(this.createRandomTile(newIndex));
        }
    }

    private Map.Entry<Integer, RoadTile> createRandomTile(int index) {
        return new AbstractMap.SimpleEntry<>(index, roadAutomaton.next());
    }

    private void drawRoad(GL2 gl, int i) {
        roadTexture.bind(gl);
        roadTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeiche Strasse
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2, (mainHeight + i) * roadTileHeight, 0,
                midOfRoad + roadTileWidth / 2, (mainHeight + i + 1) * roadTileHeight, 0, tileX, tileY);
        //Zeichne uebergang von strasse zu grass
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2, (mainHeight + i + 1) * roadTileHeight, 0, roadDropTile, tileY);

        this.drawFloor(gl, midOfRoad + roadTileWidth / 2, (mainHeight + i) * roadTileHeight, 0,
                midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, roadDropTile, tileY);

        gl.glEnd();
        roadTexture.disable(gl);
    }

    private void drawSideStripe(GL2 gl, int i) {
        concreteTexture.bind(gl);
        concreteTexture.enable(gl);
        gl.glBegin(GL2.GL_QUADS);
        //Zeichne seitenstreifen
        this.drawFloor(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i + 1) * roadTileHeight, -10, sideStripeTileX, sideStripeTileY);
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, sideStripeTileX, sideStripeTileY);

        gl.glEnd();
        concreteTexture.disable(gl);
    }

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

    private void drawGrassTile(GL2 gl, int i) {
        this.drawRoad(gl, i);

        for (int j = 0; j < 2; j++) {
            //gl.glLoadIdentity();
            gl.glPushMatrix();
            gl.glTranslatef(midOfRoad + roadTileWidth / 2 + roadDropWidth + 10, 35f, (mainHeight + i) * roadTileHeight + (roadTileHeight / 2) * j + (roadTileHeight / 4));
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
        //Zeichne grassflaechen
        this.drawFloor(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + grassTileWidth, (mainHeight + i + 1) * roadTileHeight, -10, grassTileX, grassTileY);
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - grassTileWidth, (mainHeight + i) * roadTileHeight, -10,
                midOfRoad - roadTileWidth / 2 - roadDropWidth, (mainHeight + i + 1) * roadTileHeight, -10, grassTileX, grassTileY);

        gl.glEnd();
        grassTexture.disable(gl);
    }

    private void drawTunnelBeginTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        this.drawTunnelWalls(gl, i);

        brickTexture.enable(gl);
        brickTexture.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        //wand links
        this.drawWall(gl, midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth, (mainHeight + i) * roadTileHeight, waterHeight,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + sideStripeWidth + tunnelBeginWallWidth, (mainHeight + i) * roadTileHeight, tunnelWallHeight, tunnelWallBeginTileX, tunnelWallTileY);
        //wand rechts
        this.drawWall(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth - tunnelBeginWallWidth, (mainHeight + i) * roadTileHeight, waterHeight,
                midOfRoad - roadTileWidth / 2 - roadDropWidth - sideStripeWidth, (mainHeight + i) * roadTileHeight, tunnelWallHeight, tunnelWallBeginTileX, tunnelWallTileY);
        gl.glEnd();
        brickTexture.disable(gl);
    }

    private void drawTunnelTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        this.drawTunnelWalls(gl, i);
    }

    private void drawWaterTile(GL2 gl, int i) {
        this.drawRoad(gl, i);
        this.drawSideStripe(gl, i);
        this.waterOffset += .0006f;

        waterTexture.enable(gl);
        waterTexture.bind(gl);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_CONSTANT_COLOR);
        gl.glBlendEquation(GL2.GL_FUNC_ADD);
        gl.glBlendColor(1.000f, 0.812f, 0.812f, 0.620f);

        gl.glBegin(GL2.GL_QUADS);
        this.drawFloor(gl, midOfRoad - roadTileWidth / 2 - roadDropWidth - waterWidth / 2, (mainHeight + i) * roadTileHeight, waterHeight + 1,
                midOfRoad + roadTileWidth / 2 + roadDropWidth + waterWidth / 2, (mainHeight + i + 1) * roadTileHeight, waterHeight + 1, waterTileX, waterTileY, -waterOffset, -waterOffset / 5 - (float) Math.cos(waterOffset));
        gl.glEnd();
        waterTexture.disable(gl);
        gl.glDisable(GL2.GL_BLEND);
    }

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

    private void drawFloor(GL2 gl, float x0, float y0, float z0, float x1, float y1, float z1, float tileX, float tileY) {
        this.drawFloor(gl, x0, y0, z0, x1, y1, z1, tileX, tileY, 0, 0);
    }

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
