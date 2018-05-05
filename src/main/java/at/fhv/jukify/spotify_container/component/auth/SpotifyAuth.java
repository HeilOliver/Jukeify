package at.fhv.jukify.spotify_container.component.auth;

import at.fhv.jukify.spotify_container.ApiFactory;
import at.fhv.jukify.spotify_container.Configuration;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthCallbackException;
import at.fhv.jukify.spotify_container.exception.SpotifyAuthException;
import at.fhv.jukify.spotify_container.exception.SpotifyConfigurationException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpotifyAuth {

    private List<EScopeType> scopeTypes;
    private Configuration config;

    private static SpotifyAuth instance;
    private static boolean authenticationProvided = false;

    public static boolean isAuthenticationProvided() {
        return authenticationProvided;
    }

    public static SpotifyAuth getInstance(){
        if(instance == null){
            instance = new SpotifyAuth();
        }
        return instance;
    }

    private SpotifyAuth() { }

    public void requestAuth(String callbackURL, EScopeType... types){
        configure(callbackURL, types);
        ApiFactory.setConfiguration(config);
        SpotifyApi authApi = null;
        try {
             authApi = ApiFactory.getApiForAuth();
        } catch (SpotifyConfigurationException e) {
            e.printStackTrace();
        }
        if(authApi != null){
            AuthorizationCodeUriRequest authorizationCodeUriRequest =
                    authApi.authorizationCodeUri().
                            state(config.getState()).
                            scope(config.getScope()).
                            show_dialog(true).build();
            try {
                Future<URI> uri = authorizationCodeUriRequest.executeAsync();
                System.out.println("URI retrieved, now requesting user auth");
                openWebpage(uri.get().toString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestAccessToken(String authCode, String state) throws SpotifyAuthCallbackException, SpotifyAuthException {
        if(!validateState(state)){
            throw new SpotifyAuthCallbackException("Invalid state returned by Spotify token endpoint.");
        }
        SpotifyApi authApi = null;
        config.setAuthCode(authCode);
        try {
            authApi = ApiFactory.getApiForAuth();
        } catch (SpotifyConfigurationException e) {
            e.printStackTrace();
        }
        if(authApi != null) {
            AuthorizationCodeRequest codeRequest = authApi.authorizationCode(config.getAuthCode()).build();
            try {
                Future<AuthorizationCodeCredentials> tokenCredentials = codeRequest.executeAsync();
                AuthorizationCodeCredentials codeCredentials = tokenCredentials.get();
                if(codeCredentials.getAccessToken() != null){
                    authenticationProvided = true;
                    ApiFactory.injectAccessTokens(codeCredentials.getAccessToken(), codeCredentials.getRefreshToken());
                } else {
                    throw new SpotifyAuthException("No access token for the given code could be fetched.");
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private void configure(String callbackURL, EScopeType[] types){
        Configuration cfg = new Configuration();
        cfg.setRedirectURL(callbackURL);
        StringJoiner stringJoiner = new StringJoiner(",");
        for (EScopeType eScopeType : types) {
            stringJoiner.add(eScopeType.get());
        }
        cfg.setScope(stringJoiner.toString());
        cfg.setState(IdGenerator.generateId(10));
        config = cfg;
    }

    private boolean validateState(String state){
        return (state.equals(config.getState()));
    }

    private static void openWebpage(String url) {
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
