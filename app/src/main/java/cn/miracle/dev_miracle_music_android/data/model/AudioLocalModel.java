package cn.miracle.dev_miracle_music_android.data.model;

public class AudioLocalModel {

    private String audioId;
    private String audioDisplayName;
    private String audioTitle;
    private String audioAlbum;
    private String audioArtist;
    private String audioData;
    private long audioSize;
    private long audioDuration;

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getAudioDisplayName() {
        return audioDisplayName;
    }

    public void setAudioDisplayName(String audioDisplayName) {
        this.audioDisplayName = audioDisplayName;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public String getAudioAlbum() {
        return audioAlbum;
    }

    public void setAudioAlbum(String audioAlbum) {
        this.audioAlbum = audioAlbum;
    }

    public String getAudioArtist() {
        return audioArtist;
    }

    public void setAudioArtist(String audioArtist) {
        this.audioArtist = audioArtist;
    }

    public String getAudioData() {
        return audioData;
    }

    public void setAudioData(String audioData) {
        this.audioData = audioData;
    }

    public long getAudioSize() {
        return audioSize;
    }

    public void setAudioSize(long audioSize) {
        this.audioSize = audioSize;
    }

    public long getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(long audioDuration) {
        this.audioDuration = audioDuration;
    }

}
