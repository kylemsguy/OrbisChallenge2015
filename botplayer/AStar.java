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

    private int width;
    private int height;

    public AStar(Heuristic heuristic, Queue<Move> moveQueue){
        this.heuristic = heuristic;
        this.moveQueue = moveQueue;
        this.obstacles = new ArrayList<>();
    }

    private int debugPrintcalls = 1;
    private void debugPrint(){
        System.out.println("Call number" + debugPrintcalls++);
    }

    public void setupMap(Gameboard gameboard){
        if(map != null)
            return;
        width = gameboard.getWidth();
        height = gameboard.getHeight();
        setupObstacles(gameboard);
        map = new ArrayList<>();
        for(int i = 0; i < gameboard.getWidth(); i++){
            List<Node> columns = new ArrayList<>();
            for(int ii = 0; ii < gameboard.getHeight(); ii++){
                columns.add(new Node(i, ii, false, 0, isObstacle(new Point(i, ii)), false, false));
            }
            map.add(columns);
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
     * @return
     */
    public List<Node> findNeighbours(Node node){
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

    public boolean isFacing(Direction direction, Point source, Point target){
        if(direction == Direction.UP){
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

    public Direction getFacing(Node source, Node target, Direction initialDirection){
        if(source == null)
            return initialDirection;
        if(source.x > target.x || (source.x == 0 && target.x == width - 1)){
            // facing left
            return Direction.LEFT;
        }
        else if(source.x < target.x || (source.x == width - 1 && target.x == 0)){
            // facing right
            return Direction.RIGHT;
        }
        else if(source.y > target.y || (source.y == 0 && target.y == height - 1)){
            // facing up
            return Direction.UP;
        }
        else if(source.y < target.y || (source.y == height - 1 && target.y == 0)){
            return Direction.DOWN;
        }
        else{
            System.err.println("SOMETHING HAS GONE TERRIBLY WRONG!");
            return null;
        }
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
            boolean neighbourBetter;
            // get the best node from the open set and add to closed set
            Node current = openSet.remove();
            closedSet.add(current);

            // Check if we have reached the target. If so, we are done!
            if(end.equals(current.getCoords())){
                reconstructPath(initialDirection, current);
            }

            List<Node> neighbours = findNeighbours(current);

            for(Node neighbour : neighbours){
                // skip. we've been here before
                if(closedSet.contains(neighbour))
                    continue;

                if(!neighbour.isObstacle()){
                    // calculate the cost to get to this neighbour in our tenative path
                    Direction prevDirection = getFacing(current.getParent(), current, initialDirection);
                    int cost = (int) current.getDistanceFromStart() +
                            (isFacing(prevDirection, current.getCoords(),
                                    neighbour.getCoords()) ? 1 : 2);

                    // add neighbour to open list if not there
                    if(!openSet.contains(neighbour)){
                        openSet.add(neighbour);
                        neighbourBetter = true;
                    }
                    else if(cost < current.getDistanceFromStart()){
                        neighbourBetter = true;
                    }
                    else
                        neighbourBetter = false;

                    // set neighbour parameters if better
                    if(neighbourBetter){
                        neighbour.setParent(current);
                        neighbour.setDistanceFromStart(cost);
                        neighbour.setHeuristicDistanceFromGoal(
                                heuristic.getDistance(neighbour.getCoords(), end, width, height));
                    }
                }

            }
        }
    }

    /**
     * Reconstructs the path and turns into a set of moves.
     * @param end
     */
    private void reconstructPath(Direction initialDirection, Node end){
        if(end.getParent() == null)
            return;
        List<Move> moveList = new ArrayList<>();
        Node node = end;
        Node parent = end.getParent();
        Direction previousFacing = getFacing(parent, end, initialDirection);
        Direction facing;

        while((parent = node.getParent()) != null){
            facing = getFacing(parent, node, initialDirection);
            moveList.add(Move.FORWARD);
            if(facing != previousFacing){
                moveList.add(Direction.directionToMovement(previousFacing));
            }

            // update pointers
            node = parent;
            previousFacing = facing;
        }

        // Initial direction change
        if(initialDirection != previousFacing){
            moveList.add(Direction.directionToMovement(previousFacing));
        }

        System.out.println("Printing new move plan: ");

        // Add the moves to the queue
        for(int i = moveList.size() - 1; i >= 0; i--){
            moveQueue.add(moveList.get(i));
            System.out.println(moveList.get(i));
        }
    }

    public List<List<Node>> getMap(){
        return map;
    }

    public Heuristic getHeuristic(){
        return this.heuristic;
    }

    public static class Node implements Comparable<Node>{
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
        private boolean isTurret;

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

        public boolean isTurret() {
            return isTurret;
        }

        public void setIsTurret(boolean isTurret) {
            this.isTurret = isTurret;
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