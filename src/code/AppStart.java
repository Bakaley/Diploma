package code;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Main window.
 */
public class AppStart extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    DiagramPanel panel;

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

        panel = new DiagramPanel();
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

                    mainWindow.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            if(mainWindow.panel.getDiagramObject().getFirstSubObj() != null) {
                                Object[] options = {"Сохранить", "Не сохранять", "Отмена"};

                                int option = JOptionPane.showOptionDialog(null,
                                        "Желаете сохранить текущий файл? Все несохранённые данные будут утеряны.",
                                        "Сохранить изменения?",
                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,     //do not use a custom Icon
                                        options,  //the titles of buttons
                                        options[0]); //default button title

                                switch (option) {
                                    case (2):
                                        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                        return;
                                    case (1):
                                        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                        mainWindow.dispose();
                                        break;
                                    case (0):
                                        if(mainWindow.panel.saveFile()){
                                            mainWindow.dispose();
                                        }
                                        else mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                        break;
                                }
                            }
                        }
                    });
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
