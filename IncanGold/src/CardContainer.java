/**
 * Created by Ho on 10/23/2017.
 */

import java.util.ArrayList;
import java.util.Collections;

public class CardContainer {
    private ArrayList<Card> cards;

    CardContainer() {
        this.cards = new ArrayList<>();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    Card pop() {
        Card temp = cards.get(cards.size() - 1);
        this.cards.remove(cards.size() - 1);
        return temp;
    }

    void push(Card card) {
        cards.add(card);
    }

    void remove(Card card){
        cards.remove(card);
    }

    void remove(int index){
        cards.remove(index);
    }

    void clear(){
        this.cards.clear();
    }

    void shuffle() {
        Collections.shuffle(cards);
    }

    Card readLastCard() {
        return cards.get(cards.size() - 1);
    }

}
