public class KlondikeTest
{
    public static void main(String[] args) {
        KlondikeBoard kb = new KlondikeBoard();
        System.out.println(kb.stock);
        kb.stock.deal();
        kb.stock.deal();
        kb.stock.deal();
        System.out.println(kb.stock);
        kb.stock.stack(24);
        System.out.println(kb.stock);
        System.out.println(kb.stock.getTop());
        kb.dealStock();
        System.out.println(kb.stock);
        System.out.println(kb.stock.getTop());
    }
}