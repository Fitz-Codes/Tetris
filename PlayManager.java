package main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager{
    //Main Play Area
    public final int WIDTH = 360;
    public final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;
    
    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    
    //Others
    public static int dropInterval = 40;//mino drops once per 40 frames, decrease to change speed
    boolean gameOver = false;

    // Adding effects
    boolean effectCounterOn;
    int effectCounter = 0;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Sound Effects
    private final Sound lineClearSound = new Sound();
    private final Sound BlockLandingSound = new Sound();
    private final Sound gameOverSound = new Sound();



    //Score
    int level = 1;
    int lines;
    int score;
    public PlayManager(){
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;
        
        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        
        NEXTMINO_X = right_x + 170;
        NEXTMINO_Y = top_y + 500;

        //Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }
    private Mino pickMino(){

        //Pick a random mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i) {
            case 0 -> mino = new Mino_L1();
            case 1 -> mino = new Mino_L2();
            case 2 -> mino = new Mino_Square();
            case 3 -> mino = new Mino_Bar();
            case 4 -> mino = new Mino_T();
            case 5 -> mino = new Mino_Z1();
            case 6 -> mino = new Mino_Z2();
        }
        return mino;
    }
    public void update(){


        //check if the current mino is active
        if(currentMino.active == false){
            //adds each block of each mino to the list
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            //Play the landing effect
            BlockLandingSound.play(4, false);

            // check if the game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                // means the currentMINO immediately collided a block upon spawning and couldnt move at all
                // so its xy are the same with the nextMino's
                gameOver = true;
                GamePanel.music.stop();
                gameOverSound.play(2, false);
            }
            

            currentMino.deactivating = false;
            currentMino.deactivateCounter = 0;

            //replace current mino with the next one
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            //when a mino becomes inactive, check if line(s) can be deleted
            checkDelete();
        }
        else{
            currentMino.update();
        }
    }
    
    private void checkDelete() {


        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while(x < right_x && y < bottom_y){
            
            for(int i = 0; i < staticBlocks.size(); i++){
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                    //increase the count if there is a static block
                    blockCount ++;
                }
            }
            x += Block.SIZE;

            if(x == right_x){

                if(blockCount == 12){

                    effectCounterOn = true;
                    effectY.add(y);
                    lineClearSound.play(1, false);

                    //delete all blocks in that row
                    for(int i = staticBlocks.size() - 1; i > -1; i --){
                        //remove all blocks on current line
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount ++;
                    lines ++;
                    //Drop speed
                    //if the line score hits a certain number, increase the drop speed
                    //1 = fastest
                    if(lines % 10 == 0 && dropInterval > 1){

                        level ++;
                        if(dropInterval > 10){
                            dropInterval -= 10;
                        }
                        else{
                            dropInterval -= 1;
                        }
                    }

                    //move all blocks above said line down one block
                    for(int i = 0; i < staticBlocks.size(); i++){
                        if(staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // update score
        if(lineCount > 0){
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
        }
    }
    public void draw(Graphics2D g2){
        //Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(6f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);
        //Draw Next Mino Frame
        int x = right_x +100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60); 

        //Draw Scoreboard Frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y += 70;
        g2.drawString("LINES: " + lines, x, y); y += 70;
        g2.drawString("SCORE: " + score, x, y);
        //Draw the currentMino
        if(currentMino != null){
            currentMino.draw(g2);
        }
        
        //Draw the nextMino
        if(nextMino != null){
            nextMino.draw(g2);
        }

        //Draw the non-moving blocks array
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }

        // Draw Line Effects
        if(effectCounterOn){
            effectCounter ++;
            
            g2.setColor(Color.RED);
            for(int i = 0; i < effectY.size(); i++){
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if(effectCounter == 10){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw Pause
        g2.setColor(Color.RED);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40f));
        if(gameOver == true){
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        else if(KeyHandler.pausePressed == true){
            x = left_x + 100;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        //Draw game title
         x = 30;
         y = top_y + 320;
        g2.setColor(Color.blue);
        g2.setFont(new Font("Calibri", Font.ITALIC, 50));
        g2.drawString("Tetris By Michael", x, y);
    }
}