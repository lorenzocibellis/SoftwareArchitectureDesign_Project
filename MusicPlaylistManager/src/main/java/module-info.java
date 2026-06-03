module org.unisa.musicplaylistmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;


    opens org.unisa.musicplaylistmanager to javafx.fxml;
    exports org.unisa.musicplaylistmanager.app;
    opens org.unisa.musicplaylistmanager.app to javafx.fxml;
    exports org.unisa.musicplaylistmanager.track;
    opens org.unisa.musicplaylistmanager.track to javafx.fxml;
    exports org.unisa.musicplaylistmanager.playlist;
    opens org.unisa.musicplaylistmanager.playlist to javafx.fxml;
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
    exports org.unisa.musicplaylistmanager.service;
    opens org.unisa.musicplaylistmanager.service to javafx.fxml;
}