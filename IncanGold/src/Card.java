/**
 * Created by Ho on 11/4/2017.
 */
public abstract class Card {
    private String name;
    private String imagePath;

    public Card(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }
}
