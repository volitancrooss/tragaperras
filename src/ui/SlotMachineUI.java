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

        // En la creación inicial de los reels
        reels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            reels[i] = new JLabel() {
                @Override
                public Dimension getPreferredSize() {
                    // Mantener un tamaño consistente para el contenedor
                    int size = Math.min(frame.getWidth() / 6, frame.getHeight() / 3);
                    return new Dimension(size, size);
                }
            };
            
            // Configuración inicial del reel
            reels[i].setHorizontalAlignment(SwingConstants.CENTER);
            reels[i].setVerticalAlignment(SwingConstants.CENTER);
            reels[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
            reels[i].setForeground(Color.YELLOW);
            reels[i].setText("🎰");
            reels[i].setOpaque(false);
            
            // Configuración del grid
            gbc.gridx = i;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            reelsPanel.add(reels[i], gbc);
        }

        // Modificar el ComponentListener para manejar mejor el redimensionamiento
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int fontSize = Math.min(frame.getWidth() / 8, frame.getHeight() / 4);
                fontSize = Math.max(fontSize, 32); // Establecer un tamaño mínimo
                Font newFont = new Font("Segoe UI Emoji", Font.BOLD, fontSize);
                
                for (JLabel reel : reels) {
                    reel.setFont(newFont);
                }
                
                // Notificar a la lógica del nuevo tamaño
                if (logic != null) {
                    logic.setSymbolSize(fontSize);
                }
                
                reelsPanel.revalidate();
                reelsPanel.repaint();
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

    // Modificar el método setSymbolColor para manejar los símbolos correctamente
    private void setSymbolColor(JLabel label, String symbol) {
        // Asegurarse de que la fuente sea consistente
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
        
        if (symbol.startsWith("7")) {
            label.setText("7");
            switch(symbol) {
                case "7R": label.setForeground(new Color(255, 50, 50)); break;
                case "7G": label.setForeground(new Color(50, 255, 50)); break;
                case "7B": label.setForeground(new Color(50, 50, 255)); break;
            }
        } else {
            label.setForeground(Color.YELLOW);
            // Usar el símbolo directamente sin procesamiento adicional
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
                
                // Fondo con efecto neón futurista
                GradientPaint neonGlow = new GradientPaint(
                    0, 0, new Color(25, 0, 50),
                    getWidth(), getHeight(), new Color(50, 0, 100)
                );
                g2d.setPaint(neonGlow);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Efecto de líneas de neón
                g2d.setStroke(new BasicStroke(2f));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                
                // Dibujar marcos de neón para cada rodillo
                for (int i = 0; i < 3; i++) {
                    int x = (getWidth() / 3) * i;
                    
                    GradientPaint neonBorder = new GradientPaint(
                        x, 0, new Color(0, 255, 255, 150),
                        x + getWidth()/3, getHeight(), new Color(255, 0, 255, 150)
                    );
                    g2d.setPaint(neonBorder);
                    g2d.drawRect(x + 5, 5, (getWidth()/3) - 10, getHeight() - 10);
                }
                
                // Efecto de brillo en movimiento (corregido)
                long currentTime = System.currentTimeMillis();
                float glowPosition = (float)((currentTime % 3000) / 3000.0);
                
                // Asegurar que los valores de fracción estén entre 0 y 1
                float start = Math.max(0, glowPosition - 0.2f);
                float middle = glowPosition;
                float end = Math.min(1, glowPosition + 0.2f);
                
                if (start < end) { // Solo dibujar si los valores son válidos
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setPaint(new LinearGradientPaint(
                        0, 0,
                        getWidth(), 0,
                        new float[]{start, middle, end},
                        new Color[]{
                            new Color(0, 0, 0, 0),
                            new Color(255, 255, 255, 100),
                            new Color(0, 0, 0, 0)
                        }
                    ));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        // Configurar temporizador para la animación del brillo
        Timer glowTimer = new Timer(16, e -> reelsPanel.repaint());
        glowTimer.start();
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
    
    private void setupSymbolEffects(JLabel reel) {
        reel.putClientProperty("glow", 0.0f);
        
        Timer glowTimer = new Timer(50, e -> {
            float glow = (float)reel.getClientProperty("glow");
            glow = (glow + 0.1f) % 1.0f;
            reel.putClientProperty("glow", glow);
            
            // Efecto de brillo pulsante
            float alpha = (float)(Math.sin(glow * Math.PI * 2) * 0.5 + 0.5);
            reel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(1f, 1f, 1f, alpha),
                    2
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        });
        glowTimer.start();
    }
    
    private void showWinAnimation() {
        JPanel winEffect = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Efecto de fuegos artificiales
                long time = System.currentTimeMillis();
                int particles = 20;
                for (int i = 0; i < particles; i++) {
                    double angle = (time / 1000.0 + i * (Math.PI * 2 / particles));
                    int x = getWidth()/2 + (int)(Math.cos(angle) * 100);
                    int y = getHeight()/2 + (int)(Math.sin(angle) * 100);
                    
                    g2d.setColor(new Color(
                        (float)Math.random(),
                        (float)Math.random(),
                        (float)Math.random(),
                        0.8f
                    ));
                    g2d.fillOval(x-5, y-5, 10, 10);
                }
            }
        };
        winEffect.setOpaque(false);
        reelsPanel.add(winEffect, 0);
        
        Timer winTimer = new Timer(16, e -> winEffect.repaint());
        winTimer.start();
        
        // Detener la animación después de 3 segundos
        Timer stopTimer = new Timer(3000, e -> {
            winTimer.stop();
            reelsPanel.remove(winEffect);
            reelsPanel.repaint();
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }
}