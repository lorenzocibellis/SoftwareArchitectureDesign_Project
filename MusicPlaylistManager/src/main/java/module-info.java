module org.unisa.musicplaylistmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.desktop;


    exports org.unisa.musicplaylistmanager.app;
    opens org.unisa.musicplaylistmanager.app to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track;
    opens org.unisa.musicplaylistmanager.track to javafx.fxml;
    exports org.unisa.musicplaylistmanager.observer;
    opens org.unisa.musicplaylistmanager.observer to javafx.fxml;
    exports org.unisa.musicplaylistmanager.strategy;
    opens org.unisa.musicplaylistmanager.strategy to javafx.fxml;
    exports org.unisa.musicplaylistmanager.state;
    opens org.unisa.musicplaylistmanager.state to javafx.fxml;
    exports org.unisa.musicplaylistmanager.player;
    opens org.unisa.musicplaylistmanager.player to javafx.fxml;
    exports org.unisa.musicplaylistmanager.iterator;
    opens org.unisa.musicplaylistmanager.iterator to javafx.fxml;
    exports org.unisa.musicplaylistmanager.service.player;
    opens org.unisa.musicplaylistmanager.service.player to javafx.fxml;
    exports org.unisa.musicplaylistmanager.service.navigation;
    opens org.unisa.musicplaylistmanager.service.navigation to javafx.fxml;
    exports org.unisa.musicplaylistmanager.tag;
    opens org.unisa.musicplaylistmanager.tag to javafx.fxml;
    exports org.unisa.musicplaylistmanager.service.statistics;
    opens org.unisa.musicplaylistmanager.service.statistics to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track.list;
    opens org.unisa.musicplaylistmanager.track.list to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track.list.tracklist;
    opens org.unisa.musicplaylistmanager.track.list.tracklist to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track.list.playlist;
    opens org.unisa.musicplaylistmanager.track.list.playlist to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track.list.playlistList;
    opens org.unisa.musicplaylistmanager.track.list.playlistList to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track.list.playlistCreation;
    opens org.unisa.musicplaylistmanager.track.list.playlistCreation to javafx.fxml;
}