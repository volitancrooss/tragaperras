package SlotMachineApp.ui;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import SlotMachineApp.logic.SlotMachineLogic;
import java.util.Arrays;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;
import java.awt.Toolkit;


public class SlotMachineUI {
    private static final String ADVANCE_SYMBOL = "⏩"; // Add constant
    private JFrame frame;
    private JLabel[] reels;
    private JButton spinButton;
    private JButton advanceButton;
    private SlotMachineLogic logic;
    private JPanel reelsPanel;

    public SlotMachineUI() {
        logic = new SlotMachineLogic();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Hacer que el frame sea redimensionable
        frame.setResizable(true);
        
        // Configurar el tamaño inicial como un porcentaje de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.4);
        int height = (int)(screenSize.height * 0.4);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(300, 200));

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.BLACK);

        // Panel de rodillos con GridBagLayout
        createReelPanel();
        reelsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE; // Cambiado de BOTH a NONE
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        reels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            reels[i] = new JLabel("🎰", SwingConstants.CENTER) {
                @Override
                public Dimension getPreferredSize() {
                    // Forzar un tamaño fijo para el contenedor del símbolo
                    int size = Math.min(frame.getWidth() / 4, frame.getHeight() / 3);
                    return new Dimension(size, size);
                }
            };
            
            reels[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
            reels[i].setForeground(Color.YELLOW);
            reels[i].setOpaque(false);
            
            // Centrado estricto
            reels[i].setHorizontalAlignment(SwingConstants.CENTER);
            reels[i].setVerticalAlignment(SwingConstants.CENTER);
            
            // Configuración del grid
            gbc.gridx = i;
            gbc.gridy = 0;
            reelsPanel.add(reels[i], gbc);
        }

        // Modificar el ComponentListener para manejar mejor el redimensionamiento
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int fontSize = Math.min(frame.getWidth() / 8, frame.getHeight() / 4);
                Font newFont = new Font("Segoe UI Emoji", Font.BOLD, fontSize);
                for (JLabel reel : reels) {
                    reel.setFont(newFont);
                    reel.revalidate(); // Forzar actualización del layout
                }
                reelsPanel.revalidate(); // Actualizar el panel contenedor
            }
        });

        // Panel de botones con FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.BLACK);

        spinButton = new JButton("GIRAR");
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        buttonPanel.add(spinButton);

        advanceButton = new JButton("AVANZAR");
        advanceButton.setFont(new Font("Arial", Font.BOLD, 20));
        advanceButton.setBackground(Color.GREEN);
        advanceButton.setForeground(Color.WHITE);
        advanceButton.setEnabled(false);
        buttonPanel.add(advanceButton);

        // Añadir paneles al panel principal con márgenes
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(reelsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addButtonListeners();
    }

    private void updateReels(String[] symbols) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].equals("7")) {
                reels[i].setText("7");
                switch(i) {
                    case 0: reels[i].setForeground(Color.RED); break;
                    case 1: reels[i].setForeground(Color.GREEN); break;
                    case 2: reels[i].setForeground(Color.BLUE); break;
                }
            } else {
                reels[i].setForeground(Color.YELLOW);
                reels[i].setText(symbols[i]);
            }
        }
    }

    private void setSymbolColor(JLabel label, String symbol) {
        if (symbol.startsWith("7")) {
            label.setText("7");
            switch(symbol) {
                case "7R": label.setForeground(Color.RED); break;
                case "7G": label.setForeground(Color.GREEN); break;
                case "7B": label.setForeground(Color.BLUE); break;
            }
        } else {
            label.setForeground(Color.YELLOW);
            label.setText(symbol);
        }
    }

    private void createReelPanel() {
        reelsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo metálico con menor opacidad
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                GradientPaint metallic = new GradientPaint(0, 0, new Color(120, 120, 120),
                                                          getWidth(), getHeight(), new Color(80, 80, 80));
                g2d.setPaint(metallic);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Marcos de los rodillos con menor opacidad
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                int reelWidth = getWidth() / 3;
                int reelHeight = getHeight();
                
                for (int i = 0; i < 3; i++) {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.drawRect(i * reelWidth, 0, reelWidth, reelHeight);
                    
                    // Efecto de brillo muy sutil
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                    g2d.setPaint(new GradientPaint(i * reelWidth, 0, Color.WHITE,
                                                 (i + 1) * reelWidth, reelHeight, new Color(255, 255, 255, 0)));
                    g2d.fillRect(i * reelWidth, 0, reelWidth, reelHeight);
                }
            }
        };
        reelsPanel.setOpaque(false);
    }

    private void addButtonListeners() {
        spinButton.addActionListener(e -> {
            spinButton.setEnabled(false);
            advanceButton.setEnabled(false);
            logic.spinWithAnimation(reels);
            
            Timer enableTimer = new Timer(3000, event -> {
                spinButton.setEnabled(true);
                if (logic.canAdvance()) {
                    advanceButton.setEnabled(true);
                }
            });
            enableTimer.setRepeats(false);
            enableTimer.start();
        });

        advanceButton.addActionListener(e -> {
            int column = logic.getAdvanceColumn();
            if (column >= 0 && column < reels.length) {
                String newSymbol = logic.advance();
                if (newSymbol != null) {
                    logic.updateReelSymbol(reels[column], newSymbol);
                    if (!logic.canAdvance()) {
                        advanceButton.setEnabled(false);
                    }
                }
            }
        });
    }
}