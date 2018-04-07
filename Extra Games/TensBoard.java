import java.util.List;
import java.util.ArrayList;

/**
 * The TensBoard class represents the board in a game of Tens.
 */
public class TensBoard extends Board {

	/**
	 * The size (number of cards) on the board.
	 */
	private static final int BOARD_SIZE = 13;

	/**
	 * The ranks of the cards for this game to be sent to the deck.
	 */
	private static final String[] RANKS =
		{"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};

	/**
	 * The suits of the cards for this game to be sent to the deck.
	 */
	private static final String[] SUITS =
		{"spades", "hearts", "diamonds", "clubs"};

	/**
	 * The values of the cards for this game to be sent to the deck.
	 */
	private static final int[] POINT_VALUES =
		{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0};

	/**
	 * Flag used to control debugging print statements.
	 */
	private static final boolean I_AM_DEBUGGING = false;


	/**
	 * Creates a new <code>TensBoard</code> instance.
	 */
	 public TensBoard() {
	 	super(BOARD_SIZE, RANKS, SUITS, POINT_VALUES);
	 }

	/**
	 * Determines if the selected cards form a valid group for removal.
	 * In Tens, the legal groups are (1) a pair of non-face cards
	 * whose values add to 10, and (2) a quartet of ten, jack, queen, king of same rank
	 * @param selectedCards the list of the indices of the selected cards.
	 * @return true if the selected cards form a valid group for removal;
	 *         false otherwise.
	 */
	@Override
	public boolean isLegal(List<Integer> selectedCards) {
		if (selectedCards.size() == 2) {
			return containsPairSum10(selectedCards);
		} else if (selectedCards.size() == 4) {
			return containsQuartet(selectedCards);
		} else {
			return false;
		}
	}

	/**
	 * Determine if there are any legal plays left on the board.
	 * In Tens, there is a legal play if the board contains
	 * (1) a pair of non-face cards whose values add to 10, or (2) a quartet of
	 * ten, jack, queen, king of same rank
	 * @return true if there is a legal play left on the board;
	 *         false otherwise.
	 */
	@Override
	public boolean anotherPlayIsPossible() {
		return containsPairSum10(cardIndexes()) || containsQuartet(cardIndexes());
	}

	/**
	 * Check for an 10-pair in the selected cards.
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find an 10-pair.
	 * @return true if the board entries in selectedCards
	 *              contain an 10-pair; false otherwise.
	 */
	private boolean containsPairSum10(List<Integer> selectedCards) {
		for (int i=0;i<selectedCards.size();i++) {
			int card1 =cardAt(selectedCards.get(i)).pointValue();
			for (int j=i+1;j<selectedCards.size();j++) {
				int card2=cardAt(selectedCards.get(j)).pointValue();
				if (card1+card2==10) return true;
			}
		}
		return false;
	}

	/**
	 * Check for quartet of 10, jack, queen, king of same rank
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find a king card.
	 * @return true if the board entries in selectedCards
	 *              include a quartet
	 */
	private boolean containsQuartet(List<Integer> selectedCards) {
		int t=0, j=0, q=0, k=0;
		for(int i=0;i<selectedCards.size();i++) {
			if(cardAt(selectedCards.get(i)).rank()=="10") t++;
			else if(cardAt(selectedCards.get(i)).rank()=="jack") j++;
			else if(cardAt(selectedCards.get(i)).rank()=="queen") q++;
			else if(cardAt(selectedCards.get(i)).rank()=="king") k++;
		}
		if(j>=4 || q>=4 || k>=4 || t>=4) return true;
		else return false;
	}
}
