import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by Pe4Nik on 07.11.2016.
 */
public class Login {

    private JFrame frame;
    private JLabel addressLabel;
    private JTextField addressField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton submitButton;


    public static void main(String[] args) {
        new Login();
    }

    public Login() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        addressLabel = new JLabel("Email Address: ", SwingConstants.RIGHT);
        addressLabel.setLocation(0, 40);
        addressLabel.setSize(170, 40);
        addressField = new JTextField();
        addressField.setLocation(190, 40);
        addressField.setSize(300, 45);

        // Create password field and label
        passwordLabel = new JLabel("Password: ", SwingConstants.RIGHT);
        passwordLabel.setLocation(0, 100);
        passwordLabel.setSize(170, 40);
        passwordField = new JPasswordField();
        passwordField.setLocation(190, 100);
        passwordField.setSize(300, 45);

        // Create submit button
        submitButton = new JButton("Log In");
        submitButton.setLocation(130, 170);
        submitButton.setSize(290, 45);
        submitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();

//                String[] args = {USERNAME, PASSWORD};
                String[] args = {addressField.getText(), passwordField.getText()};
                MainWin.main(args);
            }
        });

        // Create a font for all components
        Font font = new Font("Arial", 0, 18);
        addressLabel.setFont(font);
        addressField.setFont(font);
        passwordLabel.setFont(font);
        passwordField.setFont(font);
        submitButton.setFont(font);

        // Add padding to fields
        Border padding = new EmptyBorder(0, 10, 0, 0);
        addressField.setBorder(
                new CompoundBorder(addressField.getBorder(), padding));
        passwordField.setBorder(
                new CompoundBorder(passwordField.getBorder(), padding));

        // Create the frame

        frame = new JFrame("Log In");
        frame.setSize(550, 290);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.add(addressLabel);
        frame.add(addressField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(submitButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}
