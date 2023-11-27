package chessai.chessai.swing_ui;

import java.awt.*;
import java.io.*;

/**
 * Represents the application settings (preferences). This class uses a singleton pattern.
 */
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

    public Settings() {
    }

    public Settings(Settings other) {
        this.blackTileColor = other.blackTileColor;
        this.whiteTileColor = other.whiteTileColor;
        this.moveHighlightColor = other.moveHighlightColor;
        this.selectedPieceBackgroundColor = other.selectedPieceBackgroundColor;
        this.soundTheme = other.soundTheme;
        this.pieceTheme = other.pieceTheme;
    }

    /**
     * Updates the singleton instance from the persisted file
     */
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

    /**
     * Persists the singleton instance
     */
    public static void saveSettings() {
        try (var fileOutputStream = new FileOutputStream(SETTINGS_PATH)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(instance);
            }
        } catch (IOException e) {
            System.out.println("Settings could not be saved!");
        }
    }

    /**
     * Resets the singleton instance (but does not persist it)
     */
    public void reset() {
        Settings defaultValue = new Settings();
        blackTileColor = defaultValue.blackTileColor;
        whiteTileColor = defaultValue.whiteTileColor;
        moveHighlightColor = defaultValue.moveHighlightColor;
        selectedPieceBackgroundColor = defaultValue.selectedPieceBackgroundColor;
        pieceTheme = defaultValue.pieceTheme;
        soundTheme = defaultValue.soundTheme;
    }

    /**
     * Sets the singleton instance
     *
     * @param settings the new state
     */
    public static void updateInstance(Settings settings) {
        instance = new Settings(settings);
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
