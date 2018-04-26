import java.util.ArrayList;
import java.util.List;

public class KlondikeBoard
{
    private static final int BOARD_SIZE = 7;
    /**
     * The ranks of the cards for this game to be sent to the deck.
     */
    private static final String[] RANKS = { "ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king" };

    /**
     * The suits of the cards for this game to be sent to the deck.
     */
    private static final String[] SUITS = { "spades", "hearts", "diamonds", "clubs" };

    /**
     * The values of the cards for this game to be sent to the deck.
     */
    private static final int[] POINT_VALUES = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    
    /**
     * The single deck used on the board
     */
    public Deck deck;

    /**
     * The piles on the board. There are BOARD_SIZE amount of piles on the board.
     */
    public Card[][] piles;

    /**
     * The foundation on the top right part of the board.
     */
    private Card[][] foundations;

    /**
     * Some cards are flipped so that they are not visible.
     * flipStatus manages whether the card is flipped or not.
     * For example, if visible[0][1] is true. The card is facing upwards
     */
    private boolean[][] visible;

    /** the size of remaining stock */
    private int stockSize;

    /** the size of foundations */
    private int[] fSize;
    /**
     * there are BOARD_SIZE(usually 7) piles on the board.
     * pileSize manages the size of each pile.
     * for example, pileSize[0] is the size of the first pile on the board.
     */
    private int[] pileSize;

    /**
     * foundationSize manages the size of each foundation
     */
    private int[] foundationSize;

    public KlondikeBoard() {
        /** Initialize Deck */
        deck = new Deck(RANKS, SUITS, POINT_VALUES);

        /** Initialize piles. The maximum size of a single pile is 13 (Ace to King) */
        piles = new Card[BOARD_SIZE][20];

        /** Initilize foundations. The length of foundation is 4 and the maximum  */
        foundations = new Card[4][13];

        /** Initialize pile size */
        pileSize = new int[BOARD_SIZE];

        /** Size of stock */
        stockSize = 52;

        /** size of foundations */
        fSize = new int[4];

        /** Initialize visible */
        visible = new boolean[BOARD_SIZE][20];

        /** Deal cards at start of game. */
        dealMyCards();
    }

    /** Deal cards at start of game. */
    private void dealMyCards() {
        for (int i = 0; i < piles.length; i++) {
            pileSize[i] = i  + 1;

            for (int j = 0; j <= i; j++) {
                piles[i][j] = deck.deal();
                stockSize--;

                if (j == i) {
                    visible[i][j] = true;
                }
            }
        }
    }

    /**
     * deal a card.
     */
    public Card dealStock() {
        return this.deck.deal();
    }

    /**
     * @return the number of piles on the board.
     */
    public int size() {
        return BOARD_SIZE;
    }

    public Card cardAtPiles(int pile, int pos) {
        /** pos is smaller than the size of pile */
        if (pos < pileSize[pile]) {
            return piles[pile][pos];
        } else {
            return null;
        }
    }

    public Card getFoundationTop(int which) {
        if (fSize[which] > 0) {
            return foundations[which][fSize[which] - 1];
        } else {
            return null;
        }
    }
    /**
     * @return total number of cards on the  (dealt + undealt)
     */
    public int stockSize() {
        return this.stockSize;
    }

    /** 
     * There are BOARD_SIZE piles on the board.
     * This method tells how many cards there are in a designated pile
     * @param rowNum designated pile number
     */
    public int pileSize(int rowNum) {
        return pileSize[rowNum];
    }

    /**
     * @return the top card of dealt cards in the remaining deck
     */
    public Card getStockTopCard() {

        /** All cards are unflipped. Facing downwards */
        if (stockSize() == deck.size()) {
            return null;
        } else {
            return deck.getTop();
        }
    }
    /**
     * Evaluates if Card in piles[pile][pos] is visible or not
     * @param pile the index of pile of card
     * @param pos the position of card in a pile
     * @return whether the card is set visible or not
     */
    public boolean isVisible(int pile, int pos) {
        return this.visible[pile][pos];
    }
    /**
     * Evaluates if a play is legal or not. 플레이가 가능한지 판별
     * In klondike every legal play is a 2-card selection. klondike에서는 플레이가 2장의 카드로 이루어지므로 다른 경우는 일단 고려 x
     * @param selectedCards is a list of cards that determines if the selected card set is legal for playing
     */
    public boolean isLegal(List<CardInfo> selectedCards) {
        if (selectedCards.size() == 2) {
            return isAlternated(selectedCards);
        } else {
            return false;
        }
    }

    /**
     * Evaluates if two selected cards are alternate in suit color and point value
     */
    private boolean isAlternated(List<CardInfo> selectedCards) {
        if (selectedCards.size() == 2) {
            CardInfo p1 = selectedCards.get(0);
            CardInfo p2 = selectedCards.get(1);
            
            Card c1, c2;

            /** Piles to Piles. If both cards are from a pile */
            if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.PILES) {
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = piles[p2.rowNum()][p2.pos()];
                return (isBlack(c1) != isBlack(c2)) && (c2.pointValue() + 1 == c1.pointValue())
                                && isOrder(p2) && (p1.pos() == piles[p1.rowNum()].length - 1) ;
            }
            /** If one is from a pile and another is from a foundation */
            else if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.FOUNDATIONS) {
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = foundations[p2.rowNum()][p2.pos()];
                
                return (isBlack(c1) == !isBlack(c2) && c1.pointValue() == c2.pointValue() + 1);
            }
            else if (p1.from() == CardInfo.FOUNDATIONS && p2.from() == CardInfo.PILES)
            {
                c1 = foundations[p1.rowNum()][p1.pos()];
                c2 = piles[p2.rowNum()][p2.pos()];

                /** 
                 * suits are equal and pile card's value is bigger than foundation card's value by 1
                 */
                return (c1.suit().equals(c2.suit()) && c2.pointValue() == c1.pointValue() + 1);
            }
            /**  to pile */
            else if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.STOCK) {
                /** card from stock has to be lesser than that from piles */
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = deck.getTop();

                /** suits are different in color and stock card's value is smaller than pile card's value by 1 */
                return (isBlack(c1) == !isBlack(c2) && (c1.pointValue() == c2.pointValue() + 1));
            }
        }
        return false;
    }
    
    /**
     * It determines wheter a series of cards from a selected one to the bottom is in correct order or not
     *@param c is the card which the series starts from
     */
    private boolean isOrder(CardInfo c){
        int cRow = c.rowNum();
        if(c.from() == CardInfo.PILES){
            for(int seq = c.pos() + 1 ; seq < piles[cRow].length ; seq++){
                if(piles[cRow][seq].pointValue() != piles[cRow][seq-1].pointValue() - 1)
                    return false;
                if(isBlack(piles[cRow][seq]) == isBlack(piles[cRow][seq-1]))
                    return false;
            }
        }
        return true;
    }
    
    /** 
     * It determines whether a suit is black or not
     * @param suit is the input Card object to be determined black or not
     */
    private boolean isBlack(Card c) {
        String suit = c.suit();
        if (suit.equals("spades") || suit.equals("clubs")) {
            return true;
        } else {
            return false;
        }
    }

    /** 
     * It determines whether a suit is red or not
     * @param suit is the input Card object to be determined red or not
     */
    private boolean isRed(Card c) {
        String suit = c.suit();
        if (suit.equals("hearts") || suit.equals("diamonds")) {
            return true;
        } else {
            return false;
        }
    }

    public void printStatus() {
        for (int i = 0; i < piles.length; i++) {
            System.out.printf("Row %d: size: %d\n", i, pileSize[i]);
            for (int j = 0; j < pileSize[i]; j++) {
                System.out.printf("[card: %s, visible: %b] ", piles[i][j], visible[i][j]);
            }
            System.out.println();
        }
    }

}
