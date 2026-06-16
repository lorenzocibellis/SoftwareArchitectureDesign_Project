package org.unisa.musicplaylistmanager.track;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface MostPlayed {
    int getNumOfPlay();
    void incrementNumOfPlay();
    ReadOnlyIntegerProperty playCountProperty();

}

