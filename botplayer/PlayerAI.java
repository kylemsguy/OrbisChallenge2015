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

    private Move previousMove;
    private boolean killTurretMode;

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


    public Move checkForOpponent(Player player, Opponent opponent, Gameboard gameboard, Move oldMove) {
        Move move = oldMove;
        if (opponent.getLaserCount() > 0 && inLineOfSight(player.getX(), player.getY(), opponent.getX(), opponent.getY())) {
            if (player.getShieldCount() > 0) {
                move = Move.SHIELD;

            } else if (player.getTeleportCount() > 0 && player.getShieldCount() == 0) {
                Move nextMove = Move.FORWARD;
                moveQueue.clear();
                Random random = new Random();
                int numTeleports = gameboard.numberOfTeleportLocations();
                int teleportLocation = random.nextInt(numTeleports);

                switch (teleportLocation){
                    case 0:
                        nextMove = Move.TELEPORT_0;
                        break;
                    case 1:
                        nextMove = Move.TELEPORT_1;
                        break;
                    case 2:
                        nextMove = Move.TELEPORT_2;
                        break;
                    case 3:
                        nextMove = Move.TELEPORT_3;
                        break;
                    case 4:
                        nextMove = Move.TELEPORT_4;
                        break;
                    case 5:
                        nextMove = Move.TELEPORT_5;
                        break;
                }
                move = nextMove;
            } else {
                if (inRange(opponent.getY(), player.getY())) {
                    move = Move.SHOOT;
                }
            }
        }
        return move;
    }

    public Move chooseRandomDirection() {
        Random rn = new Random();
        int directionNum = rn.nextInt(4);
        if (directionNum == 0) {
            return Move.FACE_UP;
        }
        if (directionNum == 1) {
            return Move.FACE_DOWN;
        }
        if (directionNum == 2) {
            return Move.FACE_LEFT;
        }
        if (directionNum == 3) {
            return Move.FACE_RIGHT;
        }
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

    public Point getPositionFromMove(Point start, Move move, Direction facing){
        AStar.GameGrid grid = pathfinder.getMap();
        Point next = start;
        AStar.Node node = null;
        switch(move){
            case FORWARD: {
                switch (facing){
                    case UP:
                        node = grid.at(start.x, start.y-1);
                        if(!node.isObstacle())
                            next = node.getCoords();
                        break;
                    case DOWN:
                        node = grid.at(start.x, start.y+1);
                        if(!node.isObstacle())
                            next = node.getCoords();
                        break;
                    case LEFT:
                        node = grid.at(start.x-1, start.y);
                        if(!node.isObstacle())
                            next = node.getCoords();
                        break;
                    case RIGHT:
                        node = grid.at(start.x+1, start.y);
                        if(!node.isObstacle())
                            next = node.getCoords();
                        break;
                }
            }
            break;
        }
        return next;
    }

    private void killTurret(){

    }

	@Override
	public Move getMove(Gameboard gameboard, Opponent opponent, Player player) throws NoItemException, MapOutOfBoundsException {
        pathfinder.setupMap(gameboard);
        if(moveQueue.isEmpty()){
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

        // check for opponent
        nextMove = checkForOpponent(player, opponent, gameboard, nextMove);

        // process move
        Point nextPosition = getPositionFromMove(new Point(player.x, player.y), nextMove, player.getDirection());
        if(pathfinder.isDangerous(nextPosition, gameboard) == 1){
            Move peekMove = moveQueue.peek();
            if(peekMove != Move.FORWARD){
                // DANGER DANGER YOU WILL DIE WAIT A TURN
                if(nextMove == Move.FORWARD){
                    moveQueue.addFirst(nextMove);
                    nextMove = Move.NONE;
                }
                else {
                    // take evasive action before you die
                    if(player.getShieldCount() > 0){
                        moveQueue.addFirst(nextMove);
                        nextMove = Move.SHIELD;
                    }
                    else if(player.getTeleportCount() > 0){
                        moveQueue.clear();
                        Random random = new Random();
                        int numTeleports = gameboard.numberOfTeleportLocations();
                        int teleportLocation = random.nextInt(numTeleports);

                        switch (teleportLocation){
                            case 0:
                                nextMove = Move.TELEPORT_0;
                                break;
                            case 1:
                                nextMove = Move.TELEPORT_1;
                                break;
                            case 2:
                                nextMove = Move.TELEPORT_2;
                                break;
                            case 3:
                                nextMove = Move.TELEPORT_3;
                                break;
                            case 4:
                                nextMove = Move.TELEPORT_4;
                                break;
                            case 5:
                                nextMove = Move.TELEPORT_5;
                                break;
                        }
                    }
                    else{
                        moveQueue.clear();
                        // find a free space and turn in that direction and push forward on front of queue
                        Direction direction = player.getDirection();
                        nextMove = null;
                        int dx = player.x + (direction == Direction.LEFT ? -1 : 0) +
                                (direction == Direction.RIGHT ? 1 : 0);
                        int dy = player.y + (direction == Direction.UP ? -1 : 0) +
                                (direction == Direction.LEFT ? 1 : 0);
                        AStar.Node node = pathfinder.getMap().at(dx, dy);
                        if (!pathfinder.isObstacle(node.getCoords())) {
                            nextMove = Move.FORWARD;
                        } else {
                            // turn in a random direction
                            //nextMove = chooseRandomDirection();
                            nextMove = Move.SHOOT;
                        }
                    }
                }
            }
        }

        System.out.println(nextMove);
		//Write your AI here
		return nextMove;
	}

    /*
        1. If no queue, pathfind to nearest powerup
        2. If queue, dequeue next step, determine if dangerous
        2a. if danger == 1, peek at next move. If == Move.FORWARD, safe. Otherwise, see 3
        3. If dangerous, take evasive action (shield, teleport, move, nothing)
        4. If we don't move for several frames due to turret, kill turret.
     */
}
