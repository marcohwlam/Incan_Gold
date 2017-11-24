/**
 * Created by Ho on 10/23/2017.
 */
public class ArtifactCard extends Card {
    private String type;
    private int artifactValue;

    public ArtifactCard(String name, String image, int artifactValue) {
        super(name, image);
        this.artifactValue = artifactValue;
    }

    public int getArtifactValue() {
        return artifactValue;
    }
}