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
		private HashSet<Point> cacheNextMoves;
		
		Node(BoardModel state, int depth, Point move, int cutoff) {
			this.depth = depth;
			this.state = state.clone();
			this.move = move;
			player = currentPlayer();
			opponent = opponentPlayer();
			this.cutoff = cutoff;
			cacheNextMoves = nextMoves(state.gravityEnabled());
			
		}
		
		private int opponentPlayer() {
			if (player == 1)
				return 2;
			else
				return 1;
		}
		
		private int currentPlayer() {
			Point p = state.getLastMove();
			if (p == null) // game just started so player 1 goes first by default
				return 1;
			else if (state.getSpace(p) == 1)
				return 2;
			else
				return 1;
			
		}
		
		public void evalGame() {
			byte winner = state.winner();
		
			if (winner == opponent)
				rank = Integer.MIN_VALUE + 1;
			else if (winner == player)
				rank = Integer.MAX_VALUE - 1;
			else if (winner == 0)
				rank = 0;
		}
		
		public HashSet<Point> nextMoves(boolean gravity) {
			HashSet<Point> moves = new HashSet<Point>();
			
			if (gravity) {
				for (int x = 0; x < state.getWidth(); x++) {
					for (int y = 0; y < state.getHeight(); y++) {
						if (state.getSpace(x,y) == 0) {
							moves.add(new Point(x,y));
							break;
						}
					}
				}
			}
			
			else {
				for (int x = 0; x < state.getWidth(); ++x) {
					for (int y = 0; y < state.getHeight(); ++y) {
						if (state.getSpace(x,y) == 0) {
							moves.add(new Point(x,y));
						}
					}
				}
			}

			return moves;
		}
		
		public BoardModel nextState(Point move) {
			
			BoardModel nextState = state.clone();
			return nextState.placePiece(move, (byte) player);

		}
				
		public int heuristic(Point p) {
			int sum = 0;
				
			if (!state.gravityEnabled()) {
				return checkEightDirections(p);
			}
			else {
				for (Point pt : cacheNextMoves) {
					sum += checkEightDirections(pt);
				}
			}
			return sum;
		}		
	
		public int checkEightDirections(Point p) {
			// Summing the player's pieces within k spaces of P
			// By definition, a heuristic always underestimates the actual cost to a goal node.
			int pieceRank = 10;
			int emptyRank = 1;
			int hRank = -(emptyRank * 8) + pieceRank; // to account for over-counting 
			

			// top to bottom
//			System.out.println("top to bottom");
			for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
				if (state.getSpace(x, p.y) == opponent || k == state.getkLength())
					break;
				else if (state.getSpace(x, p.y) == 0)
					hRank += emptyRank;
				else if (state.getSpace(x, p.y) == player)
					hRank += pieceRank;
//				System.out.print("(" + x + "," + p.y + ")");
			}
//			System.out.println();
			
			// bottom to top
//			System.out.println("bottom to top");
			for (int x = p.x, k = 0; x >= 0; x--, k++) {
				if (state.getSpace(x, p.y) == opponent || k == state.getkLength())
					break;
				else if (state.getSpace(x, p.y) == 0)
					hRank += emptyRank;
				else if (state.getSpace(x, p.y) == player)
					hRank += pieceRank;
//				System.out.print("(" + x + "," + p.y + ")");
			}
//			System.out.println();
			
			// left to right
//			System.out.println("left to right");
			for (int y = p.y, k = 0; y < state.getHeight(); y++, k++) {
				if (state.getSpace(p.x, y) == opponent || k == state.getkLength())
					break;
				else if (state.getSpace(p.x, y) == 0)
					hRank += emptyRank;
				else if (state.getSpace(p.x, y) == player)
					hRank += pieceRank;
//				System.out.print("(" + p.x + "," + y + ")");
			}
//			System.out.println();
			
			// right to left
//			System.out.println("right to left");
			for (int y = p.y, k = 0; y >= 0; y--, k++) {
				if (state.getSpace(p.x, y) == opponent || k == state.getkLength())
					break;
				else if (state.getSpace(p.x, y) == 0)
					hRank += emptyRank;
				else if (state.getSpace(p.x, y) == player)
					hRank += pieceRank;
//				System.out.print("(" + p.x + "," + y + ")");
			}
//			System.out.println();
			
			// upper-right
//			System.out.println("upper right");
			try {
				
				int y = p.y;
				for (int x = p.x, k = 0; x >= 0; x--, k++) {
					if (state.getSpace(x, y) == opponent || k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == 0)
						hRank += emptyRank;
					else if (state.getSpace(x, y) == player)
						hRank += pieceRank;
//					System.out.print("(" + x + "," + y + ")");
					y++;
					

				}
			} catch (IndexOutOfBoundsException e) {}
//			System.out.println();
			
			// lower-left
//			System.out.println("lower left");
			try {
				
				int y = p.y;
				for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
					if (state.getSpace(x, y) == opponent || k == state.getkLength())
						break;
					else if (state.getSpace(x ,y) == 0)
						hRank += emptyRank;
					else if (state.getSpace(x, y) == player)
						hRank += pieceRank;
//					System.out.print("(" + x + "," + y + ")");
					y--;
				}
			} catch (IndexOutOfBoundsException e) {}
//			System.out.println();
			
			// upper-left
//			System.out.println("upper left");
			try {
				int y = p.y;
				for (int x = p.x, k = 0; x >= 0; x--, k++) {
					if (state.getSpace(x, y) == opponent || k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == 0)
						hRank += emptyRank;
					else if (state.getSpace(x, y) == player)
						hRank += pieceRank;
//					System.out.print("(" + x + "," + y + ")");
					y--;
				}
			} catch (IndexOutOfBoundsException e) {}
//			System.out.println();
		
			// lower-right
//			System.out.println("lower right");
			try {	
				
				int y = p.y;
				for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
					if (this.state.getSpace(x, y) == opponent || k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == 0)
						hRank += emptyRank;
					else if (state.getSpace(x, y) == player)
						hRank += pieceRank;
//					System.out.print("(" + x + "," + y + ")");
					y++;
				}
			} catch (IndexOutOfBoundsException e) {}
//			System.out.println();
					
			return hRank;
		}
		
		public void printCoord() {
			for (int r = 0; r < state.getWidth(); r++) {
				for (int c = 0 ; c < state.getHeight(); c++) {
					System.out.print("(" + r + "," + c + ")");
				}
				System.out.println();
			}
			System.out.println("top to bottom ->");
			System.out.println("<- bottom to top");
			System.out.println("left to right (UP)");
			System.out.println("right to left (DOWN)");
			
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

		public HashSet<Point> getCacheNextMoves() {
			return cacheNextMoves;
		}

		public void setCacheNextMoves(HashSet<Point> cacheNextMoves) {
			this.cacheNextMoves = cacheNextMoves;
		}
		
		

//		public static void main(String[] args) {
//			BoardModel board = new BoardModel(5, 5, 3, false);
//			board = board.placePiece(new Point(1,1), (byte) 1);
//			board = board.placePiece(new Point(0,2), (byte) 2);
//			board = board.placePiece(new Point(0,3), (byte) 2);
////			board = board.placePiece(new Point(2,0), (byte) 2);
//			board = board.placePiece(new Point(4,4), (byte) 1);
////			board = board.placePiece(new Point(2,2), (byte) 1);
//
////			board.placePiece(new Point(2,2), (byte) 1);
//			
//			Node n = new Node(board, 0, null, 3);
//			n.printCoord();
//			System.out.println("Current player:" + n.getPlayer());
//			System.out.println(n.getState().toString());
//			System.out.println(n.heuristic(new Point(0,1)));
//			System.out.println("After move is applied to where the heuristic is calculating");
//			System.out.println(n.getState().placePiece(new Point(0,1), (byte) 2).toString());
//			
//		}
	}