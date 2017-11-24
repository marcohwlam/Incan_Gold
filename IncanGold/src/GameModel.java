import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * Created by Ho on 10/23/2017.
 */
public class GameModel {
    // region Var
    private Deck deck;
    private Adventure adventure = new Adventure();
    private ArrayList<Player> playersList = new ArrayList<>();
    private ArrayList<Player> playersInTemple = new ArrayList<>();
    private Map<Player, Action> playerAction = new HashMap<>();
    private int artifactCount = 0;
    private boolean gameAlive = true;
    private enum Round {One, Two, Three, Four, Five}
    private GameView gameView;
    private enum Action {Stay, Go_Back}
    private Round currentRound;
    //endregion

    public GameModel() {
        // Spawn frame
        this.gameView = new GameView();
        this.gameView.initFrame();

        // Debug use playe
        this.playersList.add(new Player("Player 1"));
        this.playersList.add(new Player("Player 2"));
        this.playersList.add(new Player("Player 3"));
        this.playersList.add(new Player("Player 4"));

        // init deck
        this.deck = new Deck();
        this.deck.shuffle();

        // init round counter
        this.currentRound = Round.One;

        // Game Alive

        // Start round 1
        while (gameAlive){
            startRound();
        }

        endGame();
    }

    public void addPlayer() {

    }

    //region Game Logic
    private void startRound() {
        // Put all the player back to Temple
        this.playersInTemple.addAll(this.playersList);
        // Push new artifact card in the deck
        this.deck.push(makeArtifact(currentRound));
        // Shuffle
        this.deck.shuffle();
        boolean roundAlive = true;

        showRound();

        // Start turn 1
        while (roundAlive) {
            roundAlive = startTurn();
        }
        endRound();
    }

    private boolean startTurn() {

        // Deal Card
        dealQuestCard();
        showQuestCard();
        updateDeckCount();

        // Player decide action
        for (Player p : this.playersInTemple) {
            System.out.print(p.getName() + "\n");
            this.playerAction.put(p, promptChoices());
        }

        // Show action
        showPlayerAction();

        //goBackToTent
        goBackToTent();

        //End false if no one in temple
        if (playersInTemple.size() == 0) {
            return false;
        }

        // if last card is harzard
        if (this.adventure.readLastCard() instanceof HazardCard) {
            // Check Hazard
            System.out.print("Boom");
            return !hasTwoHazards();
        } else if (this.adventure.readLastCard() instanceof TreasureCard) {
            // Distribute treasure
            distributeTreasure();
        }

        return true;
    }

    private void endRound() {
        // reset adventure
        adventure.clear();

        // reset all player hand score to 0
        for (Player p : this.playersList) {
            p.setHandScore(0);
        }

        // currentRound ++
        switch (currentRound) {
            case One:
                this.currentRound = Round.Two;
                break;
            case Two:
                this.currentRound = Round.Three;
                break;
            case Three:
                this.currentRound = Round.Four;
                break;
            case Four:
                this.currentRound = Round.Five;
                break;
            case Five:
                gameAlive = false;
                break;
        }
    }

    private void endGame() {
        showScoreBoard();
    }

    private ArtifactCard makeArtifact(Round r) {
        switch (r) {
            case One:
            case Two:
            case Three:
                return new ArtifactCard("Artifact", "img", 5);

            case Four:
            case Five:
                return new ArtifactCard("Artifact", "img", 10);

            default:
                return new ArtifactCard("Artifact", "img", 5);
        }
    }

    private void dealQuestCard() {
        this.adventure.push(this.deck.pop());
    }

    private Action promptChoices() {
        System.out.print("Press [S]tay, or [G]o Back\n");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();
        while (!(s.equals("S") || s.equals("G"))) {
            System.out.print("Press [S]tay, or [G]o Back\n");
            s = scan.next();
        }
        if (s.equals("S"))
            return Action.Stay;

        return Action.Go_Back;

    }

    private void distributeTreasure() {
        TreasureCard lastCard = (TreasureCard) this.adventure.readLastCard();

        for (Player p : this.playersInTemple) {
            // divide treasure on the road
            p.addHandScore(lastCard.getTreasureValue() / this.playersInTemple.size());
        }
        // Add remainder treasure to the road
        this.adventure.addTreasureOnTheRoad(lastCard.getTreasureValue() % this.playersInTemple.size());
    }

    private void goBackToTent() {
        int goBackCount = getGoBackCount();
        // Remove go back players and add score to tent
        ArrayList<Player> pT = (ArrayList<Player>) this.playersInTemple.clone();
        for (Player p : pT) {
            if (this.playerAction.get(p) == Action.Go_Back) {
                // Add hand score to tent
                p.addTentScore(p.getHandScore());

                // Give Artifact Score
                if (goBackCount == 1) {
                    giveArtifactScore(p);
                }

                // divide treasure on the road
                p.addTentScore(this.adventure.getTreasureOnTheRoad() / goBackCount);

                // Reset hand score and remove from adventure
                p.setHandScore(0);
                this.playersInTemple.remove(p);
            }
        }
        // Treasure left on the road is the remainder
        if (goBackCount != 0){
            this.adventure.setTreasureOnTheRoad(this.adventure.getTreasureOnTheRoad() % goBackCount);
        }

    }

    private int getGoBackCount() {
        int goBackCount = 0;
        for (Player p : this.playersInTemple) {
            if (this.playerAction.get(p) == Action.Go_Back) {
                goBackCount++;
            }
        }
        return goBackCount;
    }

    private void giveArtifactScore(Player p) {
        // Remove all artifact from adventure
        ArrayList<Card> tempCardList = this.adventure.popAllArtifact();

        if (tempCardList != null) {
            // give the only leaving player artifact score
            for (Card c : tempCardList) {
                if (p.getArtifacts().size() < 4) {
                    p.addTentScore(5);
                } else {
                    p.addTentScore(10);
                }
                // Add artifacts to player hand
                p.addArtifacts(c);
            }
        }
    }

    private boolean hasTwoHazards() {
        // Check if there are two same type of hazard
        ArrayList<Card> tempCardList = this.adventure.getCards();

        // If the last card is hazard than check if there are the same
        if (tempCardList.get(tempCardList.size() - 1) instanceof HazardCard) {
            // Get the name of last card
            String hazardName = tempCardList.get(tempCardList.size() - 1).getName();
            // Check all cards except the last in the adventure
            for (int i = 0; i < tempCardList.size() - 1; i++) {
                if (tempCardList.get(i) instanceof HazardCard) {
                    //If there is same type of hazard before end the round
                    if (hazardName.equals(tempCardList.get(i).getName())) {
                        // End round
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //endregion

    // region UI update
    private void updateDeckCount() {
        this.gameView.updateDeckCount(deck.getCards().size());
    }

    private void showRound() {
        String roundImgPath = "/img/round1.PNG";
        String round = "No you are wrong ";
        switch (this.currentRound) {
            case One:
                round = "Round 1";
                roundImgPath = "/img/round1.PNG";
                break;
            case Two:
                round = "Round 2";
                roundImgPath = "/img/round2.PNG";
                break;
            case Three:
                round = "Round 3";
                roundImgPath = "/img/round3.PNG";
                break;
            case Four:
                round = "Round 4";
                roundImgPath = "/img/round4.PNG";
                break;
            case Five:
                round = "Round 5";
                roundImgPath = "/img/round5.PNG";
                break;
        }
        gameView.updateRoundIcon(roundImgPath);
        System.out.print(round + "\n");
    }

    private void showScoreBoard() {
        // player score map
        Map<Player, Integer> playerScore = new HashMap<>();


        for (Player p : playersList) {
            playerScore.put(p, p.getTentScore());

            // print all name and score
            System.out.print(p.getName());
            System.out.print(p.getTentScore());
        }
    }

    private void showPlayerAction() {
        for (Player p : this.playersInTemple) {
            Action act = this.playerAction.get(p);
            if (act == Action.Go_Back) {
                System.out.print(p.getName() + "go back to tent\n");
            } else {
                System.out.print(p.getName() + "stay in the adventure\n");
            }

        }
    }

    private void showQuestCard() {
        for (Card c : adventure.getCards()) {
            System.out.print("Adventure: " + c.getName() + "\n");
        }

    }
    // endregion
}
