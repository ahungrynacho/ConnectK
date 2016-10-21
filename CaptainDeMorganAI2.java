import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;

// minimax only
public class CaptainDeMorganAI2 extends CKPlayer {

	// CaptainDeMorgainAI's arg constructor calls CKPlayer's arg constructor
	public CaptainDeMorganAI2(byte player, BoardModel state) {
		super(player, state);
		teamName = "CaptainDeMorganAI";
	}
	
	@Override
	public Point getMove(BoardModel state) {
		int initDepth = this.depth(state);
		Node n = new Node(state, initDepth, null);
		Point move = this.minimax(n).getMove(); // initialized to null if AI is player 1 making the first move
		return move;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	public int depth(BoardModel state) {
		int depth = 0;
		for (int x = 0; x < state.getWidth(); ++x) {
			for (int y = 0; y < state.getHeight(); ++y) {
				if (state.getSpace(x, y) != 0)
					++depth;
			}
		}
		return depth;
	}
	
	public class Node {
		private int rank;
		private int depth;
		private BoardModel state;
		private Point move; // move previously made by the current player to get to Node's (belonging to current player) current state
		private int player; // current player
		private int opponent;
		
//		// when AI makes its first move
//		Node(BoardModel state, int depth) {
//			this.depth = depth;
//			this.state = state.clone();
//		}
		
		Node(BoardModel state, int depth, Point move) {
			this.depth = depth;
			this.state = state.clone();
			this.move = move;
			this.player = this.currentPlayer();
			this.opponent = this.opponentPlayer();
		}
		
		// cache this value
		public int movesLeft() {
			int moveCount = 0;
			for (int x = 0; x < this.state.getWidth(); ++x) {
				for (int y = 0; y < this.state.getHeight(); ++y) {
					if (this.state.getSpace(x,y) == 0) {
						++moveCount;
					}
				}
			}
			return moveCount;
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
			
//			return this.state.getSpace(move);
		}
		
		public BoardModel nextState(Point move) {
			
			BoardModel nextState = this.state.clone();
			
			return nextState.placePiece(move, (byte) this.player);
//			if (this.currentPlayer() == 1)
//				return nextState.placePiece(move, (byte) 1);
//			else // currentPlayer.equalsIgnoreCase("player 2")
//				return nextState.placePiece(move, (byte) 2);
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
	
	
	private Node minimax(Node n) {
		
		HashSet<Point> nextMoves = n.nextMoves();
		
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
//			System.out.println("Last Move: " + n.getState().getSpace(n.getState().getLastMove()));
			Node nextNode = new Node(n.nextState(p), n.getDepth()+1, p); // currently at depth = 1; lastMove() returns 2
			int rank = this.min(nextNode, nextNode.getDepth()).getRank();
//			System.out.println("-------------------------");
//			System.out.println("player: " + nextNode.getPlayer() + " opponent: " + nextNode.getOpponent());
//			System.out.println(nextNode.getState().toString() + "Rank: " + rank);
//			System.out.println("-------------------------");
			
			if (rank > bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}
		}
		bestNode.setRank(bestRank);
		return bestNode;
		
	}
		
	private Node min(Node n, int depth) {
		// minimize player 1
		if (n.getState().winner() != -1) {
			System.out.println("min: player " + n.getPlayer());
			n.evalGame();
			return n;
		}	
		
		HashSet<Point> nextMoves = n.nextMoves();
		int bestRank = Integer.MAX_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p);
			
			int rank = max(nextNode, nextNode.getDepth()).getRank();
			
			if (rank < bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}

		}
		bestNode.setRank(bestRank);
		return bestNode;
	}
	
	private Node max(Node n, int depth) {
		// maximize player 2
		if (n.getState().winner() != -1) {
			System.out.println("max: player " + n.getPlayer());
			n.evalGame();
			return n;
		}
		
		HashSet<Point> nextMoves = n.nextMoves();
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p);
			
			int rank = min(nextNode, nextNode.getDepth()).getRank();
			
			if (rank > bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}

		}
		bestNode.setRank(bestRank);
		return bestNode;
	}
		
}
