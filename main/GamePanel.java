package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    public static final int PANEL_WIDTH = 1240;
    public static final int PANEL_HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;
    public static Sound music = new Sound();

    private static final int STATE_MENU = 0;
    private static final int STATE_PLAY = 1;
    private static final int STATE_OPTIONS = 2;
    private int gameState = STATE_MENU;

    private final String[] menuOptions = {"Start Game", "Options", "Exit"};
    private int selectedMenuIndex = 0;
    private final String[] optionsMenuItems = {"Music", "Sound", "Drop Speed", "Back"};
    private int selectedOptionsIndex = 0;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;
    private int selectedDropSpeedIndex = 1;
    private final int[] dropSpeedIntervals = {50, 40, 30};
    private final String[] dropSpeedLabels = {"1", "2", "3"};

    public GamePanel(){
        //Panel Settings
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setMinimumSize(new Dimension(PANEL_WIDTH / 2, PANEL_HEIGHT / 2)); // Minimum size
        this.setBackground(Color.black);
        this.setLayout(null);
        //Implement KeyListener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);
        pm = new PlayManager();
    }
    public void launch(){
        gameThread = new Thread(this);
        gameThread.start();

        //Start background music
        music.play(0, true);
        music.loop();
        music.setVolume(0.8f); // Set background music volume to 80%
    }
    
    @Override
    public void run () {
        //Game loop (60 fps)
        // 1 second in nanoseconds; this keeps the loop at the intended FPS.
        double drawInterval = 1000000000.0/ FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        
        while(gameThread != null){
            currentTime = System.nanoTime();
            
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            
            if(delta >= 1 ){
                update();
                repaint();
                delta--;
            }
        }
    }
    
    private void update(){
        if(gameState == STATE_MENU){
            updateMenu();
            return;
        }

        if(gameState == STATE_OPTIONS){
            updateOptions();
            return;
        }

        if(KeyHandler.escapePressed){
            pm.update();
            openMainMenu();
            KeyHandler.escapePressed = false;
            return;
        }

        if(pm.gameOver && KeyHandler.enterPressed){
            openMainMenu();
            KeyHandler.enterPressed = false;
            return;
        }

        if(KeyHandler.pausePressed == false && pm.gameOver == false){
            pm.update();
        }
    }

    private void updateMenu(){
        if(KeyHandler.upPressed){
            selectedMenuIndex--;
            if(selectedMenuIndex < 0){
                selectedMenuIndex = menuOptions.length - 1;
            }
            KeyHandler.upPressed = false;
        }

        if(KeyHandler.downPressed){
            selectedMenuIndex++;
            if(selectedMenuIndex >= menuOptions.length){
                selectedMenuIndex = 0;
            }
            KeyHandler.downPressed = false;
        }
        
        //This determines which action will be done based on the position within the list of options
        if(KeyHandler.enterPressed){
            switch(selectedMenuIndex){
                case 0 -> startNewGame();
                case 1 -> openOptionsPage();
                default -> System.exit(0);
            }
            KeyHandler.enterPressed = false;
        }
    }

    private void updateOptions(){
        if(KeyHandler.upPressed){
            selectedOptionsIndex--;
            if(selectedOptionsIndex < 0){
                selectedOptionsIndex = optionsMenuItems.length - 1;
            }
            KeyHandler.upPressed = false;
        }

        if(KeyHandler.downPressed){
            selectedOptionsIndex++;
            if(selectedOptionsIndex >= optionsMenuItems.length){
                selectedOptionsIndex = 0;
            }
            KeyHandler.downPressed = false;
        }

        if(KeyHandler.escapePressed){
            openMainMenu();
            KeyHandler.escapePressed = false;
            return;
        }

        if(KeyHandler.enterPressed){
            switch(selectedOptionsIndex){
                case 0 -> toggleMusic();
                case 1 -> toggleSound();
                case 2 -> cycleDropSpeed();
                default -> openMainMenu();
            }
            KeyHandler.enterPressed = false;
        }
    }

    private void openOptionsPage(){
        gameState = STATE_OPTIONS;
        KeyHandler.upPressed = false;
        KeyHandler.downPressed = false;
        KeyHandler.enterPressed = false;
    }

    private void cycleDropSpeed(){
        selectedDropSpeedIndex++;
        if(selectedDropSpeedIndex >= dropSpeedIntervals.length){
            selectedDropSpeedIndex = 0;
        }
    }

    private void toggleMusic(){
        musicEnabled = !musicEnabled;
        if(musicEnabled){
            music.play(0, true);
            music.loop();
            music.setVolume(0.8f);
        }
        else{
            music.stop();
        }
    }

    private void toggleSound(){
        soundEnabled = !soundEnabled;
        Sound.setSoundEffectsEnabled(soundEnabled);
    }

    private void startNewGame(){
        pm = new PlayManager();
        PlayManager.dropInterval = dropSpeedIntervals[selectedDropSpeedIndex];
        KeyHandler.pausePressed = false;
        KeyHandler.upPressed = false;
        KeyHandler.downPressed = false;
        KeyHandler.leftPressed = false;
        KeyHandler.rightPressed = false;
        gameState = STATE_PLAY;
    }

    private void openMainMenu(){
        gameState = STATE_MENU;
        KeyHandler.pausePressed = false;
        KeyHandler.upPressed = false;
        KeyHandler.downPressed = false;
        KeyHandler.leftPressed = false;
        KeyHandler.rightPressed = false;
    }
    
    @Override
    public void paintComponent (Graphics g){
        super.paintComponent (g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        // Calculate scale factors based on current panel size
        double scaleX = (double)getWidth() / PANEL_WIDTH;
        double scaleY = (double)getHeight() / PANEL_HEIGHT;
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        
        // Center the game if aspect ratios don't match
        int offsetX = (int)((getWidth() - (PANEL_WIDTH * scale)) / 2);
        int offsetY = (int)((getHeight() - (PANEL_HEIGHT * scale)) / 2);
        
        // Enable smooth scaling
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Apply transformations
        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);
        
        switch (gameState) {
            case STATE_MENU -> drawMainMenu(g2);
            case STATE_OPTIONS -> drawOptionsPage(g2);
            default -> // Draw the game
                pm.draw(g2);
        }
    }

    private void drawMainMenu(Graphics2D g2){
        g2.setColor(Color.black);
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int playX = PlayManager.left_x - 4;
        int playY = PlayManager.top_y - 4;
        int playW = 360 + 8;
        int playH = 600 + 8;
        int sideX = PlayManager.right_x + 100;

        // Match the frame treatment used by the game HUD.
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(6f));
        g2.drawRect(playX, playY, playW, playH);
        g2.drawRect(sideX, PlayManager.top_y, 250, 300);
        g2.drawRect(sideX, PlayManager.bottom_y - 200, 200, 200);

        g2.setColor(Color.blue);
        g2.setFont(new Font("Arial", Font.ITALIC, 50));
        g2.drawString("CSA Tetris", 30, PlayManager.top_y + 320);

        
        
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("MENU", sideX + 70, PlayManager.top_y + 60);

        int optionX = PlayManager.left_x + 65;
        int optionStartY = PlayManager.top_y + 250;
        for(int i = 0; i < menuOptions.length; i++){
            if(i == selectedMenuIndex){
                g2.setColor(Color.red);
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                g2.drawString("> " + menuOptions[i], optionX - 35, optionStartY + (i * 110));
            }
            else{
                g2.setColor(Color.white);
                g2.setFont(new Font("Arial", Font.PLAIN, 40));
                g2.drawString(menuOptions[i], optionX, optionStartY + (i * 110));
            }
        }

        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.drawString("W/S or Arrow Keys", sideX + 20, PlayManager.top_y + 130);
        g2.drawString("ENTER: Select", sideX + 40, PlayManager.top_y + 170);
        g2.drawString("ESC: Return to menu", sideX + 10, PlayManager.top_y + 210);
    }

    private void drawOptionsPage(Graphics2D g2){
        g2.setColor(Color.black);
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int playX = PlayManager.left_x - 4;
        int playY = PlayManager.top_y - 4;
        int playW = 360 + 8;
        int playH = 600 + 8;
        int sideX = PlayManager.right_x + 100;

        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(6f));
        g2.drawRect(playX, playY, playW, playH);
        g2.drawRect(sideX, PlayManager.top_y, 250, 300);
        g2.drawRect(sideX, PlayManager.bottom_y - 200, 200, 200);

        g2.setColor(Color.blue);
        g2.setFont(new Font("Arial", Font.ITALIC, 50));
        g2.drawString("CSA Tetris", 30, PlayManager.top_y + 320);

        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 40));
        g2.drawString("OPTIONS", sideX + 45, PlayManager.top_y + 60);

        int textX = PlayManager.left_x + 65;
        int textY = PlayManager.top_y + 200;

        for(int i = 0; i < optionsMenuItems.length; i++){
            String line;
            line = switch (i) {
                case 0 -> "Music: " + (musicEnabled ? "ON" : "OFF");
                case 1 -> "Sound: " + (soundEnabled ? "ON" : "OFF");
                case 2 -> "Drop Speed: " + dropSpeedLabels[selectedDropSpeedIndex];
                default -> "Back";
            };

            if(i == selectedOptionsIndex){
                g2.setColor(Color.red);
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                g2.drawString("> " + line, textX - 35, textY + (i * 110));
            }
            else{
                g2.setColor(Color.white);
                g2.setFont(new Font("Arial", Font.PLAIN, 40));
                g2.drawString(line, textX, textY + (i * 110));
            }
        }

        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("W/S or Arrow Keys", sideX + 20, PlayManager.top_y + 130);
        g2.drawString("ENTER: Change/Select", sideX + 20, PlayManager.top_y + 170);
        g2.drawString("ESC: Back to menu", sideX + 20, PlayManager.top_y + 210);
    }
}
