class CardInfo
{
    /** the pile a card belongs to */
    private int rowNum;
    /** the position of a card in a pile or a foundation */
    private int pos;
    /** where the card belongs to. It can only be one of: 
     * "foundations", 
     * "stock",
     * "piles".
     */
    private String from;

    public CardInfo(int _rowNum, int _pos, String _from) {
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

    public String from() {
        return from;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CardInfo) {
            CardInfo other = (CardInfo)obj;

            return (this.rowNum == other.rowNum() && this.pos == other.pos() && this.from.equals(other.from()));
        }

        return false;
    }
}