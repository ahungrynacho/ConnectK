import java.awt.Point;
import java.util.HashSet;

import connectK.BoardModel;

public class Node {
		private int rank;
		private int depth;
		private BoardModel state;
		private Point move; // move previously made by the current player to get to Node's (belonging to current player) current state
		private int player; // current player
		private int opponent;
		private int cutoff; // maximum depth to be evaluated, inclusive; root depth = 0
		private int maxHRank;
		private int maxRank;
		private static int nodeCount = 0;
		
		Node(BoardModel state, int depth, Point move, int cutoff) {
			this.depth = depth;
			this.state = state.clone();
			this.move = move;
			this.player = this.currentPlayer();
			this.opponent = this.opponentPlayer();
			this.cutoff = cutoff;
			this.maxHRank = 8 * (this.state.getkLength() - 1); // max number of pieces surrounding but not including the winning piece
			this.maxRank = 10 - (this.maxHRank % 10) + this.maxHRank;
			
			// For a 3-in-a-row win condition, maxHRank = 16 and maxRank = 20.
			
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
			
			// this part is broken...does not play optimally for AI vs AI
//			if (winner == opponent)
//				this.rank = -10;
//			else if (winner == player)
//				this.rank = 10;
//			else if (winner == 0)
//				this.rank = 0;
			
			// this part works
			if (winner == 1)
				this.rank = -this.maxRank;
			else if (winner == 2)
				this.rank = this.maxRank;
			else if (winner == 0)
				this.rank = 0;
		}
		
		public HashSet<Point> nextMoves() {
			HashSet<Point> moves = new HashSet<Point>();
			
			for (int x = 0; x < this.state.getWidth(); ++x) {
				for (int y = 0; y < this.state.getHeight(); ++y) {
					if (this.state.getSpace(x,y) == 0) {
						moves.add(new Point(x,y));
					}
				}
			}

			return moves;
		}
		
		public BoardModel nextState(Point move) {
			
			BoardModel nextState = this.state.clone();
			return nextState.placePiece(move, (byte) this.player);

		}

		public int heuristic(Point p) {
			// direct-adjacent heuristic
			// consider passing heuristic() into the constructor as an argument (lambda in J8 an or interface in J7)
			int hRank = 0;
			
			if (this.state.winner() != -1)
			

			// top to bottom
			for (int x = p.x; x < this.state.getWidth(); x++) {
				if (this.state.getSpace(x, p.y) != this.getPlayer()) // if 0 or opponent
					break;
				else
					hRank++;
			}
			
			// bottom to top
			for (int x = p.x; x >= 0; x--) {
				if (this.state.getSpace(x, p.y) != this.getPlayer())
					break;
				else
					hRank++;
			}
			
			// left to right
			for (int y = p.y; y < this.state.getHeight(); y++) {
				if (this.state.getSpace(p.x, y) != this.getPlayer())
					break;
				else
					hRank++;
			}
			
			// right to left
			for (int y = p.y; y >= 0; y--) {
				if (this.state.getSpace(p.x, y) != this.getPlayer())
					break;
				else
					hRank++;
			}
			
			// upper-right
			try {
				
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (this.state.getSpace(x,y) != this.getPlayer())
						break;
					else {
						hRank++;
						y++;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			// upper-left
			try {
				int y = p.y;
				for (int x = p.x; x >= 0; x--) {
					if (this.state.getSpace(x,y) != this.getPlayer())
						break;
					else {
						hRank++;
						y--;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			// lower-left
			try {
				int y = p.y;
				for (int x = p.x; x < this.state.getWidth(); x++) {
					if (this.state.getSpace(x,y) != this.getPlayer())
						break;
					else {
						hRank++;
						y--;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
		
			// lower-right
			try {	
				
				int y = p.y;
				for (int x = p.x; x < this.state.getWidth(); x++) {
//					System.out.println("lower-right: " + x + "," + y);
					
					if (this.state.getSpace(x,y) != this.getPlayer())
						break;
					else {
						hRank++;
						y++;
					}
				}
			} catch (IndexOutOfBoundsException e) {}
			
			// should return a minimum hRank of 8 since the piece at p is counted in 8 directions, unless offset by -8
			return hRank - 8;
		}
		
		public static int getNodeCount() {
			return nodeCount;
		}

		public static void incNodeCount(int n) {
			Node.nodeCount += n;
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
//			board = board.placePiece(new Point(2,2), (byte) 1);
//			board = board.placePiece(new Point(0,1), (byte) 2);
//			board = board.placePiece(new Point(0,0), (byte) 2);
//			board = board.placePiece(new Point(1,0), (byte) 2);
//			board = board.placePiece(new Point(2,0), (byte) 2);
//			board = board.placePiece(new Point(2,1), (byte) 1);
////			board.placePiece(new Point(2,2), (byte) 1);
//			
//			Node n = new Node(board, 0, null, 3);
//			
//			System.out.println(n.getPlayer());
//			System.out.println(n.getState().toString());
//			System.out.println(n.heuristic(new Point(0,0)));
//			
//		}
	}