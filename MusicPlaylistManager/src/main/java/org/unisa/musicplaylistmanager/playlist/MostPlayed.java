package org.unisa.musicplaylistmanager.playlist;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface MostPlayed {
    int getNumOfPlay();
    void incrementNumOfPlay();
    ReadOnlyIntegerProperty playCountProperty();
    String getDisplayName();
}

