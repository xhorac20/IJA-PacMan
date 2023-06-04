package Model;

// Definition of the GameRestorer interface
public interface GameRestorer {

    // This method sets the value of the inProgress attribute to control the game's playback state.
    // If the value is true, it means that playback is in progress.
    // If false, playback has stopped or ended.
    void setPlaybackInProgress(boolean inProgress);

}
