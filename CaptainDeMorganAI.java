import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;

// initial attempt
public class CaptainDeMorganAI extends CKPlayer {
	private byte HUMAN = 1;
	private byte AI;
	
	// CaptainDeMorgainAI's arg constructor calls CKPlayer's arg constructor
	public CaptainDeMorganAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "CaptainDeMorganAI";
		AI = this.player;
	}
	

	@Override
	public Point getMove(BoardModel state) {
		
//		return this.minimax(state);
//		Random rand = new Random();
//		// RANDOM SELECT
//		while (state.hasMovesLeft()) {
//			int x = rand.nextInt(state.getWidth()); // nextInt returns [0, n) non-inclusive
//			int y = rand.nextInt(state.getHeight());
//			if (state.getSpace(x,y) == 0)
//				return new Point(x,y);
//		}
		return minimax(state);
//		return null;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	private Point minimax(BoardModel state) {

//		HashMap<Integer,Point> moves = new HashMap<Integer,Point>();
//		int winningRank = Collections.max(moves.keySet());
		
		
		HashSet<Point> nextMoves = this.nextMoves(state);
		Point bestMove = null;
		int bestScore = Integer.MAX_VALUE;
		
		for (Point m : nextMoves) {
			BoardModel nextState = this.nextState(state, m); // apply player 2's move
//			System.out.println(nextState.toString());
			int score = min(nextState); // player 1's turn
			
			if (score < bestScore) {
				bestScore = score;
				bestMove = m;
			}
		}
		return bestMove;
		
	}
		
	private int min(BoardModel state) {
		if (state.winner() != -1) // if a winner or draw is detected
			return evalWinner(state);
		
		HashSet<Point> nextMoves = this.nextMoves(state);
//		System.out.println("min: " + this.currentPlayer(state));
//		System.out.println(nextMoves);
		int bestScore = Integer.MAX_VALUE;
		
		for (Point m : nextMoves) {
			BoardModel nextState = this.nextState(state, m);
//			System.out.println(nextState.toString());
			int score = max(nextState);
			
			if (score < bestScore)
				bestScore = score;
		}
//		System.out.println("---------");
		return bestScore;
	}
	
	private int max(BoardModel state) {
		if (state.winner() != -1) // if a winner or draw is detected
			return evalWinner(state);
		
		HashSet<Point> nextMoves = this.nextMoves(state);
//		HashMap<Integer,Point> finalStates = new HashMap<Integer,Point>();
//		System.out.println("max: " + this.currentPlayer(state));
		int bestScore = Integer.MIN_VALUE;
		
		for (Point m : nextMoves) {
			BoardModel nextState = this.nextState(state, m);
//			System.out.println(nextState.toString());
			int score = min(nextState);
			
			if (score > bestScore)
				bestScore = score;
		}
//		System.out.println("---------");
		return bestScore;
	}
	
	private int evalWinner(BoardModel state) {
		// while the game progresses before a winner or draw is determined, winner() returns -1
		if (state.winner() == 1) // current player
			return 1;
		else if (state.winner() == 2) // opposing player
			return -1;
		
		return 0; // draw
	}
	
	HashSet<Point> nextMoves(BoardModel state) {
		HashSet<Point> moves = new HashSet<Point>();
		
		for (int x = 0; x < state.getWidth(); ++x) {
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {
					moves.add(new Point(x,y));
				}
			}
		}

		return moves;
		
	}
	
	private String currentPlayer(BoardModel state) {
		byte player = state.getSpace(state.getLastMove());
		if (player == 1)
			return "player 2";
		else if (player == 2)
			return "player 1";
		else
			return "player 1's first move"; // player == 0; the game just started
	}
	
//	private int lastPlayer(BoardModel state) {
//		byte player = state.getSpace(state.getLastMove());
//		
//		if (player == 1) 
//			return 1;
//		else if (player == 2)
//			return 2;
//		else
//			return 0;
//	}
	
	private BoardModel nextState(BoardModel state, Point move) {
		
		BoardModel nextState = state.clone();
		String currentPlayer = this.currentPlayer(state);
		
		if (currentPlayer.equalsIgnoreCase("player 1"))
			return nextState.placePiece(move, (byte) 1);
		else if (currentPlayer.equalsIgnoreCase("player 2"))
			return nextState.placePiece(move, (byte) 2);
		else
			return nextState.placePiece(move, (byte) 1); // player 1's first move; the game just started
	}
		
}
