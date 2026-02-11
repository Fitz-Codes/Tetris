package mino;
import java.awt.Color;
import java.awt.Graphics2D;
import main.KeyHandler;
import main.PlayManager;
import main.Sound;

public class Mino
{
    public Block b[] = new Block[4];
    public Block tempB[] = new Block [4];
    int autoDropCounter = 0;
    public int direction = 1; //4 permutations 1/2/3/4
    public boolean leftCollision, rightCollision, bottomCollision; 
    public boolean active = true;
    public boolean deactivating;
    public int deactivateCounter = 0;
    public static Sound rotationSound = new Sound();

    public void create(Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }
    public void setXY(int x, int y){}
    public void updateXY(int direction){
        
        checkRotationCollision();
        checkStaticBlockCollision();
        if(!leftCollision && !rightCollision && !bottomCollision){
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }
    public void getDirection1(){}
    public void getDirection2(){}
    public void getDirection3(){}
    public void getDirection4(){}
    public void checkMovementCollision(){
        
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //check static block collision
        checkStaticBlockCollision();

        // Check left frame collision
        for (Block b1 : b) {
            if (b1.x - 1 < PlayManager.left_x) {
                leftCollision = true;
            }
        }
        // Check right frame collision
        for (Block b1 : b) {
            if (b1.x + 1 + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
            }
        }
        // Check bottom frame collision
        for (Block b1 : b) {
            if (b1.y + Block.SIZE > PlayManager.bottom_y - 1) {
                bottomCollision = true;
            }
        }
    }
    public void checkRotationCollision(){
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        //check static block collision
        checkStaticBlockCollision();

        // Check left frame collision
        for(int i = 0; i < b.length; i++) {
            if (tempB[i].x == PlayManager.left_x) {
                leftCollision = true;
            }
        }
        // Check right frame collision
        for(int i = 0; i < b.length; i++) {
            if(tempB[i].x + Block.SIZE == PlayManager.right_x){
                rightCollision = true;
            }
        }
        // Check bottom frame collision
        for(int i = 0; i < b.length; i++) {
            if(tempB[i].y + Block.SIZE == PlayManager.bottom_y){
                bottomCollision = true;
            }
        }
    }
    //create static collison method to determine if mino is touching others
    private void checkStaticBlockCollision(){
        for(int i = 0; i < PlayManager.staticBlocks.size(); i ++){

            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            //check bottom collision
            for (Block b1 : b) {
                if (b1.x == targetX && b1.y + Block.SIZE == targetY) {
                    bottomCollision = true;
                }
            }
            //check left collision
            for (Block b1 : b) {
                if (b1.x - Block.SIZE == targetX && b1.y == targetY) {
                    leftCollision = true;
                }
                //check right collision
                if (b1.x + Block.SIZE == targetX && b1.y == targetY) {
                    rightCollision = true;
                }
            }
            
        }
    }
    public void update(){
        
        if(deactivating){
            deactivating();
        }

        //moving the mino

        checkStaticBlockCollision();
        checkMovementCollision();

        if(KeyHandler.downPressed == true){
            if(active == true){
                if(bottomCollision == false){
                    b[0].y += Block.SIZE;
                    b[1].y += Block.SIZE;
                    b[2].y += Block.SIZE;
                    b[3].y += Block.SIZE;
                
                    //When moved down reset autoDropCounter
                    autoDropCounter = 0;
                
                    KeyHandler.downPressed = false;
                }
                else{
                    //if bottom collision is true deactivate the mino
                    active = false;

                    KeyHandler.downPressed = false;
                }
            }
        }
        if(KeyHandler.leftPressed == true){
            if(leftCollision == false){
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            
                KeyHandler.leftPressed = false;
            }
        }
        if(KeyHandler.rightPressed == true){
            if(rightCollision == false){
               b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
            
                KeyHandler.rightPressed = false; 
            }
        }
        //rotating the mino
        if(KeyHandler.upPressed == true){
            rotationSound.play(3, false);
            switch(direction){
            case 1 -> getDirection2();
            case 2 -> getDirection3();
            case 3 -> getDirection4();
            case 4 -> getDirection1();
            }
            KeyHandler.upPressed = false;
        }
        if(bottomCollision == true){
            deactivating = true;
        }
        else{
            autoDropCounter ++;//increases by one every frame
                if (autoDropCounter == PlayManager.dropInterval){
                //blocks y-values are increased by one, lowering them one block length
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                
                autoDropCounter = 0;
            }
        }
    }
    private void deactivating(){

        deactivateCounter ++;

        //wait 45 frames before deactivating the tetromino
        if(deactivateCounter == 25){
            
            deactivateCounter = 0;
            checkMovementCollision();// check if bottom of mino is still hitting

            //if mino is still hitting
            if(bottomCollision == true){
                active = false;
                deactivating = false;
            }
        }
    }
    public void draw(Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE - (margin*2), Block.SIZE - (margin*2));
        g2.fillRect(b[1].x+margin, b[1].y+margin, Block.SIZE - (margin*2), Block.SIZE - (margin*2));
        g2.fillRect(b[2].x+margin, b[2].y+margin, Block.SIZE - (margin*2), Block.SIZE - (margin*2));
        g2.fillRect(b[3].x+margin, b[3].y+margin, Block.SIZE - (margin*2), Block.SIZE - (margin*2));
    }
}