package com.mygomoku;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameHall extends JFrame{
	private Image gamehallBg;
	
	public GameHall() {
        setTitle("五子棋");
        setSize(960, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        ImageIcon icon = new ImageIcon("gamehallbg.png");
        gamehallBg = icon.getImage();
        
        // 绘制背景图
        JPanel backgroundPanel = new JPanel() {
        	
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gamehallBg != null) {
                    g.drawImage(gamehallBg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // 大标题
        JLabel titleLabel = new JLabel("五子棋", SwingConstants.CENTER);
        titleLabel.setFont(new Font("华文行楷", Font.BOLD, 60));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(new EmptyBorder(150, 0, 50, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 按钮设计
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 15, 0)); // 一行三列
        buttonPanel.setBorder(new EmptyBorder(20, 10, 50, 10));
        
        buttonPanel.setOpaque(false);

        JButton pvpButton = createButton("双人对战");
        pvpButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(0);
            }
        });
        
        JButton pveButton = createButton("人机对战");
        pveButton.addActionListener(new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(1);
            }
        });
        
        JButton exitButton = createButton("退出游戏");
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(pvpButton);
        buttonPanel.add(pveButton);
        buttonPanel.add(exitButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // 管理按钮
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("华文行楷", Font.PLAIN, 36));
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.BLACK);
        return btn;
    }

    // 在游戏大厅启动游戏
    private void startGame(int mode) {
        this.dispose(); 
        SwingUtilities.invokeLater(() -> {
            new GomokuMain(mode); 
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(GameHall::new);
    }
}
