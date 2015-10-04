import java.awt.*;
import java.util.*;
import java.util.List;

public class AStar {
    private Queue<Move> moveQueue;
    private Heuristic heuristic;

    private Map map;

    private PriorityQueue<Node> openSet = new PriorityQueue<>();
    private List<Node> closedSet = new ArrayList<>();
    private Direction currentDirection;

    private List<Turret> turrets;
    private List<Wall> walls;
    private List<Point> tempWalls;

    private int width;
    private int height;

    private static final boolean DEBUG = true;

    public AStar(Heuristic heuristic, Queue<Move> moveQueue){
        this.heuristic = heuristic;
        this.moveQueue = moveQueue;
    }

    private int debugPrintcalls = 1;
    private void debugPrint(){
        if(DEBUG)
            System.out.println("Call number" + debugPrintcalls++);
    }

    public void setupMap(Gameboard gameboard){
        if(map != null)
            return;
        turrets = gameboard.getTurrets();
        walls = gameboard.getWalls();
        width = gameboard.getWidth();
        height = gameboard.getHeight();
        map = new Map(width, height);
        for(int i = 0; i < gameboard.getWidth(); i++){
            List<Node> columns = new ArrayList<>();
            for(int ii = 0; ii < gameboard.getHeight(); ii++){
                Node newNode = new Node(i, ii, false, 0, isObstacle(new Point(i, ii)), false, false);
                setupObstacle(i, ii, newNode);
                columns.add(newNode);
            }
            map.add(columns);
        }
    }

    private void setupObstacle(int x, int y, Node newNode){
        for(Turret turret : turrets){
            if(turret.x == x && turret.y == y){
                newNode.setIsTurret(true);
                newNode.setIsObstacle(true);
                break;
            }
        }

        for(Wall wall : walls){
            if(wall.x == x && wall.y == y){
                newNode.setIsObstacle(true);
                break;
            }
        }

    }

    public void updateTurrets(Gameboard board){
        this.turrets = board.getTurrets();
        for(Turret turret : turrets){
            Node turretNode = map.at(turret.x, turret.y);
            turretNode.setTurret(turret);
            if(turret.isDead()){
                // Dead turrets are still walls :D
                turretNode.setIsTurret(false);
            }
        }
    }

    /**
     * Returns a danger rating on the scale of 0-2 where 0 is safe and 2 is get hit if you go here
     * next turn.
     * @param point the point on the board
     * @param gameboard the gameboard itself
     * @return danger rating
     */
    public int isDangerous(Point point, Gameboard gameboard){
        int tenativeDanger = 0;
        // check for danger
        for(int i = 1; i <= 4; i++){
            for(int ii = 0; ii < 4; ii++) {
                int newX = point.x;
                int newY = point.y;

                if (ii == 0)
                    newX += i; // right
                else if (ii == 1)
                    newX -= i; // left
                else if (ii == 2)
                    newY += i; // down
                else if (ii == 3)
                    newY -= i; // up

                Node node = map.at(newX, newY);

                // check bullets in all directions (only two spaces)
                if (i == 1 || i == 2) {
                    try {
                        List<Bullet> bullets = gameboard.getBulletsAtTile(node.getX(), node.getY());

                        for (Bullet bullet : bullets) {
                            if (bullet.x == node.getX() && bullet.y == node.getY()) {
                                if((ii == 0 && bullet.getDirection() == Direction.LEFT)
                                        || (ii == 1 && bullet.getDirection() == Direction.RIGHT)
                                        || (ii == 2 && bullet.getDirection() == Direction.UP)
                                        || (ii == 3 && bullet.getDirection() == Direction.DOWN)) {
                                    int dangerLevel = 3 - i; // danger is 2 if i==1, danger is 1 if i==2
                                    if (dangerLevel > tenativeDanger)
                                        tenativeDanger = dangerLevel;
                                }
                            }
                        }
                    } catch (MapOutOfBoundsException e) {
                        System.err.println("isDangerous: Something went very wrong with the coordinates O_O");
                    }
                }

                // check turrets in all directions
                if (node.isTurret()) {
                    Turret turret = node.getTurret();

                    if (turret.isFiringNextTurn() || turret.didFire()) {
                        if (tenativeDanger < 2)
                            tenativeDanger = 2;
                    } else {
                        if (tenativeDanger < 1)
                            tenativeDanger = 1;
                    }

                }
            }

        }

        return tenativeDanger;
    }


    public boolean isObstacle(Point point){
        Node node = map.get(point.x).get(point.y);
        return node.isObstacle();
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
    
    public boolean isTempObstacle(Node node){
        if(tempWalls == null)
            return false;

        for(Point point : tempWalls){
            if(node.x == point.x && node.y == point.y)
                return true;
        }
        return false;
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
        if(DEBUG)
            System.out.println("Start position: " + start.toString());
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

            if(DEBUG)
                System.out.println("Investigating node " + current.toString());

            // Check if we have reached the target. If so, we are done!
            if(end.equals(current.getCoords())){
                reconstructPath(initialDirection, current);
            }

            List<Node> neighbours = findNeighbours(current);

            for(Node neighbour : neighbours){
                // skip. we've been here before
                if(closedSet.contains(neighbour))
                    continue;

                if(!neighbour.isObstacle() && !isTempObstacle(neighbour)){
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
                        if(DEBUG)
                            System.out.println("Adding " + current.toString() + " as parent of " + neighbour.toString());
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
            if(DEBUG)
                System.out.println("Connecting parent " + parent.toString() + " to " + node.toString());
            facing = getFacing(parent, node, initialDirection);
            System.out.println(facing.toString());
            if(facing != previousFacing){
                System.out.println("Change direction from " + facing + " to " + previousFacing);
                moveList.add(Direction.directionToMovement(previousFacing));
                System.out.println("Just added " + moveList.get(moveList.size() - 1));
            }
            moveList.add(Move.FORWARD);

            // update pointers
            node = parent;
            previousFacing = facing;
        }

        // Initial direction change
        if(initialDirection != previousFacing){
            moveList.add(Direction.directionToMovement(previousFacing));
        }

        if(DEBUG)
            System.out.println("Printing new move plan: ");

        // Add the moves to the queue
        for(int i = moveList.size() - 1; i >= 0; i--){
            moveQueue.add(moveList.get(i));

            if(DEBUG)
                System.out.println(moveList.get(i));
        }
    }

    public Map getMap(){
        return map;
    }

    public void setTempWalls(List<Point> tempWalls){
        this.tempWalls = tempWalls;
    }

    public void clearTempWalls(){
        this.tempWalls = null;
    }

    public Heuristic getHeuristic(){
        return this.heuristic;
    }

    public static class Map{
        private List<List<Node>> map;
        private int width;
        private int height;

        public Map(int width, int height){
            this.width = width;
            this.height = height;
        }

        public void add(List<Node> column){
            map.add(column);
        }

        public List<Node> get(int index){
            return map.get(index);
        }

        public Node at(int x, int y){
            if(x < 0)
                x += width * (Math.abs(x) / width);
            else if(x >= width)
                x -= width * (x / width);

            if(y < 0)
                y += height * (Math.abs(y) / height);
            else if(y >= height)
                y -= height * (y / height);

            return map.get(x).get(y);
        }
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

        private Turret turret;

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

        /**
         *  Getters and Setters
         */

        public Turret getTurret(){
            return turret;
        }

        public void setTurret(Turret turret){
            this.turret = turret;
        }

        public boolean isTurret() {
            return isTurret;
        }

        public void setIsTurret(boolean isTurret) {
            this.isTurret = isTurret;
        }

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

        @Override
        public String toString() {
            return "Node at (" + this.x + ", " + this.y + ")";
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