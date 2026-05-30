module org.unisa.musicplaylistmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;


    opens org.unisa.musicplaylistmanager to javafx.fxml;
    exports org.unisa.musicplaylistmanager;
}