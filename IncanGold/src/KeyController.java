import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ho on 11/24/2017.
 */
public abstract class KeyController implements KeyListener, GameEvent {



    private Player _activePlayer = null;
    private boolean _controllerActive = false;

    public boolean isControllerActive() {
        return _controllerActive;
    }

    public void setControllerActive(boolean _controllerActive) {
        this._controllerActive = _controllerActive;
    }

    public void setActivePlayer(Player player) {
        _activePlayer = player;
    }

    public Player getActivePlayer() {
        return _activePlayer;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (_activePlayer == null)
            return;
        switch (e.getKeyChar()) {
            case 'g' | 'G':
                triggerAction(Game.Action.Go_Back);

                break;
            case 's' | 'S':
                triggerAction(Game.Action.Stay);
                break;
        }
    }
}
