package org.unisa.musicplaylistmanager.service.statistics;

import javafx.beans.property.ReadOnlyIntegerProperty;

public interface MostPlayed {
    int getNumOfPlay();
    void incrementNumOfPlay();
    ReadOnlyIntegerProperty playCountProperty();

}

