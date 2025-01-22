package SlotMachineApp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import SlotMachineApp.logic.SlotMachineLogic; // Importar la clase SlotMachineLogic

public class SlotMachineUI {
    private JFrame frame;
    private JLabel[] reels;
    private JButton spinButton;
    private SlotMachineLogic logic;

    public SlotMachineUI() {
        logic = new SlotMachineLogic();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        panel.setBackground(Color.BLACK); // Fondo negro para el panel

        reels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            reels[i] = new JLabel("7", SwingConstants.CENTER);
            reels[i].setFont(new Font("Serif", Font.BOLD, 48));
            reels[i].setForeground(Color.YELLOW); // Texto amarillo para los carretes
            panel.add(reels[i]);
        }

        spinButton = new JButton("Spin");
        spinButton.setBackground(Color.RED); // Fondo rojo para el botón
        spinButton.setForeground(Color.WHITE); // Texto blanco para el botón
        spinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] results = logic.spin();
                for (int i = 0; i < 3; i++) {
                    reels[i].setText(results[i]);
                }
            }
        });

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(spinButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}