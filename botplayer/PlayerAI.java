import com.orbischallenge.game.enums.*;

import java.util.*;
import java.awt.Point;

public class PlayerAI extends ClientAI {
    private AStar pathfinder = new AStar();
    private Queue<Move> moveQueue = new ArrayDeque<>();

	public PlayerAI() {
		//Write your initialization here

        moveQueue.add(Move.FACE_DOWN);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.FORWARD);
        moveQueue.add(Move.LASER);

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
