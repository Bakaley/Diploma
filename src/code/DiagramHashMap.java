package code;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DiagramHashMap<K, V> extends HashMap<K, V> {


    public DiagramHashMap() {
        DiagramPanel.termButtonStart.setEnabled(true);
        DiagramPanel.termButtonEnd.setEnabled(true);
    }

    @Override
    public V put(K key, V value) {
        V result = super.put(key, value);
        DiagramPanel.termButtonStart.setEnabled(true);
        DiagramPanel.termButtonEnd.setEnabled(true);
        Iterator<Map.Entry<K, V>> itr = this.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<K, V> entry = itr.next();
            if (!entry.getValue().getClass().equals(DiagramGeneralization.class)) {
                if (((AbstractDiagramNode) (entry.getValue())).getClass().equals(DiagramTerminatorStart.class))
                    DiagramPanel.termButtonStart.setEnabled(false);
                if (((AbstractDiagramNode) (entry.getValue())).getClass().equals(DiagramTerminatorEnd.class))
                    DiagramPanel.termButtonEnd.setEnabled(false);
            }
        }
        return result;
    }

    @Override
    public V remove(Object key) {
        V result =  super.remove(key);
        DiagramPanel.termButtonStart.setEnabled(true);
        DiagramPanel.termButtonEnd.setEnabled(true);
        Iterator<Map.Entry<K, V>> itr = this.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<K, V> entry = itr.next();
            if (!entry.getValue().getClass().equals(DiagramGeneralization.class)) {
                if (((AbstractDiagramNode) (entry.getValue())).getClass().equals(DiagramTerminatorStart.class))
                    DiagramPanel.termButtonStart.setEnabled((false));
                if (((AbstractDiagramNode) (entry.getValue())).getClass().equals(DiagramTerminatorEnd.class))
                    DiagramPanel.termButtonEnd.setEnabled((false));
            }
        }
        return result;
    }
}
