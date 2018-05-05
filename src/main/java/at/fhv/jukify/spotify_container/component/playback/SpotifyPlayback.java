package at.fhv.jukify.spotify_container.component.playback;

import at.fhv.jukify.controller.EMessageType;
import at.fhv.jukify.controller.MessageCenter;
import at.fhv.jukify.controller.model.TrackPojo;
import at.fhv.jukify.spotify_container.ApiFactory;
import at.fhv.jukify.spotify_container.component.auth.SpotifyAuth;
import at.fhv.jukify.spotify_container.component.user.SpotifyUser;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;
import at.fhv.jukify.spotify_container.exception.SpotifyConfigurationException;
import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpotifyPlayback {

    private SpotifyPlayback(){}

    private static SpotifyPlayback instance;

    public static SpotifyPlayback getInstance(){
        if(instance == null){
            instance = new SpotifyPlayback();
        }
        return instance;
    }

    public TrackPojo getCurrentTrack() throws SpotifyAuthException {
        if(SpotifyAuth.isAuthenticationProvided()){
            try {
                SpotifyApi spotifyApi = ApiFactory.getInstance();
                GetUsersCurrentlyPlayingTrackRequest currentTrackRequest = spotifyApi.
                        getUsersCurrentlyPlayingTrack().
                        market(CountryCode.DE).build();

                Future<CurrentlyPlaying> currentTrackFuture = currentTrackRequest.executeAsync();
                Track track = currentTrackFuture.get().getItem();
                TrackPojo currentTrack = new TrackPojo();
                currentTrack.setTrackID(track.getId());
                currentTrack.setTrackName(track.getName());
                currentTrack.setAlbum(track.getAlbum().getName());
                ArtistSimplified[] artists = track.getArtists();
                String[] artistsString = new String[artists.length];
                for (int i = 0; i < artists.length; i++) {
                    artistsString[i] = artists[i].getName();
                }
                currentTrack.setArtists(artistsString);
                currentTrack.setCurrentTimestamp(currentTrackFuture.get().getProgress_ms());
                currentTrack.setDuration(track.getDurationMs());
                currentTrack.setPlaying(currentTrackFuture.get().getIs_playing());
                return currentTrack;

            } catch (SpotifyConfigurationException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            MessageCenter.getInstance().setMessageType(EMessageType.ERROR);
            MessageCenter.getInstance().setMessageText("No authentication/access rights provided by user");
            throw new SpotifyAuthException("No authentication provided by user or invalid access token");
        }
    }

    /**
     * Plays a playlist from the very beginning
     * @param playlistID
     */
    public void playPlaylist(String playlistID) {
        String userID = SpotifyUser.getInstance().getUser().getUserID();
        String contextURI = "spotify:user:" + userID + ":playlist:" + playlistID;
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi spotifyApi = ApiFactory.getInstance();
                StartResumeUsersPlaybackRequest playbackContext = spotifyApi.startResumeUsersPlayback().
                        context_uri(contextURI).
                        build();
                playbackContext.executeAsync();
            } catch (SpotifyConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

}
