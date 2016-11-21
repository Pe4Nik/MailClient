
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;


/**
 * Created by Pe4Nik on 08.11.2016.
 */
public class MainWin {
    private JButton newMessageButton;
    private JTree tree1;
    private JTable table1;
    private JButton refreshButton;
    private JPanel MainWin;
    private JScrollPane scroll;
    private JButton deleteButton;
    private String currentFolderName;
    private Message selectedMessage;
    private int selectedRow;
    private Folder currentFolder;
    private Folder inbox, all, sent, trash;
    private DefaultTableModel sentTableModel;
    private DefaultTableModel allTableModel;
    private DefaultTableModel inboxTableModel;
    private DefaultTableModel trashTableModel;
    private Store store;
    private static Frame fframe;
    private static boolean flagError;

    public MainWin(String USERNAME, String PASSWORD) {
        try {
            ArrayList<String> folderNames = new ArrayList<String>();
            connect(USERNAME, PASSWORD, folderNames);
            constructTree(folderNames);
            initializeTable();
            currentFolderName = "inbox";
            selectedMessage = null;
            selectedRow = 0;
            read(USERNAME, PASSWORD, currentFolderName);

            flagError = true;
            Image img = ImageIO.read(getClass().getResource("icons/ComposeMail24.gif"));
            newMessageButton.setIcon(new ImageIcon(img));
            newMessageButton.setMargin(new Insets(0, 0, 0, 0));
            //newMessageButton.setBorder(null);
            img = ImageIO.read(getClass().getResource("icons/Refresh24.gif"));
            refreshButton.setIcon(new ImageIcon(img));
            refreshButton.setMargin(new Insets(0, 0, 0, 0));
            //refreshButton.setBorder(null);
            img = ImageIO.read(getClass().getResource("icons/Delete24.gif"));
            deleteButton.setIcon(new ImageIcon(img));
            deleteButton.setMargin(new Insets(0, 0, 0, 0));
            deleteButton.setEnabled(false);
            //deleteButton.setBorder(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Incorrect email address or password. Please try again!","Error", JOptionPane.ERROR_MESSAGE);
            flagError = false;
            Login login = new Login();
        }


        //for future generations(look down)
//        Thread t = new Thread() {
//            public void run() {
//                while(true) {
//                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
//                    model.setRowCount(0);
//                    initializeTable();
//                    read(USERNAME,PASSWORD, currentFolderName);
//                    System.out.println("blah");
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t.start();


        newMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] args = {USERNAME, PASSWORD};
                NewMessage.main(args);
            }
        });


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                super.mousePressed(me);
                JTable table =(JTable)me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                selectedRow = row;
                if (me.getClickCount() == 2) {
                    try {
                        selectedMessage = ((Message) table1.getModel().getValueAt(row, 0));
                        deleteButton.setEnabled(true);
                        String[] args = {((InternetAddress)(((Message) table1.getModel().getValueAt(row, 0)).getFrom())[0]).getAddress(),
                                "" + table1.getModel().getValueAt(row, 1),
                                "" + getText((Message) table1.getModel().getValueAt(row, 0))};
                        ShowMessage sm = new ShowMessage(args[0], args[1], args[2], (Message) table1.getModel().getValueAt(row, 0));
                        //sm.setMessage((Message) table1.getModel().getValueAt(row, 0));
                        //sm.show(args, (Message) table1.getModel().getValueAt(row, 0));
                        //sm.main(args);
                        //ShowMessage.main(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(me.getClickCount() == 1) {
                    selectedMessage = ((Message) table1.getModel().getValueAt(row, 0));
                    deleteButton.setEnabled(true);
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.setRowCount(0);
                initializeTable();
                //read(USERNAME,PASSWORD, currentFolderName);
                try {
                    refresh();
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
        });

        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree1
                        .getLastSelectedPathComponent();
                String selectedNodeName = selectedNode.toString();
                if (selectedNode.isLeaf()) {
//                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
//                    model.setRowCount(0);
//                    initializeTable();
                    try {
                        changeFolder(selectedNodeName);
                    } catch (MessagingException e1) {
                        e1.printStackTrace();
                    }
                    //read(USERNAME, PASSWORD, selectedNodeName);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (currentFolder != trash) {
                        currentFolder.copyMessages(new Message[]{selectedMessage}, trash);
                        trashTableModel.addRow(new Object[] {trash.getMessage(trash.getMessageCount()), trash.getMessage(trash.getMessageCount()).getSubject(),
                                trash.getMessage(trash.getMessageCount()).getSentDate()});
                        //selectedMessage.setFlag(Flags.Flag.DELETED, true);
                        DefaultTableModel model = (DefaultTableModel) table1.getModel();
                        model.removeRow(selectedRow);
                    }
                    else {
                        DefaultTableModel model = (DefaultTableModel) table1.getModel();
                        model.removeRow(selectedRow);
                        selectedMessage.setFlag(Flags.Flag.DELETED, true);
                        currentFolder.expunge();
                        currentFolder.close(true);
                        currentFolder.open(Folder.READ_WRITE);
                    }
                    deleteButton.setEnabled(false);
//                    currentFolder.expunge();
//                    currentFolder.close(true);
//                    currentFolder.open(Folder.READ_WRITE);
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    public void refresh() throws MessagingException {
        DefaultTableModel mod1 = (DefaultTableModel) table1.getModel();
        mod1.setRowCount(0);
        initializeTable();
        //currentFolder = store.getFolder("[Gmail]").getFolder(currentFolder.getName());
        //currentFolder.open(Folder.READ_WRITE);
        Message[] messages = currentFolder.getMessages();
        String col[] = {"Message","Subject", "Date"};
        mod1 = new DefaultTableModel(col, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table1.removeColumn(table1.getColumnModel().getColumn(0));
        for (int i = 0; i < messages.length; i++) {
            Object[] row = {messages[i], messages[i].getSubject(), messages[i].getSentDate()};
            mod1.addRow(row);

        }

        switch(currentFolder.getName()) {
            case "All Mail" :
                allTableModel = mod1;
                break;
            case "inbox" :
                inboxTableModel = mod1;
                break;
            case "Sent Mail" :
                sentTableModel = mod1;
                break;
            case "Trash" :
                trashTableModel = mod1;
                break;
        }
        table1.setModel(mod1);
        changeFolder(currentFolder.getName());
    }

    private boolean textIsHtml = false;


    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }


    public static void main(String[] args) {
        String USERNAME = args[0];
        String PASSWORD = args[1];
        JFrame frame = new JFrame("Email client");
            JMenuBar bar = new JMenuBar();
            JMenu menu = new JMenu("Menu");
            bar.add(menu);
            JMenuItem logout = new JMenuItem("Logout");

            menu.add(logout);
            frame.setJMenuBar(bar);
        fframe = frame;
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                   super.mousePressed(me);
                frame.dispose();
                Login login = new Login();
            }
        });

        frame.setContentPane(new MainWin(USERNAME, PASSWORD).MainWin);
        if(flagError) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }


    public void constructTree(ArrayList<String> folderNames) {
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();

        tree1.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean isLeaf, int row, boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value,
                        selected, expanded, isLeaf, row, focused);

                Image img = null;
                try {
                    switch((((DefaultMutableTreeNode) value).getUserObject().toString())) {
                        case "Trash":
                            img = ImageIO.read(getClass().getResource("icons/Delete24.gif"));
                            setIcon(new ImageIcon(img));
                            break;
                        case "Sent Mail":
                            img = ImageIO.read(getClass().getResource("icons/Export24.gif"));
                            setIcon(new ImageIcon(img));
                            break;
                        case "INBOX":
                            img = ImageIO.read(getClass().getResource("icons/Import24.gif"));
                            setIcon(new ImageIcon(img));
                            break;
                        case "All Mail":
                            img = ImageIO.read(getClass().getResource("icons/History24.gif"));
                            setIcon(new ImageIcon(img));
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return c;
            }
        });

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        DefaultMutableTreeNode node;
//        for(String an:folderNames) {
//            if(!(an.equalsIgnoreCase("[Gmail]") || an.equalsIgnoreCase("Drafts") || an.equalsIgnoreCase("Starred") ||
//                    an.equalsIgnoreCase("Spam") || an.equalsIgnoreCase("Important")))
//                root.add(new DefaultMutableTreeNode(an));
//        }
        root.add(new DefaultMutableTreeNode("All Mail"));
        root.add(new DefaultMutableTreeNode("INBOX"));
        root.add(new DefaultMutableTreeNode("Sent Mail"));
        root.add(new DefaultMutableTreeNode("Trash"));
        root.setUserObject("Mail");
        model.nodeChanged(root);
        model.reload(root);
        tree1.setSelectionRow(1);

    }

    public void connect(String USERNAME, String PASSWORD, ArrayList<String> folderNames) throws MessagingException {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = null;
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", USERNAME, PASSWORD);
            Folder[] f = store.getDefaultFolder().list("*");
            for(Folder fd:f) {
                folderNames.add(fd.getName());
            }




    }

    public void initializeTable() {
        String col[] = {"Message","Subject", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table1.setModel(tableModel);
    }



    public void changeFolder(String folderName) throws MessagingException {
        boolean bool = true;
        switch(folderName) {
            case "Sent Mail":
                if(currentFolder == sent)
                    bool = false;
                table1.setModel(sentTableModel);
                currentFolder = sent;
                break;
            case "All Mail":
                if(currentFolder == all)
                    bool = false;
                table1.setModel(allTableModel);
                currentFolder = all;
                break;
            case "INBOX":
                if(currentFolder == inbox)
                    bool = false;
                table1.setModel(inboxTableModel);
                currentFolder = inbox;
                break;
            case "Trash":
                if(currentFolder == trash)
                    bool = false;
                table1.setModel(trashTableModel);
                currentFolder = trash;
        }
        table1.removeColumn(table1.getColumnModel().getColumn(0));
        if(bool)
            deleteButton.setEnabled(false);
    }



    public void read(String USERNAME, String PASSWORD, String folderName) {
        try {
            deleteButton.setEnabled(false);
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);

            store = session.getStore("imaps");
            store.connect("smtp.gmail.com", USERNAME, PASSWORD);

            currentFolderName = folderName;
            if(folderName.equalsIgnoreCase("inbox")) {
                inbox = store.getFolder(folderName);
                currentFolder = inbox;
            }
            else {
                inbox = store.getFolder("[Gmail]").getFolder(folderName);
                currentFolder = inbox;
            }
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();


            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);
            initializeTable();

            for (int i = 0; i < inbox.getMessageCount(); i++) {
                //System.out.println("Mail Subject:- " + messages[i].getSubject());
                Object[] row = {messages[i], messages[i].getSubject(), messages[i].getSentDate()};
                model.addRow(row);
            }
            inboxTableModel = model;
            table1.removeColumn(table1.getColumnModel().getColumn(0));



                            DefaultTableModel mod = (DefaultTableModel) table1.getModel();
                            mod.setRowCount(0);
                            initializeTable();
                            sent = store.getFolder("[Gmail]").getFolder("Sent Mail");
                            sent.open(Folder.READ_WRITE);
                            Message[] sentMails = sent.getMessages();
                            String col[] = {"Message", "Subject", "Date"};
                            sentTableModel = new DefaultTableModel(col, 0) {

                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    //all cells false
                                    return false;
                                }
                            };
                            for (int i = 0; i < sentMails.length; i++) {
                                Object[] row = {sentMails[i], sentMails[i].getSubject(), sentMails[i].getSentDate()};
                                sentTableModel.addRow(row);

                            }

            //для отправленных сообщений
            //sent.close(true);

            //для всех сообщений
            DefaultTableModel modd = (DefaultTableModel) table1.getModel();
            modd.setRowCount(0);
            initializeTable();
            all = store.getFolder("[Gmail]").getFolder("All Mail");
            all.open(Folder.READ_WRITE);
            Message[] allMails = all.getMessages();
            String coll[] = {"Message", "Subject", "Date"};
            allTableModel = new DefaultTableModel(coll, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
            for (int i = 0; i < allMails.length; i++) {
                Object[] row = {allMails[i], allMails[i].getSubject(), allMails[i].getSentDate()};
                allTableModel.addRow(row);

            }


            //трэш
            DefaultTableModel mod1 = (DefaultTableModel) table1.getModel();
            mod1.setRowCount(0);
            initializeTable();
            trash = store.getFolder("[Gmail]").getFolder("Trash");
            trash.open(Folder.READ_WRITE);
            Message[] tr = trash.getMessages();
            trashTableModel = new DefaultTableModel(col, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
            for (int i = 0; i < tr.length; i++) {
                Object[] row = {tr[i], tr[i].getSubject(), tr[i].getSentDate()};
                trashTableModel.addRow(row);

            }

            changeFolder("All Mail");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
