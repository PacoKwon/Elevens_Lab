import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class KlondikeGUI extends JFrame implements ActionListener {
    
    /** Height of the game frame. */
    private static final int DEFAULT_HEIGHT = 800;
    /** Width of the game frame. */
    private static final int DEFAULT_WIDTH = 1200;
    /** Width of a card. */
    private static final int CARD_WIDTH = 73;
    /** Height of a card. */
    private static final int CARD_HEIGHT = 97;
    /** Row (y coord) of the upper left corner of piles. */
    private static final int PILES_TOP = 200;
    /** Row (y coord) of the upper left corner of stock. */
    private static final int STOCK_TOP = 30;
    /** Column (x coord) of the upper left corner of the first card. */
    private static final int LAYOUT_LEFT = 30;
    /** Column (x coord) of the upper left corner of the first card. */
    private static final int FOUNDATION_LEFT = 330;
    /** Distance between the upper left x coords of
     *  two horizonally adjacent cards. */
    private static final int LAYOUT_WIDTH_INC = 100;
    /** y coord of the "Replace" button. */
    private static final int BUTTON_TOP = 30;
    /** x coord of the "Replace" button. */
    private static final int BUTTON_LEFT = 570;
    /** Distance between the tops of the "Replace" and "Restart" buttons. */
    private static final int BUTTON_HEIGHT_INC = 50;
    /** y coord of the "n undealt cards remain" label. */
    private static final int LABEL_TOP = 160;
    /** x coord of the "n undealt cards remain" label. */
    private static final int LABEL_LEFT = 540;
    /** Distance between the tops of the "n undealt cards" and
     *  the "You lose/win" labels. */
    private static final int LABEL_HEIGHT_INC = 35;
    /** Distance between two cards that are stacked on top of each other */
    private static final int CARD_GAP = 30;
    /** The total number of foundations */
    private static final int FOUNDATION_COUNT = 4;

    /** The main panel containing the game components. */
    private JPanel panel;
    
    /** The board */
    private KlondikeBoard board;

    /** The remaining, undealt stock */
    private JLabel stock;

    /** The top of the dealt part of stock */
    private JLabel topCard;

    /** is true if topCard is selected */
    private boolean topCardSelected;

    /** JLabel to display messages */
    private JLabel msg;

    /** The card displays */
    public JLabel[][] displayCards;

    /** Empty Piles */
    public JLabel[] emptyPiles;

    /** The foundations on the top right side */
    private JLabel[] foundations;

    /** Empty Foundations */
    private JLabel[] emptyFoundations;

    /** is true if foundation top card is selected */
    private boolean[] fSelected;

    /** is true if empty Card is selected */
    private boolean[] emptyPileSelected;

    /** The coordinates of the card displays. */
    private Point[][] cardCoords;

    /** kth element is true if the user has selected card #k. */
    private boolean[][] selections;

    /** ArrayList containing the CardInfo instances of selected cards */
    private ArrayList<CardInfo> selectedCards;

    private JLabel stockCount;

    public KlondikeGUI(KlondikeBoard gameBoard) {
        addMouseListener(new MyMouseListener());
        board = gameBoard;

        // initialize stock
        stock = new JLabel();

        // initialize topCard
        topCard = new JLabel();

        // initialize foundations
        foundations = new JLabel[FOUNDATION_COUNT];
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            foundations[i] = new JLabel();
        }

        // initialize empty foundations
        emptyFoundations = new JLabel[FOUNDATION_COUNT];
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            emptyFoundations[i] = new JLabel();
        }

        // initialize empty piles
        emptyPiles = new JLabel[board.size()];
        for (int i = 0; i < emptyPiles.length; i++) {
            emptyPiles[i] = new JLabel();
        }

        // initialize emptyPileSelected
        emptyPileSelected = new boolean[board.size()];
        for (int i = 0; i < board.size(); i++) {
            emptyPileSelected[i] = false;
        }

        // initialize message JLabel
        msg = new JLabel();

        stockCount = new JLabel();

        /** 
         * Initialize selections variable 
         * --> two-dimensional boolean array containing
         * whether a card is selected or not
         */
        selections = new boolean[board.size()][20];
        /** Initialize selectedCards variable --> arraylist containing information of cards selected */
        selectedCards = new ArrayList<CardInfo>();
        fSelected = new boolean[FOUNDATION_COUNT];
        cardCoords = new Point[board.size()][20];
        int x = LAYOUT_LEFT;
        int y = PILES_TOP;
        for (int i = 0; i < cardCoords.length; i++) {
            x = LAYOUT_LEFT + i * LAYOUT_WIDTH_INC;
            y = PILES_TOP;
            for (int j = 0; j < cardCoords[i].length; j++) {
                cardCoords[i][j] = new Point(x, y);
                y += CARD_GAP;
            }
        }
    
        initDisplay();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        repaint();
    }

    public void displayGame() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    public void showCoords() {
        for (int i = 0; i < cardCoords.length; i++) {
            for (int j = 0; j < cardCoords[i].length; j++) {
                System.out.printf("[%d %d] ", (int)cardCoords[i][j].getX(), (int)cardCoords[i][j].getY());
            }
            System.out.println();
        }
    }
    
    public void repaint() {
        System.out.printf("Current Selected Cards: %d %s\n", selectedCards.size(), selectedCards);
        /** Board Paint */
        for (int i = 0; i < board.size(); i++) {
            if (board.pileSize(i) == 0) {
                String path = "cards/none";
                if (emptyPileSelected[i]) {
                    path += "S";
                }
                path += ".GIF";

                emptyPiles[i].setIcon(new ImageIcon(getClass().getResource(path)));
                emptyPiles[i].setVisible(true);
            }
            else {
                emptyPiles[i].setVisible(false);
                for (int j = 0; j < board.pileSize(i); j++) {
                    String cardImageFileName = imageFileName(board.cardAtPiles(i, j), selections[i][j], board.isVisible(i, j));
                    URL imageURL = getClass().getResource(cardImageFileName);

                    if (imageURL != null) {
                        ImageIcon icon = new ImageIcon(imageURL);
                        displayCards[i][j].setIcon(icon);
                        displayCards[i][j].setVisible(true);
                    } else {
                        throw new RuntimeException("Card image not found: \"" + cardImageFileName + "\"");
                    }
                }
            }
        }

        /** Stock Paint */
        URL stockURL = getClass().getResource("cards/back.GIF");
        if (board.deck.size() == 0) {
            stockURL = getClass().getResource("cards/none.GIF");
        }
        if (stockURL != null) {
            ImageIcon icon = new ImageIcon(stockURL);
            stock.setIcon(icon);
            stock.setVisible(true);
        }

        /** Topcard Paint */
        String topCardName = imageFileName(board.getStockTopCard(), topCardSelected, true);
        URL topCardURL = getClass().getResource(topCardName);
        if (topCardURL != null) {
            ImageIcon icon = new ImageIcon(topCardURL);
            topCard.setIcon(icon);
            topCard.setVisible(true);
        }

        /** Foundation Paint */
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            String fName;
            URL fURL;

            // if foundation is empty
            if (board.getFoundationTop(i) == null) {
                String path = "cards/none";
                if (fSelected[i]) {
                    path += "S";
                }
                path += ".GIF";

                fURL = getClass().getResource(path);

            } else {
                fName = imageFileName(board.getFoundationTop(i), fSelected[i], true);
                fURL = getClass().getResource(fName);
            }

            if (fURL != null) {
                ImageIcon icon = new ImageIcon(fURL);
                foundations[i].setIcon(icon);
                foundations[i].setVisible(true);
            }
        }
        
        stockCount.setText("" + board.undealtSize());

        if (board.evaluateWin()) {
            System.out.println("================================ You Won! ================================");
            msg.setVisible(true);
        }
        pack();
        panel.repaint();
    }

    private String imageFileName(Card c, boolean isSelected, boolean isVisible) {
        if (!isVisible) return "cards/back.GIF";
        else {
            String str = "cards/";
            
            if (c == null) {
                return "cards/none.GIF";
            }

            str += c.rank() + c.suit();
            
            if (isSelected) {
                str += "S";
            }

            str += ".GIF";

            return str;
        }
    }

    private void initDisplay() {
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };

        /** Set background color to green */
        panel.setBackground(new Color(72, 178, 77));

        panel.add(msg);
        msg.setText("You Win!");
        msg.setBounds(800, 20, 300, 100);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("SanSerif", Font.BOLD, 50));
        msg.setVisible(false);
        
        panel.add(stockCount);
        stockCount.setText("" + board.stockSize());
        stockCount.setBounds(30, 97, CARD_WIDTH, 100);
        stockCount.setForeground(Color.WHITE);
        stockCount.setFont(new Font("SanSerif", Font.BOLD, 16));
        stockCount.setVisible(true);

        /** Set Title */
        setTitle("Klondike");

        this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(DEFAULT_WIDTH - 20, DEFAULT_HEIGHT - 20));

        /** Add stock */
        panel.add(stock);
        stock.setBounds(LAYOUT_LEFT, STOCK_TOP, CARD_WIDTH, CARD_HEIGHT);
        stock.addMouseListener(new MyMouseListener());
        
        /** Add topCard */
        panel.add(topCard);
        topCard.setBounds(LAYOUT_LEFT + LAYOUT_WIDTH_INC, STOCK_TOP, CARD_WIDTH, CARD_HEIGHT);
        topCard.addMouseListener(new MyMouseListener());

        /** Add foundations */
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            panel.add(foundations[i]);
            foundations[i].setBounds(FOUNDATION_LEFT + i * LAYOUT_WIDTH_INC, STOCK_TOP, CARD_WIDTH, CARD_HEIGHT);
            foundations[i].addMouseListener(new MyMouseListener());
        }

        /** Add displayCards */
        displayCards = new JLabel[board.size()][20];

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < 20; j++) {
                displayCards[i][j] = new JLabel();
            }
        }
        for (int i = board.size() - 1; i >= 0; i--) {
            // ***************** emptyPiles *****************
            panel.add(emptyPiles[i]);
            emptyPiles[i].setBounds(cardCoords[i][0].x, cardCoords[i][0].y, CARD_WIDTH, CARD_HEIGHT);
            emptyPiles[i].addMouseListener(new MyMouseListener());
            emptyPiles[i].setVisible(false);
            
            // ***************** displayCards *****************
            for (int j = displayCards[i].length - 1; j >= 0; j--) {
                panel.add(displayCards[i][j]);
                if (j >= board.pileSize(i)) displayCards[i][j].setVisible(false);
                displayCards[i][j].setBounds(cardCoords[i][j].x, cardCoords[i][j].y, CARD_WIDTH, CARD_HEIGHT);
                displayCards[i][j].addMouseListener(new MyMouseListener());
                displayCards[i][j].addMouseMotionListener(new MyMouseListener());
            }
        }

        pack();

        /** Finally, add the main panel */
        getContentPane().add(panel);

        panel.setVisible(true);
    }

    public void updateCoord() {
        for (int i = board.size() - 1; i >= 0; i--) {
            for (int j = board.pileSize(i) - 1; j >= 0; j--) {
                displayCards[i][j].setBounds(cardCoords[i][j].x, cardCoords[i][j].y, CARD_WIDTH, CARD_HEIGHT);
            }
        }
    }

    /**
     * This method updates the visibility of cards
     * This method is intended to be called after 2 cards are selected.
     * See mouseClicked
     */
    private void updateCardVisibility() {
        // update piles
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < 20; j++) {
                displayCards[i][j].setVisible(board.cardAtPiles(i, j) != null);
            }
        }
    }

    /** 
     * Clear variables related to selections.
     * selections variable will all be initialized to false
     * selectedCards variable will be cleared
     */
    private void clearSelections() {
        // *********** CLEAR PILES ***********
        for (int i = 0; i < selections.length; i++) {
            for (int j = 0; j < selections[i].length; j++) {
                selections[i][j] = false;
            }
        }

        // *********** CLEAR EMPTY PILE ***********
        for (int i = 0; i < board.size(); i++) {
            emptyPileSelected[i] = false;
        }
        
        // *********** CLEAR FOUNDATIONS ***********
        for (int i = 0; i < fSelected.length; i++) {
            fSelected[i] = false;
        }

        // *********** CLEAR STOCK ***********
        topCardSelected = false;

        selectedCards.clear();
    }

    public void actionPerformed(ActionEvent e) {
        // if actButtion clicked after the cards selected
        /*if(e.getSource().equals(actButton)) {
            if(board.isLegal(selectedCards)) {
                //to be implemented
                return;
            }
            else {
                //deselect the card
                selectedCards = new ArrayList<CardInfo>();
                selections = new boolean[board.size()][20];
                fSelected = new boolean[FOUNDATION_COUNT];
                // signalError();
                return;
            }

        }
        else if(e.getSource().equals(restartButton)) {
            //restart the game
        }
        else {
            // signalError();
            return;
        }*/
    }

    private class MyMouseListener implements MouseListener, MouseMotionListener {
        /**
         * Various kinds of sources are possible:
         * 1. stock (one to the left of topCard)
         * 2. topCard (one to the right of stock)
         * 3. foundations
         * 4. emptyPiles
         * 5. piles
         */
        public void mouseClicked(MouseEvent e) {
            System.out.printf("[%d, %d]\n", e.getX(), e.getY());
            // ***************** STOCK *****************
            // when stock is clicked
            if (e.getSource().equals(stock)) {
                // when stock is clicked and topCard is selected, set it to false. 
                if (topCardSelected) {
                    topCardSelected = !topCardSelected;
                }

                // when clicked and deck is empty, stack stock. repaint, and return without dealing 
                if (board.deck.isEmpty()) {
                    System.out.println("EMPTY! Stacking...");
                    board.deck.stack(board.stockSize());
                    repaint();
                    return;
                }

                // When clicked and deck is not empty, just deal another card from the deck 
                board.dealStock();
                repaint();
                return;
            }

            // ***************** TOPCARD *****************
            // When Topcard is clicked
            // Topcard : the right card of two cards on the upper left side
            if (e.getSource().equals(topCard)) {
                // if card exists ( is not null )
                if (board.getStockTopCard() != null) {
                    CardInfo info = new CardInfo(CardInfo.STOCK);
                    topCardSelected = !topCardSelected;

                    if (topCardSelected){
                        selectedCards.add(info);

                        if (selectedCards.size() == 2) {
                            clearSelections();
                            repaint();
                            return;
                        }
                    } else {
                        selectedCards.remove(info);
                    }

                    repaint();
                    return;
                }
                // if card does not exists ( is null )
                else {
                    System.out.println("Tip: Click the left card to deal!");
                }
            }

            // ***************** FOUNDATIONS *****************
            // Traverse the foundation and find which one is clicked
            for(int i = 0 ; i < FOUNDATION_COUNT ; i++) {
                if (e. getSource().equals(foundations[i]) /*&& board.getFoundationTop(i) != null*/) {
                    CardInfo info = new CardInfo(i, 0, CardInfo.FOUNDATIONS);
                    
                    // revert its selected property
                    fSelected[i] = !fSelected[i];

                    // if it is selected
                    if(fSelected[i]) {
                        // add to selectedCards and decrement selectCount
                        selectedCards.add(info);
                        
                        // selected cards reach 2
                        if (selectedCards.size() == 2) {
                            board.moveCards(selectedCards);

                            updateCardVisibility();
                            clearSelections();
                            repaint();
                            return;
                        }
                    }
                    // if it is unselected
                    else {
                        // remove from selectedCards and decrement selectCount
                        selectedCards.remove(info);
                    }
                    repaint();
                    return;
                }
            }

            

            // ***************** PILES *****************
            // Traverse Every Card on the board (piles) and find which one is clicked
            for (int i = 0; i < board.size(); i++) {
                for (int j = 0; j < board.pileSize(i); j++) {
                    if (e.getSource().equals(displayCards[i][j]) && board.cardAtPiles(i, j) != null && board.isVisible(i, j)) {
                        CardInfo info = new CardInfo(i, j, CardInfo.PILES); // make new CardInfo instance
                        selections[i][j] = !selections[i][j];
                        
                        if (selections[i][j]) {
                            /** Card is selected right now */
                            selectedCards.add(info);
                            if (selectedCards.size() == 2) {
                                board.moveCards(selectedCards);
                                
                                updateCardVisibility();
                                clearSelections();
                                repaint();
                                return;
                            }
                        } else {
                            /** Card is not selected right now */
                            selectedCards.remove(info);
                        }
                        
                        repaint();
                        return;
                    }
                }
            }

            // ***************** EMPTY PILE *****************
            for (int i = 0; i < board.size(); i++) {
                if (e.getSource().equals(emptyPiles[i])) {
                    CardInfo info = new CardInfo(i, 0, CardInfo.EMPTY_PILE);
                    emptyPileSelected[i] = !emptyPileSelected[i];

                    if (emptyPileSelected[i]) {
                        selectedCards.add(info);
                        if (selectedCards.size() == 2) {
                            board.moveCards(selectedCards);

                            updateCardVisibility();
                            clearSelections();
                            repaint();
                            return;
                        }
                    } else {
                        selectedCards.remove(info);
                    }

                    repaint();
                    return;
                }
            }
        }

        /**
         * Ignore a mouse dragged event.
         * @param e the mouse event.
         */
        public void mouseDragged(MouseEvent e) {
        }

        /**
         * Ignore a mouse exited event.
         * @param e the mouse event.
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * Ignore a mouse released event.
         * @param e the mouse event.
         */
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * Ignore a mouse entered event.
         * @param e the mouse event.
         */
        public void mouseEntered(MouseEvent e) {
        }
        /**
         * Ignore a mouse pressed event.
         * @param e the mouse event.
         */
        public void mousePressed(MouseEvent e) {
        }


        public void mouseMoved(MouseEvent e) {
        }
    }
}