package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

import static constants.constants.tileSize;

public abstract class Entity  {
    public int worldX;
    public int worldY;
    public int speed;
    public BufferedImage up1;
    public BufferedImage up2;
    public BufferedImage down1;
    public BufferedImage down2;
    public BufferedImage left1;
    public BufferedImage left2;
    public BufferedImage right1;
    public BufferedImage right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Rectangle solidArea;
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;
    public boolean collisionOn = false;

    public Entity() {
    }

    public abstract void update();

    public abstract void draw(Graphics2D g2);

    public void addEntity(Entity entity, int worldX, int worldY){
        entity.worldX = worldX * tileSize;
        entity.worldY = worldY * tileSize;
    }

//    public abstract boolean checkCollision(Rectangle rect);
}
