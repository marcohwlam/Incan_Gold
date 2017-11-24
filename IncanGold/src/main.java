import javax.swing.*;
import java.awt.*;

/**
 * Created by Ho on 10/23/2017.
 */
public class main {
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    Thread.sleep(300);
                }catch (InterruptedException ex){

                }
                GameModel G = new GameModel();
                //GameView GV = new GameView();
                //GV.showFrame();
            }
        });
    }
}
