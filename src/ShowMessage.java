import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Pe4Nik on 12.11.2016.
 */
public class ShowMessage {
    private JTextField from;
    private JTextField subject;
    private JPanel panel;
    private JLabel lsubject;
    private JLabel lfrom;
    private JButton button1;
    private JEditorPane editorPane1;
    private JLabel lfiles;
    private Message message;
    private ArrayList<File> Files;
    JFrame frame;

    public ShowMessage(String sFrom, String sSubject, String body, Message message) {
        try {
            Image img = ImageIO.read(getClass().getResource("icons/Save24.gif"));
            button1.setIcon(new ImageIcon(img));
            button1.setMargin(new Insets(0, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        from.setText(sFrom);
        subject.setText(sSubject);
        editorPane1.setContentType("text/html");
        editorPane1.setText(body);
        from.setEditable(false);
        subject.setEditable(false);
        editorPane1.setEditable(false);
        this.message = message;
        button1.setVisible(false);
        lfiles.setVisible(false);

        frame = new JFrame("ShowMessage");
        frame.setContentPane(this.panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //saveAttachment(message);
        showSaveFileButton();
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAttachment(message);
            }
        });
    }

    public void showSaveFileButton() {
        String contentType = "";
        String messageContent = "";
        String attachFiles = "";
        try {
            contentType = message.getContentType();
            messageContent = "";

            // store attachment file name, separated by comma
            attachFiles = "";

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) message.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        button1.setVisible(true);
                        lfiles.setVisible(true);
                        String fileName = part.getFileName();
                        attachFiles += fileName + ", ";
                    }
                }
            }
            if(!attachFiles.equalsIgnoreCase("")) {
                attachFiles = attachFiles.substring(0,attachFiles.length()-2);
                lfiles.setText(attachFiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAttachment(Message message) {
        String contentType = "";
        String messageContent = "";
        String attachFiles = "";
        Files = new ArrayList<File>();
        try {
            contentType = message.getContentType();
            messageContent = "";

            // store attachment file name, separated by comma
            attachFiles = "";

            if (contentType.contains("multipart")) {
                // content may contain attachments
                Multipart multiPart = (Multipart) message.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        // this part is attachment
                        String fileName = part.getFileName();
                        attachFiles += fileName + ", ";
                        //Files.add(part.)

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Specify a file to save");
                        fileChooser.setSelectedFile(new File("C:/Users/Pe4Nik/Downloads/" + part.getFileName()));
                        int userSelection = fileChooser.showSaveDialog(frame);
                        File fileToSave = new File("C:/Users/Pe4Nik/Downloads/" + part.getFileName());
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            fileToSave = fileChooser.getSelectedFile();
                            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                        }

                        InputStream is = part.getInputStream();
                        File f = new File(fileToSave.getAbsolutePath());
                        FileOutputStream fos = new FileOutputStream(f);
                        byte[] buf = new byte[4096];
                        int bytesRead;
                        while((bytesRead = is.read(buf))!=-1) {
                            fos.write(buf, 0, bytesRead);
                        }
                        fos.close();
                        Files.add(f);
                    } else {
                        // this part may be the message content
                        messageContent = part.getContent().toString();
                    }
                }

                if (attachFiles.length() > 1) {
                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = message.getContent();
                if (content != null) {
                    messageContent = content.toString();
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(!Files.isEmpty()) {
            button1.setVisible(true);
        }

    }



}
