package at.fhv.jukify.controller;

import at.fhv.jukify.controller.model.PlaylistPojo;

public class ConfigController {

    private static ConfigController instance;
    private String ip;
    private String port;
    private PlaylistPojo sourcePlaylist;

    public static ConfigController getInstance(){
        if(instance == null){
            instance = new ConfigController();
        }
        return instance;
    }

    private ConfigController(){}

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void set(String port, String ip){
        this.port = port;
        this.ip = ip;
    }


    public PlaylistPojo getSourcePlaylist() {
        return sourcePlaylist;
    }

    public void setSourcePlaylist(PlaylistPojo sourcePlaylist) {
        this.sourcePlaylist = sourcePlaylist;
    }

}
