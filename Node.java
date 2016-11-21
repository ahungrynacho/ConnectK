import java.awt.Point;
import java.util.HashSet;
import connectK.BoardModel;

public class Node {
		private int MAX_RANK = Integer.MAX_VALUE - 1;
		private int MAX_BLOCK_RANK = MAX_RANK - 1;
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
		
		public int evalGame() {
			byte winner = state.winner(); // winner = -1 if game has not reached its terminal state
			if (winner == opponent) // REVERSED
				return -MAX_RANK;
			else if (winner == player)
				return MAX_RANK;
			else if (winner == 0) // draw
				return 0;
			else
				return -1;
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
			
			if (state.gravityEnabled()) {
				for (Point pt : cacheNextMoves) {
					sum += checkEightDirections(pt);
//					if (hRank == MAX_RANK) { // killer heuristic move found
//						System.out.println(p);
//						System.out.println(state.toString());
//						System.out.println("MAX_RANK");
//						return hRank;
//					}
//					else if (hRank == MAX_BLOCK_RANK) {
//						System.out.println("MAX_BLOCK_RANK");
//						System.exit(0);
//						return hRank;
//					}
//					sum += hRank;
				}		
			}
			else {
				return checkEightDirections(p);
			}
			return sum;
		}
		
	
		public int checkEightDirections(Point p) {
			// Summing the player's pieces within k spaces of P
			// By definition, a heuristic always underestimates the actual cost to a goal node.
			int pieceRank = 10;
			int twoMovesAwayRank = 100;
			int oneMovesAwayRank = 1000;
			int emptyRank = 1;
			int hRank = -(emptyRank * 8) + pieceRank; // to account for over-counting 
			int contOpponent = 0;
			int contPlayer = 0;
			
			// top to bottom
			for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
				if (contPlayer == state.getkLength() - 2)
					hRank += twoMovesAwayRank;
				else if (contPlayer == state.getkLength() - 1) {
					System.out.println(state.toString());
					hRank += oneMovesAwayRank;
				}
//				else if (contOpponent == (state.getkLength() - 1))
//					return MAX_BLOCK_RANK;
				else if (k == state.getkLength())
					break;
				else if (state.getSpace(x, p.y) == opponent) {
					contOpponent += 1;
					contPlayer = 0;
				}
				else if (state.getSpace(x, p.y) == 0) {
					hRank += emptyRank;
					contOpponent = 0;
					contPlayer = 0;
				}
				else if (state.getSpace(x, p.y) == player) {
					contPlayer += 1;
					hRank += pieceRank;
					contOpponent = 0;
				}
			}
			contOpponent = 0;
			contPlayer = 0;
			
			// bottom to top
			for (int x = p.x, k = 0; x >= 0; x--, k++) {
				if (contPlayer == state.getkLength() - 2)
					hRank += twoMovesAwayRank;
				else if (contPlayer == state.getkLength() - 1) {
					System.out.println(state.toString());
					hRank += oneMovesAwayRank;
				}
//				else if (contOpponent == (state.getkLength()) - 1)
//					return MAX_BLOCK_RANK;
				else if (k == state.getkLength())
					break;
				else if (state.getSpace(x, p.y) == opponent) {
					contOpponent += 1;
					contPlayer = 0;
				}

				else if (state.getSpace(x, p.y) == 0) {
					hRank += emptyRank;
					contOpponent = 0;
					contPlayer = 0;
				}
				else if (state.getSpace(x, p.y) == player) {
					contPlayer += 1;
					hRank += pieceRank;
					contOpponent = 0;
				}
			}
			contOpponent = 0;
			contPlayer = 0;
			
			// left to right
			for (int y = p.y, k = 0; y < state.getHeight(); y++, k++) {
				if (contPlayer == state.getkLength() - 2)
					hRank += twoMovesAwayRank;
				else if (contPlayer == state.getkLength() - 1) {
					System.out.println(state.toString());
					hRank += oneMovesAwayRank;
				}
//				else if (contOpponent == (state.getkLength()) - 1)
//					return MAX_BLOCK_RANK;
				else if (k == state.getkLength())
					break;
				else if (state.getSpace(p.x, y) == opponent) {
					contOpponent += 1;
					contPlayer = 0;
				}
				else if (state.getSpace(p.x, y) == 0) {
					hRank += emptyRank;
					contOpponent = 0;
					contPlayer = 0;
				}
				else if (state.getSpace(p.x, y) == player) {
					contPlayer += 1;
					hRank += pieceRank;
					contOpponent = 0;
				}
			}
			contOpponent = 0;
			contPlayer = 0;
			
			// right to left
			for (int y = p.y, k = 0; y >= 0; y--, k++) {
				if (contPlayer == state.getkLength() - 2)
					hRank += twoMovesAwayRank;
				else if (contPlayer == state.getkLength() - 1) {
					System.out.println(state.toString());
					hRank += oneMovesAwayRank;
				}
//				else if (contOpponent == (state.getkLength()) - 1)
//					return MAX_BLOCK_RANK;
				else if (k == state.getkLength())
					break;
				else if (state.getSpace(p.x, y) == opponent) {
					contOpponent += 1;
					contPlayer = 0;
				}
				else if (state.getSpace(p.x, y) == 0) {
					hRank += emptyRank;
					contOpponent = 0;
					contPlayer = 0;
				}
				else if (state.getSpace(p.x, y) == player) {
					contPlayer += 1;
					hRank += pieceRank;
					contOpponent = 0;
				}
			}
			contOpponent = 0;
			contPlayer = 0;
			
			// upper-right
			try {
				
				int y = p.y;
				for (int x = p.x, k = 0; x >= 0; x--, k++) {
					if (contPlayer == state.getkLength() - 2)
						hRank += twoMovesAwayRank;
					else if (contPlayer == state.getkLength() - 1) {
						System.out.println(state.toString());
						hRank += oneMovesAwayRank;
					}
//					else if (contOpponent == (state.getkLength()) - 1)
//						return MAX_BLOCK_RANK;
					else if (k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == opponent) {
						contOpponent += 1;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == 0) {
						hRank += emptyRank;
						contOpponent = 0;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == player) {
						contPlayer += 1;
						hRank += pieceRank;
						contOpponent = 0;
					}
					y++;
				}
			} catch (IndexOutOfBoundsException e) {}
			finally {
				contOpponent = 0;
				contPlayer = 0;
			}
			
			
			// lower-left
			try {
				
				int y = p.y;
				for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
					if (contPlayer == state.getkLength() - 2)
						hRank += twoMovesAwayRank;
					else if (contPlayer == state.getkLength() - 1) {
						System.out.println(state.toString());
						hRank += oneMovesAwayRank;
					}
//					else if (contOpponent == (state.getkLength()) - 1)
//						return MAX_BLOCK_RANK;
					else if (k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == opponent) {
						contOpponent += 1;
						contPlayer = 0;
					}
					else if (state.getSpace(x ,y) == 0) {
						hRank += emptyRank;
						contOpponent = 0;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == player) {
						contPlayer += 1;
						hRank += pieceRank;
						contOpponent = 0;
					}
					y--;
				}
			} catch (IndexOutOfBoundsException e) {}
			finally {
				contOpponent = 0;
				contPlayer = 0;
			}
			
			// upper-left
			try {
				int y = p.y;
				for (int x = p.x, k = 0; x >= 0; x--, k++) {
					if (contPlayer == state.getkLength() - 2)
						hRank += twoMovesAwayRank;
					else if (contPlayer == state.getkLength() - 1) {
						System.out.println(state.toString());
						hRank += oneMovesAwayRank;
					}
//					else if (contOpponent == (state.getkLength()) - 1)
//						return MAX_BLOCK_RANK;
					else if (k == state.getkLength())
						break;
					else if (state.getSpace(x, y) == opponent) {
						contOpponent += 1;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == 0) {
						hRank += emptyRank;
						contOpponent = 0;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == player) {
						contPlayer += 1;
						hRank += pieceRank;
						contOpponent = 0;
					}
					
					y--;
				}
			} catch (IndexOutOfBoundsException e) {}
			finally {
				contOpponent = 0;
				contPlayer = 0;
			}
		
			// lower-right
			try {	
				
				int y = p.y;
				for (int x = p.x, k = 0; x < state.getWidth(); x++, k++) {
					if (contPlayer == state.getkLength() - 2)
						hRank += twoMovesAwayRank;
					else if (contPlayer == state.getkLength() - 1) {
						System.out.println(state.toString());
						hRank += oneMovesAwayRank;
					}
//					else if (contOpponent == (state.getkLength()) - 1)
//						return MAX_BLOCK_RANK;
					else if (k == state.getkLength())
						break;
					else if (this.state.getSpace(x, y) == opponent) {
						contOpponent += 1;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == 0) {
						hRank += emptyRank;
						contOpponent = 0;
						contPlayer = 0;
					}
					else if (state.getSpace(x, y) == player) {
						contPlayer += 1;
						hRank += pieceRank;
						contOpponent = 0;
					}
					y++;
				}
			} catch (IndexOutOfBoundsException e) {}
			finally {
				contOpponent = 0;
				contPlayer = 0;
			}
					
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
//			board = board.placePiece(new Point(2,1), (byte) 1);
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