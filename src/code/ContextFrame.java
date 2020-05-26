package code;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

public class ContextFrame extends JFrame {

    public static AbstractDiagramNode item;

    public ContextFrame(String title, DiagramObject diagramItem, Canvas canvas) throws HeadlessException {
        super(title);
        item = (AbstractDiagramNode)diagramItem;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.getContentPane().add(new ContextPanel((AbstractDiagramNode) item, canvas, this));
        this.pack();
        this.setLocation(1150, 60);
        this.setVisible(true);

    }



    private class ContextPanel extends JPanel {

        private int oldX;
        private int oldY;

        private ContextPanel(AbstractDiagramNode node, Canvas canvas, ContextFrame frame) {

            oldX = (int)node.getmX();
            oldY = (int)node.getmY();

            setPreferredSize(new Dimension(300, 500));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            //construct components
            JButton jcomp3 = new JButton("Цвет рамки");
            JButton jcomp4 = new JButton("Цвет текста");
            JButton jcomp5 = new JButton("Цвет заливки");


            TextField textFieldX = new TextField(String.valueOf((int)node.getmX()));
            TextField textFieldY = new TextField(String.valueOf((int)node.getmY()));
            TextField textFieldExpression = new TextField(node.getCaption());
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
                    if (textFieldX.getText().equals("") || (textFieldX.getText().equals("-"))){
                        return;
                    }
                    else try{
                        node.move(Double.valueOf(textFieldX.getText()), Double.valueOf(textFieldY.getText()));
                        canvas.repaint();}
                    catch (NumberFormatException e){
                       textFieldX.setText(String.valueOf(oldX));
                    }
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
                    if (textFieldY.getText().equals("") || (textFieldY.getText().equals("-")))
                    {
                        return;
                    }
                    else try {
                        node.move(Double.valueOf(textFieldX.getText()), Double.valueOf(textFieldY.getText()));
                        canvas.repaint();
                    } catch (NumberFormatException e) {
                        {
                            textFieldY.setText(String.valueOf(oldY));
                        }
                    }
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


            ArrayList<DiagramGeneralization> lines = node.get_lines_in();
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
            JTable table = new JTable(model);

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

            JButton jButton_delete_line = new JButton();
            jButton_delete_line.addActionListener(e->{
                int[] numbers = table.getSelectedRows();
                for (int i : numbers){
                    lines.get(i).removeFromQueue();
                    ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(lines.get(i).getId());

                }
                canvas.repaint();
                DiagramPanel.contextFrame.dispose();
                new ContextFrame("Свойства " + item.getCaption(), item, canvas);

            });
            jButton_delete_line.setIcon(new ImageIcon(AppStart.class.getResource("/resources/cross.png")));

            JButton jButton_clear_lines = new JButton();
            jButton_clear_lines.addActionListener(e->{
                Iterator <DiagramGeneralization> iterator = lines.iterator();
                AbstractDiagramLink link;
                while (iterator.hasNext()) {
                    link = iterator.next();
                    link.removeFromQueue();
                    ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(link.getId());
                }
                canvas.repaint();
                DiagramPanel.contextFrame.dispose();
                new ContextFrame("Свойства " + item.getCaption(), item, canvas);

            });
            jButton_clear_lines.setIcon(new ImageIcon(AppStart.class.getResource("/resources/clear_lines.png")));

            JButton jButton_delete_block = new JButton();
            jButton_delete_block.addActionListener( e-> {
                Iterator <DiagramGeneralization> iterator = lines.iterator();
                AbstractDiagramLink link;
                while (iterator.hasNext()) {
                    link = iterator.next();
                    link.removeFromQueue();
                    ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(link.getId());
                }
                item.removeFromQueue();
                ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.remove(item.getId());
                canvas.repaint();
                DiagramPanel.contextFrame.dispose();
            });
            jButton_delete_block.setIcon(new ImageIcon(AppStart.class.getResource("/resources/delete.png")));

            jpanelLinks.setLayout(new FlowLayout());
            jpanelLinks.add(jButton_add_line_out);
            jpanelLinks.add(jButton_add_line_in);
            jpanelLinks.add(jButton_delete_line);
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

        private  class LinesTableModel extends AbstractTableModel {

            private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

            private List<DiagramGeneralization> lines;
            private AbstractDiagramNode node;


            public LinesTableModel(ArrayList<DiagramGeneralization> lines, AbstractDiagramNode node) {
                this.lines = lines;
                this.node = node;
            }

            public void addTableModelListener(TableModelListener listener) {
                listeners.add(listener);
            }

            public int getColumnCount() {
                return 3;
            }


            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return "Начало";
                    case 1:
                        return "Конец";
                    case 2:
                        return "Надпись";
                }
                return "";
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    default:  return Object.class;
                }
            }

            public int getRowCount() {
                return lines.size();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                DiagramGeneralization line = lines.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        if(line.nFrom == node){
                            return "Этот блок";
                        }
                        else return line.nFrom.getCaption();

                    case 1:
                        if(line.nTo == node){
                            return "Этот блок";
                        }
                        else return line.nTo.getCaption();
                    case 2:
                        return line.getCaption();

                }
                return "";
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            public void removeTableModelListener(TableModelListener listener) {
                listeners.remove(listener);
            }

            public void setValueAt(Object value, int rowIndex, int columnIndex) {

            }

        }
    }
}