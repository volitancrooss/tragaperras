package SlotMachineApp.logic;

import java.util.Random;

public class SlotMachineLogic {
    private Random random;

    public SlotMachineLogic() {
        random = new Random();
    }

    public String[] spin() {
        String[] results = new String[3];
        for (int i = 0; i < 3; i++) {
            results[i] = String.valueOf(random.nextInt(10)); // Random number between 0 and 9
        }
        return results;
    }
}