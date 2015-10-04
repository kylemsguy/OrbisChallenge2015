import com.orbischallenge.engine.gameboard.*;
import com.orbischallenge.game.client.gameObjects.*;
import com.orbischallenge.game.enums.*;
import com.orbischallenge.game.enums.Direction;

import java.util.*;
import java.awt.Point;
import java.util.Random;

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
        boolean outOfRange = true;
        for (int i = 0; i < turrets.size(); i++) {
            if (Math.abs(turrets.get(i).x - Xcoordinate < 5 || Math.abs(turrets.get(i).y - Ycoordinate < 5))) {
                outOfRange = false;
                return outOfRange;
            }
        }

        return outOfRange;

    }

    public ArrayList<Direction> checkForTurrets(Player player, Gameboard gameboard) {
        int Xcoordinate = player.getX();
        int Ycoordinate = player.getY();
        ArrayList<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.LEFT);
        directions.add(Direction.DOWN);
        directions.add(Direction.RIGHT);
        checkForDiagonalTurrets(directions, player, gameboard);

        return directions;

    }

    public void checkForDiagonalTurrets(ArrayList<Direction> directions, Player player, Gameboard gameboard) {

        int Xcoordinate = player.getX();
        int Ycoordinate = player.getY();

        try {
            if (gameboard.isTurretAtTile(Xcoordinate + 1, Ycoordinate + 1)) {
                directions.remove(Direction.RIGHT);
                directions.remove(Direction.UP);
            }

            if (gameboard.isTurretAtTile(Xcoordinate - 1, Ycoordinate + 1)) {
                if (directions.contains(Direction.UP)) {
                    directions.remove(Direction.UP);
                }
                directions.remove(Direction.LEFT);
            }

            if (gameboard.isTurretAtTile(Xcoordinate + 1, Ycoordinate - 1)) {
                if (directions.contains(Direction.RIGHT)) {
                    directions.remove(Direction.RIGHT);
                }
                directions.remove(Direction.DOWN);
            }

            if (gameboard.isTurretAtTile(Xcoordinate - 1, Ycoordinate - 1)) {
                if (directions.contains(Direction.LEFT)) {
                    directions.remove(Direction.LEFT);
                }

                if (directions.contains(Direction.DOWN)) {
                    directions.remove(Direction.DOWN);
                }
            }
        } catch (MapOutOfBoundsException e){
            // SHOULD NEVER HAPPEN OAO
            System.err.println("checkForDiagonalTurrets: MapOutOfBoundsException for no good reason");
        }
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
    public int getNumberOfTeleportLocations(Gameboard gameboard) {
        ArrayList TeleportLocations = gameboard.getTeleportLocations();
        int numLocations = 0;
        for (int i = 0; i < TeleportLocations.size(); i++) {
            numLocations += 1;
        }

        return numLocations;
    }

    public boolean checkForDiagonalTurrets(int playerXCoordinate, int playerYCoordinate, Gameboard gameboard) {
        try {
            if (gameboard.isTurretAtTile(playerXCoordinate + 1, playerYCoordinate + 1)) {
                if (gameboard.getTurretAtTile(playerXCoordinate + 1, playerYCoordinate + 1).isFiringNextTurn() == false) {

                }

            }

            if (gameboard.isTurretAtTile(playerXCoordinate + 1, playerYCoordinate - 1)) {
                if (gameboard.getTurretAtTile(playerXCoordinate + 1, playerYCoordinate - 1).isFiringNextTurn() == false) {

                }

            }

            if (gameboard.isTurretAtTile(playerXCoordinate - 1, playerYCoordinate + 1)) {
                if (gameboard.getTurretAtTile(playerXCoordinate - 1, playerYCoordinate + 1).isFiringNextTurn() == false) {

                }

            }

            if (gameboard.isTurretAtTile(playerXCoordinate - 1, playerYCoordinate - 1)) {
                if (gameboard.getTurretAtTile(playerXCoordinate - 1, playerYCoordinate - 1).isFiringNextTurn() == false) {

                }

            }
        } catch(NoItemException e){
            System.err.println("checkForDiagonalTurrets: NoItemException even though we checked O_O");
        } catch(MapOutOfBoundsException e){
            System.err.println("checkForDiagonalTurrets: MapOutOfBoundsException for no good reason");
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
