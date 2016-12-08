import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.util.List;

public class RemoveConstraintButton extends JButton implements ActionListener{

    private JPanel source;
    private JPanel container;
    private List<String[]> constraints;
    private int count;

    public RemoveConstraintButton(JPanel source, JPanel container, List<String[]> constraints, int count) {
        super(" X ");
        this.source = source;
        this.container = container;
        this.constraints = constraints;
        this.count = count;
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        source.remove(container);
        source.revalidate();
        source.repaint();
        constraints.remove(count);
    }

}
