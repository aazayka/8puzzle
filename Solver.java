import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Function;

public class Solver {
    private static Function<Board, Integer> DIST = Board::hamming;

    private static class Node{
        Node prior;
        int moves;
        Board board;
        int dist = -1;

        public Node(Board board, Node prior, int moves) {
            this.prior = prior;
            this.moves = moves;
            this.board = board;
            this.dist = DIST.apply(board);
        }

        public void addNeibohrs(MinPQ<Node> q){
            for (Board b: board.neighbors()) {
                if (prior != null && b == prior.board) continue;
                q.insert(new Node(b, this, moves + 1));
            }
        }
    }
    private Comparator<Node> comp =  new Comparator<Node>() {
        public int compare(Node o1, Node o2) {
            return (o1.dist + o1.moves) - (o2.dist + o2.moves);
        }
    };
    private MinPQ<Node> q = new MinPQ<>(comp);
    private MinPQ<Node> twinQ = new MinPQ<>(comp);

    private Node solution;
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial){
        if (initial == null) throw new IllegalArgumentException("Null input");
        Node node = new Node(initial, null, 0);
        Node twinNode = new Node(initial.twin(), null, 0);
        q.insert(node);
        twinQ.insert(twinNode);
        while(true){
            node = q.delMin();
            twinNode = twinQ.delMin();
            if (node.dist == 0){
                solution = node;
                break;
            }
            if (twinNode.dist == 0){
                break;
            }
            node.addNeibohrs(q);
            twinNode.addNeibohrs(twinQ);
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable(){
        return (solution != null);
    }

    // min number of moves to solve initial board
    public int moves(){
        if (isSolvable()) return solution.moves;
        else return -1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution(){
        Node it = solution;
        LinkedList<Board> l = new LinkedList<>();
        while (it.prior != null){
            l.add(it.board);
            it = it.prior;
        }
        return l;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}