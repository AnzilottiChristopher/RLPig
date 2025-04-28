import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PigSolver {
    int goal;
    double epsilon;
    double[][][] p;
    boolean[][][] roll;

    PigSolver(int goal, double epsilon) {
        this.goal = goal;
        this.epsilon = epsilon;
        p = new double[goal][goal][goal];
        roll = new boolean[goal][goal][goal];
        valueIterate();
    }

    void valueIterate() {
        double maxChange;
        do {
            maxChange = 0.0;
            for (int i = 0; i < goal; i++) {
                for (int j = 0; j < goal; j++) {
                    for (int k = 0; k < goal - i; k++) {
                        if (i + k >= goal) {
                            p[i][j][k] = 1.0;
                            roll[i][j][k] = false;
                            continue;
                        }
                        if (j >= goal) {
                            p[i][j][k] = 0.0;
                            roll[i][j][k] = false;
                            continue;
                        }

                        double oldProb = p[i][j][k];

                        // HOLD
                        double pHold = 1.0 - pWin(j, i + k, 0);

                        // ROLL
                        double pRoll = (1.0 / 6.0) * (1.0 - pWin(j, i, 0)); // rolled a 1
                        for (int r = 2; r <= 6; r++) {
                            if (i + k + r >= goal) {
                                pRoll += (1.0 / 6.0) * 1.0; // win immediately
                            } else {
                                pRoll += (1.0 / 6.0) * pWin(i, j, k + r);
                            }
                        }

                        p[i][j][k] = Math.max(pRoll, pHold);
                        roll[i][j][k] = pRoll > pHold;

                        double change = Math.abs(p[i][j][k] - oldProb);
                        maxChange = Math.max(maxChange, change);
                    }
                }
            }
        } while (maxChange >= epsilon);
    }

    public double pWin(int i, int j, int k) {
        if (i + k >= goal) {
            return 1.0;
        } else if (j >= goal) {
            return 0.0;
        } else {
            return p[i][j][k];
        }
    }

    public void outputHoldValues() {
        for (int i = 0; i < goal; i += 1) {
            for (int j = 0; j < goal; j += 1) {
                int k = 0;
                while (k < goal - i && roll[i][j][k]) {
                    k++;
                }
                System.out.printf("%2d ", k);
            }
            System.out.println();
        }
    }

    // this function was to convert the output into csv that could be loaded into excel and turned into a heatmap pivot table
    public void outputHoldValuesToCSV() {
    String filePath = "hold_values.csv";
    
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write("Your Score, Opponent Score, Amount to Hold After\n");
        
        for (int i = 0; i < goal; i++) {
            for (int j = 0; j < goal; j++) {
                int k = 0;
                while (k < goal - i && roll[i][j][k]) {
                    k++;
                }
                writer.write(i + "," + j + "," + k + "\n");
            }
        }
        
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        new PigSolver(100, 1e-9).outputHoldValues();
        // new PigSolver(100, 1e-9).outputHoldValuesToCSV();
    }
}
