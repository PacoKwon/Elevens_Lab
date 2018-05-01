/**
 * This class provides a convenient way to test shuffling methods.
 */
public class Shuffler {

	/**
	 * The number of consecutive shuffle steps to be performed in each call
	 * to each sorting procedure.
	 */
	private static final int SHUFFLE_COUNT = 5;


	/**
	 * Tests shuffling methods.
	 * @param args is not used.
	 */
	public static void main(String[] args) {
		System.out.println("Results of " + SHUFFLE_COUNT +
								 " consecutive perfect shuffles:");
		int[] values1 = {0, 1, 2, 3};
		for (int j = 1; j <= SHUFFLE_COUNT; j++) {
			perfectShuffle(values1);
			System.out.print("  " + j + ":");
			for (int k = 0; k < values1.length; k++) {
				System.out.print(" " + values1[k]);
			}
			System.out.println();
		}
		System.out.println();

		System.out.println("Results of " + SHUFFLE_COUNT +
								 " consecutive efficient selection shuffles:");
		int[] values2 = {0, 1, 2, 3};
		for (int j = 1; j <= SHUFFLE_COUNT; j++) {
			selectionShuffle(values2);
			System.out.print("  " + j + ":");
			for (int k = 0; k < values2.length; k++) {
				System.out.print(" " + values2[k]);
			}
			System.out.println();
		}
		System.out.println();
	}


	/**
	 * Apply a "perfect shuffle" to the argument.
	 * The perfect shuffle algorithm splits the deck in half, then interleaves
	 * the cards in one half with the cards in the other.
	 * @param values is an array of integers simulating cards to be shuffled.
	 */
	public static void perfectShuffle(int[] values) {
		// make a new array of same size
		int[] shuffled = new int[values.length];

		int k = 0;
		// traverse even indexes
		for (int j = 0; j < values.length / 2; j++) {
			shuffled[k] = values[j];
			k += 2;
		}

		k = 1;
		// traverse odd indexes
		for (int j = values.length / 2; j < values.length; j++) {
			shuffled[k] = values[j];
			k += 2;
		}

		// copy to original array
		for (int i = 0; i < values.length; i++) {
			values[i] = shuffled[i];
		}
	}

	/**
	 * Apply an "efficient selection shuffle" to the argument.
	 * The selection shuffle algorithm conceptually maintains two sequences
	 * of cards: the selected cards (initially empty) and the not-yet-selected
	 * cards (initially the entire deck). It repeatedly does the following until
	 * all cards have been selected: randomly remove a card from those not yet
	 * selected and add it to the selected cards.
	 * An efficient version of this algorithm makes use of arrays to avoid
	 * searching for an as-yet-unselected card.
	 * @param values is an array of integers simulating cards to be shuffled.
	 */
	public static void selectionShuffle(int[] values) {
		// for k = 51 down to 1
		for (int k = values.length - 1; k >= 1; k--) {
			// generate a random integer r between 0 and k, inclusive
			int r = (int)(Math.random() * k);

			// exchange cards[k] and cards
			int tmp = values[k];
			values[k] = values[r];
			values[r] = tmp;
		}
	}

	public static String flip() {
		int p = (int)(Math.random() * 3);
		if (p < 2) {
			return "heads";
		} else {
			return "tails";
		}
	}

	public static boolean arePermutations(int[] a, int[] b) {
		if (a.length != b.length) return false;

		java.util.Arrays.sort(a);
		java.util.Arrays.sort(b);

		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) return false;
		}
		return true;
	}
}
