import com.orbischallenge.engine.gameboard.*;
import com.orbischallenge.game.enums.*;
import com.orbischallenge.game.enums.Direction;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStar {
    private Queue<Move> moveQueue;
    private Heuristic heuristic;
    private List<List<Node>> map;
    private List<Point> obstacles;

    private PriorityQueue<Node> openSet = new PriorityQueue<>();
    private List<Node> closedSet = new ArrayList<>();
    private Direction currentDirection;

    public AStar(Heuristic heuristic, Queue<Move> moveQueue){
        this.heuristic = heuristic;
        this.moveQueue = moveQueue;
    }

    public void setupMap(Gameboard gameboard){
        setupObstacles(gameboard);
        map = new ArrayList<>();

        for(int i = 0; i < gameboard.getWidth(); i++){
            List<Node> columns = new ArrayList<>();
            for(int ii = 0; i < gameboard.getHeight(); i++){
                columns.add(new Node(i, ii, false, 0, isObstacle(new Point(i, ii)), false, false));
            }
        }
    }

    private void setupObstacles(Gameboard gameboard){
        obstacles.clear();
        List<Turret> turrets = gameboard.getTurrets();
        List<Wall> walls = gameboard.getWalls();

        for(Turret turret : turrets){
            obstacles.add(new Point(turret.x, turret.y));
        }

        for(Wall wall : walls){
            obstacles.add(new Point(wall.x, wall.y));
        }
    }

    public boolean isObstacle(Point point){
        for(Point obstacle : obstacles){
            if(point.equals(obstacle))
                return true;
        }
        return false;
    }


    /**
     * Returns all valid (non-obstacle) neighbours of the given node.
     *
     * @param node
     * @param width
     * @param height
     * @return
     */
    public List<Node> findNeighbours(Node node, int width, int height){
        List<Node> neighbours = new ArrayList<>();
        final int x = node.getX();
        final int y = node.getY();

        int newX = x - 1;

        // wraparound test
        if(newX < 0)
            newX = width - 1;

        Node west = map.get(newX).get(y);

        int newY = y - 1;

        // wraparound test
        if(newY < 0)
            newY = height - 1;

        Node north = map.get(newX).get(newY);

        newX = x + 1;

        // wraparound test
        if(newX >= width)
            newX = 0;

        Node east = map.get(newX).get(y);

        newY = y + 1;

        if(newY >= height)
            newY = 0;

        Node south = map.get(x).get(newY);

        //if(!north.isObstacle())
            neighbours.add(north);
        //if(!south.isObstacle())
            neighbours.add(south);
        //if(!east.isObstacle())
            neighbours.add(east);
        //if(!west.isObstacle())
            neighbours.add(west);


        return neighbours;
    }

    public boolean isInClosedSet(Node node){
        for(Node current : closedSet){
            if(node.equals(current))
                return true;
        }
        return false;
    }

    public boolean isFacing(Direction direction, Point source, Point target, int width, int height){
        if(direction == Direction.UP){
            System.out.println("Here1");
            // checking above
            if(source.y == 0 && target.y == height - 1){
                return true;
            }
            else if(source.y > target.y){
                return true;
            }
        }
        else if(direction == Direction.DOWN){
            // checking below
            if(source.y == height - 1 && target.y == 0){
                return true;
            }
            else if(source.y < target.y){
                return true;
            }

        }
        // TODO check AAAAA
        else if(direction == Direction.LEFT){
            // checking left
            if(source.x > target.x)
                return true;
            else if(source.x == 0 && target.x == width - 1)
                return true;
        }
        else if(direction == Direction.RIGHT){
            // checking right
            if(source.x < target.x)
                return true;
            else if(source.x == width - 1 && target.x == 0)
                return true;
        }

        return false;
    }

    public Direction getFacing(Node source, Node target){
        return null;
    }

    /**
     * Uses A* to find a path from A to B. Ignores bullets, players and lasers,
     * and anything else dynamic.
     *
     * @param start
     * @param end
     * @param board
     */
    public void findPath(Point start, Point end, Gameboard board, Direction initialDirection){
        int width = board.getWidth();
        int height = board.getHeight();
        Node startNode = new Node(start.x, start.y, false, 0, false, true, false);
        openSet.clear();
        closedSet.clear();

        openSet.add(startNode);

        // while we haven't reached the goal yet
        while(openSet.size() > 0){
            // get the best node from the open set
            Node current = openSet.remove();
            closedSet.add(current);

            List<Node> neighbours = findNeighbours(current, width, height);

            for(Node neighbour : neighbours){
                // skip. we've been here before
                if(closedSet.contains(neighbour))
                    continue;

                if(!neighbour.isObstacle()){
                    // calculate the cost to get to this neighbour in our tenative path
                    //int cost = current.getDistanceFromStart() + isFacing() ? 1 : 2;
                }

            }

        }
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

        @Override
        public boolean equals(Object other){
            if(other instanceof Node) {
                Node otherNode = (Node) other;
                if (this.x == otherNode.getX() && this.y == otherNode.getY())
                    return true;
            }
            return false;
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

            return Math.min(dx_normal, dx_wrap) + Math.min(dy_normal, dy_wrap) + 1; // + 1 due to the turning required
		}
	}
}