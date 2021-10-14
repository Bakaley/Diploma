package code;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Main window.
 */
public class AppStart extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    /**
     * Create the frame.
     */
    public AppStart() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBounds(0, 0, 600, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        DiagramPanel panel = new DiagramPanel();
        panel.setDiagramObject(new Scheme(true));
        contentPane.add(panel);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    mainWindow = new AppStart();
                    mainWindow.setVisible(true);
                    changeWindowTitle("New scheme");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static AppStart mainWindow = null;

    public static void changeWindowTitle(String fineName){
        mainWindow.setTitle(fineName + " - Scheme checker 1.0");
    }
}
