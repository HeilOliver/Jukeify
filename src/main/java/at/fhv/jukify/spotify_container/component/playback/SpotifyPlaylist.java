package at.fhv.jukify.spotify_container.component.playback;

import at.fhv.jukify.controller.EMessageType;
import at.fhv.jukify.controller.MessageCenter;
import at.fhv.jukify.controller.model.PlaylistPojo;
import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.ApiFactory;
import at.fhv.jukify.spotify_container.component.auth.SpotifyAuth;
import at.fhv.jukify.spotify_container.component.user.SpotifyUser;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;
import at.fhv.jukify.spotify_container.exception.SpotifyConfigurationException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.AddTracksToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsTracksRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpotifyPlaylist {

    private String next;
    private Paging.Builder<PlaylistTrack> pageCrawler;
    private static SpotifyPlaylist instance;

    public static SpotifyPlaylist getInstance(){
        if(instance == null){
            instance = new SpotifyPlaylist();
        }
        return instance;
    }


    public PlaylistPojo createPlaylist(String name) throws SpotifyAuthException {
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi api = ApiFactory.getInstance();
                String userID = SpotifyUser.getInstance().getUser().getUserID();
                CreatePlaylistRequest playlistRequest = api.createPlaylist(userID, name).
                        collaborative(false).public_(false).build();
                try {
                    Future<Playlist> playlistFuture = playlistRequest.executeAsync();
                    Playlist playlist = playlistFuture.get();
                    PlaylistPojo playlistPojo = new PlaylistPojo();
                    playlistPojo.setName(playlist.getName());
                    playlistPojo.setPlaylistID(playlist.getId());
                    return playlistPojo;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }

            } catch (SpotifyConfigurationException e) {
                e.printStackTrace();
                return null;
            }
        } else{
            MessageCenter.getInstance().setMessageType(EMessageType.ERROR);
            MessageCenter.getInstance().setMessageText("No authentication/access rights provided by user");
            throw new SpotifyAuthException("No authentication provided by user or invalid access token");
        }
    }

    public void addToPlaylist(String playlistID, String trackID) throws SpotifyAuthException {
        String[] trackURIs = new String[]{trackID};
        String userID = SpotifyUser.getInstance().getUser().getUserID();
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi api = ApiFactory.getInstance();
                AddTracksToPlaylistRequest addTracksToPlaylistRequest =
                        api.addTracksToPlaylist(userID, playlistID, trackURIs).build();
                Future<SnapshotResult> snapshotResultFuture = addTracksToPlaylistRequest.executeAsync();
                snapshotResultFuture.get();
            } catch (SpotifyConfigurationException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else{
            MessageCenter.getInstance().setMessageType(EMessageType.ERROR);
            MessageCenter.getInstance().setMessageText("No authentication/access rights provided by user");
            throw new SpotifyAuthException("No authentication provided by user or invalid access token");
        }
    }

    public List<TrackPojo> getTracksFromPlaylist(String playlistID) throws SpotifyAuthException {
        List<TrackPojo> resultList = new LinkedList<>();
        String userID = SpotifyUser.getInstance().getUser().getUserID();
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi api = ApiFactory.getInstance();
                GetPlaylistsTracksRequest playlistsTracksRequest = api.getPlaylistsTracks(userID, playlistID).build();
                Future<Paging<PlaylistTrack>> pagingFuture = playlistsTracksRequest.executeAsync();
                Paging<PlaylistTrack> playlistTrackPaging = pagingFuture.get();
                next = playlistTrackPaging.getNext();
                pageCrawler = playlistTrackPaging.builder();
                for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                    resultList.add(mapToPojo(playlistTrack));
                }
                return resultList;
            } catch (SpotifyConfigurationException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return resultList;
            }
        } else{
            MessageCenter.getInstance().setMessageType(EMessageType.ERROR);
            MessageCenter.getInstance().setMessageText("No authentication/access rights provided by user");
            throw new SpotifyAuthException("No authentication provided by user or invalid access token");
        }
    }

    private TrackPojo mapToPojo(PlaylistTrack track){
        TrackPojo res = new TrackPojo();
        Track playlistTrackContent = track.getTrack();
        res.setTrackID(playlistTrackContent.getId());
        res.setTrackName(playlistTrackContent.getName());
        res.setDuration(playlistTrackContent.getDurationMs());
        ArtistSimplified[] artists = playlistTrackContent.getArtists();
        String[] artistsString = new String[artists.length];
        for (int i = 0; i < artists.length; i++) {
            artistsString[i] = artists[i].getName();
        }
        res.setArtists(artistsString);
        res.setAlbum(playlistTrackContent.getAlbum().getName());
        res.setTimestamp(-1);
        return res;
    }

    public List<TrackPojo> crawlNextPage(){
        List<TrackPojo> nextPojoPage = new LinkedList<>();
        if(next != null) {
            Paging<PlaylistTrack> playlistTrackPaging = pageCrawler.setNext(next).build();
            next = playlistTrackPaging.getNext();
            pageCrawler = playlistTrackPaging.builder();
            for (PlaylistTrack playlistTrack : playlistTrackPaging.getItems()) {
                nextPojoPage.add(mapToPojo(playlistTrack));
            }
        }
        return nextPojoPage;
    }

}
