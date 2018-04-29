import java.util.List;
import java.util.ArrayList;

/**
 * The ElevensBoard class represents the board in a game of Elevens.
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
	 * Creates a new <code>ElevensBoard</code> instance.
	 */
	 public TensBoard() {
	 	super(BOARD_SIZE, RANKS, SUITS, POINT_VALUES);
	 }

	/**
	 * Determines if the selected cards form a valid group for removal.
	 * In Elevens, the legal groups are (1) a pair of non-face cards
	 * whose values add to 10, and (2) a quartet of ten, jack, queen, king of same rank
	 * @param selectedCards the list of the indices of the selected cards.
	 * @return true if the selected cards form a valid group for removal;
	 *         false otherwise.
	 */
	@Override
	public boolean isLegal(List<Integer> selectedCards) {
		/* *** TO BE MODIFIED IN ACTIVITY 11 *** */
		if (selectedCards.size() == 2) {
			return findPairSum10(selectedCards).size() > 0;
		} else if (selectedCards.size() == 4) {
			return findQuartet(selectedCards).size() > 0;
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
		return ( findQuartet(cardIndexes()).size() > 0 || findPairSum10(cardIndexes()).size() > 0 ) ;
	}

	/**
	 * Check for an 10-pair in the selected cards.
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find an 10-pair.
	 * @return a list of the indexes of an 10-pair, if an 10-pair was found;
	 *         an empty list, if an 10-pair was not found.
	 */
	private List<Integer> findPairSum10(List<Integer> selectedCards) {
		List<Integer> pair = new ArrayList<Integer>();
		for(int i = 0; i < selectedCards.size(); i++) {
			int card1 = cardAt(selectedCards.get(i)).pointValue();
			for(int j = i + 1; j < selectedCards.size(); j++) {
				int card2 = cardAt(selectedCards.get(j)).pointValue();
				if(card1 + card2 == 10) {
					pair.add(selectedCards.get(i));
					pair.add(selectedCards.get(j));
					return pair;
				}
			}
		}
		return pair;
	}

	/**
	 * Check for quartet of 10, jack, queen, king of same rank
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find a king card.
	 * @return a list of the indexes of a Quartet, if a Quartet was found;
	 *         an empty list, if a Quartet was not found.
	 */
	private List<Integer> findQuartet(List<Integer> selectedCards) {
		List<Integer> quartet = new ArrayList<Integer>();
		int t=0, j=0, q=0, k=0;
		int[][] place = new int[4][4];
		for(int i = 0 ; i < selectedCards.size() ; i++) {
			int pl = selectedCards.get(i);
			if(cardAt(pl).rank() == "10") {
				place[0][t] = pl;
				t++;
			}
			else if(cardAt(pl).rank() == "jack") {
				place[1][j] = pl;
				j++;
			}
			else if(cardAt(pl).rank() == "queen") {
				place[2][q] = pl;
				q++;
			}
			else if(cardAt(pl).rank() == "king") {
				place[3][k] = pl;
				k++;
			}
		}
		if(t >= 4) {
			for(int i = 0; i<4 ; i++) 
				quartet.add(place[0][i]);
			return quartet;
		}
		else if(j >= 4) {
			for(int i = 0; i<4 ; i++) 
				quartet.add(place[1][i]);
			return quartet;
		}
		else if(q >= 4) {
			for(int i = 0; i<4 ; i++) 
				quartet.add(place[2][i]);
			return quartet;
		}
		else if(k >= 4) {
			for(int i = 0; i<4 ; i++) 
				quartet.add(place[3][i]);
			return quartet;
		}
		return quartet;
	}

	/**
	 * Looks for a legal play on the board.  If one is found, it plays it.
	 * @return true if a legal play was found (and made); false othewise.
	 */
	public boolean playIfPossible() {
		// if either one is possible, play and return true.
		// if both are not possible, return false.
		return playPairSum10IfPossible() || playQuartetIfPossible();
	}

	/**
	 * Looks for a pair of non-face cards whose values sum to 10.
	 * If found, replace them with the next two cards in the deck.
	 * The simulation of this game uses this method.
	 * @return true if an 10-pair play was found (and made); false othewise.
	 */
	private boolean playPairSum10IfPossible() {
		// cardIndexes() : returns an arraylist of indexes of places that are not empty.
		List<Integer> indexes = findPairSum10(cardIndexes()); 
		if (indexes.size() == 2) {
			// if there are two elements ( or one pair ), replace and return true.
			replaceSelectedCards(indexes);
			return true;
		}
		return false;
	}

	/**
	 * Looks for a group of four face cards Quartet.
	 * If found, replace them with the next four cards in the deck.
	 * The simulation of this game uses this method.
	 * @return true if a Quartet play was found (and made); false othewise.
	 */
	private boolean playQuartetIfPossible() {
		// cardIndexes() : returns an arraylist of indexes of places that are not empty
		List<Integer> indexes = findQuartet(cardIndexes());
		if (indexes.size() == 4) {
			// if there are four elements ( or one triplet of Jack, Queen, King ),
			// replace them with new cards and return true
			replaceSelectedCards(indexes);
			return true;
		}

		return false;
	}
}
