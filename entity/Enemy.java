package entity;

import main.GamePanel;
import object.OBJ_Health_Portion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Enemy extends Entity {
    GamePanel gp;
    private BufferedImage enemyImage1, enemyImage2;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    int health = 100;
    boolean isAlive = true;

    private int attackPower = 10;
    private int attackRange = 20;
    private int attackCooldown = 60;
    private int attackTimer = 0;

    public Enemy(GamePanel gp) {
        this.gp = gp;
        this.direction = "down";
        this.speed = 2;
        this.worldX = 400;
        this.worldY = 400;
        this.solidArea = new Rectangle(8, 16, 16, 16);
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;

        getEnemyImage();
    }

    public void getEnemyImage() {
        try {
            enemyImage1 = ImageIO.read(this.getClass().getResourceAsStream("/enemy/enemy_1.png"));
            enemyImage2 = ImageIO.read(this.getClass().getResourceAsStream("/enemy/enemy_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (isAlive) {
            moveTowardPlayer();

            if (canAttackPlayer()) {
                attackPlayer(gp.player);
            }

            updateSprite();
        }
    }


    private void moveTowardPlayer() {
        int dx = gp.player.worldX - this.worldX;
        int dy = gp.player.worldY - this.worldY;

        if (Math.abs(dx) > Math.abs(dy)) {
            this.direction = (dx > 0) ? "right" : "left";
        } else {
            this.direction = (dy > 0) ? "down" : "up";
        }

        if (Math.abs(dx) > speed) {
            worldX += dx > 0 ? speed : -speed;
        }
        if (Math.abs(dy) > speed) {
            worldY += dy > 0 ? speed : -speed;
        }
    }

    private boolean canAttackPlayer() {
        int distanceX = Math.abs(gp.player.worldX - this.worldX);
        int distanceY = Math.abs(gp.player.worldY - this.worldY);

        if (distanceX < attackRange && distanceY < attackRange) {
            if (attackTimer <= 0) {
                return true;
            }
        }

        if (attackTimer > 0) {
            attackTimer--;
        }

        return false;
    }

    public void takeDamageByPlayer(int damage) {
        health -= damage;
        if (health <= 0) {
            isAlive = false;
            gp.enemies.remove(this);
            System.out.println("Enemy defeated!");
        }
    }

    private void die() {

        System.out.println("Enemy defeated!");
        this.gp.enemies.remove(this);
    }

    public void attackPlayer(Player player) {
        if (player != null) {
            player.takeDamage(1);
        }
    }

    private void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {
        if (isAlive) {
            BufferedImage image = (spriteNum == 1) ? enemyImage1 : enemyImage2;
            g2.drawImage(image, worldX - gp.player.worldX + gp.player.screenX, worldY - gp.player.worldY + gp.player.screenY, gp.tileSize, gp.tileSize, null);
        }

        }

}
