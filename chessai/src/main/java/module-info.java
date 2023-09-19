module chess.ai.chessai {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
            requires net.synedra.validatorfx;
                requires org.kordamp.bootstrapfx.core;
            requires eu.hansolo.tilesfx;
        
    opens chess.ai.chessai to javafx.fxml;
    exports chess.ai.chessai;
}