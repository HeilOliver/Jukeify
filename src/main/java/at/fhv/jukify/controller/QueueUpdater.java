package at.fhv.jukify.controller;

import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlayback;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;

public class QueueUpdater implements Runnable {

    private static boolean running = true;
    private static int updateInterval = 2000;
    private static int queueUpdateOffset = 15000;

    public static void setRunning(boolean status) {
        running = status;
    }

    @Override
    public void run() {
        while (running){
            try {
                TrackPojo currentTrack = SpotifyPlayback.getInstance().getCurrentTrack();
                if(currentTrack.isPlaying() &&
                        currentTrack.getDuration() - currentTrack.getCurrentTimestamp() <= queueUpdateOffset){
                    QueueController.getInstance().updateQueue();
                    Thread.sleep(queueUpdateOffset);
                }
            } catch (SpotifyAuthException | InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
