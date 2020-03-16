import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Optional;
import java.util.LinkedList;
import java.util.List;

public class Board {

    private int[][] board;
    private int zeroX;
    private int zeroY;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles){
        board = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                board[i][j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    zeroX = i;
                    zeroY = j;
                }
            }
        }
    }

    // string representation of this board
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(board.length + "\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                sb.append(String.format("%2d ", board[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return board.length;
    }

    // number of tiles out of place
    public int hamming(){
        int result = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (i * board.length + j + 1 != board[i][j] && board[i][j] != 0) {
                    result++;
                }
            }
        }
        return result;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan(){
        int result = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != 0) {
                    result += Math.abs((board[i][j] - 1)/ board.length - i) + Math.abs(((board[i][j] - 1) % board.length) - j);
                }
            }
        }
        return result;
    }

    // is this board the goal board?
    public boolean isGoal(){
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y){
        if (y == this)
            return true;
        if (!(y instanceof Board)) return false;
        Board others = (Board) y;
        if (others.dimension() != this.dimension()) return false;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != others.board[i][j]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors(){
        List<Board> l = new LinkedList<>();
        moveZero(zeroX - 1, zeroY).ifPresent(l::add);
        moveZero(zeroX, zeroY + 1).ifPresent(l::add);
        moveZero(zeroX + 1, zeroY).ifPresent(l::add);
        moveZero(zeroX, zeroY - 1).ifPresent(l::add);
        return l;
    }

    private Optional<Board> moveZero(int newX, int newY){
        if (newX < 0 || newX >= board.length || newY < 0 || newY >= board.length) return Optional.empty();
        Board b = new Board(board);
        b.board[b.zeroX][b.zeroY] = b.board[newX][newY];
        b.board[newX][newY] = 0;
        b.zeroX = newX; b.zeroY = newY;
        return Optional.of(b);
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin(){
        Board b = new Board(this.board);
        int from = getRandomTileCoord(0);
        int fromVal = get(from);
        int to = getRandomTileCoord(from);
        int toVal = get(to);
        b.board[from / b.dimension()][from % b.dimension()] = toVal;
        b.board[to / b.dimension()][to % b.dimension()] = fromVal;
        return b;
    }

    private int getRandomTileCoord(int excl){
        int res = 0;
        while (get(res) == 0 || res == excl) {
            res = StdRandom.uniform(dimension() * dimension());
        }

        return res;
    }

    //i = 0 .. n^2 -1
    private int get(int i){
        return board[i / dimension()][i % dimension()];
    }

    // unit testing (not graded)
    public static void main(String[] args){
        // read in the board specified in the filename
        In in = new In("puzzle3x3-00.txt");
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board b = new Board(tiles);
        System.out.println(b);
        System.out.println("3=" + b.dimension());
        System.out.println("0=" + b.hamming());
        System.out.println("0=" + b.manhattan());

        Board b2 = new Board(tiles);
        System.out.println("true=" + b2.equals(b));
        System.out.println("Some neighbors");
        for (Board bn : b.neighbors()) {
            System.out.println(bn);
        }

        System.out.println("Some twins");
        System.out.println(b.twin());
    }

}