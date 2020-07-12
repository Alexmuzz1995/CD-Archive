/****************************************************************
 PROGRAM:   CD Archive Console
 AUTHOR:    Alex Murray
 DUE DATE:  28/05/2020

 FUNCTION:  The purpose of the program is to control a CD Archive robots inventory.

 INPUT:     Data file "CDRecords"

 OUTPUT:    Date file "CDRecords , Data file "Hash"

 NOTES:     This section of code is for the main Archive console that'll
 connect to the Automation Console.
 ****************************************************************/

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.HashMap;
import javax.swing.JScrollPane;

/*
 * @author Alex Murray 470977079
 */

public class ArchiveConsole extends JFrame implements WindowListener {

    ArrayList<Object[]> dataValues;
    DList dList;
    int maxEntries = 100;
    int numberOfEntries = 0;
    int currentEntry = 0;

    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientArchive client = null;
    private String serverName = "localhost";
    private int serverPort = 4444;

    private JTextField txtSearch, txtTitle, txtAuthor, txtSection, txtX, txtY, txtBarcode, txtDesc, txtSortSec;
    private JLabel lblMessage, lblSearch, lblCDs, lblSort, lblLog, lblBinary, lblSet, lblTitle2, lblAuthor2, lblSec2, lblX2, lblY2, lblBarcode2, lblDesc2, lblAction, lblSortSec;
    private JButton btnTitle, btnConnect, btnAuthor, btnBarcode, btnLog, btnPreO, btnInO, btnPostO, btnSave, btnDisplay, btnNew, btnUpdate, btnRetrieve, btnReturn, btnRemove, btnAdd, btnRanSort, btnMSort, btnRevSort, btnExit;
    private JTextArea txtLog;
    private JTable table;
    private MyModel wordModel;
    private String whichButton = "";
    private String whichSection = "";
    private String whichBarcode = "";
    private BinaryTree mytree = new BinaryTree();
    Map<String, Integer> hm;

    CDRecord[] CDInfo = new CDRecord[maxEntries];

    public static void main(String[] args) {
        JFrame myJFrame = new ArchiveConsole();
        myJFrame.setSize(950, 600);
        myJFrame.setLocation(400, 200);
        myJFrame.setResizable(false);
        myJFrame.setVisible(true);
    }

    public ArchiveConsole() {
        setTitle("Archive Console");
        setBackground(Color.DARK_GRAY);
        SpringLayout myLayout = new SpringLayout();
        setLayout(myLayout);
        displayTextFields(myLayout);
        displayLabels(myLayout);
        displayButtons(myLayout);
        eventButtons();
        displayTextAreas(myLayout);
        ArchiveConsole(myLayout);
        this.addWindowListener(this);
        jTextFieldKeyPressed();
        getParameters();
    }

    //Search funtion that filters the table
    private void jTextFieldKeyPressed() {
        AbstractTableModel MyModel = (AbstractTableModel)table.getModel();
        class MKeyListener extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent event) {
                String Search = txtSearch.getText();
                TableRowSorter<AbstractTableModel> tr = new TableRowSorter<AbstractTableModel>(MyModel);
                table.setRowSorter(tr);
                tr.setRowFilter(RowFilter.regexFilter("(?i)" + Search, 0));
            }
        }
        table.repaint();
        txtSearch.addKeyListener(new MKeyListener());
    }

    //Displays textFields
    public void displayTextFields(SpringLayout myTextFieldLayout) {
        txtSearch = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 15, 80, 10);
        txtTitle = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 12, 720, 20);
        txtAuthor = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 12, 720, 45);
        txtSection = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 5, 720, 70);
        txtX = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 5, 720, 95);
        txtY = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 5, 720, 120);
        txtBarcode = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 12, 720, 145);
        txtDesc = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 15, 720, 170);
        txtSortSec = LibraryComponents.LocateAJTextField(this, myTextFieldLayout, 5, 720, 375);
    }

    //Displays Labels
    public void displayLabels(SpringLayout myLabelLayout) {
        lblSearch = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Search:", 20, 10);
        lblCDs = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Archive CDs", 270, 35);
        lblSort = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Sort:", 15, 215);
        lblLog = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Process Log:", 15, 250);
        lblBinary = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Display Binary Tree:", 15, 460);
        lblSet = LibraryComponents.LocateAJLabel(this, myLabelLayout, "HashMap / Set:", 35, 485);
        lblMessage = LibraryComponents.LocateAJLabel(this, myLabelLayout, "", 130, 522);
        lblTitle2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Title:", 635, 20);
        lblAuthor2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Author:", 635, 45);
        lblSec2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Section:", 635, 70);
        lblX2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "X:", 635, 95);
        lblY2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Y:", 635, 120);
        lblBarcode2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "BarCode:", 635, 145);
        lblDesc2 = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Description:", 635, 170);
        lblAction = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Automation Action Request for the item above:", 635, 265);
        lblSortSec = LibraryComponents.LocateAJLabel(this, myLabelLayout, "Sort Selection:", 635, 375);
    }

    //Displays buttons
    public void displayButtons(SpringLayout myButtonLayout) {
        btnTitle = LibraryComponents.LocateAJButton(this, myButtonLayout, "By Title", 50, 215, 100, 20);
        btnAuthor = LibraryComponents.LocateAJButton(this, myButtonLayout, "By Author", 155, 215, 100, 20);
        btnBarcode = LibraryComponents.LocateAJButton(this, myButtonLayout, "By Barcode", 370, 215, 100, 20);
        btnLog = LibraryComponents.LocateAJButton(this, myButtonLayout, "Process Log", 452, 250, 120, 20);
        btnPreO = LibraryComponents.LocateAJButton(this, myButtonLayout, "Pre-Order", 130, 460, 100, 20);
        btnInO = LibraryComponents.LocateAJButton(this, myButtonLayout, "In-Order", 235, 460, 100, 20);
        btnPostO = LibraryComponents.LocateAJButton(this, myButtonLayout, "Post-Order", 340, 460, 100, 20);
        btnSave = LibraryComponents.LocateAJButton(this, myButtonLayout, "Save", 130, 485, 100, 20);
        btnDisplay = LibraryComponents.LocateAJButton(this, myButtonLayout, "Display", 235, 485, 100, 20);
        btnNew = LibraryComponents.LocateAJButton(this, myButtonLayout, "New Item", 635, 210, 100, 20);
        btnUpdate = LibraryComponents.LocateAJButton(this, myButtonLayout, "Save/Update", 770, 210, 110, 20);
        btnRetrieve = LibraryComponents.LocateAJButton(this, myButtonLayout, "Retrieve", 635, 295, 130, 20);
        btnReturn = LibraryComponents.LocateAJButton(this, myButtonLayout, "Return", 635, 325, 130, 20);
        btnRemove = LibraryComponents.LocateAJButton(this, myButtonLayout, "Remove", 770, 295, 130, 20);
        btnAdd = LibraryComponents.LocateAJButton(this, myButtonLayout, "Add to Collection", 770, 325, 130, 20);
        btnRanSort = LibraryComponents.LocateAJButton(this, myButtonLayout, "Random Collection Sort", 720, 415, 170, 20);
        btnMSort = LibraryComponents.LocateAJButton(this, myButtonLayout, "Mostly Sorted Sort", 720, 445, 170, 20);
        btnRevSort = LibraryComponents.LocateAJButton(this, myButtonLayout, "Reverse Order Sort", 720, 475, 170, 20);
        btnExit = LibraryComponents.LocateAJButton(this, myButtonLayout, "Exit", 635, 520, 255, 20);
        btnConnect = LibraryComponents.LocateAJButton(this, myButtonLayout, "Connect", 20, 520, 100, 20);
        btnRanSort.setEnabled(false);
        btnMSort.setEnabled(false);
        btnRevSort.setEnabled(false);

    };
    //Displays textarea
    public void displayTextAreas(SpringLayout myPanelLayout) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);

        //Setting up a JTextArea
        txtLog = LibraryComponents.LocateAJTextArea(this, myPanelLayout, txtLog, 15, 2000, 11, 50);
        myPanelLayout.putConstraint(SpringLayout.WEST, panel, 15, SpringLayout.WEST, this);
        myPanelLayout.putConstraint(SpringLayout.NORTH, panel, 274, SpringLayout.NORTH, this);

        //Setting up a ScrollPane for the JTextArea
        JScrollPane spDataScrollPane = new JScrollPane(txtLog);
        spDataScrollPane.setPreferredSize(new Dimension(560,180));
        myPanelLayout.putConstraint(SpringLayout.WEST, spDataScrollPane, 10, SpringLayout.WEST,panel);
        myPanelLayout.putConstraint(SpringLayout.NORTH,spDataScrollPane , 10, SpringLayout.NORTH,panel);
        spDataScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(spDataScrollPane);
    }

    //button events for each buttons
    public void eventButtons() {
        btnExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        btnTitle.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                JavaSort.BubbleSort(dataValues);
                table.repaint();
            }
        });
        btnAuthor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                JavaSort.SelectionSort(dataValues);
                table.repaint();
            }
        });
        btnBarcode.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
               JavaSort.InsertionSort(dataValues);
                table.repaint();
            }
        });
        btnNew.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                txtTitle.setText("");
                txtAuthor.setText("");
                txtSection.setText("");
                txtX.setText("");
                txtY.setText("");
                txtBarcode.setText("");
                txtDesc.setText("");

//                 Only if the array is large enough to store another record...
                if (numberOfEntries < maxEntries - 1) {
                    // Increment the numberOfEntries
                    numberOfEntries++;
                    // Set the current entry to the new record
                    currentEntry = numberOfEntries - 1;
                    // Blank out any existing data in the arrays, ready
                    //       for adding the new record.
                    CDInfo[currentEntry] = new CDRecord("", "", "", "", "", "", "","");
                    dataValues.add(new Object[]{"","","","","","","",""});
                    // Display this new blank entry on screen
                }
            }
        });
        btnLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtLog.setText(dList.toString());
            }
        });
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect(serverName, serverPort);
            }
        });
        btnRetrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();

                whichButton = btnRetrieve.getText();
                whichBarcode = txtBarcode.getText();
                whichSection = txtSection.getText();
                createDList(df.format(dateobj), " SENT ", whichButton , whichBarcode, whichSection);
                txtLog.append(dList.toString());
                send("Retrieve");
            }
        });
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();

                whichButton = btnRemove.getText();
                whichBarcode = txtBarcode.getText();
                whichSection = txtSection.getText();
                createDList(df.format(dateobj), " SENT ", whichButton , whichBarcode, whichSection);
                txtLog.append(dList.toString());
                send("Remove");
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();

                whichButton = btnAdd.getText();
                whichBarcode = txtBarcode.getText();
                whichSection = txtSection.getText();
                createDList(df.format(dateobj), " SENT ", whichButton , whichBarcode, whichSection);
                txtLog.append(dList.toString());
                send("Add");
            }
        });
        btnReturn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();

                whichButton = btnReturn.getText();
                whichBarcode = txtBarcode.getText();
                whichSection = txtSection.getText();
                createDList(df.format(dateobj), " SENT ", whichButton , whichBarcode, whichSection);
                txtLog.append(dList.toString());
                send("Return");
            }
        });
        btnRanSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();

                whichButton = btnRanSort.getText();
                whichSection = txtSortSec.getText();
                createDList(df.format(dateobj), " SENT ", whichButton , whichSection, null);
                txtLog.append(dList.toString());
                send("Random collection Sort");
            }
        });
        btnPostO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBinaryTree();
                mytree.postOrderTraverseTree(mytree.root);
                txtLog.setText(mytree.list);
            }
        });
        btnPreO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBinaryTree();
                mytree.preorderTraverseTree(mytree.root);
                txtLog.setText(mytree.list);
            }
        });
        btnInO.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBinaryTree();
                mytree.inOrderTraverseTree(mytree.root);
                txtLog.setText(mytree.list);
            }
        });
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HashFunction();
                writeFileHash();
            }
        });
        btnDisplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HashFunction();
            }
        });
        txtSortSec.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(txtSortSec.getText().length() > 0)
                    btnRanSort.setEnabled(true);
                else
                    btnRanSort.setEnabled(false);
                if (txtSortSec.getText().length() > 0)
                    btnMSort.setEnabled(true);
                else
                    btnMSort.setEnabled(false);
                if (txtSortSec.getText().length() > 0)
                    btnRevSort.setEnabled(true);
                else
                    btnRevSort.setEnabled(false);
            }
        });
    }

    //Method reading file and populating table with that data
    public void ArchiveConsole(SpringLayout myPanelLayout) {
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
        topPanel.setPreferredSize(new Dimension(600, 150));
        myPanelLayout.putConstraint(SpringLayout.WEST, topPanel, 15, SpringLayout.WEST, this);
        myPanelLayout.putConstraint(SpringLayout.NORTH, topPanel, 60, SpringLayout.NORTH, this);
        // get selected row data
        selectionListener();
    }

    // creates the binary tree
    public void createBinaryTree(){
        mytree = new BinaryTree();
        for(int i = 0; i < dataValues.size(); i++){
            int barcode = Integer.parseInt(dataValues.get(i)[5].toString());
            String title = dataValues.get(i)[0].toString();
            mytree.addNode(barcode, title);
        }
    }
    //hashing values from binary tree
    public void HashFunction(){
        hm = new HashMap<String, Integer>();

        for(int i = 0; i < dataValues.size(); i++)
        {
            hm.put(dataValues.get(i)[0].toString(), Integer.parseInt(dataValues.get(i)[5].toString()));
        }
        txtLog.setText(" ");
        txtLog.append(String.valueOf(hm));
    }

    //Fills textfields with data from table
    public void selectionListener()
    {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting()) return;
                currentEntry = table.getSelectedRow();
                for(int i = 0; i < 7; i++) {
                    if (i == 0){
                        txtTitle.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else if (i == 1) {
                        txtAuthor.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else if (i == 2) {
                        txtSection.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else if (i == 3) {
                        txtX.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else if (i == 4) {
                        txtY.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else if (i == 5) {
                        txtBarcode.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    } else {
                        txtDesc.setText(table.getValueAt(table.getSelectedRow(), i).toString());
                    }
                }
            }
        });
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

        void add(String title, String author, String section, String x, String y, String barcode, String description, String loan)
        {
            String[] str = new String[8];
            str[0] = title;
            str[1] = author;
            str[2] = section;
            str[3] = x;
            str[4] = y;
            str[5] = barcode;
            str[6] = description;
            str[7] = loan;
            al.add(str);
            // inform the GUI that I have change
            fireTableDataChanged();
        }

    }

    //Connects to server
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

    //sends request to server
    private void send(String action)
    {
        try
        {
            streamOut.writeUTF("ArchiveConsole" + ":" + whichButton + ":"+ whichBarcode + ":"+ whichSection);
            streamOut.flush();
        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    //Handles incoming requests
    public void handle(String msg)
    {
            Separate(msg);
            println(msg);
    }

    //seperates incoming data and places them in arraylist
    public void Separate(String msg){
        String array[] = msg.split(Pattern.quote(":"));
        if (array[0].equals("AutomationConsole")){
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();

            createDList(df.format(dateobj), " RCVD ", array[2] , array[3], array[4]);
            txtLog.append(dList.toString());
        }
    }

    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientArchive(this, socket);
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
        client.close();
        client.stop();
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

    public void windowOpened(WindowEvent windowEvent) {
        readFile();
        displayEntry(currentEntry);
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

    //Displays first cell item from table in textfields
    public void displayEntry(int index)
    {
        txtTitle.setText(CDInfo[index].getTitle());
        txtAuthor.setText(CDInfo[index].getAuthor());
        txtSection.setText(CDInfo[index].getSection());
        txtX.setText(CDInfo[index].getX());
        txtY.setText(CDInfo[index].getY());
        txtBarcode.setText(CDInfo[index].getBarcode());
        txtDesc.setText(CDInfo[index].getDescription());
    }

    //saves entry that's in textfields
    public void saveEntry(int index)
    {
        //dataValues.add(new Object[] {txtTitle.getText(), txtAuthor.getText(), txtSection.getText(), txtX.getText(), txtY.getText(), txtBarcode.getText(), txtDesc.getText(), "No"});
        //if statement if new record then .add else .set
        //index will tell if its a new entry
        Object[] obj = {txtTitle.getText(), txtAuthor.getText(), txtSection.getText(), txtX.getText(), txtY.getText(), txtBarcode.getText(), txtDesc.getText(), "No" };
        CDInfo[index].setTitle(txtTitle.getText());
        CDInfo[index].setAuthor(txtAuthor.getText());
        CDInfo[index].setSection(txtSection.getText());
        CDInfo[index].setX(txtX.getText());
        CDInfo[index].setY(txtY.getText());
        CDInfo[index].setBarcode(txtBarcode.getText());
        CDInfo[index].setDescription(txtDesc.getText());
        CDInfo[index].setLoan("No");
        dataValues.set(index, obj);
        writeFile();
    }

    //Creates Dlist
    public void createDList(String date, String action, String message, String barcode, String section) {
        dList = new DList();              // create an empty dList, make global
        dList.print();

        dList.head.append(new Node("\n" + date + " - " + action +" - " + message + " - " + barcode));
        dList.print();
    }

    //reads text file
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

    //Writes a new text file
    public void writeFile()
    {
        try
        {
            PrintWriter out = new PrintWriter(new FileWriter("CDRecords.txt"));

            for(int m = 0; m < numberOfEntries; m++){
                out.println(CDInfo[m].getTitle() +";" + CDInfo[m].getAuthor() + ";" + CDInfo[m].getSection()+ ";" + CDInfo[m].getX()+ ";" + CDInfo[m].getY()+ ";" + CDInfo[m].getBarcode()+ ";" + CDInfo[m].getDescription()+ ";" + CDInfo[m].getLoan());
            }

            // Close the printFile (and in so doing, empty the print buffer)
            out.close();
        }
        catch (Exception e)
        {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Writing File: " + e.getMessage());
        }
    }

    //writes text file for hashcode
    public void writeFileHash()
    {
        try
        {
            PrintWriter out = new PrintWriter(new FileWriter("Hash.txt"));

            out.println(hm);

            out.close();
        }
        catch (Exception e)
        {
            // If an exception occurs, print an error message on the console.
            System.err.println("Error Writing File: " + e.getMessage());
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

        public CDRecord() {
            Title = "Title";
            Author = "Author";
            Section = "Section";
            X = "X";
            Y = "Y";
            Barcode = "Barcode";
            Description = "Description";
            Loan = "Loan";
        }

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

        public void setCDInfo(String title, String author, String section, String x, String y, String barcode, String description, String loan) {
            Title = title;
            Author = author;
            Section = section;
            X = x;
            Y = y;
            Barcode = barcode;
            Description = description;
            Loan = loan;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getAuthor() {
            return Author;
        }

        public void setAuthor(String Author) {
            this.Author = Author;
        }

        public String getSection() {
            return Section;
        }

        public void setSection(String Section) {
            this.Section = Section;
        }

        public String getX() {
            return X;
        }

        public void setX(String X) {
            this.X = X;
        }

        public String getY() {
            return Y;
        }

        public void setY(String Y) {
            this.Y = Y;
        }

        public String getBarcode() {
            return Barcode;
        }

        public void setBarcode(String Barcode) {
            this.Barcode = Barcode;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getLoan() {
            return Loan;
        }

        public void setLoan(String Loan) {
            this.Loan = Loan;
        }
    }
}




