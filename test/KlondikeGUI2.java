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

public class KlondikeGUI2 extends JFrame implements ActionListener {
    
    /** Height of the game frame. */
    private static final int DEFAULT_HEIGHT = 800;
    /** Width of the game frame. */
    private static final int DEFAULT_WIDTH = 800;
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
    /** The card displays */
    public JLabel[][] displayCards;
    /** The foundations on the top right side */
    private JLabel[] foundations;
    /** is true of foundation top card is selected */
    private boolean[] fSelected;
    /** The coordinates of the card displays. */
    private Point[][] cardCoords;
    /** kth element is true iff the user has selected card #k. */
    private boolean[][] selections;
    private ArrayList<CardInfo> selectedCards;

    public KlondikeGUI2(KlondikeBoard gameBoard) {
        board = gameBoard;
        MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);

        stock = new JLabel();
        topCard = new JLabel();

        foundations = new JLabel[FOUNDATION_COUNT];
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            foundations[i] = new JLabel();
        }

        /** 
         * Initialize selections variable 
         * --> two-dimensional boolean array containing
         * whether a card is selected or not
         */
        selections = new boolean[board.size()][20];
        /** Initialize selectedCards variable --> arraylist containing information of cards selected */
        selectedCards = new ArrayList<CardInfo>();

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
        System.out.println(selectedCards);
        /** Board Paint */
        for (int i = 0; i < board.size(); i++) {
            // if (board.pileSize(i) == 0) {
            //     System.out.println(displayCards[i][0]);
            //     displayCards[i][0].setIcon(new ImageIcon(getClass().getResource("cards/none.GIF")));
            //     displayCards[i][0].setVisible(true);
            // }
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
        System.out.println(topCardName);
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
            if (board.getFoundationTop(i) == null) {
                fURL = getClass().getResource("cards/none.GIF");
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
            for (int j = board.pileSize(i) - 1; j >= 0; j--) {
                panel.add(displayCards[i][j]);
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
    public void actionPerformed(ActionEvent e) {

    }

    private class MyMouseListener implements MouseListener, MouseMotionListener {
        public void mouseClicked(MouseEvent e) {
            if (e.getSource().equals(stock)) {
                /** when stock is clicked and topCard is selected, set it to false. */
                if (topCardSelected) {
                    topCardSelected = !topCardSelected;
                }
                /** when clicked and deck is empty, stack stock. repaint, and return without dealing */
                if (board.deck.size() == 0) {
                    board.deck.stack(board.stockSize());
                    repaint();
                    return;
                }

                /** When clicked and deck is not empty, just deal another card from the deck */
                System.out.println(board.dealStock());
                System.out.println(board.stockSize());
                System.out.println(board.deck.size());
                repaint();
                return;
            }
            /** When Topcard is clicked */
            if (e.getSource().equals(topCard)) {
                if (board.getStockTopCard() != null) {
                    CardInfo info = new CardInfo("stock");
                    topCardSelected = !topCardSelected;

                    if (topCardSelected){
                        selectedCards.add(info);
                    } else {
                        selectedCards.remove(info);
                    }

                    repaint();
                    return;
                }
            }
            /** Traverse Every Card on the board and find which one is clicked */
            for (int i = 0; i < board.size(); i++) {
                for (int j = 0; j < board.pileSize(i); j++) {
                    if (e.getSource().equals(displayCards[i][j]) && board.cardAtPiles(i, j) != null) {
                        CardInfo info = new CardInfo(i, j, "piles");
                        selections[i][j] = !selections[i][j];
                        
                        if (selections[i][j]) {
                            /** Card is selected right now */
                            selectedCards.add(info);
                        } else {
                            /** Card is not selected right now */
                            selectedCards.remove(info);
                        }
                        
                        System.out.printf("Card %s Clicked!!\n", board.cardAtPiles(i, j));
                        // System.out.printf("Location: [%d, %d]\n", ((JLabel)e.getSource()).getLocation().x, ((JLabel)e.getSource()).getLocation().y);
                        repaint();
                        return;
                    }
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            for (int i = 0; i < board.size(); i++) {
                for (int j = 0; j < board.pileSize(i); j++) {
                    if (e.getSource().equals(displayCards[i][j]) && board.cardAtPiles(i, j) != null) {
                        Point loc = ((JLabel)e.getSource()).getLocation();
                        System.out.printf("[%d, %d] Dragged\n", e.getX(), e.getY());
                        cardCoords[i][j].move(loc.x + e.getX(), loc.y + e.getY());
                        updateCoord();
                        repaint();
                        return;
                    }
                }
            }
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