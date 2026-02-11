package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sound {

    Clip musicClip;
    URL url[] = new URL[10];

    public Sound(){

        url[0] = getClass().getResource("/white-labyrinth-active.wav");
        url[1] = getClass().getResource("/delete line.wav");
        url[2] = getClass().getResource("/gameover.wav");
        url[3] = getClass().getResource("/rotation.wav");
        url[4] = getClass().getResource("/touch floor.wav");
    }
    public void play(int i, boolean music){

        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();
                
            if(music){
                musicClip = clip;
            }

            clip.open(ais);
            clip.addLineListener((LineListener) new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                         clip.close();
                    }
                }
            });
            ais.close();
            clip.start();
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void loop(){
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        musicClip.stop();
        musicClip.close();
    }
    
    // Set volume: 0.0 to 1.0 (0 = silent, 1 = max volume)
    public void setVolume(float volume){
        if(musicClip != null && musicClip.isOpen()){
            try{
                FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
