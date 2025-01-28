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
import javax.swing.Icon;

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
        30,    // diamond
        25,    // sandia
        20,    // campana
        15,    // apple
        10,    // dinero
        40     // extraspin - valor alto para el comodín
    };

    // Añadir constante para el premio de tres comodines
    private static final int EXTRASPIN_TRIPLE_PRIZE = 5; // Premio bajo para 3 comodines

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
    
    // Añadir variables para controlar el estado del comodín
    private boolean isExtraSpinActive = false;
    private int extraSpinsRemaining = 0;
    private int extraSpinColumn = -1;
    private String[] currentCombination = new String[3];

    public interface WinListener {
        void onWin(int amount);
        void onExtraSpinActivated(int column);
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
                URL resourceUrl = getClass().getResource(path);
                if (resourceUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(resourceUrl);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(
                        symbolSize, symbolSize, Image.SCALE_SMOOTH);
                    symbolIcons[i] = new ImageIcon(scaledImage);
                }
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
        // Pre-generate random symbols
        String[][] preGeneratedSymbols = new String[3][30];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 30; j++) {
                preGeneratedSymbols[i][j] = symbols[random.nextInt(symbols.length)];
            }
        }

        // Load and scale icons
        loadIcons(); // Reload icons with current size

        for (int i = 0; i < 3; i++) {
            final int col = i;
            spinCounts[col] = 0;
            
            spinTimers[col] = new Timer(16, new ActionListener() {
                private int symbolIndex = 0;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    String symbol = preGeneratedSymbols[col][symbolIndex++ % 30];
                    updateReelSymbol(reels[col], symbol);
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
        if (index >= 0 && symbolIcons != null && index < symbolIcons.length) {
            reel.setIcon(symbolIcons[index]);
        }
        
        reel.setHorizontalAlignment(SwingConstants.CENTER);
        reel.setVerticalAlignment(SwingConstants.CENTER);
    }

    // Modificar checkResults para no interferir con los avances del comodín
    public void checkResults(JLabel[] reels) {
        String[] results = new String[3];
        for (int i = 0; i < 3; i++) {
            Icon icon = reels[i].getIcon();
            if (icon instanceof ImageIcon) {
                int index = Arrays.asList(symbolIcons).indexOf(icon);
                if (index >= 0) {
                    results[i] = symbols[index];
                }
            }
        }
        currentCombination = results.clone();

        int prize = 0;
        boolean hasWinningCombo = false;

        // Caso especial: tres comodines
        if (results[0].equals("extraspin") && results[1].equals("extraspin") && results[2].equals("extraspin")) {
            prize = currentBet * EXTRASPIN_TRIPLE_PRIZE;
            hasWinningCombo = true;
            // No activar extraSpin en este caso
            isExtraSpinActive = false;
            extraSpinsRemaining = 0;
        }
        // Verificar tres símbolos iguales (que no sean comodines)
        else if (results[0].equals(results[1]) && results[1].equals(results[2]) && !results[0].equals("extraspin")) {
            int symbolIndex = Arrays.asList(symbols).indexOf(results[0]);
            if (symbolIndex >= 0) {
                prize = currentBet * symbolValues[symbolIndex] * 3;
                hasWinningCombo = true;
                // Si esto ocurre durante un extraSpin, desactivar el modo extraSpin
                if (isExtraSpinActive) {
                    isExtraSpinActive = false;
                    extraSpinsRemaining = 0;
                }
            }
        }
        // Solo verificar combinaciones con comodín si no hay triple
        else if (!hasWinningCombo && !isExtraSpinActive) {
            // Caso 1: SIMBOLO SIMBOLO EXTRASPIN
            if (results[0].equals(results[1]) && results[2].equals("extraspin")) {
                activateExtraSpin(2, results[0]);
            }
            // Caso 2: SIMBOLO EXTRASPIN SIMBOLO
            else if (results[0].equals(results[2]) && results[1].equals("extraspin")) {
                activateExtraSpin(1, results[0]);
            }
            // Caso 3: EXTRASPIN SIMBOLO SIMBOLO
            else if (results[1].equals(results[2]) && results[0].equals("extraspin")) {
                activateExtraSpin(0, results[1]);
            }
        }

        // Actualizar créditos
        if (prize > 0) {
            if (hasWinningCombo || !isExtraSpinActive) {
                playerCredits += prize - currentBet;
            } else {
                playerCredits += prize; // No cobrar apuesta durante extraSpin
            }
            notifyWin(prize);
        } else if (!isExtraSpinActive) {
            playerCredits -= currentBet;
        }
    }

    // Modificar la clase SlotMachineLogic
    private void activateExtraSpin(int column, String matchingSymbol) {
        isExtraSpinActive = true;
        extraSpinColumn = column;
        extraSpinsRemaining = 2; // Asegurar que siempre sean 2 tiradas
        
        // Notificar a la UI
        if (winListener != null) {
            winListener.onExtraSpinActivated(column);
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

    // Modificar la clase SlotMachineLogic
    public boolean canAdvance() {
        return isExtraSpinActive && extraSpinsRemaining > 0;
    }

    // Modificar la clase SlotMachineLogic
    public String advance() {
        if (isExtraSpinActive && extraSpinsRemaining > 0) {
            String newSymbol = symbols[random.nextInt(symbols.length)];
            extraSpinsRemaining--;
            
            // Solo desactivar cuando se hayan usado las dos tiradas
            if (extraSpinsRemaining == 0) {
                isExtraSpinActive = false;
            }
            return newSymbol;
        }
        return null;
    }

    public int getExtraSpinColumn() {
        return extraSpinColumn;
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
        return playerCredits >= currentBet && !isExtraSpinActive;
    }

    private void notifyWin(int amount) {
        if (winListener != null) {
            winListener.onWin(amount);
        }
    }
    
    public String getNextSpinSymbol() {
        return symbols[random.nextInt(symbols.length)];
    }
}