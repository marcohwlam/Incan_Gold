/**
 * Created by Ho on 10/23/2017.
 */


public class Deck extends CardContainer {
    public Deck() {
        super();
        loadDeck();
        shuffle();
    }

    private void loadDeck() {
        push(new TreasureCard("1 Gem", "/img/T1.PNG", 1));
        for (int i = 2; i <= 5; i++) {
            push(new TreasureCard(i + " Gems", "/img/T" + i + ".PNG", i));
        }
        for (int i = 5; i <= 15; i += 2) {
            push(new TreasureCard(i + " Gems", "/img/T" + i + ".PNG", i));
        }
        push(new TreasureCard("11 Gems", "/img/T11.PNG", 11));
        push(new TreasureCard("14 Gems", "/img/T14.PNG", 14));
        push(new TreasureCard("5 Gems", "/img/T5.PNG", 5));
        push(new TreasureCard("7 Gems", "/img/T7.PNG", 7));

        for (int i = 0; i < 3; i++) {
            push(new HazardCard("Lava", "/img/lava.PNG"));
            push(new HazardCard("Rock Slide", "/img/rockSlide.PNG"));
            push(new HazardCard("Undead", "/img/undead.PNG"));
            push(new HazardCard("Spider", "/img/spider.PNG"));
            push(new HazardCard("Snake", "/img/snake.PNG"));
        }
    }
}

