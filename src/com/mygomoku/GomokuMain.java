package com.mygomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GomokuMain extends JFrame {
	private static final int REVIEW_INTERVAL = 1000;
	
	private ChessBoard Chessboard;
	private JButton undoButton;
	private JButton autoReviewButton;
	private JButton nextReviewButton;
	private Timer reviewTimer;
	
    public GomokuMain() {
        Chessboard = new ChessBoard();
        
        setLayout(new BorderLayout()); 
        setTitle("五子棋");
        add(Chessboard, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        // 悔棋按钮
        undoButton = new JButton("悔棋");
        undoButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                Chessboard.Undo(); 
            }
        });
        undoButton.setBackground(Color.WHITE);
        
        // 自动复盘按钮
        autoReviewButton = new JButton("自动复盘");
        reviewTimer = new Timer(REVIEW_INTERVAL, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                if (!Chessboard.finishReview()) {
                	Chessboard.startReview();
                }
                else {
                	reviewTimer.stop();
                }
            }
		});        
        autoReviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Chessboard.getReviewStatus()) {
	                Chessboard.prepareReview();					
				}
                reviewTimer.start();
			}
		});
        autoReviewButton.setBackground(Color.WHITE);
        
        // 手动复盘按钮
        nextReviewButton = new JButton("复盘下一步");
        nextReviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reviewTimer.stop();
				if(!Chessboard.getReviewStatus()) {
					Chessboard.prepareReview();
				}
				else {
					if(!Chessboard.finishReview()) {
						Chessboard.startReview();
					}	
				}	
			}
		});
        nextReviewButton.setBackground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(undoButton);
        buttonPanel.add(autoReviewButton);
        buttonPanel.add(nextReviewButton);
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBackground(new Color(245, 222, 179));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GomokuMain::new);
    }
}
