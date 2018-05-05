package at.fhv.jukify.spotify_container;

public class Configuration {

    private static final String clientID = "0178b85b213d441699553f4de78c6931";
    private static final String clientSecret = "132343a5a7e84713a262c4f22664f594";

    private String authCode;
    private String scope;
    /* Random String provided by this application to validate callback */
    private String state;
    private String redirectURL;

    public boolean validate(){
        return (state != null && scope != null && redirectURL != null);
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Configuration(){

    }

    public static String getClientID() {
        return clientID;
    }

    public static String getClientSecret() {
        return clientSecret;
    }
}
