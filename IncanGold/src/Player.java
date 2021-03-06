import java.util.ArrayList;

/**
 * Created by Ho on 10/23/2017.
 */
public class Player implements Comparable<Player>{
    private int handScore;
    private int tentScore;
    private String name;
    private ArrayList<Card> artifacts = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.handScore = 0;
        this.tentScore = 0;
    }

    public int getHandScore() {
        return handScore;
    }

    public void setHandScore(int handScore) {
        this.handScore = handScore;
    }

    public void addHandScore(int handScore) {
        this.handScore += handScore;
    }

    public int getTentScore() {
        return tentScore;
    }

    public void setTentScore(int score) {
        this.tentScore = score;
    }

    public void addTentScore(int score) {
        this.tentScore += score;
    }

    public void addArtifacts(ArrayList<Card> artifactCards ) {
        this.artifacts.addAll(artifactCards);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getArtifacts() {
        return artifacts;
    }

    public void addArtifacts(Card c) {
        this.artifacts.add(c);
    }

    @Override
    public int compareTo(Player comparePlayer) {
        int compareScore = comparePlayer.getTentScore();

        //ascending order
        //return this.tentScore - compareScore;

        //descending order
        return compareScore - this.tentScore;
    }
}
