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
    private int symbolSize = 148;
    
    // Rutas a los iconos
    private static final String ICON_PATH = "/icons/";
    private ImageIcon[] symbolIcons;
    private String[] symbols = {
        "siete",   // Siete normal
        "sandia",  // Sandía
        "diamond", // Diamante
        "campana", // Campana
        "apple",   // Manzana
        "dinero",  // Bolsa de dinero
        "extraspin"    // Comodín
    };
    
    // Valores de los símbolos (multiplicadores)
    private static final int[] symbolValues = {
        50,    // siete - mayor premio
        30,    // sandia
        25,    // diamond
        20,    // campana
        15,    // apple
        10,    // dinero
        40     // extraspin - valor alto para el comodín
    };

    // Sistema de apuestas
    private int currentBet = 100;  // Apuesta inicial
    private static final int[] BET_OPTIONS = {100, 200, 500, 1000};
    private int currentBetIndex = 0;
    private int playerCredits = 5000; // Créditos iniciales del jugador

    private Timer[] spinTimers;
    private int[] spinCounts;
    private int advancesRemaining = 0;
    private int advanceColumn = -1;

    // Listener para notificar premios
    private WinListener winListener;
    
    public interface WinListener {
        void onWin(int amount);
    }

    public void setWinListener(WinListener listener) {
        this.winListener = listener;
    }

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
        for (int i = 0; i < 3; i++) {
            results[i] = reels[i].getText();
        }

        int prize = 0;
        boolean hasWinningCombo = false;

        // Tres símbolos iguales
        if (results[0].equals(results[1]) && results[1].equals(results[2])) {
            int symbolIndex = Arrays.asList(symbols).indexOf(results[0]);
            if (symbolIndex >= 0) {
                prize = currentBet * symbolValues[symbolIndex] * 3;
                hasWinningCombo = true;
            }
        }
        
        // Dos símbolos iguales + posible comodín
        if (!hasWinningCombo) {
            for (int i = 0; i < 2; i++) {
                if (results[i].equals(results[i + 1])) {
                    int symbolIndex = Arrays.asList(symbols).indexOf(results[i]);
                    if (symbolIndex >= 0) {
                        int nextCol = i + 2;
                        if (nextCol < 3 && results[nextCol].equals("extraspin")) {
                            // Comodín encontrado - dar 2 tiradas extra en esa columna
                            prize = currentBet * symbolValues[symbolIndex];
                            advancesRemaining = 2;
                            advanceColumn = nextCol;
                        } else if (i > 0 && results[i - 1].equals("extraspin")) {
                            // Comodín en primera columna
                            prize = currentBet * symbolValues[symbolIndex];
                            advancesRemaining = 2;
                            advanceColumn = i - 1;
                        } else {
                            // Premio normal por dos símbolos
                            prize = currentBet * symbolValues[symbolIndex];
                            advancesRemaining = 2;
                            advanceColumn = i;
                        }
                        break;
                    }
                }
            }
        }

        // Actualizar créditos del jugador
        if (advancesRemaining > 0 && results[advanceColumn].equals("extraspin")) {
            // No cobrar por tiradas extra con comodín
            playerCredits += prize;
        } else {
            playerCredits += prize - currentBet;
        }
        
        // Notificar a la UI del resultado
        if (prize > 0) {
            notifyWin(prize);
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

    // Añadir getters para UI
    public int getCurrentBet() {
        return currentBet;
    }

    public int getPlayerCredits() {
        return playerCredits;
    }

    // Método para incrementar la apuesta
    public boolean increaseBet() {
        if (currentBetIndex < BET_OPTIONS.length - 1) {
            currentBetIndex++;
            currentBet = BET_OPTIONS[currentBetIndex];
            return true;
        }
        return false;
    }

    // Método para resetear la apuesta al mínimo
    public void resetBet() {
        currentBetIndex = 0;
        currentBet = BET_OPTIONS[currentBetIndex];
    }

    // Método para verificar si el jugador puede apostar
    public boolean canBet() {
        return playerCredits >= currentBet;
    }

    private void notifyWin(int amount) {
        if (winListener != null) {
            winListener.onWin(amount);
        }
    }
}