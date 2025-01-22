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
import java.awt.Image;
import java.util.Arrays;
import javax.swing.SwingConstants;
import java.net.URL;

public class SlotMachineLogic {
    private Random random;
    private int symbolSize = 48;
    
    // Rutas a los iconos
    private static final String ICON_PATH = "/icons/";
    private ImageIcon[] symbolIcons;
    private String[] symbols = {
        "seven", // Siete normal
        "star",  // Estrella
        "diamond", // Diamante
        "clover",  // Trébol
        "apple",   // Manzana
        "slot",    // Máquina tragaperras
        "money"    // Bolsa de dinero
    };
    
    private Timer[] spinTimers;
    private int[] spinCounts;
    private int advancesRemaining = 0;
    private int advanceColumn = -1;

    public SlotMachineLogic() {
        random = new Random();
        spinTimers = new Timer[3];
        spinCounts = new int[3];
        loadIcons();
    }
    
    private void loadIcons() {
        symbolIcons = new ImageIcon[symbols.length];
        try {
            for (int i = 0; i < symbols.length; i++) {
                String path = ICON_PATH + symbols[i] + ".png";
                URL resourceUrl = SlotMachineLogic.class.getResource(path);
                if (resourceUrl == null) {
                    System.err.println("No se pudo encontrar el recurso: " + path);
                    continue;
                }
                ImageIcon icon = new ImageIcon(resourceUrl);
                Image img = icon.getImage().getScaledInstance(symbolSize, symbolSize, Image.SCALE_SMOOTH);
                symbolIcons[i] = new ImageIcon(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSymbolSize(int size) {
        this.symbolSize = size;
        loadIcons(); // Recargar iconos con nuevo tamaño
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
        reel.setText("");
        reel.setBorder(null);
        
        int index = Arrays.asList(symbols).indexOf(symbol);
        if (index >= 0) {
            reel.setIcon(symbolIcons[index]);
        }
        
        reel.setHorizontalAlignment(SwingConstants.CENTER);
        reel.setVerticalAlignment(SwingConstants.CENTER);
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