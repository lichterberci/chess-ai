module chessai.chessai {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens chessai.chessai to javafx.fxml;
    exports chessai.chessai;
    exports chessai.chessai.lib;
}