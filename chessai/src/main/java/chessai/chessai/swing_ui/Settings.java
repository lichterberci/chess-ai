package chessai.chessai.swing_ui;

import java.awt.*;
import java.io.*;

public class Settings implements Serializable {

    private static final String SETTINGS_PATH = "./settings.dat";
    private static Settings instance;
    private String pieceTheme = "neo";
    private String soundTheme = "sounds";
    private Color whiteTileColor = new Color(237, 214, 179, 255);
    private Color blackTileColor = new Color(179, 134, 98, 255);
    private Color selectedPieceBackgroundColor = new Color(255, 28, 28, 147);
    private Color moveHighlightColor = new Color(225, 214, 47, 255);

    static {
        loadSavedSettingsOrSetToDefault();
    }

    public static void loadSavedSettingsOrSetToDefault() {
        try (var fileInputStream = new FileInputStream(SETTINGS_PATH)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                instance = (Settings) objectInputStream.readObject();
            }
        } catch (IOException e) {
            System.out.println("Settings data not found! Loading default settings...");
            instance = new Settings();
        } catch (ClassNotFoundException e) {
            System.out.println("Settings found, but not loadable! Loading default settings...");
            instance = new Settings();
        }
    }

    public static void saveSettings() {
        try (var fileOutputStream = new FileOutputStream(SETTINGS_PATH)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(instance);
            }
        } catch (IOException e) {
            System.out.println("Settings could not be saved!");
        }
    }

    public static Settings getInstance() {
        return instance;
    }

    public String getPieceTheme() {
        return pieceTheme;
    }

    public void setPieceTheme(String pieceTheme) {
        this.pieceTheme = pieceTheme;
    }

    public String getSoundTheme() {
        return soundTheme;
    }

    public void setSoundTheme(String soundTheme) {
        this.soundTheme = soundTheme;
    }

    public Color getWhiteTileColor() {
        return whiteTileColor;
    }

    public void setWhiteTileColor(Color whiteTileColor) {
        this.whiteTileColor = whiteTileColor;
    }

    public Color getBlackTileColor() {
        return blackTileColor;
    }

    public void setBlackTileColor(Color blackTileColor) {
        this.blackTileColor = blackTileColor;
    }

    public Color getSelectedPieceBackgroundColor() {
        return selectedPieceBackgroundColor;
    }

    public void setSelectedPieceBackgroundColor(Color selectedPieceBackgroundColor) {
        this.selectedPieceBackgroundColor = selectedPieceBackgroundColor;
    }

    public Color getMoveHighlightColor() {
        return moveHighlightColor;
    }

    public void setMoveHighlightColor(Color moveHighlightColor) {
        this.moveHighlightColor = moveHighlightColor;
    }
}
