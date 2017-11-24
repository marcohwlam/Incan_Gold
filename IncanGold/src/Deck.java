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
        for (int i = 1; i <= 5; i++) {
            push(new TreasureCard("Treasure", "/img/T" + i + ".PNG", i));
        }
        for (int i = 5; i <= 17; i += 2) {
            push(new TreasureCard("Treasure", "/img/T" + i + ".PNG", i));
        }
        push(new TreasureCard("Treasure", "/img/T14.PNG", 14));
        push(new TreasureCard("Treasure", "/img/T5.PNG", 5));
        push(new TreasureCard("Treasure", "/img/T7.PNG", 7));

        for (int i = 0; i < 3; i++) {
            push(new HazardCard("Lava", "/img/lava.PNG"));
            push(new HazardCard("Rock Slide", "/img/rockSlide.PNG"));
            push(new HazardCard("Undead", "/img/undead.PNG"));
            push(new HazardCard("Spider", "/img/spider.PNG"));
            push(new HazardCard("Snake", "/img/snake.PNG"));
        }
    }
}

