/****************************************************************
 PROGRAM:   CD Automation Console
 AUTHOR:    Alex Murray
 DUE DATE:  28/05/2020

 FUNCTION:  The purpose of the program is for the user to request CDs.

 INPUT:     Data file "CDRecords"

 OUTPUT:    Date file "CDRecords , Data file "Hash"

 NOTES:     This section of code is for the Automation console that'll
 connect to the Archive Console.
 ****************************************************************/

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

/*
 * @author Alex Murray
 */

public class AutomationConsole extends JFrame implements WindowListener {

    ArrayList<Object[]> dataValues;
    int maxEntries = 100;
    int numberOfEntries = 0;

    private Socket socket = null;
    //private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientAutomation client2 = null;
    private String serverName = "localhost";
    private int serverPort = 4444;

    private JTextField txtSection, txtBarcode;
    private JLabel lblCurrent, lblBar, lblSection, lblCDs, lblMessage;
    private JButton btnProcess, btnAdd, btnExit, btnConnect;
    private JComboBox cboRequest;
    private JTable table;
    private MyModel wordModel;
    private String whichButton = "";
    private  String whichAction = "";

    CDRecord[] CDInfo = new CDRecord[maxEntries];

    public static void main(String[] args) {
        JFrame myJFrame = new AutomationConsole();
        myJFrame.setSize(680, 335);
        myJFrame.setLocation(700, 300);
        myJFrame.setResizable(false);
        myJFrame.setVisible(true);
    }

    public AutomationConsole() {
        setTitle("Automation Console");
        setBackground(Color.DARK_GRAY);
        SpringLayout myLayout = new SpringLayout();
        setLayout(myLayout);
        displayTextFields(myLayout);
        displayLabels(myLayout);
        displayButtons(myLayout);
        displayComboBox(myLayout);
        eventButtons();
        AutomationConsole(myLayout);
        this.addWindowListener(this);
        getParameters();
        jTextFieldKeyPressed();
    }

    public void displayComboBox(SpringLayout myComboBoxLayout) {
        String[] petStrings = {"Add", "Remove", "Retrieve", "Return", "Random Collection Sort", "Mostly Sorted Sort", "Reverse Sorted Sort"};
        JComboBox myJComboBox = new JComboBox(petStrings);
        Dimension preferredSize = myJComboBox.getPreferredSize();
        preferredSize.width = 165;
        myJComboBox.setPreferredSize(preferredSize);
        cboRequest = LibraryComponents.LocateAJComboBox(this, myComboBoxLayout, myJComboBox, 500, 10, 200, 10);
    }

    public void displayTextFields(SpringLayout myTextFieldLayout) {
        txtBarcode = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 6, 200, 50);
        txtSection = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 3, 330, 50);
    }

    public void displayLabels(SpringLayout myLabelLayout) {
        lblCurrent = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Current Requested Action:", 40, 10);
        lblCDs = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Archive CDs", 270, 80);
        lblBar = LibraryComponents.LocateAJLabel(this, myLabelLayout, "BarCode of Selected Item:", 40, 50);
        lblSection = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Section:", 275, 50);
        lblMessage = LibraryComponents.LocateAJLabel(this, myLabelLayout, "", 120, 268);
    }

    public void displayButtons(SpringLayout myButtonLayout) {
        btnProcess = LibraryComponents.LocateAJButton(this, myButtonLayout, "Process", 380, 10, 90, 20);
        btnAdd = LibraryComponents.LocateAJButton(this, myButtonLayout, "Add Item", 380, 50, 90, 20);
        btnExit = LibraryComponents.LocateAJButton(this, myButtonLayout, "Exit", 544, 268, 100, 20);
        btnConnect = LibraryComponents.LocateAJButton(this, myButtonLayout, "Connect", 16, 268, 100, 20);
    };

    private void jTextFieldKeyPressed() {
        AbstractTableModel MyModel = (AbstractTableModel)table.getModel();
        class MKeyListener extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent event) {
                String Search = txtSection.getText();
                TableRowSorter<AbstractTableModel> tr = new TableRowSorter<AbstractTableModel>(MyModel);
                table.setRowSorter(tr);
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + Search, 2));
            }
        }
        table.repaint();
        txtSection.addKeyListener(new MKeyListener());
    }

    public void selectionListener()
    {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            for(int i = 0; i < 7; i++) {
                if (i == 2){
                    txtSection.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                } else if (i == 5) {
                    txtBarcode.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                }
            }
        });
    }

    public void eventButtons() {
        btnExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                CloseFrame();
            }
        });

        btnAdd.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                whichButton = btnAdd.getText();
                txtBarcode.requestFocus();
                send();
            }
        });

        btnProcess.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                whichAction = (String) cboRequest.getSelectedItem();
                whichButton = btnProcess.getText();
                //cboRequest.requestFocus();
                txtBarcode.requestFocus();
                send();
            }
        });

        btnConnect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                connect(serverName, serverPort);
            }
        });
    }

    public void connect(String serverName, int serverPort)
    {
        println("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
        }
        catch (UnknownHostException uhe)
        {
            println("Host unknown: " + uhe.getMessage());
        }
        catch (IOException ioe)
        {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    private void send()
    {
        try
        {
            streamOut.writeUTF("AutomationConsole:" + whichButton + ":"+ whichAction + ":"+ txtBarcode.getText() + ":" + txtSection.getText());
            streamOut.flush();
            txtBarcode.setText("");
        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(String msg)
    {
        if (msg.equals(".bye"))
        {
            println("Good bye. Press EXIT button to exit ...");
            close();
        }
        else
        {
            Separate(msg);
            System.out.println("Handle: " + msg);
            table.repaint();
            wordModel.fireTableDataChanged();
        }
    }

    public void Separate(String msg){
        String array[] = msg.split(Pattern.quote(":"));
        if (array[0].equals("ArchiveConsole")){
            txtBarcode.setText(array[2]);
            txtSection.setText(array[3]);
            cboRequest.getModel().setSelectedItem(array[1]);
        }
    }

    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client2 = new ChatClientAutomation(this, socket);
        }
        catch (IOException ioe)
        {
            println("Error opening output stream: " + ioe);
        }
    }

    public void close()
    {
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ioe)
        {
            println("Error closing ...");
        }
        client2.close();
        client2.stop();
    }

    void println(String msg)
    {
        lblMessage.setText(msg);
    }

    public void getParameters()
    {
        serverName = "localhost";
        serverPort = 4444;
    }

    private void CloseFrame() {
        super.dispose();
    }

    public void AutomationConsole(SpringLayout myPanelLayout) {
        // Create a panel to hold all other components
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        add(topPanel);

        // Create column names
        String[] columnNames =
                {"Title", "Author", "Section", "X", "Y", "Barcode", "Description", "On loan"};

        // Create some data
        dataValues = new ArrayList();

        try {
            FileInputStream fstream = new FileInputStream("CDRecords.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int i = 0;
            String line;

            while ((line = br.readLine()) != null) {

                String[] temp = line.split(";");

                dataValues.add(new Object[]{temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7]});
                i++;
            }

            br.close();            // Close the BufferedReader
            in.close();            // Close the DataInputStream
            fstream.close();       // Close the FileInputStream

        } catch (Exception e) {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Reading File: " + e.getMessage());
        }

        // constructor of JTable model
        wordModel = new MyModel(dataValues, columnNames);

        // Create a new table instance
        table = new JTable(wordModel);

        // Configure some of JTable's parameters
        table.isForegroundSet();
        table.setShowHorizontalLines(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        add(table);

        // Change the text and background colours
        table.setSelectionForeground(Color.white);
        table.setSelectionBackground(Color.gray);

        // Add the table to a scrolling pane, size and locate
        JScrollPane scrollPane = JTable.createScrollPaneForTable(table);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.setPreferredSize(new Dimension(630, 160));
        myPanelLayout.putConstraint(SpringLayout.WEST, topPanel, 15, SpringLayout.WEST, this);
        myPanelLayout.putConstraint(SpringLayout.NORTH, topPanel, 100, SpringLayout.NORTH, this);
        selectionListener();
    }

    class MyModel extends AbstractTableModel {
        ArrayList<Object[]> al;

        // the headers
        String[] header;

        // constructor
        MyModel(ArrayList<Object[]> obj, String[] header) {
            // save the header
            this.header = header;
            // and the data
            al = obj;
        }

        // method that needs to be overload. The row count is the size of the ArrayList
        public int getRowCount() {
            return al.size();
        }

        // method that needs to be overload. The column count is the size of our header
        public int getColumnCount() {
            return header.length;
        }

        // method that needs to be overload. The object is in the arrayList at rowIndex
        public Object getValueAt(int rowIndex, int columnIndex) {
            return al.get(rowIndex)[columnIndex];
        }

        // a method to return the column name
        public String getColumnName(int index) {
            return header[index];
        }
    }

    public void windowOpened(WindowEvent windowEvent) {
        readFile();
    }

    public void windowClosing(WindowEvent windowEvent)
    {
        System.exit(0);
    }
    public void windowClosed(WindowEvent windowEvent) {

    }

    public void windowIconified(WindowEvent windowEvent) {

    }

    public void windowDeiconified(WindowEvent windowEvent) {

    }

    public void windowActivated(WindowEvent windowEvent) {

    }

    public void windowDeactivated(WindowEvent windowEvent) {

    }

    public void readFile()
    {
        try
        {
            FileInputStream fstream = new FileInputStream("CDRecords.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int i = 0;
            String line;

            while ((line = br.readLine()) != null)
            {

                String[] temp = line.split(";");

                CDInfo[i] = new CDRecord(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7]);
                i++;
            }

            numberOfEntries = i;   // Set numberOfEntries equal to i, so as to remember how many entries are now in the arrays

            br.close();            // Close the BufferedReader
            in.close();            // Close the DataInputStream
            fstream.close();       // Close the FileInputStream
        }
        catch (Exception e)
        {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Reading File: " + e.getMessage());
        }
    }
    class CDRecord {

        private String Title = new String();
        private String Author = new String();
        private String Section = new String();
        private String X = new String();
        private String Y = new String();
        private String Barcode = new String();
        private String Description = new String();
        private String Loan = new String();

        public CDRecord(String title, String author, String section, String x, String y, String barcode, String description, String loan) {
            Title = title;
            Author = author;
            Section = section;
            X = x;
            Y = y;
            Barcode = barcode;
            Description = description;
            Loan = loan;
        }
    }
}




