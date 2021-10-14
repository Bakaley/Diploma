package code;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public  class LinesTableModel extends AbstractTableModel {

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
        return 2;
    }


    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Начало";
            case 1:
                return "Конец";
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
                else return line.nFrom.getName();

            case 1:
                if(line.nTo == node){
                    return "Этот блок";
                }
                else return line.nTo.getName();
        }
        return "";
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {

    }

}