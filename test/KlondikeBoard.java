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
            pileSize[i] = i + 1;

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
            System.out.printf("which: %d\n", fSize[which]);
            return foundations[which][fSize[which] - 1];
        } else {
            return null;
        }
    }
    /**
     * @return total number of cards on the stock (dealt + undealt)
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
        if (stockSize == deck.size()) {
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
     * Sets the visibility of a card in a pile to true
     */
    public void setCardVisible(int pile, int pos) {
        visible[pile][pos] = true;
    }

    public void moveCards(ArrayList<CardInfo> positions) {
        // CardInfo: rowNum, pos, from
        // let's say that element 0 goes on top of element 1. c1 to c2
        CardInfo c1 = positions.get(0);
        CardInfo c2 = positions.get(1);
        System.out.println(c1);
        System.out.println(c2);

        // ***************** PILES TO PILES *****************
        if (c1.from() == CardInfo.PILES && c2.from() == CardInfo.PILES) {
            // if the destination card's position is not last of its pile,
            // return without doing anything
            if (c2.pos() != pileSize(c2.rowNum()) - 1){
                // TODO ERROR
                return;
            } 

            // if the moving card is the last card of its pile
            if (c1.pos() == pileSize(c1.rowNum()) - 1) {
                Card tmp = piles[c1.rowNum()][c1.pos()]; // get the last card of its pile
                piles[c1.rowNum()][c1.pos()] = null; // set the last card to null

                System.out.printf("PileSize c1: %d\n", pileSize[c1.rowNum()]);
                System.out.printf("PileSize c2: %d\n", pileSize[c2.rowNum()]);
                pileSize[c1.rowNum()]--; // decrease the pile's size
                pileSize[c2.rowNum()]++; // increase the destination pile's size

                piles[c2.rowNum()][c2.pos() + 1] = tmp; // set the card to the destination pile

                setCardVisible(c2.rowNum(), c2.pos() + 1); // set the moved card's visibility true
                if (c1.pos() != 0) {
                    setCardVisible(c1.rowNum(), c1.pos() - 1); // visibility of card that was originally beneath the moved card
                } else { // if the original card was the only card of its pile

                }
            }

            // if the moving card is not the last card of its pile
            // in other words, when there are other cards on top of c1
            else {
                // series of cards
                Card[] tmp = new Card[pileSize[c1.rowNum()] - c1.pos()];

                // initialize tmp array, and then set the series of cards to null.
                for (int i = c1.pos(); i < pileSize[c1.rowNum()]; i++) {
                    tmp[i - c1.pos()] = piles[c1.rowNum()][i];
                    piles[c1.rowNum()][i] = null;
                }

                // change pileSize
                pileSize[c1.rowNum()] = c1.pos();

                // set the card beneath c1 to visible
                if (c1.pos() != 0) setCardVisible(c1.rowNum(), c1.pos() - 1);

                // add all the cards in tmp array on top of c2
                for (int i = pileSize[c2.rowNum()]; i < pileSize[c2.rowNum()] + tmp.length; i++) {
                    piles[c2.rowNum()][i] = tmp[i - pileSize[c2.rowNum()]];
                    setCardVisible(c2.rowNum(), i);
                }
                pileSize[c2.rowNum()] += tmp.length;
            }
        }
        
        // ***************** PILES TO FOUNDATIONS *****************
        else if (c1.from() == CardInfo.PILES && c2.from() == CardInfo.FOUNDATIONS) {
            // only the top card of piles should be movable towards foundations
            if (c1.pos() == pileSize[c1.rowNum()] - 1) {
                
                System.out.println("Pile: " + piles[c1.rowNum()][c1.pos()]);
                // set the piles card to the foundations
                foundations[c2.rowNum()][fSize[c2.rowNum()]] = piles[c1.rowNum()][c1.pos()];
                
                // increase respective foundation size
                fSize[c2.rowNum()]++;
                
                // decrease respective piles size
                pileSize[c1.rowNum()]--;
                
                // set the original piles card position to null
                piles[c1.rowNum()][pileSize[c1.rowNum()]] = null;
                // set the card beneath the original piles card to visible
                if (pileSize[c1.rowNum()] != 0) {
                    setCardVisible(c1.rowNum(), pileSize[c1.rowNum()] - 1);
                }
            }
        }

        // ***************** FOUNDATIONS TO PILES *****************
        else if (c1.from() == CardInfo.FOUNDATIONS && c2.from() == CardInfo.PILES) {
            if (c2.pos() == pileSize[c2.rowNum()] - 1) {
                
                System.out.println(foundations[c1.rowNum()][fSize[c1.rowNum()] - 1]);
                piles[c2.rowNum()][pileSize[c2.rowNum()]] = foundations[c1.rowNum()][fSize[c1.rowNum()] - 1];
                
                // decrease foundations size
                fSize[c1.rowNum()]--;
                
                // increase piles size
                pileSize[c2.rowNum()]++;
                
                // set the original foundation card position to null
                foundations[c1.rowNum()][fSize[c1.rowNum()]] = null;
                
                setCardVisible(c2.rowNum(), pileSize[c2.rowNum()] - 1);
                for (int i = 0; i < foundations.length; i++) {
                    System.out.println(fSize[i]);
                }
            }
        }
        
        // ***************** STOCK TO PILES *****************
        // TODO Stock deal --> shift?? Think about it
        else if (c1.from() == CardInfo.STOCK && c2.from() == CardInfo.PILES) {
            /*
                Decrement Stock Size
                Move the selected Card just after the last stock card
            */
            Card tmp = getStockTopCard();
            // index stockSize - 1 is the first element of already-dealt cards
            deck.moveCard(tmp, this.stockSize - 1);
            
            // decrement stock size
            this.stockSize--;
            
            // add the card to the pile (new destination)
            piles[c2.rowNum()][pileSize[c2.rowNum()]] = tmp;
            
            // set moved card visible
            setCardVisible(c2.rowNum(), pileSize[c2.rowNum()]);
            
            // increment pile size
            pileSize[c2.rowNum()]++;
        }
        
        // ***************** STOCK TO EMPTY PILE *****************
        else if (c1.from() == CardInfo.STOCK && c2.from() == CardInfo.EMPTY_PILE) {
            Card tmp = getStockTopCard();
            
            // index stockSize - 1 is the first element of already-dealt cards
            deck.moveCard(tmp, this.stockSize - 1);
            
            // decrement stock size
            this.stockSize--;
            
            // add the card to the pile (new destination)
            piles[c2.rowNum()][pileSize[c2.rowNum()]] = tmp;
            
            // set moved card visible
            setCardVisible(c2.rowNum(), pileSize[c2.rowNum()]);
            
            // increment pile size
            pileSize[c2.rowNum()]++;
        }

        // ***************** PILE TO EMPTY PILE *****************
        else if (c1.from() == CardInfo.PILES && c2.from() == CardInfo.EMPTY_PILE) {
            // System.out.println("PILES ===>> EMPTY_PILE");

            // series of cards
            Card[] tmp = new Card[pileSize[c1.rowNum()] - c1.pos()];

            // initialize tmp array, and then set the series of cards to null.
            for (int i = c1.pos(); i < pileSize[c1.rowNum()]; i++) {
                tmp[i - c1.pos()] = piles[c1.rowNum()][i];
                piles[c1.rowNum()][i] = null;
            }

            // change pileSize
            pileSize[c1.rowNum()] = c1.pos();

            // set the card beneath c1 to visible
            if (c1.pos() != 0) setCardVisible(c1.rowNum(), c1.pos() - 1);

            for (int i = pileSize[c2.rowNum()]; i < pileSize[c2.rowNum()] + tmp.length; i++) {
                piles[c2.rowNum()][i] = tmp[i - pileSize[c2.rowNum()]];
                setCardVisible(c2.rowNum(), i);
            }
            pileSize[c2.rowNum()] += tmp.length;
        }

        // ***************** STOCK TO FOUNDATIONS *****************
        else if (c1.from() == CardInfo.STOCK && c2.from() == CardInfo.FOUNDATIONS) {
            Card tmp = getStockTopCard();

            deck.moveCard(tmp, this.stockSize - 1);

            this.stockSize--;

            foundations[c2.rowNum()][fSize[c2.rowNum()]] = tmp;

            fSize[c2.rowNum()]++;
        }
    }

    private void printFoundations() {
        for (int i = 0; i < foundations.length; i++) {
            System.out.println("Foundation #" + i);
            for (int j = 0; j < fSize[i]; j++) {
                System.out.printf("%s\t", foundations[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Evaluates if a play is legal or not. 
     * In klondike every legal play is a 2-card selection. 
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
            CardInfo p1 = selectedCards.get(1);
            CardInfo p2 = selectedCards.get(0);
            //move from c2 to c1
            Card c1, c2;

            // Piles to Piles. If both cards are from a pile 
            if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.PILES) {
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = piles[p2.rowNum()][p2.pos()];

                // two cards':
                // colors are different,
                // c1's point value is one larger than that of c2
                return (isBlack(c1) != isBlack(c2)) && (c2.pointValue() + 1 == c1.pointValue())
                                && isOrder(p2) && isLast(p1);
            }
            // to Empty Piles
            else if(p1.from() == CardInfo.EMPTY_PILE) {
                if(p2.from() == CardInfo.PILES) {
                    return (piles[p2.rowNum()][p2.pos()].pointValue() == 13 && isOrder(p2));
                }
                else if(p2.from() == CardInfo.STOCK) {
                    return deck.getTop().pointValue() == 13;
                }

            }
            // If one is from a pile and another is from a foundation. foundation to pile
            else if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.FOUNDATIONS) {
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = foundations[p2.rowNum()][p2.pos()];
                
                return (isBlack(c1) == !isBlack(c2) && c1.pointValue() == c2.pointValue() + 1);
            }

            // pile to foundation
            else if (p1.from() == CardInfo.FOUNDATIONS && p2.from() == CardInfo.PILES)
            {
                c1 = foundations[p1.rowNum()][p1.pos()];
                c2 = piles[p2.rowNum()][p2.pos()];

                 
                //suits are equal and pile card's value is bigger than foundation card's value by 1
                 
                return (c1.suit().equals(c2.suit()) && c2.pointValue() == c1.pointValue() + 1
                                    && isLast(p1));
            }
            // stock to pile 
            else if (p1.from() == CardInfo.PILES && p2.from() == CardInfo.STOCK) {
                // card from stock has to be lesser than that from piles 
                c1 = piles[p1.rowNum()][p1.pos()];
                c2 = deck.getTop();

                // suits are different in color and stock card's value is smaller than pile card's value by 1
                return (isBlack(c1) == !isBlack(c2) && (c1.pointValue() == c2.pointValue() + 1)
                                    && isLast(p1));
            }
            //stock to foundation
            else if(p1.from() == CardInfo.FOUNDATIONS && p2.from() == CardInfo.STOCK) {
                c1 = foundations[p1.rowNum()][p1.pos()];
                c2 = deck.getTop();

                return (isBlack(c1) != isBlack(c2) && c1.pointValue() == c2.pointValue() + 1);
            }

            //to empty foundation
            else if(p1.from() == CardInfo.EMPTY_FOUNDATION) {
                if(p2.from() == CardInfo.PILES) {
                    return (piles[p2.rowNum()][p1.pos()].pointValue() == 1 && isLast(p2));
                }
                else if(p2.from() == CardInfo.STOCK) {
                    return deck.getTop().pointValue() == 1;
                }
            }
        }
        return false;
    }
    
    /**
     * It determines whether a series of cards from a selected one to the bottom is in correct order or not
     *@param c is the card which the series starts from
     */
    private boolean isOrder(CardInfo c){
        int cRow = c.rowNum();
        if(c.from() == CardInfo.PILES){
            for(int seq = c.pos() + 1 ; seq < pileSize(cRow); seq++){
                if(piles[cRow][seq].pointValue() != piles[cRow][seq - 1].pointValue() - 1)
                    return false;
                if(isBlack(piles[cRow][seq]) == isBlack(piles[cRow][seq-1]))
                    return false;
            }
        }
        return true;
    }
    /**
     * It determines whether a selected card is the last card at the pile or not
     * @param c is the selected card
     */
    private boolean isLast(CardInfo c) {
        return (c.from() == CardInfo.PILES && c.pos() == piles[c.rowNum()].length -1 );
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
