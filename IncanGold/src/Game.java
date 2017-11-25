import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ho on 11/11/2017.
 */
public class Game extends JFrame implements Runnable {

    //region GUI var
    public JPanel mainPanel;
    public JPanel topBar;
    public JPanel centerPan;
    public JPanel botPan;
    public JPanel leftPan;
    public JPanel rightPan;
    public JPanel deckArea;
    public JPanel questArea;
    public JPanel roundTrackerArea;
    public JPanel platerArea;
    public JLabel deckIcon;
    public JLabel deckCount;
    public JLabel roundIcon;
    public ArrayList<JLabel> qCardLabel;
    public JFrame frame;

    //endregion

    // region Var
    private Deck deck;
    private Adventure adventure = new Adventure();
    private ArrayList<Player> playersList = new ArrayList<>();
    private ArrayList<Player> playersInTemple = new ArrayList<>();
    private ArrayList<Player> playersInDecision = new ArrayList<>();
    private Map<Player, Game.Action> playerAction = new HashMap<>();
    private int artifactCount = 0;
    private boolean gameAlive = true;
    private boolean roundAlive = true;

    @Override
    public void run() {

//        while (gameAlive){
//            startRound();
//        }
//        endGame();
        startRound();

        // Deal Card
        dealQuestCard();
        showQuestCard();

    }

    private enum Round {One, Two, Three, Four, Five}
    public enum Action {Stay, Go_Back}
    private Game.Round currentRound;
    //endregion

    Game() {
        System.out.println("in Game Constuct");
        // Spawn frame
        initFrame();
        //updateRoundIcon("/img/round1.PNG");
        startGame();
    }

    boolean _gameActive = false;

    KeyController _keyController = new KeyController() {
        @Override
        public void triggerAction(Action action) {

            // determine player
            // if no player in the temple
            if (playersInTemple.size()==0){
                System.out.println("No player in temple");
                roundAlive = false;
                _keyController.setActivePlayer(null);
            }else {
                System.out.println("Put player action in action list");
                playerAction.put(_keyController.getActivePlayer(), action);
                //handel last player
                if (playersInDecision.size() > 1){
                    //set active player to the next
                    _keyController.setActivePlayer(playersInDecision.get(1));
                    System.out.println( _keyController.getActivePlayer().getName() + " turn");
                }
                playersInDecision.remove(0);
            }

            // action
            // When everyone is done with action
            if (playersInDecision.size() == 0){
                //  detect end of game = gameAlive
                startTurn();
                if(!roundAlive) {
                    //  detect if end of round -> setup new round = roundAlive
                    endRound();
                    startRound();
                    if (!gameAlive) {
                        endGame();
                    }
                }
            }

        }
    };

    //region Game Logic
    void startGame(){
        // Debug use playe
        this.playersList.add(new Player("Player 1"));
        this.playersList.add(new Player("Player 2"));
        this.playersList.add(new Player("Player 3"));
        this.playersList.add(new Player("Player 4"));

        // init deck
        this.deck = new Deck();
        this.deck.shuffle();

        // init round counter
        this.currentRound = Game.Round.One;
        frame.addKeyListener(_keyController);
        //  Start round 1
        Thread t = new Thread(this);
        t.start();
    }

    void checkAndStartRound() {}

    private void startRound() {
        System.out.println("Start new round");
        // Put all the player back to Temple
        playersInTemple.addAll(this.playersList);
        playersInDecision.addAll(this.playersList);

        _keyController.setActivePlayer(playersInTemple.get(0));
        System.out.println(_keyController.getActivePlayer().getName() + " turn");

        // Push new artifact card in the deck
        this.deck.push(makeArtifact(currentRound));
        // Shuffle
        this.deck.shuffle();


        showRound();

//        // Start turn 1
//        while (roundAlive) {
//            roundAlive = startTurn();
//        }
        //endRound();
    }

    private  void startTurn() {
        //Set active player
        _keyController.setActivePlayer(playersInTemple.get(0));

        // Put all the player back to decision
        this.playersInDecision.addAll(this.playersInTemple);


//        // Player decide action
//        for (Player p : this.playersInTemple) {
//            System.out.print(p.getName() + "\n");
//            this.playerAction.put(p, promptChoices());
//        }

        // Show action
        showPlayerAction();

        //goBackToTent
        goBackToTent();

        //End false if no one in temple
        if (playersInTemple.size() == 0) {
            roundAlive = false;
        }

        // if last card is harzard
        if (this.adventure.readLastCard() instanceof HazardCard) {
            // Check Hazard
            System.out.print("Boom");
            roundAlive = !hasTwoHazards();
        } else if (this.adventure.readLastCard() instanceof TreasureCard) {
            // Distribute treasure
            distributeTreasure();
        }

        // Deal Card
        dealQuestCard();
        showQuestCard();
        updateDeckCount(deck.getCards().size());

        roundAlive = true;
    }

    private void endRound() {
        System.out.println("End of Round");
        // reset adventure
        adventure.clear();

        // reset all player hand score to 0
        for (Player p : this.playersList) {
            p.setHandScore(0);
        }

        // currentRound ++
        switch (currentRound) {
            case One:
                this.currentRound = Game.Round.Two;
                break;
            case Two:
                this.currentRound = Game.Round.Three;
                break;
            case Three:
                this.currentRound = Game.Round.Four;
                break;
            case Four:
                this.currentRound = Game.Round.Five;
                break;
            case Five:
                gameAlive = false;
                break;
        }
    }

    private void endGame() {
        showScoreBoard();
    }

    private ArtifactCard makeArtifact(Game.Round r) {
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

    private Game.Action promptChoices() {
        System.out.print("Press [S]tay, or [G]o Back\n");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();
        while (!(s.equals("S") || s.equals("G"))) {
            System.out.print("Press [S]tay, or [G]o Back\n");
            s = scan.next();
        }
        if (s.equals("S"))
            return Game.Action.Stay;

        return Game.Action.Go_Back;

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
            if (this.playerAction.get(p) == Game.Action.Go_Back) {
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
            if (this.playerAction.get(p) == Game.Action.Go_Back) {
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
        updateRoundIcon(roundImgPath);
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
            Game.Action act = this.playerAction.get(p);
            if (act == Game.Action.Go_Back) {
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

    void initFrame() {
        frame = new JFrame("Game");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(800, 600) );
        frame.setVisible(true);
        // show deck image
        showDeck();
        // show player image
        // TODO
        System.out.println("End init");
    }

    void showFrame() {
//        frame = new JFrame("Game");
//        frame.setContentPane(new Game().mainPanel);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        System.out.println("End showFrame");
    }

    void showDeck() {
        try {
            BufferedImage img = ImageIO.read(this.getClass().getResource("/img/deck.PNG"));
            deckIcon.setIcon(new ImageIcon(img));
            System.out.println("End showDeck");
        } catch (IOException ex) {
            // handle exception...
            System.out.print("Error loading deck image");
        }
    }

    void updateDeckCount(int count) {
        System.out.println(deckCount.getText());
        deckCount.setText("Number of Card in the deck: " + count);
        System.out.println(deckCount.getText());

    }

    void updateRoundIcon(String iconPath) {
        try {
            BufferedImage img = ImageIO.read(this.getClass().getResource(iconPath));
            roundIcon.setIcon(new ImageIcon(img));
            roundIcon.revalidate();
            roundIcon.repaint();

        } catch (IOException ex) {
            // handle exception...
            System.out.print("Error loading round image");
        }
    }

    void updateQuestArea(Adventure adventure) {
        qCardLabel = new ArrayList<>();
        for (Card c : adventure.getCards()) {
            try {
                BufferedImage img = ImageIO.read(this.getClass().getResource(c.getImagePath()));
                qCardLabel.add(new JLabel(new ImageIcon(img)));
            } catch (IOException ex) {
                System.out.print("Error loading round image");
            }
        }

        for (JLabel label : qCardLabel) {
            questArea.add(label);
        }

    }
    // endregion

}
