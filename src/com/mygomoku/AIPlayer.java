package com.mygomoku;

public class AIPlayer {
    private final int player;
    private final Tree tree;

    public AIPlayer(int player) {
        this.player = player;
        this.tree = new Tree(10.0); 
    }

    public Move getMove(ChessBoard board) {
        Move action = this.tree.bestChoice(board, this.player);     
        return action;
    }
}
