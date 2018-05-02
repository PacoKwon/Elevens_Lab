import java.util.ArrayList;

/**
 * The Deck class represents a shuffled deck of cards.
 * It provides several operations including
 *      initialize, shuffle, deal, and check if empty.
 */
public class Deck {

	/**
	 * cards contains all the cards in the deck.
	 */
	private ArrayList<Card> cards;

	/**
	 * size is the number of not-yet-dealt cards.
	 * Cards are dealt from the top (highest index) down.
	 * The next card to be dealt is at size - 1.
	 */
	private int size;


	/**
	 * Creates a new <code>Deck</code> instance.<BR>
	 * It pairs each element of ranks with each element of suits,
	 * and produces one of the corresponding card.
	 * @param ranks is an array containing all of the card ranks.
	 * @param suits is an array containing all of the card suits.
	 * @param values is an array containing all of the card point values.
	 */
	public Deck(String[] ranks, String[] suits, int[] values) {
		cards = new ArrayList<Card>();
		for (int j = 0; j < ranks.length; j++) {
			for (String suitString : suits) {
				cards.add(new Card(ranks[j], suitString, values[j]));
			}
		}
		size = cards.size();
		shuffle();
	}


	/**
	 * Determines if this deck is empty (no undealt cards).
	 * @return true if this deck is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Accesses the number of undealt cards in this deck.
	 * @return the number of undealt cards in this deck.
	 */
	public int size() {
		return size;
	}

	/**
	 * Moves the card in index "from" to index "to"
	 * @param from the desired card's index
	 * @param to the card's destination
	 */
	public void moveCard(Card c, int to) {
		int from = cards.indexOf(c);
		if (from == -1) {
			System.out.println("ERROR! RETURNING");
			return;
		}
		cards.remove(from);
		cards.add(to, c);
	}

	/**
	 * Randomly permute the given collection of cards
	 * and reset the size to represent the entire deck.
	 */
	public void shuffle() {
		for (int k = cards.size() - 1; k > 0; k--) {
			int howMany = k + 1;
			int start = 0;
			int randPos = (int) (Math.random() * howMany) + start;
			Card temp = cards.get(k);
			cards.set(k, cards.get(randPos));
			cards.set(randPos, temp);
		}
		size = cards.size();
	}

	/**
	 * Deals a card from this deck.
	 * @return the card just dealt, or null if all the cards have been
	 *         previously dealt.
	 */
	public Card deal() {
		if (isEmpty()) {
			return null;
		}
		size--;
		Card c = cards.get(size);
		return c;
	}

	public Card pull(int index) {
		if (isEmpty()) {
			return null;
		}
		size--;
		Card c = cards.remove(index);

		return c;
	}
	/**
	 * Stacks the deck until the designated location
	 * This stack method is inclusive of stackUntil.
	 * In other words, card of index stackUntil will also be stacked to the deck
	 * @param stackUntil the amount to stack until
	 */
	public void stack(int stackUntil) {
		if (stackUntil < this.size) {
			System.out.println("Parameter value less than current deck size. Has to be larger.");
			return;
		}
		this.size = stackUntil;
	}

	/**
	 * Stack all cards into the deck
	 */
	public void stack() {
		this.size = cards.size();
	}

	/**
	 * Get the last card of dealt deck
	 * @return last card of dealt deck
	 */
	public Card getTop() {
		return cards.get(size);
	}

	/**
	 * Generates and returns a string representation of this deck.
	 * @return a string representation of this deck.
	 */
	@Override
	public String toString() {
		String rtn = "size = " + size + "\nUndealt cards: \n";

		for (int k = size - 1; k >= 0; k--) {
			rtn = rtn + cards.get(k);
			if (k != 0) {
				rtn = rtn + ", ";
			}
			if ((size - k) % 2 == 0) {
				// Insert carriage returns so entire deck is visible on console.
				rtn = rtn + "\n";
			}
		}

		rtn = rtn + "\nDealt cards: \n";
		for (int k = cards.size() - 1; k >= size; k--) {
			rtn = rtn + cards.get(k);
			if (k != size) {
				rtn = rtn + ", ";
			}
			if ((k - cards.size()) % 2 == 0) {
				// Insert carriage returns so entire deck is visible on console.
				rtn = rtn + "\n";
			}
		}

		rtn = rtn + "\n";
		return rtn;
	}
}
