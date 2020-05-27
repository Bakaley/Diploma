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
}