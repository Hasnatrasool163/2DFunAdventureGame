package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import object.*;

import static constants.constants.tileSize;
import static main.LevelSelection.lvl;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    MouseHandler mouseHandler;
    public final int screenX;
    public final int screenY;
    public int hasKey = 0;
    boolean bootsOn = false;
    int bootsCounter = 0;
    public int health = 100;
    private Point targetPosition = null;
    private final int moveSpeed = 4;
    boolean weaponDrawn = true;
    private BufferedImage sword, sword_down, sword_left;
    private int attackTimer = 0;
    private int attackRange = 25;
    public int ghost;


    public Player(GamePanel gp, KeyHandler keyH, MouseHandler mouseHandler) {
        this.gp = gp;
        this.keyH = keyH;
        this.mouseHandler = mouseHandler;
        int var10001 = 768 / 2;
        this.screenX = var10001 - 48 / 2;
        var10001 = 576 / 2;
        this.screenY = var10001 - 48 / 2;
        this.solidArea = new Rectangle();
        this.solidArea.x = 8;
        this.solidArea.y = 16;
        this.solidAreaDefaultX = this.solidArea.x;
        this.solidAreaDefaultY = this.solidArea.y;
        this.solidArea.width = 32;
        this.solidArea.height = 32;
        this.setDefaultValues();
        this.getPlayerImage();
        this.getWeaponImages();
    }

    public void setDefaultValues() {
        if (lvl == 1) {
            addEntity(this, 1, 1);
        } else if (lvl == 2) {
            addEntity(this, 5, 2);
        } else if (lvl == 3) {
            addEntity(this, 4, 1);
        } else if (lvl == 4) {
            addEntity(this, 1, 1);
        } else if (lvl == 5) {
            addEntity(this, 23, 21);
        } else if (lvl == 6) {
            addEntity(this, 23, 21);
        } else if (lvl == 7) {
            addEntity(this, 50, 50);
        }
        this.speed = 4;
        this.direction = "down";
    }


    public void getPlayerImage() {
        try {
            this.up1 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_up_1.png"));
            this.up2 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_up_2.png"));
            this.down1 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_down_1.png"));
            this.down2 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_down_2.png"));
            this.left1 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_left_1.png"));
            this.left2 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_left_2.png"));
            this.right1 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_right_1.png"));
            this.right2 = ImageIO.read(this.getClass().getResourceAsStream("/player/boy_right_2.png"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }


    public void getWeaponImages() {
        try {
            this.sword = ImageIO.read(this.getClass().getResourceAsStream("/weapons/sword1.png"));
            this.sword_down = ImageIO.read(this.getClass().getResourceAsStream("/weapons/sword1_down.png"));
            this.sword_left = ImageIO.read(this.getClass().getResourceAsStream("/weapons/sword1_left.png"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            direction = keyH.upPressed ? "up" : keyH.downPressed ? "down" : keyH.leftPressed ? "left" : "right";
            collisionOn = false;
            gp.cChecker.checkTile(this);
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            if (!collisionOn) {
                move();
            }

            updateSprite();

            if (mouseHandler.doubleClicked && gp.inventory.getSelectedItem() != null) {
                useItem();
                mouseHandler.doubleClicked = false;
            }

            if (keyH.spacePressed) {
                weaponDrawn = !weaponDrawn;
                keyH.spacePressed = false;
            }

            checkPlayerHealth();
            if (canAttackEnemy(gp.enemies)) {
                checkForAttack();
            }

        }

    }

    private void checkForAttack() {
        if (weaponDrawn) {
            Enemy enemy = checkEnemyCollision();
            attackEnemy(enemy);
        }
    }

    private Enemy checkEnemyCollision() {
        for (Enemy enemy : gp.enemies) {
            if (this.solidArea.intersects(enemy.solidArea)) {
                return enemy;
            }
        }
        return null;
    }


    private void move() {
        switch (direction) {
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
        }
    }

    private void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void pickUpObject(int index) {
        if (index == 999) return;

        switch (gp.objects.get(index).name) {
            case "Key":
                handleKeyPickup(index);
                break;
            case "Door":
                handleDoorInteraction(index);
                break;
            case "Boots":
                handleBootsPickup(index);
                break;
            case "Crab":
                handleCrabEncounter(index);
                break;
            case "Chest":
                handleChestInteraction();
                break;
            case "healthPotion":
                handlePotionPickup(index);
                break;
            case "Ghost":
                handleGhostInteraction(index);
                break;
            case "Blob":
                handleBlobInteraction(index);
                break;
            case "Banana":
                handleFoodInteraction(index);
            case "Chest_Gold":
                handleGoldChestInteraction(index);


        }
    }

    private void handleGoldChestInteraction(int index) {
        gp.objects.set(index, null);
        gp.ui.showMessage("oops it was empty find Another!");
    }

    private void handleFoodInteraction(int index) {
        gp.objects.set(index, null);
        gp.ui.showMessage("You Eat the Banana");
        if (health < 100) {
            health += 2;
        }
    }

    private void handleBlobInteraction(int index) {
        gp.objects.set(index, null);
        gp.ui.showMessage("Blob took your 10% health!");
        this.health -= 10;
    }

    private void handleGhostInteraction(int index) {
        ghost+=1;
        gp.ui.showMessage("You met with a ghost!");
        gp.objects.set(index, null);
        int axis = 50;
        this.worldX = axis * tileSize;
        this.worldY = axis * tileSize;
        Enemy enemy = new Enemy(gp);
        gp.enemies.add(enemy);
//        gp.enemy = new Enemy(gp);
        gp.repaint();
    }

    private void handleKeyPickup(int index) {
        gp.playSE(1);
        hasKey++;
        gp.objects.set(index, null);
        gp.ui.showMessage("You got a key!");
        gp.addItemToInventory(new OBJ_Key());
    }

    private void handleDoorInteraction(int index) {
        if (hasKey > 0 && gp.inventory.getSelectedItem() != null && gp.inventory.getSelectedItem().equals("Key")) {
            gp.playSE(3);
            gp.objects.set(index, null);
            hasKey--;
            gp.removeItemFromInventory("Key", 1);
            gp.ui.showMessage("You opened the door!");
        } else {
            gp.ui.showMessage("You need a key!");
        }
    }

    private void handleBootsPickup(int index) {
        gp.playSE(2);
        gp.ui.showMessage("Open Inventory Double Click to Use");
        gp.objects.set(index, null);
        gp.addItemToInventory(new OBJ_Boots());
    }

    private void handleCrabEncounter(int index) {
        gp.playSE(5);
        speed = Math.max(1, speed - 1);
        health -= 20;
        gp.objects.set(index, null);
        gp.removeItemFromInventory("Boots", 1);
        gp.ui.showMessage("Caught by Crab!");
    }

    private void handleChestInteraction() {
        if(lvl==7){
            if ( this.hasKey > 3 && ghost>1) {
                gp.stopMusic();
                gp.playSE(4);
                gp.ui.gameEnd=true;

            } else {
                gp.ui.showMessage("You need 3 keys first! and met all ghosts");
            }
        }
        else {

            gp.stopMusic();
            gp.playSE(4);
            gp.ui.LvlFinished = true;
        }
    }

    private void handlePotionPickup(int index) {

        this.gp.playSE(6);
        if (this.health == 100) {
            gp.objects.set(index, null);
            gp.addItemToInventory(new OBJ_Health_Portion());
            this.gp.ui.showMessage("Health Potion Added to Inventory");
        } else {
            this.health = Math.max(100, this.health + 20);
            this.gp.ui.showMessage("Health Increased!");
        }
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };

        g2.drawImage(image, screenX, screenY, 48, 48, null);

        BufferedImage weaponImage = null;
        if (weaponDrawn) {
            weaponImage = switch (direction) {
                case "down" -> sword_down;
                case "up", "right" -> sword;
                case "left" -> sword_left;
                default -> null;
            };
        }

        int weaponX = screenX + 5;
        int weaponY = screenY + 5;
        switch (direction) {
            case "up":
                weaponX += 10;
                weaponY -= 10;
                break;
            case "down":
                weaponX += 10;
                weaponY += 10;
                break;
            case "left":
                weaponX -= 25;
                break;
            case "right":
                weaponX += 10;
                break;
        }

        if (weaponImage != null) {
            g2.drawImage(weaponImage, weaponX, weaponY, 48, 48, null);
        }

        if (targetPosition != null) {
            g2.setColor(Color.RED);
            g2.drawOval(targetPosition.x - gp.player.worldX + gp.player.screenX - 10, targetPosition.y - gp.player.worldY + gp.player.screenY - 10, 20, 20);
        }
    }


    public void useItem() {
        mouseHandler.doubleClicked = false;
        String itemName = gp.inventory.getSelectedItem();
        switch (itemName) {
            case "healthPotion":
                if (this.health == 100) {
                    gp.ui.showMessage("Health Already Full");
                    gp.inventory.setSelectedItem(null);
                } else {
                    gp.removeItemFromInventory("healthPotion", 1);
                    this.health = Math.min(100, this.health + 20);
                    gp.ui.showMessage("Health restored!");
                    gp.inventory.setSelectedItem(null);

                }
                break;
            case "Boots":
                gp.removeItemFromInventory("Boots", 1);
                gp.inventory.setSelectedItem(null);
                this.speed = Math.min(6, this.speed + 1);
                gp.ui.showMessage("Speed increased!");
                break;
            case "Key":
                gp.ui.showMessage("Use Key to Open Closed Doors");
                gp.inventory.setSelectedItem(null);
                break;
            default:
                gp.ui.showMessage("Can't use this item.");
                break;
        }

}

    public int getWorldX() {
        return this.worldX;
    }

    public void setWorldX(int x) {
        this.worldX = x;
    }

    public int getWorldY() {
        return this.worldY;
    }

    public void setWorldY(int y) {
        this.worldY = y;
    }


    public void checkPlayerHealth() {
        if (this.health <= 0) {
            System.out.println("Player has died!");
//            gp.ui.showMessage("You died!");
        }
    }

    public void attackEnemy(Enemy enemy) {

        if(enemy!=null) {
            enemy.health -= 5;
            gp.playSE(7);
            if (enemy.health <= 0) {
                enemy.health = 0;
                enemy.isAlive = false;
                gp.enemies.remove(enemy);
                enemy=null;
                gp.inventory.addItem(new OBJ_Health_Portion());
                gp.ui.showMessage("Enemy defeated!");
                this.weaponDrawn=false;

            }
        }

    }




    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Player health: " + health);
        if (health <= 0) {
            die();
        }
    }

    private void die() {
        System.out.println("Player has died!");
        gp.ui.gameOver=true;

    }


    private void moveForward() {
        this.worldX += (direction.equals("left") ? -5 : direction.equals("right") ? 5 : 0);
        this.worldY += (direction.equals("up") ? -5 : direction.equals("down") ? 5 : 0);
    }

    private void moveBackward() {
        this.worldX -= (direction.equals("left") ? -5 : direction.equals("right") ? 5 : 0);
        this.worldY -= (direction.equals("up") ? -5 : direction.equals("down") ? 5 : 0);
    }

    private boolean canAttackEnemy(ArrayList<Enemy> enemy) {
        if(!enemy.isEmpty()){

        int distanceX = Math.abs(enemy.getFirst().worldX - this.worldX);
        int distanceY = Math.abs(enemy.getFirst().worldY - this.worldY);

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
        return false;
    }

}
