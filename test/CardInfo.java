class CardInfo
{
    public static int FOUNDATIONS = 0;
    public static int STOCK = 1;
    public static int PILES = 2;

    /** the pile a card belongs to */
    private int rowNum;
    /** the position of a card in a pile or a foundation */
    private int pos;
    /** where the card belongs to. It can only be one of: 
     * 0: foundations, 
     * 1: stock,
     * 2: piles.
     */
    private int from;

    public CardInfo(int _from) {
        this(0, 0, _from);
    }
    public CardInfo(int _rowNum, int _pos, int _from) {
        this.rowNum = _rowNum;
        this.pos = _pos;
        this.from = _from;
    }

    public int rowNum() {
        return rowNum;
    }

    public int pos() {
        return pos;
    }

    public int from() {
        return from;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CardInfo) {
            CardInfo other = (CardInfo)obj;

            return (this.rowNum == other.rowNum() && this.pos == other.pos() && this.from == other.from);
        }

        return false;
    }
    
    @Override
    public String toString() {
        String[] fromArr = {"Foundations", "Stock", "Piles"};
        String ret = String.format("Card from \"%s\": [rowNum: %d, pos: %d]", fromArr[from], rowNum, pos);
        return ret;
    }
}