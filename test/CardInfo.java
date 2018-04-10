/**
 * 이 클래스는 카드의 추가적인 요소를 하나의 인스턴스로 처리하기 위해 만들었다.
 * 카드가 어디에 속해 있는지, 그리고 카드의 위치를 멤버면수로 가지고 있다.
 */
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