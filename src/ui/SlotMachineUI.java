package SlotMachineApp.ui;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import SlotMachineApp.logic.SlotMachineLogic;

public class SlotMachineUI {
    private static final String ADVANCE_SYMBOL = "⏩"; // Add constant
    private JFrame frame;
    private JLabel[] reels;
    private JButton spinButton;
    private JButton advanceButton;
    private SlotMachineLogic logic;

    public SlotMachineUI() {
        logic = new SlotMachineLogic();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        JPanel reelsPanel = new JPanel(new GridLayout(1, 5)); // 5 columnas para incluir separadores
        reelsPanel.setBackground(Color.BLACK);

        reels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                JLabel separator = new JLabel("|", SwingConstants.CENTER);
                separator.setFont(new Font("Arial", Font.BOLD, 48));
                separator.setForeground(Color.WHITE);
                reelsPanel.add(separator);
            }
            reels[i] = new JLabel("🎰", SwingConstants.CENTER);
            reels[i].setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            reels[i].setForeground(Color.YELLOW);
            reelsPanel.add(reels[i]);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);

        spinButton = new JButton("GIRAR");
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);

        advanceButton = new JButton("AVANZAR");
        advanceButton.setFont(new Font("Arial", Font.BOLD, 20));
        advanceButton.setBackground(Color.GREEN);
        advanceButton.setForeground(Color.WHITE);
        advanceButton.setEnabled(false);
        
        spinButton.addActionListener(e -> {
            spinButton.setEnabled(false);
            advanceButton.setEnabled(false);
            logic.spinWithAnimation(reels);
            
            // Habilitar el botón después de que termine la animación
            Timer enableTimer = new Timer(3000, ev -> {
                spinButton.setEnabled(true);
                advanceButton.setEnabled(logic.canAdvance());
            });
            enableTimer.setRepeats(false);
            enableTimer.start();
        });

        advanceButton.addActionListener(e -> {
            if (logic.canAdvance()) {
                int column = logic.getAdvanceColumn();
                String newSymbol = logic.advance();
                if (newSymbol != null) {
                    reels[column].setText(newSymbol);
                }
                advanceButton.setEnabled(logic.canAdvance());
            }
        });

        buttonPanel.add(spinButton);
        buttonPanel.add(advanceButton);

        mainPanel.add(reelsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}