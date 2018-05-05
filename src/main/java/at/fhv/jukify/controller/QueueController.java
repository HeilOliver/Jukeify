package at.fhv.jukify.controller;

import at.fhv.jukify.controller.model.PlaylistPojo;
import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlayback;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlaylist;
import at.fhv.jukify.spotify_container.component.user.SpotifyUser;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class QueueController {

    private static LinkedList<TrackPojo> queue = new LinkedList<>();
    private static PlaylistPojo playlistQueue;
    private static PlaylistPojo sourcePlaylist;
    private static Thread updaterThread;

    private static Iterator<TrackPojo> sourcePlayListTrackPaging;

    private static QueueController instance;

    public static QueueController getInstance(){
        if(instance == null){
            instance = new QueueController();
        }
        return instance;
    }

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
        SpotifyPlayback playback = SpotifyPlayback.getInstance();
        try {
            playlistQueue = playlist.createPlaylist(playlistNameString);
            sourcePlayListTrackPaging = playlist.getTracksFromPlaylist(sourcePlaylist.getPlaylistID()).iterator();
            addFromSourcePlaylist();
            playback.playPlaylist(playlistQueue.getPlaylistID());
            updaterThread = new Thread(new QueueUpdater());
            updaterThread.start();
        } catch (SpotifyAuthException e) {
            e.printStackTrace();
        }

    }

    public void addOrUpdateVote(String trackID){
        boolean found = false;
        for (TrackPojo trackPojo : queue) {
            if(trackPojo.getTrackID().equals(trackID)){
                found = true;
                trackPojo.setVoteCount(trackPojo.getVoteCount() + 1);
            }
        }
        if(!found){
            TrackPojo tempPojo = new TrackPojo();
            tempPojo.setVoteCount(1);
            tempPojo.setTrackID(trackID);
            queue.add(tempPojo);
        }
    }

    public void updateQueue(){
        String nextTrack = getNextTrackFromQueue();
        if(!nextTrack.isEmpty()) {
            SpotifyPlaylist playlist = SpotifyPlaylist.getInstance();
            try {
                playlist.addToPlaylist(playlistQueue.getPlaylistID(), nextTrack);
            } catch (SpotifyAuthException e) {
                e.printStackTrace();
            }
        } else {
            addFromSourcePlaylist();
        }
    }

    public String getNextTrackFromQueue(){
        if(!queue.isEmpty()) {
            Collections.sort(queue, new Comparator<TrackPojo>() {
                @Override
                public int compare(TrackPojo o1, TrackPojo o2) {
                    return Integer.compare(o1.getVoteCount(), o2.getVoteCount());
                }
            });
            return queue.getFirst().getTrackID();
        }
        return "";
    }

    public void addFromSourcePlaylist(){
        SpotifyPlaylist playlist = SpotifyPlaylist.getInstance();
        if(sourcePlayListTrackPaging.hasNext()){
            try {
                playlist.addToPlaylist(playlistQueue.getPlaylistID(), sourcePlayListTrackPaging.next().getTrackID());
            } catch (SpotifyAuthException e) {
                e.printStackTrace();
            }
        } else {
            List<TrackPojo> trackPojos = playlist.crawlNextPage();
            if(!trackPojos.isEmpty()){
                sourcePlayListTrackPaging = trackPojos.iterator();
            }
        }
    }


    public void dispose(){
        QueueUpdater.setRunning(false);
    }

}
