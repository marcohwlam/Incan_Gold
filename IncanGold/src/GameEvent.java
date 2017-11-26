/**
 * Created by Ho on 11/24/2017.
 */
public interface GameEvent {

    public void triggerAction(Game.Action action);

    public void confirmNextRound();
}
