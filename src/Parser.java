import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {

    private List<String> variables;
    private List<String> slackVariables;

    public Parser() {
        variables = new ArrayList<String>();
        slackVariables = new ArrayList<String>();
    }

    public void parseVariables(String input) {
        input = input.replaceAll("\\s+", "");
        String terms[] = input.split("[\\+]");

        for(int i = 0; i < terms.length; i++) {
            variables.add(terms[i].replaceAll(".*\\*", ""));
        }
    }

    public void parseSlackVariable() {
        slackVariables.add("S" + String.valueOf(slackVariables.size() + 1));
    }

    public float[] parseObjectiveFunction(String input) {
        input = input.replaceAll("\\s+", "");
        String terms[] = input.split("[\\+]");
        float row[] = new float[terms.length + slackVariables.size() + 2];
        for(int i = 0; i < terms.length; i++) {
            terms[i] = terms[i].replaceAll("x[0-9]", "");
            row[i] = -Float.parseFloat(terms[i].replaceAll("[^0-9.-]", ""));
        }
        for(int i = terms.length; i < (terms.length + slackVariables.size()); i++) {
            row[i] = 0F;
        }
        row[terms.length + slackVariables.size()] = 1F;
        row[terms.length + slackVariables.size() + 1] = 0F;

        return row;
    }

    public float[] parseConstraint(String leftHand, String sign, String rightHand, int count) {
        // System.out.println("var = " + variables.size());
        // System.out.println("slackvar = " + slackVariables.size());
        leftHand = leftHand.replaceAll("\\s+", "");
        sign = sign.replaceAll("\\s+", "");
        rightHand = rightHand.replaceAll("\\s+", "");

        // determine which variables are involved
        String terms[] = leftHand.split("[\\+\\-]");
        String coefficient[] = new String[terms.length];
        for(int i = 0; i < terms.length; i++) {
            String temp = terms[i].replaceAll("x[0-9]", "");
            coefficient[i] = temp.replaceAll("[^0-9.]", "");
            terms[i] = terms[i].replaceAll(".*\\*", "");
        }

        float row[] = new float[variables.size() + slackVariables.size() + 2];
        for(int i = 0; i < variables.size(); i++) {
            row[i] = 0F;
        }
        for(int i = 0; i < terms.length; i++) {
            row[Integer.parseInt(terms[i].replaceAll("x",""))-1] = Float.parseFloat(coefficient[i]);
        }
        for(int i = variables.size(); i < (variables.size() + slackVariables.size() + 1); i++) {
            // System.out.println(i-variables.size());
            if(i-variables.size() == count) {
                if(sign.equals(">") || sign.equals(">=")) {
                    row[i] = -1F;
                } else {
                    row[i] = 1F;
                }
            } else {
                row[i] = 0F;
            }
        }
        row[variables.size() + slackVariables.size() + 1] = Float.parseFloat(rightHand);

        return row;
    }

    public void reset() {
        slackVariables.clear();
        variables.clear();
    }

    public void resetVariables() {
        variables.clear();
    }

    public float[][] generateInitialTableau(String objective, List<String[]> constraints) {
        float[][] tableau = new float[constraints.size() + 1][];

        for(int i = 0; i < constraints.size(); i++) {
            String[] constraint = constraints.get(i);
            tableau[i] = parseConstraint(constraint[0], constraint[1], constraint[2], i);
        }
        tableau[tableau.length-1] = parseObjectiveFunction(objective);

        return tableau;
    }

    public HashMap<String, Float> getBasicSolution(float[][] tableau) {
        HashMap<String, Float> solution = new HashMap<String, Float>();
        int row;

        for(int i = 0; i < variables.size(); i++) {
            row = hasSolution(tableau, i);
            solution.put(variables.get(i), row == -1 ? 0 : tableau[row][tableau[0].length - 1]);
        }

        for(int i = variables.size(); i < variables.size() + slackVariables.size(); i++) {
            row = hasSolution(tableau, i);
            solution.put(slackVariables.get(i-variables.size()), row == -1 ? 0 : tableau[row][tableau[0].length - 1]);
        }

        row = hasSolution(tableau, variables.size() + slackVariables.size());
        solution.put("z", row == -1 ? 0 : tableau[row][tableau[0].length - 1]);

        return solution;
    }

    private int hasSolution(float[][] tableau, int col) {
        int nonZeroCount = 0;
        int row = -1;
        for(int i = 0; i < tableau.length; i++) {
            if(tableau[i][col] != 0F) {
                nonZeroCount++;
                row = i;
            }
        }

        if(nonZeroCount > 1) return -1;
        else return row;
    }

}
