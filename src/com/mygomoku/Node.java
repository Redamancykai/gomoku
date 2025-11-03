package com.mygomoku;

import java.util.*;

public class Node {
	private static final double PARAM_C = 2.0; 
    
    public Node parent;
    public ArrayList<Node> children;
    public int player;
    public ChessBoard board;
    public Move move;  
    public double reward;
    public int visits;
    public ArrayList<Move> untriedMoves;
    
    public Node(int player, ChessBoard board, Node parent, Move move) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.player = player;
        this.board = board;
        this.move = move;
        this.reward = 0.0;
        this.visits = 0;
        this.untriedMoves = new ArrayList<>(this.board.getLegalActions(this.player)); 
    }
    
    private int getOpponentPlayer(int player) {
        return (player == ChessBoard.BLACK) ? ChessBoard.WHITE : ChessBoard.BLACK;
    }
    
    public boolean isFullyExpanded() {
        return untriedMoves.isEmpty();
    }
    
    protected boolean isGameover() {
    	return this.board.isWinning(move.x, move.y, move.player);
    }
    
    private double calculateReward(int winner, int rootplayer) {
        if (winner == 0) {
            return 0.5;
        }
        if (winner == rootplayer) {
            return 1.0;
        }
        return 0.0; 
    }

    public double calculateUCB() {
        if (visits == 0) {
            return Double.MAX_VALUE;
        }
        return reward / visits + PARAM_C * Math.sqrt(2 * Math.log(parent.visits) / visits);
    }
    
    // 核心函数
    public Node select() {
        Node bestChild = null;
        double bestUCB = Double.NEGATIVE_INFINITY;
        
        for (Node child : children) {
            double ucb = child.calculateUCB();
            if (ucb > bestUCB) {
                bestChild = child;
                bestUCB = ucb;
            }
        }
        return bestChild;
    }
    
    public Node expand() {
        Move move = untriedMoves.remove(untriedMoves.size() - 1); 
        ChessBoard newBoard = board.deepCopy();
        newBoard.simMove(move);

        int nextplayer = getOpponentPlayer(this.player);
        Node childNode = new Node(nextplayer, newBoard, this, move);
        this.children.add(childNode);
        return childNode;
    }
    
    public double simulate(Node rootNode) {
        ChessBoard currentBoard = board.deepCopy();
        int currentPlayer = this.player;
        
        Random random = new Random();
        int winner = 0;
        while (true) {
            List<Move> legalMoves = currentBoard.getLegalActions(currentPlayer);
            Move move = legalMoves.get(random.nextInt(legalMoves.size()));
            currentBoard.simMove(move);
            currentPlayer = getOpponentPlayer(currentPlayer);
            if(currentBoard.isWinning(move.x, move.y, currentPlayer)) {
            	winner = currentPlayer;
            	break;
            }
        }
        return calculateReward(winner, rootNode.player);
    }

    public void backPropagate(double addReward) {
        this.visits++;
        this.reward += addReward;

        if (this.parent != null) {
            this.parent.backPropagate(addReward);
        }
    }

}
