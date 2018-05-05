package at.fhv.jukify.controller;

import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlayback;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;

public class PlaybackController {

    public TrackPojo getCurrentTrack(){
        SpotifyPlayback playback = SpotifyPlayback.getInstance();
        try {
            return playback.getCurrentTrack();
        } catch (SpotifyAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
}
