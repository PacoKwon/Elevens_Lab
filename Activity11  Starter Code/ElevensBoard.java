import java.util.List;
import java.util.ArrayList;

/**
 * The ElevensBoard class represents the board in a game of Elevens.
 */
public class ElevensBoard extends Board {

	/**
	 * The size (number of cards) on the board.
	 */
	private static final int BOARD_SIZE = 9;

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
		{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0};

	/**
	 * Creates a new <code>ElevensBoard</code> instance.
	 */
	 public ElevensBoard() {
	 	super(BOARD_SIZE, RANKS, SUITS, POINT_VALUES);
	 }

	/**
	 * Determines if the selected cards form a valid group for removal.
	 * In Elevens, the legal groups are (1) a pair of non-face cards
	 * whose values add to 11, and (2) a group of three cards consisting of
	 * a jack, a queen, and a king in some order.
	 * @param selectedCards the list of the indices of the selected cards.
	 * @return true if the selected cards form a valid group for removal;
	 *         false otherwise.
	 */
	@Override
	public boolean isLegal(List<Integer> selectedCards) {
		/* *** TO BE MODIFIED IN ACTIVITY 11 *** */
		if (selectedCards.size() == 2) {
			return findPairSum11(selectedCards).size() > 0;
		} else if (selectedCards.size() == 3) {
			return findJQK(selectedCards).size() > 0;
		} else {
			return false;
		}
	}

	/**
	 * Determine if there are any legal plays left on the board.
	 * In Elevens, there is a legal play if the board contains
	 * (1) a pair of non-face cards whose values add to 11, or (2) a group
	 * of three cards consisting of a jack, a queen, and a king in some order.
	 * @return true if there is a legal play left on the board;
	 *         false otherwise.
	 */
	@Override
	public boolean anotherPlayIsPossible() {
		return ( findJQK(cardIndexes()).size() > 0 || findPairSum11(cardIndexes()).size() > 0 ) ;
	}

	/**
	 * Look for an 11-pair in the selected cards.
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find an 11-pair.
	 * @return a list of the indexes of an 11-pair, if an 11-pair was found;
	 *         an empty list, if an 11-pair was not found.
	 */
	private List<Integer> findPairSum11(List<Integer> selectedCards) {
		List<Integer> pair = new ArrayList<Integer>();
		for(int i = 0; i < selectedCards.size(); i++) {
			int card1 = cardAt(selectedCards.get(i)).pointValue();
			for(int j = i + 1; j < selectedCards.size(); j++) {
				int card2 = cardAt(selectedCards.get(j)).pointValue();
				if(card1 + card2 == 11) {
					pair.add(selectedCards.get(i));
					pair.add(selectedCards.get(j));
					return pair;
				}
			}
		}
		return pair;
	}

	/**
	 * Look for a JQK in the selected cards.
	 * @param selectedCards selects a subset of this board.  It is list
	 *                      of indexes into this board that are searched
	 *                      to find a JQK group.
	 * @return a list of the indexes of a JQK, if a JQK was found;
	 *         an empty list, if a JQK was not found.
	 */
	private List<Integer> findJQK(List<Integer> selectedCards) {
		List<Integer> pair = new ArrayList<Integer>();
		int J = -1, Q = -1, K = -1;
		for (int i = 0; i < selectedCards.size(); i++)
		{
			int place = selectedCards.get(i);

			if(cardAt(place).rank() == "jack" && J == -1) J = place;
			else if(cardAt(place).rank() == "queen" && Q == -1) Q = place;
			else if(cardAt(place).rank() == "king" && K == -1) K = place;
		}

		if(J > -1 && Q > -1 && K > -1) {
			// if all three face cards exist, add them to the arraylist,
			// and then return the arraylist
			pair.add(J);
			pair.add(Q);
			pair.add(K);

			return pair;
		}

		return pair; // return an empty arraylist
	}

	/**
	 * Looks for a legal play on the board.  If one is found, it plays it.
	 * @return true if a legal play was found (and made); false othewise.
	 */
	public boolean playIfPossible() {
		// if either one is possible, play and return true.
		// if both are not possible, return false.
		return playPairSum11IfPossible() || playJQKIfPossible();
	}

	/**
	 * Looks for a pair of non-face cards whose values sum to 11.
	 * If found, replace them with the next two cards in the deck.
	 * The simulation of this game uses this method.
	 * @return true if an 11-pair play was found (and made); false othewise.
	 */
	private boolean playPairSum11IfPossible() {
		// cardIndexes() : returns an arraylist of indexes of places that are not empty.
		List<Integer> indexes = findPairSum11(cardIndexes()); 
		if (indexes.size() == 2) {
			// if there are two elements ( or one pair ), replace and return true.
			replaceSelectedCards(indexes);
			return true;
		}

		 return false;
	}

	/**
	 * Looks for a group of three face cards JQK.
	 * If found, replace them with the next three cards in the deck.
	 * The simulation of this game uses this method.
	 * @return true if a JQK play was found (and made); false othewise.
	 */
	private boolean playJQKIfPossible() {
		// cardIndexes() : returns an arraylist of indexes of places that are not empty
		List<Integer> indexes = findJQK(cardIndexes());
		if (indexes.size() == 3) {
			// if there are two elements ( or one triplet of Jack, Queen, King ),
			// replace them with new cards and return true
			replaceSelectedCards(indexes);
			return true;
		}

		return false;
	}
}
