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
		
		Node(BoardModel state, int depth, Point move, int cutoff) {
			this.depth = depth;
			this.state = state.clone();
			this.move = move;
			this.player = this.currentPlayer();
			this.opponent = this.opponentPlayer();
			this.cutoff = cutoff;
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
		
		public BoardModel nextState(Point move) {
			
			BoardModel nextState = this.state.clone();
			
			return nextState.placePiece(move, (byte) this.player);

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
				this.rank = -10;
			else if (winner == 2)
				this.rank = 10;
			else if (winner == 0)
				this.rank = 0;
		}
		
		public int heuristic(Point p) {
			// direct-adjacent heuristic
			// consider passing heuristic() into the constructor as an argument (lambda in J8 an or interface in J7)
			int hRank = 0;
			
			try {
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
	//			for (int x = p.x;)
				// upper-left
				
				// lower-left
				
				// lower-right
				for (int x = p.x; x < this.state.getWidth(); x++) {
					int y = p.y;
					System.out.println("lower-right: " + x + "," + y);
					this.state.getSpace(x,y);
					y++;
				}
			}catch (IndexOutOfBoundsException e) {
				System.out.println("IndexOutOfBoundsException");
			}
			
			return hRank;
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
	}