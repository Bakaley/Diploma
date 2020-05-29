package code;

import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ContextPanelTest {


    DiagramPanel diagramPanel;
    Canvas canvas = new Canvas();
    DiagramRectangle rectangle1 = new DiagramRectangle(50, 100, "Caption");
    DiagramRectangle rectangle2 = new DiagramRectangle(50, 100, "Caption");
    DiagramRectangle rectangle3 = new DiagramRectangle(50, 100, "Caption");

    DiagramGeneralization diagramGeneralization1;
    DiagramGeneralization diagramGeneralization2;
    DiagramGeneralization diagramGeneralization3;
    DiagramGeneralization diagramGeneralization4;
    DiagramGeneralization diagramGeneralization5;
    DiagramGeneralization diagramGeneralization6;


    ArrayList<DiagramGeneralization> lines;
    ContextPanel contextPanel;

    @Before
    public void setUp() throws Exception {

        diagramPanel = new DiagramPanel();
        diagramPanel.setDiagramObject(new Scheme(true));

        diagramGeneralization1 = new DiagramGeneralization(rectangle1, rectangle2, "");
        diagramGeneralization2 = new DiagramGeneralization(rectangle2, rectangle3, "");
        diagramGeneralization3 = new DiagramGeneralization(rectangle3, rectangle1, "");
        diagramGeneralization4 = new DiagramGeneralization(rectangle1, rectangle2,"");
        diagramGeneralization5 = new DiagramGeneralization(rectangle1, rectangle2,"");
        diagramGeneralization6 = new DiagramGeneralization(rectangle1, rectangle3,"");

        rectangle1.setId(0);
        rectangle1.setId(1);
        rectangle2.setId(2);
        diagramGeneralization1.setId(3);
        diagramGeneralization2.setId(4);
        diagramGeneralization3.setId(5);
        diagramGeneralization4.setId(6);
        diagramGeneralization5.setId(7);
        diagramGeneralization6.setId(8);

        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(rectangle1.getId(), rectangle1);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(rectangle2.getId(), rectangle2);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(rectangle3.getId(), rectangle3);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization1.getId(), diagramGeneralization1);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization2.getId(), diagramGeneralization2);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization3.getId(), diagramGeneralization3);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization4.getId(), diagramGeneralization4);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization5.getId(), diagramGeneralization5);
        ((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.put(diagramGeneralization6.getId(), diagramGeneralization6);


        DiagramPanel.contextFrame = new ContextFrame("Свойства " + rectangle1.getCaption(), rectangle1, canvas);

        contextPanel= new ContextPanel(rectangle1, canvas, new ContextFrame("", rectangle1, canvas));

        lines = rectangle1.get_lines_in();
        lines.addAll(rectangle1.get_lines_out());

    }

    @Test
    public void deleteLines() {
        contextPanel.deleteLines();
        lines = rectangle1.get_lines_in();
        lines.addAll(rectangle1.get_lines_out());


        int expected = 0;
        int actual  = lines.size();

        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 4);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteLines1() {
        contextPanel.deleteLines(new int[]{1, 2});
        lines = (rectangle1.get_lines_in());
        lines.addAll(rectangle1.get_lines_out());


        int expected = 3;
        int actual  = lines.size();

        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 7);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteLines2() {
        contextPanel.deleteLines(new int[0]);
        lines = (rectangle1.get_lines_in());
        lines.addAll(rectangle1.get_lines_out());


        int expected = 5;
        int actual  = lines.size();

        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 9);
        assertEquals(expected, actual);
    }


    @Test
    public void deleteBlock() {

        lines = rectangle1.get_lines_in();
        lines.addAll(rectangle1.get_lines_out());

        int lines_count = lines.size();

        contextPanel.deleteBlock();

        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 3);
        assertEquals(0, rectangle1.get_lines_in().size() + rectangle1.get_lines_out().size());

    }

    @Test
    public void setCoordinateFromField() {

        contextPanel.getTextFieldX().setText("ssq");
        assertEquals(15, contextPanel.setCoordinateFromField(contextPanel.getTextFieldX(), 15), 0.1);
        contextPanel.getTextFieldY().setText("-");
        assertEquals(-40, contextPanel.setCoordinateFromField(contextPanel.getTextFieldY(), -40), 0.1);
        contextPanel.getTextFieldY().setText("80");
        assertEquals(80, contextPanel.setCoordinateFromField(contextPanel.getTextFieldY(), 15), 0.1);
        contextPanel.getTextFieldX().setText("");
        assertEquals(80, contextPanel.setCoordinateFromField(contextPanel.getTextFieldX(), 80), 0.1);
        contextPanel.getTextFieldX().setText("-15");
        assertEquals(-15, contextPanel.setCoordinateFromField(contextPanel.getTextFieldX(), 15), 0.1);

    }
}