package at.fhv.jukify.spotify_container.component.user;

import at.fhv.jukify.controller.EMessageType;
import at.fhv.jukify.controller.MessageCenter;
import at.fhv.jukify.controller.model.PlaylistPojo;
import at.fhv.jukify.controller.model.UserPojo;
import at.fhv.jukify.spotify_container.ApiFactory;
import at.fhv.jukify.spotify_container.component.auth.SpotifyAuth;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;
import at.fhv.jukify.spotify_container.exception.SpotifyConfigurationException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpotifyUser {

    private UserPojo user;

    private static SpotifyUser instance;

    public static SpotifyUser getInstance(){
        if(instance == null){
            instance = new SpotifyUser();
        }
        return instance;
    }

    public UserPojo getUser(){
        if(user == null){
            try {
                user = getUserID();
            } catch (SpotifyAuthException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    private UserPojo getUserID() throws SpotifyAuthException {
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi api = ApiFactory.getInstance();
                GetCurrentUsersProfileRequest userProfileRequest = api.getCurrentUsersProfile().build();
                Future<User> userProfileFuture = userProfileRequest.executeAsync();
                UserPojo user = new UserPojo();
                user.setUserID(userProfileFuture.get().getId());
                return user;
            } catch (SpotifyConfigurationException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        } else{
            MessageCenter.getInstance().setMessageType(EMessageType.ERROR);
            MessageCenter.getInstance().setMessageText("No authentication/access rights provided by user");
            throw new SpotifyAuthException("No authentication provided by user or invalid access token");
        }
    }

    public List<PlaylistPojo> getUserPlaylists() throws SpotifyAuthException {
        if (SpotifyAuth.isAuthenticationProvided()) {
            try {
                SpotifyApi api = ApiFactory.getInstance();
                GetListOfUsersPlaylistsRequest listOfPlaylistsRequest = api.
                        getListOfUsersPlaylists(getUser().getUserID()).
                        limit(50).build();
                Future<Paging<PlaylistSimplified>> playlistPagerFuture = listOfPlaylistsRequest.executeAsync();
                Paging<PlaylistSimplified> playlistPager = playlistPagerFuture.get();

                List<PlaylistPojo> playlists = new LinkedList<>();
                for (PlaylistSimplified playlistSimplified : playlistPager.getItems()) {
                    PlaylistPojo tempPlaylistPojo = new PlaylistPojo();
                    tempPlaylistPojo.setName(playlistSimplified.getName());
                    tempPlaylistPojo.setPlaylistID(playlistSimplified.getId());
                    playlists.add(tempPlaylistPojo);
                }
                return playlists;
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
}
