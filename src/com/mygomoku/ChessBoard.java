package com.mygomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ChessBoard extends JPanel {

    private static final int GRID_SIZE = 15;
    private static final int CELL_SIZE = 40;
    private static final int MARGIN = 60;
    private static final int STAR_RADIUS = 4;
    private static final int PIECE_RADIUS = 15;  
    private static final int WIDTH = CELL_SIZE * (GRID_SIZE - 1) + 2 * MARGIN;
    private static final int HEIGHT = CELL_SIZE * (GRID_SIZE - 1) + 2 * MARGIN;
    protected static final int BLACK = 1;
    protected static final int WHITE = 2;
    
    private int[][] ChessBoardState = new int[GRID_SIZE][GRID_SIZE];
    private int currentPlayer = BLACK;
    private boolean GameOver = false;
    private ArrayList<Move> history = new ArrayList<>(); 
    private int reviewIndex = 0;
    private boolean isReviewing = false;

    public ChessBoard() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(245, 222, 179)); 

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMove(e.getX(), e.getY());
            }
        });
    }
    
    // 深拷贝
    public ChessBoard deepCopy() {
        ChessBoard newBoard = new ChessBoard();
        for (int x = 0; x < GRID_SIZE; x++) {
            System.arraycopy(this.ChessBoardState[x], 0, newBoard.ChessBoardState[x], 0, GRID_SIZE);
        }
        newBoard.currentPlayer = this.currentPlayer;
        newBoard.GameOver = this.GameOver;
        return newBoard;
    }

    // 处理下棋
    private void handleMove(int px, int py) {	
    	if(GameOver || isReviewing) {
    		return;
    	}
    	
        int x = Math.round((float)(px - MARGIN) / CELL_SIZE);
        int y = Math.round((float)(py - MARGIN) / CELL_SIZE);

        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            return;
        }
        if (ChessBoardState[x][y] == 0) {
            ChessBoardState[x][y] = currentPlayer;
            history.add(new Move(x, y, currentPlayer));
            
            repaint();
            
            // 判定胜利
            if(isWinning(x, y, currentPlayer)) {
            	GameOver = true;
            	String winner = (currentPlayer == BLACK ? "黑方" : "白方");
                JOptionPane.showMessageDialog(this, winner + " 获胜了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            }

            if(!GameOver) {
            	currentPlayer = (currentPlayer == BLACK ? WHITE : BLACK);
            }

        } 
    }
    
    
    // 判定胜利条件
    protected boolean isWinning(int x, int y, int player) {
        // 检查左右方向
        int count1 = 0;
        int cur_x = x;
        int cur_y = y;
        while (cur_y < GRID_SIZE && ChessBoardState[cur_x][cur_y] == player) {
            count1++;
            cur_y++;
        }
        cur_y = y - 1;
        while (cur_y >= 0 && ChessBoardState[cur_x][cur_y] == player) {
            count1++;
            cur_y--;
        }       
        if (count1 >= 5) {
            return true;
        }

        // 检查上下方向
        int count2 = 0;
        cur_x = x;
        cur_y = y;
        while (cur_x < GRID_SIZE && ChessBoardState[cur_x][cur_y] == player) {
            count2++;
            cur_x++;
        }
        cur_x = x - 1;
        while (cur_x >= 0 && ChessBoardState[cur_x][cur_y] == player) {
            count2++;
            cur_x--;
        }       
        if (count2 >= 5) {
            return true;
        }

        // 检查对角线
        int count3 = 0;
        cur_x = x;
        cur_y = y;
        while (cur_x < GRID_SIZE && cur_y < GRID_SIZE && ChessBoardState[cur_x][cur_y] == player) {
            count3++;
            cur_x++;
            cur_y++;
        }
        cur_x = x - 1;
        cur_y = y - 1;
        while (cur_x >= 0 && cur_y >= 0 && ChessBoardState[cur_x][cur_y] == player) {
            count3++;
            cur_x--;
            cur_y--;
        }
        if (count3 >= 5) {
            return true;
        }
        int count4 = 0;
        cur_x = x;
        cur_y = y;
        while (cur_x < GRID_SIZE && cur_y >= 0 && ChessBoardState[cur_x][cur_y] == player) {
            count4++;
            cur_x++;
            cur_y--;
        }
        cur_x = x - 1;
        cur_y = y + 1;
        while (cur_x >= 0 && cur_y < GRID_SIZE && ChessBoardState[cur_x][cur_y] == player) {
            count4++;
            cur_x--;
            cur_y++;
        }
        if (count4 >= 5) {
            return true;
        }

        return false;
    }
    
    // 悔棋
    protected void Undo() {
    	if(history.isEmpty()) {
    		return;
    	}
    	Move lastMove = history.remove(history.size() - 1);
    	ChessBoardState[lastMove.x][lastMove.y] = 0;
    	currentPlayer = lastMove.player;
    	GameOver = false;
    	repaint();
    }
    
    // 复盘部分函数  
    protected void prepareReview() {
    	if(history.isEmpty()) {
    		return;
    	}
    	
    	GameOver = false;
    	isReviewing = true;
    	
    	for(int i = 0; i < GRID_SIZE; i++) {
    		for(int j = 0; j < GRID_SIZE; j++) {
    			ChessBoardState[i][j] = 0;
    		}
    	}
    	
    	repaint();
    }

    protected void startReview() {
    	Move reviewMove = history.get(reviewIndex);
    	ChessBoardState[reviewMove.x][reviewMove.y] = reviewMove.player;
    	reviewIndex++;
    	repaint();
    }
    
    protected boolean finishReview() {
    	if(reviewIndex >= history.size()) {
    		isReviewing = false;
    		reviewIndex = 0;
    		repaint();
    		return true;
    	}
    	return false;
    }
    
    protected boolean getReviewStatus() {
    	return isReviewing;
    }
    
    // 绘图
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        // 抗锯齿
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);

        // 画棋盘线
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(1.5f));

        for (int i = 0; i < GRID_SIZE; i++) {
            int x = MARGIN + i * CELL_SIZE;
            int y = MARGIN + i * CELL_SIZE;
            g2D.drawLine(x, MARGIN, x, HEIGHT - MARGIN);
            g2D.drawLine(MARGIN, y, WIDTH - MARGIN, y);
        }
        
        // 画星位
        for(int i = 3; i <= 11; i += 4) {
        	for(int j = 3; j <= 11 ;j += 4) {
        		int x = MARGIN + i * CELL_SIZE - STAR_RADIUS;
                int y = MARGIN + j * CELL_SIZE - STAR_RADIUS;
                g2D.fillOval(x, y, 2 * STAR_RADIUS, 2 * STAR_RADIUS);
        	}
        }
        

        // 画棋子
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                
                if (ChessBoardState[x][y] != 0) {
                    int px = MARGIN + x * CELL_SIZE - PIECE_RADIUS;
                    int py = MARGIN + y * CELL_SIZE - PIECE_RADIUS;

                    if (ChessBoardState[x][y] == BLACK) {
                        g2D.setColor(Color.BLACK);
                    } 
                    else {
                        g2D.setColor(Color.WHITE); 
                    }

                    g2D.fillOval(px, py, 2 * PIECE_RADIUS, 2 * PIECE_RADIUS);
                    g2D.setColor(Color.BLACK);
                    g2D.drawOval(px, py, 2 * PIECE_RADIUS, 2 * PIECE_RADIUS);
                }
            }
        }
        
    }
    
    
    // 和AI有关的辅助函数
    public ArrayList<Move> getLegalActions(int color) {
        ArrayList<Move> legalMoves = new ArrayList<>();

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if (ChessBoardState[x][y] == 0) {
                    legalMoves.add(new Move(x, y, color)); 
                }
            }
        }
        return legalMoves;
    }
    
    public void simMove(Move move) {
    	this.ChessBoardState[move.x][move.y] = move.player;
    }
}