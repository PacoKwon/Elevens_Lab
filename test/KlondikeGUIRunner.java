/**
 * This is a class that plays the GUI version of the Elevens game.
 * See accompanying documents for a description of how Elevens is played.
 */
public class KlondikeGUIRunner {

    /**
     * Plays the GUI version of Elevens.
     * @param args is not used.
     */
    public static void main(String[] args) {
        Board board = new ElevensBoard();
        KlondikeGUI gui = new KlondikeGUI(board);
        gui.displayGame();
    }
}
