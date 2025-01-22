package SlotMachineApp.logic;

import java.util.Random;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.FontMetrics;
import javax.swing.border.Border;
import java.awt.AlphaComposite;
import javax.swing.BorderFactory;

public class SlotMachineLogic {
    private Random random;
    private static final String ADVANCE_SYMBOL = "⏩";
    private static final String SEVEN_RED = "7R";
    private static final String SEVEN_GREEN = "7G";
    private static final String SEVEN_BLUE = "7B";
    
    private String[] symbols = {
        SEVEN_RED, SEVEN_GREEN, SEVEN_BLUE,
        "⭐", "💎", "🍀", "🎲", "🎰", "💰"
    };
    
    private Timer[] spinTimers;
    private int[] spinCounts;
    private int advancesRemaining = 0;
    private int advanceColumn = -1;

    public SlotMachineLogic() {
        random = new Random();
        spinTimers = new Timer[3];
        spinCounts = new int[3];
    }

    public void spinWithAnimation(JLabel[] reels) {
        // Pre-generar algunos símbolos aleatorios
        String[][] preGeneratedSymbols = new String[3][30];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 30; j++) {
                preGeneratedSymbols[i][j] = symbols[random.nextInt(symbols.length)];
            }
        }

        for (int i = 0; i < 3; i++) {
            final int col = i;
            spinCounts[col] = 0;
            
            spinTimers[col] = new Timer(16, new ActionListener() { // Cambiar a 16ms (aprox. 60 FPS)
                private int symbolIndex = 0;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateReelSymbol(reels[col], preGeneratedSymbols[col][symbolIndex++ % 30]);
                    spinCounts[col]++;
                    
                    if (spinCounts[col] >= 20 + (col * 10)) {
                        spinTimers[col].stop();
                        String finalSymbol = symbols[random.nextInt(symbols.length)];
                        updateReelSymbol(reels[col], finalSymbol);
                        
                        if (allReelsStopped()) {
                            checkResults(reels);
                        }
                    }
                }
            });
            
            Timer startDelay = new Timer(col * 200, e -> spinTimers[col].start());
            startDelay.setRepeats(false);
            startDelay.start();
        }
    }

    public void updateReelSymbol(JLabel reel, String symbol) {
        // Evitar actualizaciones innecesarias si el símbolo es el mismo
        if (reel.getText().equals(symbol)) return;
        
        if (symbol.startsWith("7")) {
            Color newColor;
            switch(symbol) {
                case SEVEN_RED: newColor = Color.RED; break;
                case SEVEN_GREEN: newColor = Color.GREEN; break;
                case SEVEN_BLUE: newColor = Color.BLUE; break;
                default: newColor = reel.getForeground(); break;
            }
            
            if (!reel.getForeground().equals(newColor)) {
                reel.setForeground(newColor);
            }
            reel.setText("7");
        } else {
            if (!reel.getForeground().equals(Color.YELLOW)) {
                reel.setForeground(Color.YELLOW);
            }
            reel.setText(symbol);
        }
    }

    private void updateReelSymbolOld(JLabel reel, String symbol) {
        // Configurar un tamaño de fuente más grande
        reel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 60));
        
        // Añadir un borde con efecto de resplandor
        reel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        if (symbol.startsWith("7")) {
            reel.setText("7");
            switch(symbol) {
                case SEVEN_RED: 
                    reel.setForeground(new Color(255, 50, 50));
                    reel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    break;
                case SEVEN_GREEN: 
                    reel.setForeground(new Color(50, 255, 50));
                    reel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    break;
                case SEVEN_BLUE: 
                    reel.setForeground(new Color(50, 50, 255));
                    reel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                    break;
            }
        } else {
            switch(symbol) {
                case "⭐":
                    reel.setForeground(new Color(255, 215, 0)); // Dorado brillante
                    break;
                case "💎":
                    reel.setForeground(new Color(0, 191, 255)); // Azul celeste brillante
                    break;
                case "🍀":
                    reel.setForeground(new Color(50, 205, 50)); // Verde lima brillante
                    break;
                case "🎲":
                    reel.setForeground(new Color(255, 255, 255)); // Blanco puro
                    break;
                case "🎰":
                    reel.setForeground(new Color(255, 140, 0)); // Naranja brillante
                    break;
                case "💰":
                    reel.setForeground(new Color(255, 215, 0)); // Dorado brillante
                    break;
                default:
                    reel.setForeground(new Color(255, 255, 0)); // Amarillo brillante
            }
            reel.setText(symbol);
        }
    }

    private void checkResults(JLabel[] reels) {
        String[] results = new String[3];
        Color[] colors = new Color[3];
        
        for (int i = 0; i < 3; i++) {
            results[i] = reels[i].getText();
            colors[i] = reels[i].getForeground();
        }

        // Verificar combinaciones ganadoras
        boolean hasWinningCombo = false;
        
        // Verificar tres iguales
        if (results[0].equals(results[1]) && results[1].equals(results[2])) {
            if (results[0].equals("7")) {
                if (colors[0] == colors[1] && colors[1] == colors[2]) {
                    hasWinningCombo = true;
                }
            } else {
                hasWinningCombo = true;
            }
        }
        
        // Verificar dos iguales
        for (int i = 0; i < 2 && !hasWinningCombo; i++) {
            if (results[i].equals(results[i + 1])) {
                if (results[i].equals("7")) {
                    if (colors[i] == colors[i + 1]) {
                        advancesRemaining = 2;
                        advanceColumn = i;
                        break;
                    }
                } else {
                    advancesRemaining = 2;
                    advanceColumn = i;
                    break;
                }
            }
        }
    }

    private boolean allReelsStopped() {
        for (Timer timer : spinTimers) {
            if (timer != null && timer.isRunning()) {
                return false;
            }
        }
        return true;
    }

    public boolean canAdvance() {
        return advancesRemaining > 0;
    }

    public int getAdvanceColumn() {
        return advanceColumn;
    }

    public String advance() {
        if (advancesRemaining > 0) {
            advancesRemaining--;
            return symbols[random.nextInt(symbols.length)];
        }
        return null;
    }
}