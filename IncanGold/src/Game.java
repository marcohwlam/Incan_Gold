import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

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
    public JPanel playerArea;
    public JLabel deckIcon;
    public JLabel deckCount;
    public JLabel roundIcon;
    private JLabel turnTraker;
    private JLabel treasureOnTheRoadLabel;
    private JPanel player1Area;
    private JPanel player5Area;
    private JPanel player2Area;
    private JPanel player3Area;
    private JLabel player1Hand;
    private JLabel player4Tent;
    private JLabel player2Tent;
    private JLabel player3Tent;
    private JLabel player3Hand;
    private JLabel player1Tent;
    private JLabel player1ActionCard;
    private JLabel player2Hand;
    private JLabel player2ActionCard;
    private JLabel player3ActionCard;
    private JLabel player4ActionCard;
    private JLabel player4Hand;
    private JLabel noticeBar;
    public ArrayList<JLabel> qCardLabels = new ArrayList<>();
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
    boolean _gameActive = false;


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
                playerAction.put(_keyController.getActivePlayer(), action);
                //handel last player
                if (playersInDecision.size() > 1){
                    //set active player to the next
                    _keyController.setActivePlayer(playersInDecision.get(1));
                }
                playersInDecision.remove(0);
            }

            // action
            // When everyone is done with action
            if (playersInDecision.size() == 0){
                //  detect end of game = gameAlive
                startNextTurn();
                if(!roundAlive) {
                    //  detect if end of round -> setup new round = roundAlive
                    endRound();
                    //Disable player turn input and wait for confirm
                    _keyController.set_waitForConfirm(true);
                    _keyController.set_playerInputActive(false);
                }
                if (roundAlive){
                    _keyController.setActivePlayer(playersInTemple.get(0));
                }
            }
            if (roundAlive){
                String  msg = _keyController.getActivePlayer().getName() + " turn, Press [S]tay [G]o back to tent";
                System.out.println( msg);
                turnTraker.setText(msg);
            }
        }

        @Override
        public void confirmNextRound(){
            if (!gameAlive) {
                endGame();
            }else {
                startRound();
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

        //  Start round 1
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        // init deck
        this.deck = new Deck();
        this.deck.shuffle();

        // init round counter
        this.currentRound = Game.Round.One;
        frame.addKeyListener(_keyController);

        // Setup first turn
        startRound();
        showDeck();
        _keyController.set_playerInputActive(true);
    }

    private void startRound() {
        System.out.println("Start new round");
        roundAlive =true;
        // Put all the player back to Temple
        playersInTemple.clear();
        playersInDecision.clear();
        playersInTemple.addAll(this.playersList);
        playersInDecision.addAll(this.playersList);

        //reset action card icon
        for (Player p : playersInTemple) {
            updatePlayerActionCard(p);
        }

        _keyController.setActivePlayer(playersInDecision.get(0));
        // Prompt Player 1 action
        String  msg = _keyController.getActivePlayer().getName() + " turn Press [S]tay, [G]o back to tent";
        System.out.println(msg);
        turnTraker.setText(msg);

        // Push new artifact card in the deck
        this.deck.push(makeArtifact(currentRound));

        // Shuffle
        this.deck.shuffle();

        showRound();
        updateDeckCount(deck.getCards().size());

        // Deal Card first card
        dealQuestCard();
        showQuestCard();
        if (this.adventure.readLastCard() instanceof TreasureCard) {
            // Distribute treasure
            distributeTreasure();
        }
//        // Start turn 1
//        while (roundAlive) {
//            roundAlive = startNextTurn();
//        }
        //endRound();
    }

    private  void startNextTurn() {

//        // Player decide action
//        for (Player p : this.playersInTemple) {
//            System.out.print(p.getName() + "\n");
//            this.playerAction.put(p, promptChoices());
//        }

        // Show action
        showPlayerAction();

        //goBackToTent
        goBackToTent();

        // Put all the player back to decision
        System.out.print("Player still in temple: ");
        for (Player p : playersInTemple){
            System.out.print(p.getName()+" ");
        }
        System.out.println();
        this.playersInDecision.addAll(this.playersInTemple);

        //End false if no one in temple
        if (playersInTemple.size() == 0) {
            roundAlive = false;
            String  msg = "All explore head back to tent.";
            System.out.println( msg);
            turnTraker.setText(msg);
        }

        if (roundAlive){
            // Deal Card for next turn
            dealQuestCard();
            showQuestCard();
            updateDeckCount(deck.getCards().size());
            if (this.adventure.readLastCard() instanceof TreasureCard) {
                // Distribute treasure
                distributeTreasure();
            }
        }

        // Handel first card is artifact
        if (roundAlive && adventure.getCards().size() > 1){
            // if last card is harzard
            if (this.adventure.readLastCard() instanceof HazardCard) {
                // Check Hazard
                System.out.print("Boom");
                roundAlive = !hasTwoHazards();
            }
        }
    }

    private void endRound() {
        String  msg = "Round ended. Press Enter to start next round";
        System.out.println( msg);
        turnTraker.setText(msg);

        System.out.println("End of Round");


        if (hasTwoHazards()){
            //Remove the hazard from deck
            ArrayList<Card> deckCopy = (ArrayList<Card>) deck.getCards().clone();
            for(Card c: deckCopy){
                if (c.getName().equals(adventure.readLastCard().getName())){
                    deck.remove(c);
                }
            }
            //Remove the hazard from adventure
            ArrayList<Card> adventureCopy = (ArrayList<Card>) adventure.getCards().clone();
            for(Card c: adventureCopy){
                if (c.getName().equals(adventure.readLastCard().getName())){
                    adventure.remove(c);
                }
            }
        }

        // push quest cards back to deck
        while (adventure.getCards().size() > 0){
            deck.push(adventure.pop());
        }
        updateDeckCount(deck.getCards().size());

        // reset all player hand score to 0
        for (Player p : this.playersList) {
            p.setHandScore(0);
            updatePlayerHandScore(p);
        }
        // reset adventure score to 0
        adventure.setTreasureOnTheRoad(0);

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
        String  msg = "Game Ended";
        System.out.println( msg);
        turnTraker.setText(msg);
        List<Player> playerRank = new ArrayList<>();
        playerRank.addAll(playersList);
        Collections.sort(playerRank);
        msg =  "The winner is " + playerRank.get(0).getName() + ", with a score of "+ playerRank.get(0).getTentScore();
        System.out.println( msg);
        noticeBar.setText(msg);

        showScoreBoard();
    }

    private ArtifactCard makeArtifact(Game.Round r) {
        switch (r) {
            case One:
            case Two:
            case Three:
                return new ArtifactCard("Artifact", "/img/artifact1.PNG", 5);

            case Four:
            case Five:
                return new ArtifactCard("Artifact", "/img/artifact2.PNG", 10);

            default:
                return new ArtifactCard("Artifact", "/img/artifact1.PNG", 5);
        }
    }

    private void dealQuestCard() {
        this.adventure.push(this.deck.pop());
        noticeBar.setText("You found: " + adventure.getCards().get(adventure.getCards().size()-1).getName());
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
        System.out.println("In distrubuteTreasure");
        TreasureCard lastCard = (TreasureCard) this.adventure.readLastCard();

        //update notice bar
        noticeBar.setText( noticeBar.getText() + ". Explorers still in the temple obtain "+ lastCard.getTreasureValue()/ this.playersInTemple.size() + "  gem(s) each.");

        for (Player p : this.playersInTemple) {
            // divide treasure on the road
            p.addHandScore(lastCard.getTreasureValue() / this.playersInTemple.size());
            updatePlayerHandScore(p);
        }

        // Add remainder treasure to the road
        this.adventure.addTreasureOnTheRoad(lastCard.getTreasureValue() % this.playersInTemple.size());
        updateTreasureOnTheRoad();
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
                updateTentScore(p);

                // Reset hand score and remove from adventure
                p.setHandScore(0);
                this.playersInTemple.remove(p);
                updatePlayerActionCard(p);
                updatePlayerHandScore(p);
            }
        }
        // Treasure left on the road is the remainder
        if (goBackCount != 0){
            this.adventure.setTreasureOnTheRoad(this.adventure.getTreasureOnTheRoad() % goBackCount);
        }
        updateTreasureOnTheRoad();
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
                if (artifactCount < 4) {
                    p.addTentScore(5);
                    System.out.println(" " + p.getName() + " got 5 point from artifact");
                } else {
                    p.addTentScore(10);
                    System.out.println(" " + p.getName() + " got 10 point from artifact");
                }
                // Add artifacts to player hand
                p.addArtifacts(c);
                artifactCount ++;
            }
            //update notice bar
            noticeBar.setText( noticeBar.getText() + p.getName() + "got " + tempCardList.size() + "artifact back to tent");
            updateTentScore(p);
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
                        noticeBar.setText("The explorers saw two " + tempCardList.get(i).getName() + "They ran away from the temple. (These type of hazard will be removed from the deck)");
                        noticeBar.revalidate();
                        noticeBar.repaint();
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
            System.out.print("Adventure: " + c.getName());
            if (c instanceof TreasureCard){
                System.out.print(((TreasureCard) c).getTreasureValue());
            }
            System.out.println();
        }
        updateQuestArea();
    }

    void initFrame() {
        frame = new JFrame("Game");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(1920, 1080) );
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
        } catch (IOException ex) {
            // handle exception...
            System.out.print("Error loading deck image");
        }
    }

    void updateDeckCount(int count) {
        deckCount.setText("Deck size:  " + count);
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

    private void updateTentScore(Player p) {
        System.out.println("In updata hand score");
        JLabel tent = player1Tent;
        switch (p.getName()){
            case "Player 1":
                tent = player1Tent;
                break;
            case "Player 2":
                tent = player2Tent;
                break;
            case "Player 3":
                tent = player3Tent;
                break;
            case "Player 4":
                tent = player4Tent;
                break;
        }
        tent.setText("Gems in tent :" + p.getTentScore());
        tent.revalidate();
        tent.repaint();
    }

    private void updatePlayerHandScore(Player p) {
        System.out.println("In updata hand score");
        JLabel hand = player1Hand;
        switch (p.getName()){
            case "Player 1":
                hand = player1Hand;
                break;
            case "Player 2":
                hand = player2Hand;
                break;
            case "Player 3":
                hand = player3Hand;
                break;
            case "Player 4":
                hand = player4Hand;
                break;
        }
        hand.setText("Gems on hand :" + p.getHandScore());
        hand.revalidate();
        hand.repaint();
    }

    private void updatePlayerActionCard(Player p) {
        System.out.println("In updata tent icon");
        JLabel actionCard = player1ActionCard;
        switch (p.getName()){
            case "Player 1":
                actionCard = player1ActionCard;
                break;
            case "Player 2":
                actionCard = player2ActionCard;
                break;
            case "Player 3":
                actionCard = player3ActionCard;
                break;
            case "Player 4":
                actionCard = player4ActionCard;
                break;
        }
        try {
            String imgPath;
            if (playersInTemple.contains(p)){
                imgPath = "/img/actionCardBack.PNG";
            }else {
                imgPath = "/img/actionCardGoTent.PNG";
            }
            BufferedImage img = ImageIO.read(this.getClass().getResource(imgPath));
            actionCard.setIcon(new ImageIcon(img));
            actionCard.revalidate();
            actionCard.repaint();
        } catch (IOException ex) {
            System.out.print("Error loading actionCardBack image");
        }

    }

    private void updateTreasureOnTheRoad() {
        String msg = "Treasure on the road :" + adventure.getTreasureOnTheRoad();
        System.out.println(msg);
        treasureOnTheRoadLabel.setText(msg);
        questArea.revalidate();
        questArea.repaint();
    }

    void updateQuestArea() {
        System.out.println("Updating Quest Area");

        for (Component c : questArea.getComponents()) {
            questArea.remove(c);
            questArea.revalidate();
            questArea.repaint();
        }

        // Create Cards label array from adventure
        for (Card c : adventure.getCards()) {
            try {
                BufferedImage img = ImageIO.read(this.getClass().getResource(c.getImagePath()));
                JLabel L =  new JLabel(new ImageIcon(img));
                L.setHorizontalAlignment(JLabel.LEFT);
                qCardLabels.add(L);
            } catch (IOException ex) {
                System.out.print("Error loading round image");
            }
        }

        // Show cards
        for (JLabel label : qCardLabels) {
            questArea.add(label);
            questArea.revalidate();
            questArea.repaint();
        }
        // Clear label array
        qCardLabels.clear();
    }
    // endregion

}
