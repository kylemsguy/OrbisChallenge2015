import java.awt.Point;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class PlayerAI extends ClientAI {
    private AStar pathfinder;
    private ArrayDeque<Move> moveQueue = new ArrayDeque<>();
    private AStar.Heuristic distanceManager = new AStar.ManhattenDistance();

	public PlayerAI() {
		//Write your initialization here
        pathfinder = new AStar(distanceManager, moveQueue);
        moveQueue.add(Move.FACE_DOWN);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);

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


    public void checkForOpponent(Player player, Opponent opponent) {
        if (opponent.getLaserCount() > 0 && inLineOfSight(player.getX(), player.getY(), opponent.getX(), opponent.getY())) {
            if (player.getShieldCount() > 0) {
                moveQueue.add(Move.SHIELD);

            } else if (player.getTeleportCount() > 0 && player.getShieldCount() == 0) {
                moveQueue.add(Move.TELEPORT_0);
            } else {
                if (inRange(opponent.getY(), player.getY())) {
                    moveQueue.add(Move.SHOOT);
                } else {
                    if (inLineOfSight(player.getX(), player.getY(), opponent.getX(), opponent.getY())) {
                        moveQueue.add(Move.FACE_DOWN);
                        moveQueue.add(Move.FORWARD);
                    }
                }
            }
        }
    }

    public Move chooseRandomDirection(int maximum, int minimum) {
        Random rn = new Random();
        int directionNum = rn.nextInt(maximum - minimum + 1) + minimum;

        return Move.NONE;
    }

    public boolean turretsOutOfRange(Gameboard gameboard, Player player) {
        ArrayList<Turret> turrets = gameboard.getTurrets();
        int Xcoordinate = player.getX();
        int Ycoordinate = player.getY();
        boolean outOfRange = true;
        for (int i = 0; i < turrets.size(); i++) {
            if (Math.abs(turrets.get(i).x - Xcoordinate) < 5 || Math.abs(turrets.get(i).y - Ycoordinate) < 5) {
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

    public boolean inLineOfSight(int playerX, int playerY, int opponentX, int opponentY) {
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
        List<Point> TeleportLocations = gameboard.getTeleportLocations();
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
        return false;  // TODO implement
    }

	@Override
	public Move getMove(Gameboard gameboard, Opponent opponent, Player player) throws NoItemException, MapOutOfBoundsException {
        if(moveQueue.isEmpty()){
            pathfinder.setupMap(gameboard);
            pathfinder.updateTurrets(gameboard);
            // find closest powerup
            List<PowerUp> powerUps = gameboard.getPowerUps();

            Point currentMinPosition = null;
            int currentMinDistance = Integer.MAX_VALUE;

            Point playerPosition = new Point(player.x, player.y);

            for(PowerUp powerUp : powerUps){
                Point powerUpPosition = new Point(powerUp.x, powerUp.y);
                if(currentMinPosition == null || distanceManager.getDistance(playerPosition, powerUpPosition,
                        gameboard.getWidth(), gameboard.getHeight()) < currentMinDistance){
                    currentMinPosition = powerUpPosition;
                    currentMinDistance = distanceManager.getDistance(playerPosition, currentMinPosition,
                            gameboard.getWidth(), gameboard.getHeight());
                }
            }

            pathfinder.findPath(playerPosition, currentMinPosition, gameboard, player.getDirection());
        }

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
