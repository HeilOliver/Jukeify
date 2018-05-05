package at.fhv.jukify.spotify_container.component.auth_callback;

import at.fhv.jukify.controller.AuthController;
import at.fhv.jukify.controller.PlaybackController;
import at.fhv.jukify.spotify_container.component.auth.SpotifyAuth;
import at.fhv.jukify.spotify_container.component.playback.SpotifyPlayback;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthCallbackException;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTokenReceiverController {

    @RequestMapping(value = "/receiveToken")
    public void getToken(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error
    ){
        if(code != null) {
            try {
                SpotifyAuth.getInstance().requestAccessToken(code, state);
                SpotifyPlayback.getInstance().playPlaylist();
            } catch (SpotifyAuthCallbackException | SpotifyAuthException e) {
                e.printStackTrace();
            }
        }

    }
}
