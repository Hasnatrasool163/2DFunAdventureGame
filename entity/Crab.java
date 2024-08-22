package entity;

import main.GamePanel;
import object.SuperObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

import static main.LevelSelection.lvl;

public class Crab extends Entity{
    String name;
    GamePanel gp;
    BufferedImage crabLeft, crabRight, crabUp, crabDown;
    int moveCounter = 0;
    Random random = new Random();

    boolean isHit = false;
    int hitTimer = 0;
    final int hitDuration = 20;

    int actionLockCounter = 0;
    int actionDuration = 0;


    public Crab(GamePanel gp) {
        this.name = "Crab";
        this.gp = gp;
        this.solidArea = new Rectangle(8, 16, 32, 32);
        this.solidAreaDefaultX = this.solidArea.x;
        this.solidAreaDefaultY = this.solidArea.y;
        this.speed = 2;
        this.direction = "left";
        setDefaultValues();
        loadCrabImages();
    }

    public Crab(GamePanel gp, int worldX , int worldY) {
        this.name = "Crab";
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.solidArea = new Rectangle(8, 16, 32, 32);
        this.solidAreaDefaultX = this.solidArea.x;
        this.solidAreaDefaultY = this.solidArea.y;
        this.speed = 2;
        this.direction = "left";
        setDefaultValues();
        loadCrabImages();
    }

    public void setDefaultValues() {
//        this.gp.getClass();
        if(lvl==1){
            addEntity(this, 1,2);}
        else if(lvl==2){
            addEntity(this,5,3);
        }
        else if (lvl==3) {
            addEntity(this,23,22);
        }
        else if(lvl==4){
            addEntity(this,4,2);
        } else if (lvl==5) {
            addEntity(this,1,2);
        }
//        this.gp.getClass();
        this.speed = 4;
        this.direction = "down";
    }

    public void loadCrabImages() {
        try {
            crabLeft = ImageIO.read(this.getClass().getResourceAsStream("/crab/crab-left.png"));
            crabRight = ImageIO.read(this.getClass().getResourceAsStream("/crab/crab-right.png"));
            crabUp = ImageIO.read(this.getClass().getResourceAsStream("/crab/crab-up.png"));
            crabDown = ImageIO.read(this.getClass().getResourceAsStream("/crab/crab-down.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void update() {
//        actionLockCounter++;
//        if (actionLockCounter > actionDuration) {
//            actionLockCounter = 0;
//            actionDuration = random.nextInt(120) + 60;
//
//            int dir = random.nextInt(5);
//            switch (dir) {
//                case 0: this.direction = "up"; break;
//                case 1: this.direction = "down"; break;
//                case 2: this.direction = "left"; break;
//                case 3: this.direction = "right"; break;
//                case 4: this.direction = "idle"; break;
//            }
//        }
//
//        if (!this.direction.equals("idle")) {
//            int prevX = this.worldX;
//            int prevY = this.worldY;
//
//            switch (this.direction) {
//                case "up": this.worldY -= this.speed; break;
//                case "down": this.worldY += this.speed; break;
//                case "left": this.worldX -= this.speed; break;
//                case "right": this.worldX += this.speed; break;
//            }
//
//            collisionOn =false;
//            gp.cChecker.checkTile(this);
//            if (gp.cChecker.checkObject(this, false) != 999 && !collisionOn) {
//                this.worldX = prevX;
//                this.worldY = prevY;
//                this.direction = "idle";
//            }
//        }
//
//        spriteCounter++;
//        if (spriteCounter > 12) {
//            if (spriteNum == 1) {
//                spriteNum = 2;
//            } else if (spriteNum == 2) {
//                spriteNum = 1;
//            }
//            spriteCounter = 0;
//        }
//
//        if (isHit) {
//            hitTimer++;
//            if (hitTimer >= hitDuration) {
//                isHit = false;
//                hitTimer = 0;
//            }
//        }
//    }
public void update() {
    moveCounter++;
    if (moveCounter > 60) {
        moveCounter = 0;
        if (this.direction.equals("left")) {
            this.direction = "right";
        } else {
            this.direction = "left";
        }
    }

    if (this.direction.equals("left")) {
        this.worldX -= this.speed;
    } else {
        this.worldX += this.speed;
    }
}
    public void draw(Graphics2D g2) {
        BufferedImage image = switch (this.direction) {
            case "left" -> crabLeft;
            case "right" -> crabRight;
            case "up" -> crabUp;
            case "down" -> crabDown;
            default -> null;
        };

        int screenX = this.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = this.worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        if (isHit) {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    public void onCollision(Player player) {
        if (!isHit) {
            player.health -= 20;
            player.speed = Math.max(1, player.speed - 1);
            isHit = true;
            gp.playSE(5);
            gp.ui.showMessage("You've been hit by a crab!");
        }
    }
}
