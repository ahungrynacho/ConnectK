import java.awt.Point;
import java.util.HashSet;
import connectK.BoardModel;

public class Node {
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
				this.rank = -10;
			else if (winner == 2)
				this.rank = 10;
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
		
		private boolean isStreak(int s) {
			return s == this.state.getkLength();
		}
		
		private boolean isDirStreak(int s) {
			return (s-1) == this.state.getkLength();
		}
		
		public int heuristic(Point p) {
			// direct-adjacent heuristic
			// consider passing heuristic() into the constructor as an argument (lambda in J8 an or interface in J7)
			int hRank = 0;

			// top to bottom
			int verticalStreak = 0;
			int streak = 0;
			for (int x = p.x; x < this.state.getWidth(); x++) {
				if (streak >= this.state.getkLength() || this.state.getSpace(x, p.y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
					verticalStreak++;
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
					verticalStreak++;
				}
			}
			if (this.isStreak(streak) || this.isDirStreak(verticalStreak))
				hRank++;
			streak = 0;
			
			// left to right
			int horizontalStreak = 0;
			for (int y = p.y; y < this.state.getHeight(); y++) {
				if (streak >= this.state.getkLength() || this.state.getSpace(p.x, y) == this.opponent)
					break;
				else {// if (this.state.getSpace(x, p.y) == 0 or == this.player
					streak++;
					horizontalStreak++;
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
					horizontalStreak++;
				}
			}
			
			if (this.isStreak(streak) || this.isDirStreak(horizontalStreak))
				hRank++;
			streak = 0;
			
			// upper-right
			int diagUpStreak = 0;
			try {
				
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						diagUpStreak++;
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
						diagUpStreak++;
						y--;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak) || this.isDirStreak(diagUpStreak))
				hRank++;
			streak = 0;
			
			// upper-left
			int diagDownStreak = 0;
			try {
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (streak >= this.state.getkLength() || this.state.getSpace(x, y) == this.opponent)
						break;
					else { // if (this.state.getSpace(x, p.y) == 0 or == this.player
						streak++;
						diagDownStreak++;
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
						diagDownStreak++;
						y++;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			if (this.isStreak(streak) || this.isDirStreak(diagDownStreak))
				hRank++;
			streak = 0;
			
			return hRank;
		}
		
		public static int getNodeCount() {
			return nodeCount;
		}

		public static void incNodeCount(int n) {
			Node.nodeCount += n;
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
//			BoardModel board = new BoardModel(3, 3, 2, false);
////			board = board.placePiece(new Point(0,2), (byte) 1);
////			board = board.placePiece(new Point(0,1), (byte) 2);
////			board = board.placePiece(new Point(1,0), (byte) 2);
////			board = board.placePiece(new Point(2,0), (byte) 2);
////			board = board.placePiece(new Point(2,1), (byte) 1);
////			board = board.placePiece(new Point(0,0), (byte) 1);
//
////			board.placePiece(new Point(2,2), (byte) 1);
//			
//			Node n = new Node(board, 0, null, 3);
//			
//			System.out.println("Current player:" + n.getPlayer());
//			System.out.println(n.getState().toString());
//			System.out.println(-n.heuristic(new Point(1,1)));
//			
//		}
	}