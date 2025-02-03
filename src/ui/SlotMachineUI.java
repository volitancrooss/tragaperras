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
import java.net.URL;
import java.awt.BasicStroke;


public class SlotMachineUI {
    private JFrame frame;
    private JLabel[] reels;
    private JButton spinButton;
    private JButton advanceButton;
    private JButton betButton;
    private JLabel creditsLabel;
    private SlotMachineLogic logic;
    private JPanel reelsPanel;
    private Timer blinkTimer; // Añadir como variable de clase
    private JPanel mainPanel; // Añadir como variable de clase
    private Timer creditBlinkTimer;
    private boolean isGreen = true;
    private Timer resizeTimer;

    public SlotMachineUI() {
        logic = new SlotMachineLogic();
        creditBlinkTimer = new Timer(2000, e -> {
            isGreen = !isGreen;
            creditsLabel.repaint();
        });
        creditBlinkTimer.start();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Hacer que el frame sea redimensionable
        frame.setResizable(true);
        
        // Configurar el tamaño inicial como un porcentaje de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.8); // Aumentar el tamaño inicial
        int height = (int)(screenSize.height * 0.8); // Aumentar el tamaño inicial
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(500, 400)); // Aumentar el tamaño mínimo

        // Panel principal con BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
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
            reels[i] = new JLabel();
            URL resourceUrl = getClass().getResource("/icons/inicio.png");
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                int iconSize = Math.min(frame.getWidth() / 6, frame.getHeight() / 3);
                iconSize = Math.max(iconSize, 48); // Tamaño mínimo
                Image img = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                reels[i].setIcon(new ImageIcon(img));
            } else {
                System.err.println("No se pudo cargar el icono inicial: /icons/inicio.png");
            }
            
            // Configuración inicial del reel
            reels[i].setHorizontalAlignment(SwingConstants.CENTER);
            reels[i].setVerticalAlignment(SwingConstants.CENTER);
            reels[i].setOpaque(false);
            
            // Configuración del grid
            gbc.gridx = i;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            reelsPanel.add(reels[i], gbc);
        }

        // Modificar el ComponentListener para manejar el redimensionamiento de iconos y contenido
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (resizeTimer != null && resizeTimer.isRunning()) {
                    resizeTimer.restart();
                } else {
                    resizeTimer = new Timer(200, evt -> {
                        resizeTimer.stop();
                        new ResizeWorker().execute();
                    });
                    resizeTimer.setRepeats(false);
                    resizeTimer.start();
                }
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

        // Añadir botón de apuesta y etiqueta de créditos
        betButton = new JButton("APUESTA: 100");
        betButton.setFont(new Font("Arial", Font.BOLD, 20));
        betButton.setBackground(Color.BLUE);
        betButton.setForeground(Color.WHITE);
        buttonPanel.add(betButton);

        creditsLabel = new JLabel("CRÉDITOS: 5000") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // LED glow effect
                g2d.setColor(isGreen ? new Color(0, 255, 0, 50) : new Color(255, 0, 0, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Calculate center position
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = ((getHeight() - fm.getDescent() + fm.getAscent()) / 2);
                
                // Text with glow
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                
                // Outer glow
                g2d.setColor(isGreen ? new Color(0, 255, 0, 60) : new Color(255, 0, 0, 60));
                g2d.drawString(getText(), x+1, y-1);
                g2d.drawString(getText(), x-1, y+1);
                
                // Main text
                g2d.setColor(isGreen ? Color.GREEN : Color.RED);
                g2d.drawString(getText(), x, y);
            }
        };
        creditsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        creditsLabel.setForeground(Color.GREEN);
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buttonPanel.add(creditsLabel);

        // Añadir paneles al panel principal con márgenes
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(reelsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        addPaytablePanel(); // Añadir la tabla de pagos
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addButtonListeners();
    }

    

    // Modificar el método setSymbolColor para manejar los símbolos correctamente
    

    private void createReelPanel() {
        // Usar GridLayout con gaps horizontales y verticales
        reelsPanel = new JPanel(new GridLayout(1, 3, 2, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Dibujar líneas divisorias negras
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                
                // Línea divisoria 1
                int x1 = getWidth() / 3;
                g2d.drawLine(x1, 0, x1, getHeight());
                
                // Línea divisoria 2
                int x2 = (getWidth() * 2) / 3;
                g2d.drawLine(x2, 0, x2, getHeight());
            }
        };
        reelsPanel.setBackground(new Color(100, 50, 150));
        reelsPanel.setOpaque(true);
    }

    private void addButtonListeners() {
        spinButton.addActionListener(e -> {
            if (logic.canBet()) {
                spinButton.setEnabled(false);
                advanceButton.setEnabled(false);
                betButton.setEnabled(false);
                
                // Pass current icon size to logic
                logic.setSymbolSize(getCurrentIconSize());
                logic.spinWithAnimation(reels);
                
                Timer enableTimer = new Timer(3000, event -> {
                    spinButton.setEnabled(true);
                    betButton.setEnabled(true);
                    if (logic.canAdvance()) {
                        advanceButton.setEnabled(true);
                    }
                    creditsLabel.setText("CRÉDITOS: " + logic.getPlayerCredits());
                });
                enableTimer.setRepeats(false);
                enableTimer.start();
            }
        });

        advanceButton.addActionListener(e -> {
            int column = logic.getExtraSpinColumn();
            if (column >= 0 && column < reels.length) {
                String newSymbol = logic.advance();
                if (newSymbol != null) {
                    logic.updateReelSymbol(reels[column], newSymbol);
                    
                    // Verificar combinación ganadora después de cada avance
                    logic.checkResults(reels);
                    
                    // Verificar si se terminaron los avances o hubo combinación ganadora
                    if (!logic.canAdvance()) {
                        // Detener el parpadeo
                        if (blinkTimer != null) {
                            blinkTimer.stop();
                        }
                        advanceButton.setEnabled(false);
                        advanceButton.setBackground(Color.GREEN); // Restaurar color original
                        reels[column].setBorder(null);
                    }
                }
            }
        });

        betButton.addActionListener(e -> {
            if (logic.increaseBet()) {
                betButton.setText("APUESTA: " + logic.getCurrentBet());
            } else {
                logic.resetBet();
                betButton.setText("APUESTA: " + logic.getCurrentBet());
            }
        });

        logic.setWinListener(new SlotMachineLogic.WinListener() {
            @Override
            public void onWin(int amount) {
                creditsLabel.setText("CRÉDITOS: " + logic.getPlayerCredits());
                showWinMessage(amount);
            }
            
            @Override
            public void onExtraSpinActivated(int column) {
                handleExtraSpin(column);
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

    private void showWinMessage(int amount) {
        JOptionPane.showMessageDialog(frame,
            "¡Has ganado " + amount + " créditos!",
            "¡Premio!",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleExtraSpin(int column) {
        // Detener el timer anterior si existe
        if (blinkTimer != null && blinkTimer.isRunning()) {
            blinkTimer.stop();
        }
        
        // Hacer que el botón AVANZAR parpadee
        blinkTimer = new Timer(500, e -> {
            advanceButton.setBackground(
                advanceButton.getBackground().equals(Color.GREEN) ?
                Color.YELLOW : Color.GREEN
            );
        });
        blinkTimer.start();
        
        // Resaltar la columna del comodín
        reels[column].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        advanceButton.setEnabled(true);
    }

    private void addPaytablePanel() {
        JPanel paytablePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                adjustPaytableSize();
            }
        };
        paytablePanel.setLayout(new BoxLayout(paytablePanel, BoxLayout.Y_AXIS));
        paytablePanel.setBackground(new Color(100, 50, 150));
        paytablePanel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255, 150), 2));
        
        // Título
        JLabel titleLabel = new JLabel("PREMIOS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paytablePanel.add(titleLabel);
        paytablePanel.add(Box.createVerticalStrut(10));
    
        // Crear filas para cada símbolo
        String[] symbolNames = {"siete", "sandia", "diamond", "campana", "apple", "dinero", "extraspin"};
        int[] multipliers = {50, 30, 25, 20, 15, 10, 5};
    
        for (int i = 0; i < symbolNames.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            row.setOpaque(false);
            
            // Cargar icono
            URL resourceUrl = getClass().getResource("/icons/" + symbolNames[i] + ".png");
            if (resourceUrl != null) {
                ImageIcon icon = new ImageIcon(resourceUrl);
                JLabel iconLabel = new JLabel(icon);
                row.add(iconLabel);
            }
    
            // Texto del premio
            JLabel prizeLabel = new JLabel("x3 = " + multipliers[i] + "x apuesta");
            prizeLabel.setForeground(Color.WHITE);
            row.add(prizeLabel);
    
            paytablePanel.add(row);
            paytablePanel.add(Box.createVerticalStrut(5));
        }
    
        // Modificar el mainPanel para incluir la tabla
        mainPanel.add(paytablePanel, BorderLayout.EAST);
    }

    private void adjustPaytableSize() {
        if (resizeTimer != null && resizeTimer.isRunning()) {
            resizeTimer.restart();
        } else {
            resizeTimer = new Timer(200, e -> {
                resizeTimer.stop();
                new ResizeWorker().execute();
            });
            resizeTimer.setRepeats(false);
            resizeTimer.start();
        }
    }

    private class ResizeWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            int newSize = getCurrentIconSize();
            int newFontSize = newSize / 6;

            // Redimensionar iconos y fuentes en los rodillos
            for (JLabel reel : reels) {
                Icon currentIcon = reel.getIcon();
                if (currentIcon instanceof ImageIcon) {
                    reel.setIcon(scaleIcon((ImageIcon) currentIcon));
                }
            }

            // Redimensionar iconos y fuentes en la tabla de premios
            Component[] components = mainPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel panel = (JPanel) component;
                    Component[] rows = panel.getComponents();
                    for (Component row : rows) {
                        if (row instanceof JPanel) {
                            JPanel rowPanel = (JPanel) row;
                            Component[] rowComponents = rowPanel.getComponents();
                            for (Component rowComponent : rowComponents) {
                                if (rowComponent instanceof JLabel) {
                                    JLabel label = (JLabel) rowComponent;
                                    if (label.getIcon() != null) {
                                        ImageIcon icon = (ImageIcon) label.getIcon();
                                        Image scaledImage = icon.getImage().getScaledInstance(newSize / 5, newSize / 5, Image.SCALE_SMOOTH);
                                        label.setIcon(new ImageIcon(scaledImage));
                                    } else {
                                        label.setFont(new Font("Arial", Font.BOLD, newFontSize));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
    
    private int getCurrentIconSize() {
        int iconSize = Math.min(frame.getWidth() / 6, frame.getHeight() / 3);
        return Math.max(iconSize, 48); // Minimum size of 48px
    }

    private ImageIcon scaleIcon(ImageIcon originalIcon) {
        int size = getCurrentIconSize();
        Image scaledImage = originalIcon.getImage().getScaledInstance(
            size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    
}