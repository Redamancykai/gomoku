package com.mygomoku;

public class Tree {
	private final double timeLimit;

    public Tree(double timeLimit) {
        this.timeLimit = timeLimit;
    }
    
    public Move bestChoice(ChessBoard board, int color) {
        Node rootNode = new Node(color, board.deepCopy(), null, null);

        if (rootNode.untriedMoves.isEmpty()) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (long) (timeLimit * 1000);

        while (System.currentTimeMillis() < endTime) {
            Node node = rootNode;
            while (node.isFullyExpanded() && !node.isGameover()) {
                node = node.select();
            }
            if (!node.isGameover()) {
                node = node.expand();
            }
            double addReward = node.simulate(rootNode);
            node.backPropagate(addReward);
        }

        Node bestChild = null;
        int maxVisits = -1;
        for (Node child : rootNode.children) {
            if (child.visits > maxVisits) {
                bestChild = child;
                maxVisits = child.visits;
            }
        }

        return (bestChild != null) ? bestChild.move : null;
    }
}
