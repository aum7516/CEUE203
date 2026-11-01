public class Driver {
    public static void main(String[] args) {
        Card[] stream = {
            new Card("Queen", "Hearts"),
            new Card("Ace", "Spades"),
            new Card("10", "Diamonds"),
            new Card("Ace", "Spades"),
            new Card("King", "Clubs")
        };

        Card[] cards = new Card[stream.length];
        int count = 0;

        for (Card card : stream) {
            boolean duplicate = false;
            for (int i = 0; i < count; i++) {
                if (card.equals(cards[i])) {
                    System.out.println("Duplicate found: " + card);
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                cards[count++] = card;
            }
        }
    }
}
