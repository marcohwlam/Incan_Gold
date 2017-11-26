import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Ho on 11/24/2017.
 */
public abstract class KeyController implements KeyListener, GameEvent {

    private boolean _waitForConfirm = false;
    private Player _activePlayer = null;
    private boolean _playerInputActive = false;

    public boolean is_waitForConfirm() {
        return _waitForConfirm;
    }

    public void set_waitForConfirm(boolean _waitForConfirm) {
        this._waitForConfirm = _waitForConfirm;
    }

    public boolean isControllerActive() {
        return _playerInputActive;
    }

    public void setControllerActive(boolean _controllerActive) {
        this._playerInputActive = _controllerActive;
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
        if (_waitForConfirm){
            if (e.getKeyChar() ==KeyEvent.VK_ENTER){
                confirmNextRound();
            }
        }

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
