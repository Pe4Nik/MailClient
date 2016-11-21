import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Pe4Nik on 12.11.2016.
 */
public class NewMessage {
    private JTextField to;
    private JTextField subject;
    private JTextArea text;
    private JButton sendButton;
    private JButton attachButton;
    private JPanel panel;
    private JLabel label;
    private JLabel l2;
    private JLabel l1;
    private ArrayList<File> lst_Attachments;
    private String fileNames = "";
    private static JFrame frame;

    public NewMessage(String USERNAME, String PASSWORD) {
        try {
            Image img = ImageIO.read(getClass().getResource("icons/SendMail24.gif"));
            sendButton.setIcon(new ImageIcon(img));
            sendButton.setMargin(new Insets(0, 0, 0, 0));

            img = ImageIO.read(getClass().getResource("icons/attach.png"));
            attachButton.setIcon(new ImageIcon(img));
            attachButton.setMargin(new Insets(0, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lst_Attachments = new ArrayList<File>();
        attachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    lst_Attachments.add(selectedFile);

                    if(lst_Attachments.size() != 1)
                        fileNames +=  ", " + selectedFile.getName();
                    else
                        fileNames +=  selectedFile.getName();
                    label.setText(fileNames);
                }
            }
        });


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", true);
                    props.put("mail.smtp.starttls.enable", true);
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");
                    //props.put("mail.smtp.EnableSSL.enable","true");
//                    props.put("mail.smtp.host", "smtp.gmail.com");
//                    props.put("mail.smtp.socketFactory.port", "465");
//                    props.put("mail.smtp.socketFactory.class",
//                            "javax.net.ssl.SSLSocketFactory");
//                    props.put("mail.smtp.auth", "true");
//                    props.put("mail.smtp.port", "465");

                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(USERNAME, PASSWORD);}
                    });

                    javax.mail.Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(USERNAME));
                    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to.getText()));
                    message.setSubject(subject.getText());
                    if(lst_Attachments.size() == 0) {
                        message.setText(text.getText());
                        Transport.send(message);
                    }

                    else {
                        MimeBodyPart messageBodyPart = new MimeBodyPart();
                        MimeMultipart multipart = new MimeMultipart();
                        messageBodyPart.setText(text.getText());
                        multipart.addBodyPart(messageBodyPart);
                        //add attachments
                        if (lst_Attachments.size() > 0) {
                            for(File file:lst_Attachments) {
                                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                                String filename = file.getPath();
                                DataSource source = new FileDataSource(filename);
                                messageBodyPart2.setDataHandler(new DataHandler(source));
                                messageBodyPart2.setFileName(filename);
                                multipart.addBodyPart(messageBodyPart2);
                            }
                        }
                        message.setContent(multipart);
                        message.saveChanges();
                        Transport.send(message);
                    }
                    frame.dispose();
                    JOptionPane.showMessageDialog(null, "Your message has been sent.","Message sent",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ee) {
                    frame.dispose();
                    JOptionPane.showMessageDialog(null, "Your message has not been sent. Please try again.","Error",
                            JOptionPane.ERROR_MESSAGE);
                    ee.printStackTrace();
                }
            }
        });
    }


    public static void main(String[] args) {
        String USERNAME = args[0];
        String PASSWORD = args[1];
        frame = new JFrame("NewMessage");
        frame.setContentPane(new NewMessage(USERNAME, PASSWORD).panel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
