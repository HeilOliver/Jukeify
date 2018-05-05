package at.fhv.jukify.controller;

import at.fhv.jukify.controller.model.PlaylistPojo;
import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlaylist;
import at.fhv.jukify.spotify_container.component.user.SpotifyUser;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class QueueController {

    private static LinkedList<TrackPojo> queue = new LinkedList<>();
    private static PlaylistPojo playlistQueue;
    private static PlaylistPojo sourcePlaylist;

    public List<PlaylistPojo> getUserPlaylists(){
        try {
            return SpotifyUser.getInstance().getUserPlaylists();
        } catch (SpotifyAuthException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public void initSpotifyQueue(PlaylistPojo srcPlaylist){
        sourcePlaylist = srcPlaylist;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.now();
        String playlistNameString = "Jukify + " + dtf.format(localDate);

        SpotifyPlaylist playlist = SpotifyPlaylist.getInstance();
        try {
            playlistQueue = playlist.createPlaylist(playlistNameString);
        } catch (SpotifyAuthException e) {
            e.printStackTrace();
        }
    }

    public void addOrUpdateVote(String trackID){

    }

    public void addFromSourcePlaylist(){

    }

}
