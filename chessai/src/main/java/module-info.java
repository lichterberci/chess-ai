module chessai.chessai {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.jetbrains.annotations;

    opens chessai.chessai to javafx.fxml;
    exports chessai.chessai;
    exports chessai.chessai.lib;
    exports chessai.chessai.ui;
    opens chessai.chessai.ui to javafx.fxml;
}