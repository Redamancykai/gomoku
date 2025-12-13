package com.mygomoku;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel{
	private JTextArea displayArea;
    private JTextField inputField;
    private JButton sendButton;

    public ChatPanel() {
        setLayout(new BorderLayout(5, 5)); // 间距
        setPreferredSize(new Dimension(280, 0)); // 自适应高度
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(10, 10, 10, 10) // 内部边距
        ));

        // 聊天区
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true); // 自动换行
        displayArea.setWrapStyleWord(true); // 按单词换行
        displayArea.setFont(new Font("楷体", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        JLabel titleLabel = new JLabel("聊天框", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 输入区
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField();
        inputField.setFont(new Font("楷体", Font.PLAIN, 14));
        sendButton = new JButton("发送");
        sendButton.setBackground(Color.GRAY);
        sendButton.setForeground(Color.WHITE);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        add(inputPanel, BorderLayout.SOUTH);

        
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        
    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (!content.isEmpty()) {
            displayArea.append("玩家:\n" + content + "\n\n");
            inputField.setText("");
            displayArea.setCaretPosition(displayArea.getDocument().getLength()); // 移动光标插入
        }
    }
}
