package main;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    private static boolean soundEffectsEnabled = true;
    Clip musicClip;
    URL url[] = new URL[10];

    public Sound(){

        url[0] = getSoundUrl("white-labyrinth-active.wav");
        url[1] = getSoundUrl("delete line.wav");
        url[2] = getSoundUrl("gameover.wav");
        url[3] = getSoundUrl("rotation.wav");
        url[4] = getSoundUrl("touch floor.wav");
    }

    private URL getSoundUrl(String fileName){
        URL soundUrl = getClass().getResource("/" + fileName);
        if(soundUrl == null){
            // Fallback when assets are packaged under a SOURCES folder.
            soundUrl = getClass().getResource("/SOURCES/" + fileName);
        }
        return soundUrl;
    }

    public void play(int i, boolean music){

        if(!music && !soundEffectsEnabled){
            return;
        }

        try{
            if(i < 0 || i >= url.length || url[i] == null){
                System.err.println("Missing audio resource at index: " + i);
                return;
            }

            try(AudioInputStream ais = AudioSystem.getAudioInputStream(url[i])){
                Clip clip = AudioSystem.getClip();
                clip.open(ais);

                if(music){
                    if(musicClip != null && musicClip.isOpen()){
                        musicClip.stop();
                        musicClip.close();
                    }
                    musicClip = clip;
                }
                else{
                    clip.addLineListener((LineListener) new LineListener() {
                        @Override
                        public void update(LineEvent event) {
                            if(event.getType() == LineEvent.Type.STOP){
                                clip.close();
                            }
                        }
                    });
                }

                clip.start();
            }
        }catch(UnsupportedAudioFileException | IOException | LineUnavailableException e){
            System.err.println("Audio playback failed: " + e.getMessage());
        }
    }
    public void loop(){
        if(musicClip != null && musicClip.isOpen()){
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public void stop(){
        if(musicClip != null){
            if(musicClip.isRunning()){
                musicClip.stop();
            }
            if(musicClip.isOpen()){
                musicClip.close();
            }
            musicClip = null;
        }
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
                System.err.println("Could not set music volume: " + e.getMessage());
            }
        }
    }

    public static boolean isSoundEffectsEnabled(){
        return soundEffectsEnabled;
    }

    public static void setSoundEffectsEnabled(boolean enabled){
        soundEffectsEnabled = enabled;
    }
}
