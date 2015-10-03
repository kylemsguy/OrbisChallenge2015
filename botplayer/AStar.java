import com.orbischallenge.engine.gameboard.*;
import com.orbischallenge.engine.gameboard.Gameboard;
import com.orbischallenge.game.enums.*;
import com.orbischallenge.game.enums.Move;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStar {
    private Queue<Move> moveQueue;
    private Heuristic heuristic;

    public AStar(Heuristic heuristic){
        this.heuristic = heuristic;
        moveQueue = new ArrayDeque<>();
    }

    public AStar(Heuristic heuristic, Queue<Move> moveQueue){
        this.heuristic = heuristic;
        this.moveQueue = moveQueue;
    }

    /**
     * Uses A* to find a path from A to B. Ignores bullets, players and lasers.
     *
     * @param start
     * @param end
     * @param gameboard
     */
    public void findPath(Point start, Point end, Gameboard gameboard){
        Node startNode = new Node(start.x, start.y, false, 0, false, true, false);
        
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        List<Node> closedSet = new ArrayList<>();

        openSet.add(startNode);
    }

    private List<Move> getMoves(){
        return null;
    }

    private static class Node implements Comparable<Node>{
        private Node parent;
        private List<Node> neighbours;
        private boolean visited;
        private float distanceFromStart;
        private float heuristicDistanceFromGoal;
        private int x;
        private int y;
        private boolean isObstacle;
        private boolean isStart;
        private boolean isGoal;

        private Node north;
        private Node south;
        private Node east;
        private Node west;

        public Node(int x, int y){
            neighbours = new ArrayList<>();
            this.x = x;
            this.y = y;
            visited = false;
            distanceFromStart = Integer.MAX_VALUE;
            isObstacle = false;
            isStart = false;
            isGoal = false;
        }

        public Node(int x, int y, boolean visited, int distanceFromStart, boolean isObstacle, boolean isStart, boolean isGoal) {
            neighbours = new ArrayList<>();
            this.x = x;
            this.y = y;
            this.visited = visited;
            this.distanceFromStart = distanceFromStart;
            this.isObstacle = isObstacle;
            this.isStart = isStart;
            this.isGoal = isGoal;
        }

        public int compareTo(Node other){
            float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
            float otherTotalDistanceFromGoal = other.getHeuristicDistanceFromGoal() + other.getDistanceFromStart();

            if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
                return -1;
            } else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
                return 1;
            } else {
                return 0;
            }
        }

        public Node getNorth() {
            return north;
        }

        public void setNorth(Node north) {
            // replace old node in the neighbours list
            if(neighbours.contains(north))
                neighbours.remove(north);
            neighbours.add(north);

            this.north = north;
        }

        public Node getSouth() {
            return south;
        }

        public void setSouth(Node south) {
            // replace old node in the neighbours list
            if(neighbours.contains(south))
                neighbours.remove(south);
            neighbours.add(south);

            this.south = south;
        }

        public Node getEast() {
            return east;
        }

        public void setEast(Node east) {
            // replace old node in the neighbours list
            if(neighbours.contains(east))
                neighbours.remove(east);
            neighbours.add(east);

            this.east = east;
        }

        public Node getWest() {
            return west;
        }

        public void setWest(Node west) {
            // replace old node in the neighbours list
            if(neighbours.contains(west))
                neighbours.remove(west);
            neighbours.add(west);

            this.west = west;
        }

        /**
         *  Getters and Setters
         */

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getNeighbours() {
            return neighbours;
        }

        public void setNeighbours(List<Node> neighbours) {
            this.neighbours = neighbours;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public float getDistanceFromStart() {
            return distanceFromStart;
        }

        public void setDistanceFromStart(float distanceFromStart) {
            this.distanceFromStart = distanceFromStart;
        }

        public float getHeuristicDistanceFromGoal() {
            return heuristicDistanceFromGoal;
        }

        public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
            this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
        }

        public int getX(){
            return x;
        }

        public void setX(int x){
            this.x = x;
        }

        public int getY(){
            return y;
        }

        public void setY(int y){
            this.y = y;
        }

        public void setCoords(Point point){
            x = point.x;
            y = point.y;
        }

        public Point getCoords(){
            return new Point(x, y);
        }

        public boolean isObstacle() {
            return isObstacle;
        }

        public void setIsObstacle(boolean isObstacle) {
            this.isObstacle = isObstacle;
        }

        public boolean isStart() {
            return isStart;
        }

        public void setIsStart(boolean isStart) {
            this.isStart = isStart;
        }

        public boolean isGoal() {
            return isGoal;
        }

        public void setIsGoal(boolean isGoal) {
            this.isGoal = isGoal;
        }


    }

    public interface Heuristic{
        int getDistance(Point start, Point end, int width, int height);
    }

	public static class ManhattenDistance implements Heuristic{
        /**
         * Returns the shortest manhatten distance from start to end on the game board.
         *
         * @param start Start position
         * @param end Target position
         * @param board The game board (torus)
         * @return shortest manhatten distance between points
         */
		public int getDistance(Point start, Point end, int width, int height){
            // calculate normal manhatten distance
            int dx_normal = Math.abs(start.x - end.x);
            int dy_normal = Math.abs(start.y - end.y);

            // calculate wrap around manhatten distances in x and y
            int dx_wrap = width - 1 - dx_normal;
            int dy_wrap = height - 1 - dy_normal;

            return Math.min(dx_normal, dx_wrap) + min(dy_normal, dy_wrap);
		}
	}
}