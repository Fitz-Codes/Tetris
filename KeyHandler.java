package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**when you implement clases that arent 
*made by u u need to create the methods that 
*satisfy what needs to be in the class
**/
public class KeyHandler implements KeyListener
{
    public static boolean upPressed;
    public static boolean downPressed;
    public static boolean leftPressed;
    public static boolean rightPressed;
    public static boolean pausePressed;
    @Override
    public void keyTyped(KeyEvent e){}//unused in this program but still required
    @Override
    public void keyPressed(KeyEvent e){
        
        int selectedKey = e.getKeyCode();//which key is pressed
        
        if(selectedKey == KeyEvent.VK_W){
            upPressed = true;
        }
        if(selectedKey == KeyEvent.VK_A){
            leftPressed = true;
        }
        if(selectedKey == KeyEvent.VK_D){
            rightPressed = true;
        }
        if(selectedKey == KeyEvent.VK_S){
            downPressed = true;
        }
        //Allowing for arrow keys to be used as controls aswell.
        if(selectedKey == KeyEvent.VK_UP){
            upPressed = true;
        }
        if(selectedKey == KeyEvent.VK_LEFT){
            leftPressed = true;
        }
        if(selectedKey == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }
        if(selectedKey == KeyEvent.VK_DOWN){
            downPressed = true;
        }
        if(selectedKey == KeyEvent.VK_P){
            pausePressed = pausePressed != true;
        }
    }
    @Override
    public void keyReleased(KeyEvent e){}//unused in this program but still required
}