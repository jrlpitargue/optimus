import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Optimus implements ActionListener {

    private JFrame frame;

    private JButton solveButton;
    private JButton resetButton;
    private JButton addConstraintButton;

    private JTextField objectiveField;
    private JTextField leftSideConstraintField;
    private JComboBox<String> signList;
    private JTextField rightSideConstraintField;

    private ButtonGroup minMaxGroup;
    private JRadioButton maximizeRadio;
    // private JRadioButton minimizeRadio;

    private JPanel constraintsListPanel;

    private Parser parser;
    private List<String[]> constraints;
    private Simplex simplex;

    public Optimus() {
        parser = new Parser();
        constraints = new ArrayList<String[]>();
        simplex = new Simplex();

        frame = new JFrame("Optimus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel objectiveFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel zLabel = new JLabel("Z");
        objectiveFieldPanel.add(zLabel);
        JLabel equalsLabel = new JLabel("=");
        objectiveFieldPanel.add(equalsLabel);
        objectiveField = new JTextField(30);
        objectiveFieldPanel.add(objectiveField);

        inputPanel.add(objectiveFieldPanel);

        JPanel minMaxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        minMaxGroup = new ButtonGroup();

        maximizeRadio = new JRadioButton("Maximize", true);
        minMaxGroup.add(maximizeRadio);
        minMaxPanel.add(maximizeRadio);

        // minimizeRadio = new JRadioButton("Minimize");
        // minMaxGroup.add(minimizeRadio);
        // minMaxPanel.add(minimizeRadio);

        inputPanel.add(minMaxPanel);

        JPanel constraintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        leftSideConstraintField = new JTextField(10);
        constraintPanel.add(leftSideConstraintField);

        signList = new JComboBox<String>(new String[] { "<", ">", "<=", ">=" });
        signList.setSelectedIndex(0);
        constraintPanel.add(signList);

        rightSideConstraintField = new JTextField(5);
        constraintPanel.add(rightSideConstraintField);

        addConstraintButton = new JButton(" + ");
        addConstraintButton.setActionCommand("ADD_CONSTRAINT");
        addConstraintButton.addActionListener(this);
        constraintPanel.add(addConstraintButton);

        inputPanel.add(constraintPanel);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Optimization Options"));

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();

        solveButton = new JButton("Solve");
        solveButton.setActionCommand("SOLVE");
        solveButton.addActionListener(this);

        buttonPanel.add(solveButton);

        resetButton = new JButton("Reset");
        resetButton.setActionCommand("RESET");
        resetButton.addActionListener(this);

        buttonPanel.add(resetButton);

        optionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        constraintsListPanel = new JPanel();
        constraintsListPanel.setLayout(new BoxLayout(constraintsListPanel, BoxLayout.Y_AXIS));
        constraintsListPanel.setSize(new Dimension(constraintsListPanel.getWidth(), 50));
        constraintsListPanel.setBorder(BorderFactory.createTitledBorder("Constraints"));

        optionsPanel.add(constraintsListPanel, BorderLayout.CENTER);

        c.add(optionsPanel, BorderLayout.WEST);

        frame.pack();
        frame.setMinimumSize(frame.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void solve() {
        String objectiveString = objectiveField.getText();

        if(objectiveString.equals("")) {
            JOptionPane.showMessageDialog(frame, "Please provide an objective function.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(constraints.size() == 0) {
            int n = JOptionPane.showConfirmDialog(frame, "You have not specified any constraints. Are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
            if(n != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            parser.parseVariables(objectiveString);
            float[][] tableau = parser.generateInitialTableau(objectiveString, constraints);
            System.out.println("ITERATION 1");
            System.out.println("Tableau");
            for(int j = 0; j < tableau.length; j++) {
                for(int k = 0; k < tableau[0].length; k++) {
                    System.out.printf("%.4f ", tableau[j][k]);
                }
                System.out.println();
            }
            HashMap<String, Float> soln = parser.getBasicSolution(tableau);
            System.out.println("Basic Solution");
            for (Map.Entry<String, Float> entry : soln.entrySet()) {
                String key = entry.getKey();
                float value = entry.getValue();
                System.out.println(key + " = " + value);
            }
            List<float[][]> result = simplex.solve(tableau);
            
            for(int i = 0; i < result.size(); i++) {
                System.out.println("ITERATION " + (i+2));
                System.out.println("Tableau");
                float[][] resTableau = result.get(i);
                for(int j = 0; j < resTableau.length; j++) {
                    for(int k = 0; k < resTableau[0].length; k++) {
                        System.out.printf("%.4f ", resTableau[j][k]);
                    }
                    System.out.println();
                }
                HashMap<String, Float> solution = parser.getBasicSolution(resTableau);
                System.out.println("Basic Solution");
                for (Map.Entry<String, Float> entry : solution.entrySet()) {
                    String key = entry.getKey();
                    float value = entry.getValue();
                    System.out.println(key + " = " + value);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            parser.resetVariables();
            JOptionPane.showMessageDialog(frame, "Something went wrong! Check your inputs and try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addConstraint() {
        String leftSideString = leftSideConstraintField.getText();
        String sign = (String) signList.getSelectedItem();
        String rightSideString = rightSideConstraintField.getText();

        if(leftSideString.equals("") || rightSideString.equals("")) {
            JOptionPane.showMessageDialog(frame, "Please provide a complete equation for the constraint.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        parser.parseSlackVariable();
        constraints.add(new String[] {leftSideString, sign, rightSideString});

        // display constraint on ui
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JPanel(), BorderLayout.WEST);
        panel.add(new JLabel(leftSideString + " " + sign + " " + rightSideString), BorderLayout.CENTER);
        panel.add(new RemoveConstraintButton(constraintsListPanel, panel, constraints, constraints.size()-1), BorderLayout.EAST);
        constraintsListPanel.add(panel);
        constraintsListPanel.setSize(constraintsListPanel.getPreferredSize());
        constraintsListPanel.validate();
        frame.pack();

        rightSideConstraintField.setText("");
        leftSideConstraintField.setText("");
        signList.setSelectedIndex(0);
    }

    private void reset() {
        constraints.clear();
        objectiveField.setText("");
        rightSideConstraintField.setText("");
        leftSideConstraintField.setText("");
        signList.setSelectedIndex(0);
        minMaxGroup.setSelected(maximizeRadio.getModel(), true);
        constraintsListPanel.removeAll();
        constraintsListPanel.revalidate();
        constraintsListPanel.repaint();
        frame.pack();
        parser.reset();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "SOLVE":
                solve();
                break;
            case "ADD_CONSTRAINT":
                addConstraint();
                break;
            case "RESET":
                reset();
                break;
            default:
                break;
        }
    }

}
