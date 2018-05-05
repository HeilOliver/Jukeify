package at.fhv.jukify.controller.model;

public class TrackPojo {
    private String trackID;
    private String trackName;
    private String album;
    private String[] artists;
    private int currentTimestamp;
    private int duration;
    private int voteCount = 0;
    private boolean isPlaying;

    public int getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(int currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

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

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

}
