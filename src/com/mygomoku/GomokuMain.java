package com.mygomoku;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.sound.sampled.*;

class GomokuMain extends JFrame {
	private static final int REVIEW_INTERVAL = 1000;
	private int countdown = 30;
	private boolean musicOn = false;
	private int gameMode;
	
	private ChessBoard Chessboard;
	
	private JButton undoButton;
	private JButton autoReviewButton;
	private JButton nextReviewButton;
	private JButton resetButton;
	private JButton musicButton;
	private JButton returnButton;
	
	private Timer reviewTimer;
	private Timer countdownTimer;
	
	private JLabel countdownLabel;
	
	private Clip bgMusic;
	private Clip moveSound;
	
	private ChatPanel chatPanel;
	
	private AIPlayer aiPlayer;
	
    public GomokuMain(int mode) {
    	this.gameMode = mode;
        Chessboard = new ChessBoard();
        
        setLayout(new BorderLayout()); 
        setTitle("五子棋");
        add(Chessboard, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        if(gameMode == 1) {
        	aiPlayer = new AIPlayer(ChessBoard.WHITE);
        }
        
        // 聊天面板
        chatPanel = new ChatPanel();
        
        add(chatPanel, BorderLayout.EAST);
        
        // 悔棋按钮
        undoButton = createButton("悔棋");
        undoButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                Chessboard.Undo(); 
            }
        });
        
        // 自动复盘按钮
        autoReviewButton = createButton("自动复盘");
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
        
        // 手动复盘按钮
        nextReviewButton = createButton("复盘下一步");
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
        
        // 重置棋盘按钮
        resetButton = createButton("重置棋盘");
        resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Chessboard.resetBoard();
			}
		});
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(undoButton);
        buttonPanel.add(autoReviewButton);
        buttonPanel.add(nextReviewButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBackground(new Color(245, 222, 179));
        buttonPanel.setBorder(new EmptyBorder(0, 90, 30, 0));
        
        // 落子反馈
        Chessboard.setMoveListener(new Runnable() {
			
			@Override
			public void run() {
				// 音效
				playMoveSound();
				
				// 倒计时
				if(Chessboard.isGameOver()) {
					countdownTimer.stop();
				}
				else {
					resetCountdown();
					checkAITurn();
				}
			}
		});
        
        startCountdownTimer();
        
        // 背景音乐按钮
        musicButton = createButton("音乐：关");
        musicButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!musicOn) {
					playMusic();
					musicButton.setText("音乐：开");
				}
				else {
					stopMusic();
					musicButton.setText("音乐：关");
				}
			}
		});
        
        startBgMusic();
        
        startMoveSound();
        
        // 返回主界面按钮
        returnButton = createButton("返回");
        returnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				countdownTimer.stop();
				reviewTimer.stop();
				stopMusic();
				dispose();
				SwingUtilities.invokeLater(GameHall::new);
			}
		});
        
        // 顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(245, 222, 179));
        
        JLabel countdownTimerTitle = new JLabel("剩余落子时间: ");
        countdownTimerTitle.setFont(new Font("宋体", Font.BOLD, 16));
        countdownLabel = new JLabel(countdown + "s");
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 20));
        countdownLabel.setForeground(Color.RED);
        
        topPanel.add(countdownTimerTitle);
        topPanel.add(countdownLabel);
        topPanel.add(musicButton);
        topPanel.add(returnButton);
        add(topPanel, BorderLayout.NORTH);
    }
    
    // 背景音乐辅助函数
    private void startBgMusic() {
    	try {
            File musicFile = new File("bgm.wav"); 
            if (musicFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
                bgMusic = AudioSystem.getClip();
                bgMusic.open(audioInput);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    
    private void playMusic() {
    	if(bgMusic != null) {
    		bgMusic.setFramePosition(0);
    		bgMusic.loop(Clip.LOOP_CONTINUOUSLY); //循环播放
    		bgMusic.start();
    		musicOn = true;
    	}
    }
    
    private void stopMusic() {
    	if(bgMusic != null && musicOn == true) {
    		bgMusic.stop();
    		musicOn = false;
    	}
    }
    
    // 落子音效辅助函数
    private void startMoveSound() {
    	try {
            File soundFile = new File("movesound.wav");
            if (soundFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
                moveSound = AudioSystem.getClip();
                moveSound.open(audioInput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void playMoveSound() {
    	if(moveSound != null) {
    		moveSound.setFramePosition(0);
    		moveSound.start();
    	}
    }

	// 倒计时辅助函数
    private void startCountdownTimer() {
        countdownTimer = new Timer(1000, new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                countdownLabel.setText(countdown + "s");
                
                if (countdown <= 0) {
                    countdownTimer.stop();
                    JOptionPane.showMessageDialog(GomokuMain.this, "落子时间到！请尽快落子。", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        countdownTimer.start();
    }
    
    protected void resetCountdown() {
    	countdown = 30;
        countdownLabel.setText(countdown + "s");
        countdownTimer.restart();
	}
    
    // 管理按钮
    private JButton createButton(String text) {
    	JButton btn = new JButton(text);
        btn.setFont(new Font("华文行楷", Font.PLAIN, 24));
        btn.setFocusPainted(false);
        btn.setForeground(Color.BLACK);
        btn.setBackground(Color.WHITE);
        return btn;
    }
    
    // AI辅助函数
    private void checkAITurn() {
    	if (gameMode == 1 && Chessboard.getCurrentPlayer() == ChessBoard.WHITE && !Chessboard.isGameOver()) {
            Chessboard.setAIthinking(true);
            
            // 利用多线程进行AI计算
            new Thread(() -> {
                Move move = aiPlayer.getMove(Chessboard);
                SwingUtilities.invokeLater(() -> {
                    if (move != null) {
                        Chessboard.AIMove(move.x, move.y);
                    }
                    Chessboard.setAIthinking(false);
                });
            }).start();
        }
    }
}
