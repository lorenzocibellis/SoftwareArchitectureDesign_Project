module org.unisa.musicplaylistmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.unisa.musicplaylistmanager to javafx.fxml;
    exports org.unisa.musicplaylistmanager;
}