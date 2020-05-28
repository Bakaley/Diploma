package code;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ContextPanel extends JPanel {

    private AbstractDiagramNode node;
    private ArrayList<DiagramGeneralization> lines;
    private TextField textFieldX;
    private TextField textFieldY;
    private TextField textFieldExpression;
    private Canvas canvas;
    private ContextFrame frame;

    private int oldX;
    private int oldY;

    public TextField getTextFieldX() {
        return textFieldX;
    }

    public TextField getTextFieldY() {
        return textFieldY;
    }

    JTable table;

    public ContextPanel(AbstractDiagramNode nodeIn, Canvas canvasIn, ContextFrame frameIn) {

        this.node = nodeIn;
        this.canvas = canvasIn;
        this.frame = frameIn;

        oldX = (int)node.getmX();
        oldY = (int)node.getmY();


        setPreferredSize(new Dimension(300, 500));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton jcomp3 = new JButton("Цвет рамки");
        JButton jcomp4 = new JButton("Цвет текста");
        JButton jcomp5 = new JButton("Цвет заливки");


        textFieldX = new TextField(String.valueOf((int)node.getmX()));
        textFieldY = new TextField(String.valueOf((int)node.getmY()));
        textFieldExpression = new TextField(node.getCaption());
        textFieldExpression.setPreferredSize(new Dimension(100, 20));

        Label labelX = new Label("X: ");
        Label labelY = new Label("Y: ");
        Label labelExpression = new Label("Выражение: ");


        JPanel jPanelCoordinates = new JPanel();
        jPanelCoordinates.setLayout(new FlowLayout());
        jPanelCoordinates.add(labelX);
        jPanelCoordinates.add(textFieldX);
        jPanelCoordinates.add(labelY);
        jPanelCoordinates.add(textFieldY);

        JPanel jPanelExpression = new JPanel();
        jPanelExpression.setLayout(new FlowLayout());
        jPanelExpression.add(labelExpression);
        jPanelExpression.add(textFieldExpression);


        jcomp3.addActionListener(e -> {
            Color initialcolor = Color.RED;

            Color color = new JColorChooser().showDialog(frame, "Выберите цвет", initialcolor);
            node.setColorBorder(color);
            canvas.repaint();

        });

        jcomp4.addActionListener(e -> {
            Color initialcolor = Color.RED;
            Color color = new JColorChooser().showDialog(frame, "Выберите цвет", initialcolor);
            node.setColorFont(color);
            canvas.repaint();
        });

        jcomp5.addActionListener(e -> {
            Color initialcolor = Color.RED;
            Color color = new JColorChooser().showDialog(frame, "Выберите цвет", initialcolor);
            node.setColorFill(color);
            canvas.repaint();
        });


        textFieldX.addKeyListener(new KeyListener()  {

            @Override
            public void keyPressed(KeyEvent event) {
            }

            @Override
            public void keyTyped(KeyEvent event) {
            }

            @Override
            public void keyReleased(KeyEvent event) {
                setCoordinateFromField(textFieldX, oldX);
            }


        });

        textFieldY.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent event) {
            }

            @Override
            public void keyTyped(KeyEvent event) {
            }

            @Override
            public void keyReleased(KeyEvent event) {
                setCoordinateFromField(textFieldY, oldY);
            }
        });

        textFieldExpression.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent event) {
            }

            @Override
            public void keyReleased(KeyEvent event) {
                node.setCaption(textFieldExpression.getText());
                canvas.repaint();
            }

            @Override
            public void keyPressed(KeyEvent event) {
            }
        });

        lines = node.get_lines_in();
        lines.addAll(node.get_lines_out());

        if (node.getClass().getName().contains("Terminator")) textFieldExpression.setEditable(false);

        JPanel jpanelbutton3 = new JPanel();
        jpanelbutton3.setLayout(new FlowLayout());
        jpanelbutton3.add(jcomp3);

        JPanel jpanelbutton4 = new JPanel();
        jpanelbutton4.setLayout(new FlowLayout());
        jpanelbutton4.add(jcomp4);

        JPanel jpanelbutton5 = new JPanel();
        jpanelbutton5.setLayout(new FlowLayout());
        jpanelbutton5.add(jcomp5);

        JPanel jpanelLinks = new JPanel();

        TableModel model = new LinesTableModel(lines, node);
        table = new JTable(model);

        JButton jButton_add_line_in = new JButton();
        jButton_add_line_in.addActionListener(e ->{
            DiagramPanel.creatingLink = true;
            DiagramPanel.creatingIn = false;
        });
        jButton_add_line_in.setIcon(new ImageIcon(AppStart.class.getResource("/resources/line_in.png")));

        JButton jButton_add_line_out = new JButton();
        jButton_add_line_out.addActionListener( e-> {
            DiagramPanel.creatingLink = true;
            DiagramPanel.creatingIn = true;
        });

        jButton_add_line_out.setIcon(new ImageIcon(AppStart.class.getResource("/resources/line_out.png")));

        JButton jButton_delete_lines = new JButton();
        jButton_delete_lines.addActionListener(e->{
            int[] linesToDelete = table.getSelectedRows();
            deleteLines(linesToDelete);
        });
        jButton_delete_lines.setIcon(new ImageIcon(AppStart.class.getResource("/resources/cross.png")));

        JButton jButton_clear_lines = new JButton();
        jButton_clear_lines.addActionListener(e->{
            deleteLines();
        });
        jButton_clear_lines.setIcon(new ImageIcon(AppStart.class.getResource("/resources/clear_lines.png")));

        JButton jButton_delete_block = new JButton();
        jButton_delete_block.addActionListener( e-> {
            deleteBlock();
        });
        jButton_delete_block.setIcon(new ImageIcon(AppStart.class.getResource("/resources/delete.png")));

        jpanelLinks.setLayout(new FlowLayout());
        jpanelLinks.add(jButton_add_line_out);
        jpanelLinks.add(jButton_add_line_in);
        jpanelLinks.add(jButton_delete_lines);
        jpanelLinks.add(jButton_clear_lines);
        jpanelLinks.add(jButton_delete_block);

        add(Box.createRigidArea(new Dimension(0, 25)));
        add(jPanelCoordinates);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(jPanelExpression);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(jpanelbutton3);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(jpanelbutton4);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(jpanelbutton5);
        add(Box.createRigidArea(new Dimension(0, 25)));

        add(new JScrollPane(table));
        add(Box.createRigidArea(new Dimension(0, 0)));
        add(jpanelLinks);
    }

    public void deleteLines() {
        Iterator<DiagramGeneralization> iterator = lines.iterator();
        AbstractDiagramLink link;
        while (iterator.hasNext()) {
            link = iterator.next();
            link.removeFromQueue();
            ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(link.getId());
        }
        canvas.repaint();
        DiagramPanel.contextFrame.dispose();
        new ContextFrame("Свойства " + node.getCaption(), node, canvas);

    }


    public void deleteLines(int[] numbers) {
        for (int i : numbers){
            lines.get(i).removeFromQueue();
            ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(lines.get(i).getId());
        }

        canvas.repaint();

        DiagramPanel.contextFrame.dispose();
        new ContextFrame("Свойства " + node.getCaption(), node, canvas);
    }

    public HashMap<Integer, DiagramObject> deleteBlock(){
        deleteLines();
        node.removeFromQueue();
        ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(node.getId());
        canvas.repaint();
        DiagramPanel.contextFrame.dispose();

        return ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects;

    }



    public double setCoordinateFromField (TextField settedTextField, double oldValue){
        if (settedTextField.getText().equals("") || (settedTextField.getText().equals("-")))
        {
            return oldValue;
        }
        else try {
            node.move(Double.valueOf(textFieldX.getText()), Double.valueOf(textFieldY.getText()));
            oldValue = Double.valueOf(settedTextField.getText());
        } catch (NumberFormatException e) {
            {
                settedTextField.setText(String.valueOf(oldValue));
            }
        }
        canvas.repaint();
        return oldValue;
    }
}