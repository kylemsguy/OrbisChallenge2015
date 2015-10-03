import com.orbischallenge.game.enums.*;

import java.util.*;
import java.awt.Point;

public class PlayerAI extends ClientAI {
    //private AStar pathfinder = new AStar();
    private ArrayDeque<Move> moveQueue = new ArrayDeque<>();

	public PlayerAI() {
		//Write your initialization here

        moveQueue.add(Move.FACE_DOWN);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.LASER);

	}
        /* Check if the opponent is in our line of sight and has any lasers. If these conditions
     * are true, shield. If we don't have any shields but we have teleport power ups, teleport
     * away. If we have neither shields nor teleports, if they're in range shoot the opponent.
     * Otherwise, try to move away.*/

    /* Problems:
    * 1. How do we randomize which teleport location we go to? Right now I put the default
    * as TELEPORT_0, but what if it's more beneficial to go to anothe teleport location?
    * 2. If we have no power ups and we're trying to escape, how do we determine if we need to face another direction
    * before moving, or if we can simply move forward.  I set that as Move.FACE_DOWN for now. */

    public void checkForOpponent() {
        if (opponent.laserCount() > 0 && inLineOfSight(opponent.getX(), player.getX())) {
            if (player.getShieldCount() > 0) {
                moveQueue.add(Move.SHIELD);

            } else if (player.getTeleportCount() > 0 && player.getShieldCount() == 0) {
                moveQueue.add(Move.TELEPORT_0);
            } else {
                if (inRange(opponent.getY(), player.getY())) {
                    moveQueue.add(Move.SHOOT);
                } else {
                    if (inLineOfSight(opponent.getY, player.getY)) {
                        moveQueue.add(Move.FACE_DOWN);
                        moveQueue.add(Move.FORWARD);
                    }
                }
            }
        }
    }
/* Helper Functions */

    public boolean inLineOfSight(int opponentY, int playerY) {
        if (opponentX == playerX) {
            return true;
        }
        return false;
    }

    public boolean inRange(int opponentX, int playerX) {
        if (Math.abs(opponentX - playerX) < 5) {
            return true;
        }
        return false;
    }

    public int getNumberOfTeleportLocations() {
        ArrayList TeleportLocations = gameboard.getTeleportLocations();
        int i;
        int numLocations;
        for (int i = 0; i < TeleportLocations.size(); i++) {
            numLocations += 1;
        }

        return numLocations;
    }

	@Override
	public Move getMove(Gameboard gameboard, Opponent opponent, Player player) throws NoItemException, MapOutOfBoundsException {

        // Execute queue
        Move nextMove;
        try{
            nextMove = moveQueue.remove();
        } catch(NoSuchElementException e){
            nextMove = Move.NONE;
            System.err.println("Empty Queue");
        }
        System.out.println(nextMove);
		//Write your AI here
		return nextMove;
	}
}
