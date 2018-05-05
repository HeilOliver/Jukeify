package at.fhv.jukify.controller;

import at.fhv.jukify.spotify_container.component.auth.EScopeType;
import at.fhv.jukify.spotify_container.component.auth.SpotifyAuth;

public class AuthController {

    private static AuthController controller;

    public static AuthController getInstance(){
        if(controller == null){
            controller = new AuthController();
        }
        return controller;
    }

    public void requestUserAuthentification(){
        SpotifyAuth auth = SpotifyAuth.getInstance();
        auth.requestAuth("http://localhost:8080/receiveToken",
                EScopeType.PLAYLIST_MODIFY_PUBLIC,
                EScopeType.USER_READ_CURRENTLY_PLAYING,
                EScopeType.USER_MODIFY_PLAYBACK_STATE,
                EScopeType.PLAYLIST_READ_PRIVATE,
                EScopeType.USER_READ_PRIVATE);
    }

}
