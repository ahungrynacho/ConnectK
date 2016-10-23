import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;

public class CaptainDeMorganAI3 extends CKPlayer {
	private int cutOff = 4;
	// CaptainDeMorgainAI's arg constructor calls CKPlayer's arg constructor
	public CaptainDeMorganAI3(byte player, BoardModel state) {
		super(player, state);
		teamName = "CaptainDeMorganAI";
	}
	
	@Override
	public Point getMove(BoardModel state) {
		int initDepth = this.depth(state);
		Node n = new Node(state, initDepth, null, this.cutOff);
		
		Point move = this.minimax(n).getMove(); // initialized to null if AI is player 1 making the first move
		System.out.println("Node Count: " + Node.getNodeCount());
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
	
	private Node minimax(Node n) {
		HashSet<Point> nextMoves = n.nextMoves();
//		Node.incNodeCount(nextMoves.size());
		
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
//			System.out.println("Last Move: " + n.getState().getSpace(n.getState().getLastMove()));
			Node nextNode = new Node(n.nextState(p), n.getDepth()+1, p, n.getCutoff()); // currently at depth = 1; lastMove() returns 2
			
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

		if (n.getState().winner() != -1) { // base case 1: no more available moves
			n.evalGame();
			return n;
		}
		
		if (n.getDepth() >= n.getCutoff()) { // base case 2: depth cut-off reached so rank current state with the heuristic function
			n.setRank(-n.heuristic(n.getMove())); // negative heuristic value
			return n;
		}
		
		HashSet<Point> nextMoves = n.nextMoves();
		int bestRank = Integer.MAX_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p, n.getCutoff());
			
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
		if (n.getState().winner() != -1) { // base case 1
			n.evalGame();
			return n;
		}
		
		if (n.getDepth() >= n.getCutoff()) { // base case 2: depth cut-off reached so rank current state with the heuristic function
			n.setRank(n.heuristic(n.getMove())); // positive heuristic value
			return n;
		}
		
		HashSet<Point> nextMoves = n.nextMoves();
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p, n.getCutoff());
			
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
