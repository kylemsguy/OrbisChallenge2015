import com.orbischallenge.game.enums.*;

import java.util.*;
import java.awt.Point;
import java.util.Random;

pathfinder.getmap(); /* .get(1).get(2) gets the point (1, 2)) */

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
    * as TELEPORT_0, but what if it's more beneficial to go to another teleport location?
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

    public Move chooseRandomDiretion(int maximum, int minimum) {
        Random rn = new Random();
        int directionNum = random.nextInt(maximum - minimum + 1) + minimum;
    }

    public boolean turretsOutOfRange() {
        ArrayList turrets = gameboard.getTurrets();
        int Xcoordinate = player.getX();
        int Ycoordinate = player.getY();
        int i;
        boolean outOfRange = true;
        for (i = 0; i < turrets.size(); i++) {
            if (Math.abs(turrets[i].x - Xcoordinate < 5 || Math.abs(turrets[i].y - Ycoordinate < 5) {
                outOfRange = false;
                return outOfRange;
            }
        }

        return outOfRange;

    }

    public ArrayList checkForTurrets() {
        int Xcoordinate = player.getX();
        int Ycoordinate = player.getY();
        ArrayList<String> directions = new ArrayList<String>(Arrays.asList("left", "right", "up", "down"));

        if (isTurretAtTile(Xcoordinate + 1, Ycoordinate + 1)) {
            directions.remove("right");
            directions.remove("up");
        }

        if (isTurretAtTile(Xcoordinate -1, Ycoordinate + 1)) {
            if (directions.contains("up")) {
                directions.remove("up");
            }
            directions.remove("left");
        }

        if (isTurretAtTile(Xcoordinate + 1, Ycoordinate - 1)) {
            if (directions.contains("right")) {
                directions.remove("right");
            }
            directions.remove("down");
        }

        if (isTurretAtTile(Xcoordinate - 1, Ycoordinate - 1)) {
            if (directions.contains("left")) {
                directions.remove("left");
            }

            if (directions.contains("down")) {
                directions.remove("down");
            }
        }

        return directions;
    }

    public boolean inLineOfSight(int opponentY, int playerY, int opponentX, int playerX) {
        if (opponentX == playerX || opponentY == playerY) {
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

/* getNumberofTeleportLocations is actually already done in the gameboard class */
    public int getNumberOfTeleportLocations() {
        ArrayList TeleportLocations = gameboard.getTeleportLocations();
        int i;
        int numLocations;
        for (int i = 0; i < TeleportLocations.size(); i++) {
            numLocations += 1;
        }

        return numLocations;
    }

    public boolean checkForDiagonalTurrets(int playerXCoordinate, int playerYCoordinate) {
        if (isTurretAtTile(playerXCoordinate + 1, playerYCoordinate + 1)) {
            if (getTurretAtTile(playerXCoordinate + 1, playerYCoordinate + 1).isFiringNextTurn() == false) {

            }

        }

        if (isTurretAtTile(playerXCoordinate + 1, playerYCoordinate - 1)) {
            if (getTurretAtTile(playerXCoordinate + 1, playerYCoordinate - 1).isFiringNextTurn() == false) {

            }

        }

        if (isTurretAtTile(playerXCoordinate - 1, playerYCoordinate + 1)) {
            if (getTurretAtTile(playerXCoordinate - 1, playerYCoordinate + 1).isFiringNextTurn() == false) {

            }

        }

        if (isTurretAtTile(playerXCoordinate - 1, playerYCoordinate - 1)) {
            if (getTurretAtTile(playerXCoordinate - 1, playerYCoordinate - 1).isFiringNextTurn() == false) {

            }

        }
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
