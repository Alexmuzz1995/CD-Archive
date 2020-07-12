import javax.swing.*;
import java.awt.Dimension;


public class LibraryComponents {

    public static JLabel LocateAJLabel(JFrame myJFrame, SpringLayout myJLabelLayout, String JLabelCaption, int x, int y)
    {
        // Instantiate the JLabel
        JLabel myJLabel = new JLabel(JLabelCaption);
        // Add the JLabel to the screen
        myJFrame.add(myJLabel);
        // Set the position of the JLabel (From left hand side of the JFrame (West), and from top of JFrame (North))
        myJLabelLayout.putConstraint(SpringLayout.WEST, myJLabel, x, SpringLayout.WEST, myJFrame);
        myJLabelLayout.putConstraint(SpringLayout.NORTH, myJLabel, y, SpringLayout.NORTH, myJFrame);
        // Return the label to the calling method
        return myJLabel;
    }
    public static JTextField LocateAJTextField(JFrame myJFrame, SpringLayout myJTextFieldLayout, int width, int x, int y)
    {
        JTextField myJTextField = new JTextField(width);
        myJFrame.add(myJTextField);
        myJTextFieldLayout.putConstraint(SpringLayout.WEST, myJTextField, x, SpringLayout.WEST, myJFrame);
        myJTextFieldLayout.putConstraint(SpringLayout.NORTH, myJTextField, y, SpringLayout.NORTH, myJFrame);
        return myJTextField;
    }

    public static JButton LocateAJButton(JFrame myJFrame, SpringLayout myJButtonLayout, String  JButtonCaption, int x, int y, int w, int h)
    {
        JButton myJButton = new JButton(JButtonCaption);
        myJFrame.add(myJButton);
        myJButtonLayout.putConstraint(SpringLayout.WEST, myJButton, x, SpringLayout.WEST, myJFrame);
        myJButtonLayout.putConstraint(SpringLayout.NORTH, myJButton, y, SpringLayout.NORTH, myJFrame);
        myJButton.setPreferredSize(new Dimension(w,h));
        return myJButton;
    }

    public static JTextArea LocateAJTextArea(JFrame myJFrame, SpringLayout myLayout, JTextArea myJTextArea, int x, int y, int w, int h)
    {
        myJTextArea = new JTextArea(w,h);
        myJFrame.add(myJTextArea);
        myLayout.putConstraint(SpringLayout.WEST, myJTextArea, x, SpringLayout.WEST, myJFrame);
        myLayout.putConstraint(SpringLayout.NORTH, myJTextArea, y, SpringLayout.NORTH, myJFrame);
        return myJTextArea;
    }

    public static JTable LocateAJTable(JFrame myJFrame, SpringLayout myLayout, JTable myJTable, int x, int y, int w, int h)
    {
        myJTable = new JTable(w,h);
        myJFrame.add(myJTable);
        myLayout.putConstraint(SpringLayout.WEST, myJTable, x, SpringLayout.WEST, myJFrame);
        myLayout.putConstraint(SpringLayout.NORTH, myJTable, y, SpringLayout.NORTH, myJFrame);
        return myJTable;
    }

    public static JComboBox LocateAJComboBox(JFrame myJFrame, SpringLayout myLayout, JComboBox myJComboBox, int w, int h, int x, int y)
    {

        myJFrame.add(myJComboBox);
        myLayout.putConstraint(SpringLayout.WEST, myJComboBox, x, SpringLayout.WEST, myJFrame);
        myLayout.putConstraint(SpringLayout.NORTH, myJComboBox, y, SpringLayout.NORTH, myJFrame);
        return myJComboBox;
    }
}
