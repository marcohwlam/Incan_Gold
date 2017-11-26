import com.sun.xml.internal.bind.v2.TODO;
import java.util.ArrayList;
/**
 * Created by Ho on 10/23/2017.
 */
public class Adventure extends CardContainer {
    private int treasureOnTheRoad;

    public Adventure() {
        super();
        this.treasureOnTheRoad = 0;
    }

    public int getTreasureOnTheRoad() {
        return treasureOnTheRoad;
    }

    public void setTreasureOnTheRoad(int treasureOnTheRoad) {
        this.treasureOnTheRoad = treasureOnTheRoad;
    }

    public void addTreasureOnTheRoad(int treasureOnTheRoad) {
        this.treasureOnTheRoad += treasureOnTheRoad;
    }

//    @Override
//    void push(Card card) {
//        super.push(card);
//        if (card instanceof TreasureCard){
//            this.treasureOnTheRoad += ((TreasureCard) card).getTreasureValue();
//        }
//    }

    int getTotalArtifectScore(){
        int totalArtScore = 0;
        for (Card c: getCards()) {
            if (c instanceof ArtifactCard){
                totalArtScore += ((ArtifactCard) c).getArtifactValue();
            }
        }
        return totalArtScore;
    }

    ArrayList<Card> popAllArtifact(){
        ArrayList<Card> artCards = new ArrayList<>();
        ArrayList<Card> cloneCards = (ArrayList<Card>) getCards().clone();
        for (Card c: cloneCards) {
            if (c instanceof ArtifactCard){
                artCards.add(c);
                this.remove(c);
            }
        }
        return artCards;
    }


}
