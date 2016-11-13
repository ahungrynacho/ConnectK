
import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import connectK.BoardModel;

public class Node implements Comparable<Node>{
		private int rank;
		private int depth;
		private BoardModel state;
		private Point move; // move made by the current player to get to the current player Node's current state
		private int player; // current player
		private int opponent;
		private int cutoff; // maximum depth to be evaluated, inclusive; root depth = 0
		private static int nodeCount = 0;
		
		Node(BoardModel state, int depth, Point move, int cutoff) {
			this.depth = depth;
			this.state = state.clone();
			this.move = move;
			this.player = this.currentPlayer();
			this.opponent = this.opponentPlayer();
			this.cutoff = cutoff;
			
		}
		
		@Override
		public int compareTo(Node n) {
			if (this.rank < n.getRank())
				return -1;
			else if (this.rank == n.getRank())
				return 0;
			else
				return 1;
		}
		
		private int opponentPlayer() {
			if (this.player == 1)
				return 2;
			else
				return 1;
		}
		
		private int currentPlayer() {
			Point p = this.state.getLastMove();
			if (p == null) // game just started so player 1 goes first by default
				return 1;
			else if (this.state.getSpace(p) == 1)
				return 2;
			else
				return 1;
			
		}
		
		public void evalGame() {
			byte winner = this.state.winner();
			
			// this part works
			if (winner == 1)
				this.rank = Integer.MIN_VALUE;
			else if (winner == 2)
				this.rank = Integer.MAX_VALUE;
			else if (winner == 0)
				this.rank = 0;
		}
		
		public HashSet<Point> nextMoves(boolean gravity) {
			HashSet<Point> moves = new HashSet<Point>();
			
			if (gravity) {
				for (int x = 0; x < this.state.getWidth(); x++) {
					for (int y = 0; y < this.state.getHeight(); y++) {
						if (this.state.getSpace(x,y) == 0) {
							moves.add(new Point(x,y));
							break;
						}
					}
				}
			}
			
			else {
				for (int x = 0; x < this.state.getWidth(); ++x) {
					for (int y = 0; y < this.state.getHeight(); ++y) {
						if (this.state.getSpace(x,y) == 0) {
							moves.add(new Point(x,y));
						}
					}
				}
			}

			return moves;
		}
		
		public BoardModel nextState(Point move) {
			
			BoardModel nextState = this.state.clone();
			return nextState.placePiece(move, (byte) this.player);

		}
				
		public int heuristic() {
			int rank = 0;
			for (int x = 0; x < this.state.getWidth(); x++) {
				for (int y = 0; y < this.state.getHeight(); y++) {
					if (this.state.getSpace(x, y) == this.getPlayer()) {
						rank += checkEightDirections(new Point(x,y));
					}
				}
			}
			return rank;
		}
		
		private boolean isStreak(int s) {
			return s == this.state.getkLength();
		}
	
		public int checkEightDirections(Point p) {
			// number-of-possible-wins heuristic
			// consider passing heuristic() into the constructor as an argument (lambda in J8 an or interface in J7)
			// By definition, a heuristic always underestimates the actual cost to a goal node.
			// minimizer: -10 < h(n) <= 0
			// maximizer: 0 <= h(n) < 10
			int hRank = 0;

			// top to bottom
			int streak = 0;
			for (int x = p.x; x < this.state.getWidth(); x++) {
				if (streak >= this.state.getkLength() || this.state.getSpace(x, p.y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
				}
			}
			if (this.isStreak(streak))
				hRank++;
				
			streak = 0;
			
			// bottom to top
			for (int x = p.x; x >= 0; x--) {
				if (streak >= this.state.getkLength() || this.state.getSpace(x, p.y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
				}
			}
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			// left to right
			for (int y = p.y; y < this.state.getHeight(); y++) {
				if (streak >= this.state.getkLength() || this.state.getSpace(p.x, y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
				}
			}
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			// right to left
			for (int y = p.y; y >= 0; y--) {
				if (streak >= this.state.getkLength() || this.state.getSpace(p.x, y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
				}
			}
			
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			// upper-right
			try {
				
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						y++;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			// lower-left
			try {
				int y = p.y;
				for (int x = p.x; x < this.state.getWidth(); x++) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						y--;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			// upper-left
			try {
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						y--;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
		
			// lower-right
			try {	
				
				int y = p.y;
				for (int x = p.x; x < this.state.getWidth(); x++) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						y++;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak))
				hRank++;
			streak = 0;
			
			return hRank;
		}
		
		public static int getNodeCount() {
			return nodeCount;
		}

		public static void incNodeCount() {
			Node.nodeCount++;
		}
		
		public static void resetNodeCount() {
			Node.nodeCount = 0;
		}
		
		public int getPlayer() {
			return this.player;
		}
		
		public int getOpponent() {
			return this.opponent;
		}
		
		public BoardModel getState() {
			return this.state;
		}
		
		public int getRank() {
			return this.rank;
		}
		
		public int getDepth() {
			return this.depth;
		}
		
		public Point getMove() {
			return this.move;
		}
		
		public int getCutoff() {
			return this.cutoff;
		}
		
		public void setDepth(int d) {
			this.depth = d;
		}
		
		public void setMove(Point p) {
			this.move = p;
		}
		
		public void setRank(int r) {
			this.rank = r;
		}






		
//		public static void main(String[] args) {
//			BoardModel board = new BoardModel(3, 3, 3, false);
//			board = board.placePiece(new Point(1,1), (byte) 1);
//			board = board.placePiece(new Point(0,1), (byte) 2);
////			board = board.placePiece(new Point(1,0), (byte) 2);
////			board = board.placePiece(new Point(2,0), (byte) 2);
////			board = board.placePiece(new Point(2,1), (byte) 1);
////			board = board.placePiece(new Point(2,2), (byte) 1);
//
////			board.placePiece(new Point(2,2), (byte) 1);
//			
//			Node n = new Node(board, 0, null, 3);
//			
//			System.out.println("Current player:" + n.getPlayer());
//			System.out.println(n.getState().toString());
//			System.out.println(n.heuristic());
//			System.out.println(n.getState().toString());
//			
//		}
	}