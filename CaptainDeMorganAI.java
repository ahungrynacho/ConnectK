import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.Random;
import java.util.HashSet;
import java.util.Scanner;

public class CaptainDeMorganAI extends CKPlayer {
	private int cutOff = 6;
	private int alpha = Integer.MIN_VALUE; // eventually becomes the largest value along Max's path
	private int beta = Integer.MAX_VALUE; // eventually becomes the smallest value along Min's path
	private int maxNodes = Integer.MAX_VALUE;
	private Scanner input = new Scanner(System.in);
	
	// CaptainDeMorgainAI's arg constructor calls CKPlayer's arg constructor
	public CaptainDeMorganAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "CaptainDeMorganAI";
	}
	
	@Override
	public Point getMove(BoardModel state) {
		Node n = new Node(state, 0, null, this.cutOff);
		Point move = this.minimax(n, this.alpha, this.beta).getMove(); // initialized to null if AI is player 1 making the first move
		
		int nodes = Node.getNodeCount();
		System.out.println("Total Nodes Computed:" + nodes);
		
		if (nodes > maxNodes) {
			System.out.println("Node.getNodeCount() > maxNodes: " + move);
			input.nextLine();
		}
		else {
			maxNodes = nodes;
			Node.resetNodeCount();
		}
		
		return move;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	private Node minimax(Node n, int alpha, int beta) {
//		gravity on: branching factor = width
//		gravity off: branching factor = width * height

		HashSet<Point> nextMoves = n.nextMoves(n.getState().gravityEnabled());
		
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), n.getDepth()+1, p, n.getCutoff()); // currently at depth = 1; lastMove() returns 2
			Node.incNodeCount(1);
			int rank = this.min(nextNode, nextNode.getDepth(), alpha, beta).getRank();
			
			if (rank > alpha)
				alpha = rank;
			
			if (rank > bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}
		}
		bestNode.setRank(bestRank);
		return bestNode;
		
	}
	
		
	private Node min(Node n, int depth, int alpha, int beta) {
		// minimize player 1

		if (n.getState().winner() != -1) { // base case 1: no more available moves
			n.evalGame();
			return n;
		}
		
		if (n.getDepth() >= n.getCutoff()) { // base case 2: depth cut-off reached so rank current state with the heuristic function
			n.setRank(-n.heuristic(n.getMove())); // negative heuristic value
			return n;
		}
		
		HashSet<Point> nextMoves = n.nextMoves(n.getState().gravityEnabled());
		int bestRank = Integer.MAX_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p, n.getCutoff());
			Node.incNodeCount(1);
			int rank = max(nextNode, nextNode.getDepth(), alpha, beta).getRank();
			
			if (rank < beta)
				beta = rank;
			
			if (rank < bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}
			
			if (alpha >= beta)
				break;
		}
				
		bestNode.setRank(bestRank);
		return bestNode;
	}


	private Node max(Node n, int depth, int alpha, int beta) {
		// maximize player 2
		if (n.getState().winner() != -1) { // base case 1
			n.evalGame();
			return n;
		}
		
		if (n.getDepth() >= n.getCutoff()) { // base case 2: depth cut-off reached so rank current state with the heuristic function
			n.setRank(n.heuristic(n.getMove())); // positive heuristic value
			return n;
		}
		
		HashSet<Point> nextMoves = n.nextMoves(n.getState().gravityEnabled());
		int bestRank = Integer.MIN_VALUE;
		Node bestNode = null;
		
		for (Point p : nextMoves) {
			Node nextNode = new Node(n.nextState(p), depth+1, p, n.getCutoff());
			Node.incNodeCount(1);
			int rank = min(nextNode, nextNode.getDepth(), alpha, beta).getRank();
			
			if (rank > alpha)
				alpha = rank;
			
			if (rank > bestRank) {
				bestRank = rank;
				bestNode = nextNode;
			}
			
			if (alpha >= beta)
				break;

		}
		
		bestNode.setRank(bestRank);
		return bestNode;
	}
		
}
