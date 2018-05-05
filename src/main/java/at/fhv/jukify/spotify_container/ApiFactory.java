package at.fhv.jukify.spotify_container;

import at.fhv.jukify.spotify_container.exception.SpotifyConfigurationException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;

public class ApiFactory {

    private static SpotifyApi.Builder builder;
    private static Configuration configuration;
    private static SpotifyApi apiInstance;

    private static SpotifyApi.Builder getBuilder(){
        if(builder == null){
            builder = SpotifyApi.builder();
        }
        return builder;
    }

    public static SpotifyApi getInstance() throws SpotifyConfigurationException {
        if(apiInstance != null){
            return apiInstance;
        } else{
            throw new SpotifyConfigurationException("No API was built yet. Set config and call getApiForAuth before");
        }
    }

    /**
     *
     * @param config
     */
    public static void setConfiguration(Configuration config){
        configuration = config;
    }

    /**
     *
     * @return
     */
    public static SpotifyApi getApiForAuth() throws SpotifyConfigurationException {
        if(configuration == null){
            throw new SpotifyConfigurationException("No config provided. Please pass a valid configuration to API Factory.");
        }
        if(!configuration.validate()){
            throw new SpotifyConfigurationException("Invalid config. ");
        }
        if(apiInstance == null) {
            apiInstance = getBuilder().setClientId(Configuration.getClientID()).
                    setClientSecret(Configuration.getClientSecret()).
                    setRedirectUri(SpotifyHttpManager.makeUri(configuration.getRedirectURL())).build();
        }
        return apiInstance;
    }

    public static void injectAccessTokens(String accessToken, String refreshToken){
        if(apiInstance != null && accessToken != null && refreshToken != null){
            apiInstance.setAccessToken(accessToken);
            apiInstance.setRefreshToken(refreshToken);
        }
    }


    /**
     *
     * @return
     */
    public static SpotifyApi getApiForSimpleAuth(){
        // TODO: implement
        return null;
    }


}
