package at.fhv.jukify.controller.model;

public class TrackPojo {
    private String trackID;
    private String trackName;
    private String album;
    private String[] artists;
    private int current_timestamp;
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getArtists() {
        return artists;
    }

    public void setArtists(String[] artists) {
        this.artists = artists;
    }

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getTimestamp() {
        return current_timestamp;
    }

    public void setTimestamp(int current_timestamp) {
        this.current_timestamp = current_timestamp;
    }
}
