package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements Runnable
{
    public static final int WIDTH = 1240;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;
    public static Sound music = new Sound();

    public GamePanel(){
        //Panel Settings
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        this.setMinimumSize(new Dimension(WIDTH/2, HEIGHT/2)); // Minimum size
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
        double drawInterval = 1000000000/FPS;
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
        if(KeyHandler.pausePressed == false && pm.gameOver == false){
            pm.update();
        }
        
    }
    
    @Override
    public void paintComponent (Graphics g){
        super.paintComponent (g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        // Calculate scale factors based on current panel size
        double scaleX = (double)getWidth() / WIDTH;
        double scaleY = (double)getHeight() / HEIGHT;
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        
        // Center the game if aspect ratios don't match
        int offsetX = (int)((getWidth() - (WIDTH * scale)) / 2);
        int offsetY = (int)((getHeight() - (HEIGHT * scale)) / 2);
        
        // Enable smooth scaling
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Apply transformations
        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);
        
        // Draw the game
        pm.draw(g2);
    }
}
