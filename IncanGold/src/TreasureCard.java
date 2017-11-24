/**
 * Created by Ho on 10/23/2017.
 */
public class TreasureCard extends Card {
    private int treasureValue;

    public TreasureCard(String name, String imagePath, int treasureValue) {
        super(name, imagePath);
        this.treasureValue = treasureValue;
    }

    public int getTreasureValue() {
        return treasureValue;
    }
}
