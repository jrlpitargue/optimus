import java.util.List;
import java.util.ArrayList;

public class Simplex {

    public List<float[][]> solve(float[][] tableau) {
        int pr, pc;

        List<float[][]> iterations = new ArrayList<float[][]>();

        // for(int i = 0; i < tableau.length; i++) {
        //     for(int j = 0; j < tableau[0].length; j++) {
        //         System.out.printf("%.4f ", tableau[i][j]);
        //     }
        //     System.out.println();
        // }
        // System.out.println();

        while(bottomRowHasNegative(tableau[tableau.length-1])) {
            pc = choosePivotColumn(tableau[tableau.length-1]);
            pr = choosePivotRow(pc, tableau);

            // System.out.println("(" + pr + ", " + pc + ")");

            float pivotElement = tableau[pr][pc];
            for(int i = 0; i < tableau[0].length; i++) {
                tableau[pr][i] = tableau[pr][i] / pivotElement;
            }

            for(int i = 0; i < tableau.length; i++) {
                if(i != pr) {
                    float temp = tableau[i][pc];
                    for(int j = 0; j < tableau[0].length; j++) {
                        tableau[i][j] = tableau[i][j] - (temp * tableau[pr][j]);
                    }
                }
            }

            // for(int i = 0; i < tableau.length; i++) {
            //     for(int j = 0; j < tableau[0].length; j++) {
            //         System.out.printf("%.2f ", tableau[i][j]);
            //     }
            //     System.out.println();
            // }
            // System.out.println();

            float[][] newTableau = new float[tableau.length][tableau[0].length];
            for(int i = 0; i < tableau.length; i++) {
                System.arraycopy(tableau[i], 0, newTableau[i], 0, tableau[i].length);
            }
            iterations.add(newTableau);
        }

        return iterations;
    }

    private boolean bottomRowHasNegative(float[] row) {
        for(int i = 0; i < row.length; i++) {
            if(row[i] < 0) {
                return true;
            }
        }

        return false;
    }

    private int choosePivotColumn(float[] row) {
        float max = Float.MIN_VALUE;
        int col = -1;
        for(int i = 0; i < row.length; i++) {
            if(row[i] < 0 && Math.abs(row[i]) > max) {
                max = Math.abs(row[i]);
                col = i;
            }
        }

        return col;
    }

    private int choosePivotRow(int pc, float[][] tableau) {
        float min = Float.MAX_VALUE;
        int lastCol = tableau[0].length - 1;
        int row = -1;
        float tr;
        for(int i = 0; i < tableau.length - 1; i++) {
            if(tableau[i][pc] != 0F) {
                tr = tableau[i][lastCol] / tableau[i][pc];
                // System.out.println("row " + i + " tr=" + tr);
                if(tr > 0 && tr < min) {
                    row = i;
                    min = tr;
                }
            }
        }

        return row;
    }

}
