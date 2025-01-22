package SlotMachineApp.logic;

import java.util.Random;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlotMachineLogic {
    private Random random;
    private static final String ADVANCE_SYMBOL = "⏩";
    private String[] symbols = {"💎", "🍎", "💣", "⏩", "🐍", "💀", "7G", "7R", "7B"};
    private int advancesRemaining = 0;
    private int advanceColumn = -1;
    private Timer[] spinTimers;
    private int[] spinCounts;

    public SlotMachineLogic() {
        random = new Random();
        spinTimers = new Timer[3];
        spinCounts = new int[3];
    }

    public void spinWithAnimation(JLabel[] reels) {
        for (int i = 0; i < 3; i++) {
            final int col = i;  // Make a final copy of i
            spinCounts[col] = 0;
            
            spinTimers[col] = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String symbol = symbols[random.nextInt(symbols.length)];
                    reels[col].setText(symbol);
                    spinCounts[col]++;
                    
                    // Detener después de un número aleatorio de giros
                    if (spinCounts[col] >= 20 + (col * 10)) {
                        spinTimers[col].stop();
                        // Establecer símbolo final
                        String finalSymbol = symbols[random.nextInt(symbols.length)];
                        reels[col].setText(finalSymbol);
                        
                        // Comprobar si todos los reels han parado
                        if (allReelsStopped()) {
                            String[] results = new String[3];
                            for (int j = 0; j < 3; j++) {
                                results[j] = reels[j].getText();
                            }
                            checkAdvanceCombo(results);
                        }
                    }
                }
            });
            
            // Usar col en lugar de i para el Timer
            Timer startDelay = new Timer(col * 400, e -> spinTimers[col].start());
            startDelay.setRepeats(false);
            startDelay.start();
        }
    }

    private boolean allReelsStopped() {
        for (Timer timer : spinTimers) {
            if (timer.isRunning()) {
                return false;
            }
        }
        return true;
    }

    public String[] spin() {
        String[] results = new String[3];
        for (int i = 0; i < 3; i++) {
            results[i] = symbols[random.nextInt(symbols.length)];
        }
        checkAdvanceCombo(results);
        return results;
    }

    private void checkAdvanceCombo(String[] results) {
        // Buscar el símbolo de avance
        for (int i = 0; i < results.length; i++) {
            if (results[i].equals(ADVANCE_SYMBOL)) {
                // Verificar si los otros dos símbolos son iguales
                String otherSymbol = null;
                boolean validCombo = true;
                
                for (int j = 0; j < results.length; j++) {
                    if (j != i) {
                        if (otherSymbol == null) {
                            otherSymbol = results[j];
                        } else if (!results[j].equals(otherSymbol)) {
                            validCombo = false;
                            break;
                        }
                    }
                }
                
                if (validCombo) {
                    advancesRemaining = 2;
                    advanceColumn = i;
                }
            }
        }
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