package code;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class DiagramPanelTest {


    DiagramPanel diagramPanel;
    Canvas canvas = new Canvas();


    @Before
    public void setUp() throws Exception {

        diagramPanel = new DiagramPanel();
        diagramPanel.setDiagramObject(new Scheme(true));

    }




    @Test
    public void openJSONfile() throws IOException, NoSuchMethodException, ParseException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        diagramPanel.openJSONfile("C:\\Users\\L\\Documents\\Schemes\\26.json");
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 16);
    }

    @Test(expected = FileNotFoundException.class)
    public void openJSONfile2() throws IOException, NoSuchMethodException, ParseException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        diagramPanel.openJSONfile("C:\\Users\\L\\Documents\\Schemes\\456845.json");
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 16);
    }

    @Test(expected = ParseException.class)
    public void openJSONfile3() throws NoSuchMethodException, ParseException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, IOException {
        diagramPanel.openJSONfile("C:\\Users\\L\\Documents\\Schemes\\air1.png");
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 16);
    }

    @Test(expected = ParseException.class)
    public void openJSONfile4() throws NoSuchMethodException, ParseException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, IOException {
        diagramPanel.openJSONfile("C:\\Users\\L\\Documents\\Schemes\\18.json");
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 16);
    }

    @Test(expected = ClassNotFoundException.class)
    public void openJSONfile5() throws NoSuchMethodException, ParseException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, IOException {
        diagramPanel.openJSONfile("C:\\Users\\L\\Documents\\Schemes\\19.json");
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 16);
    }


    @Test
    public void canvasMouseDown() {
        diagramPanel.creatingBlock = DiagramRectangle.class;
        MouseEvent mouseEvent = new MouseEvent(canvas, 3, 15, 0, 70, 80, 1, true, 0);
        diagramPanel.canvasMouseDown(mouseEvent);
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 1);

        diagramPanel.creatingBlock = DiagramRhombus.class;
        MouseEvent mouseEvent2 = new MouseEvent(canvas, 4, 40, 0, 100, 100, 1, true, 0);
        diagramPanel.canvasMouseDown(mouseEvent2);
        assertEquals(((Scheme)DiagramPanel.getDiagramObject()).diagramObjects.size(), 2);
    }


    @Test
    public void createJSON() {

        diagramPanel.setDiagramObject(new Scheme(true));
        diagramPanel.saveFile();

        Scheme scheme = new Scheme(false);
        diagramPanel.setDiagramObject(scheme);

        /*JSONObject jsonObject = diagramPanel.createJSON();
        assertEquals(8, ((JSONArray)jsonObject.get("blocks")).size());
        assertEquals(8, ((JSONArray)jsonObject.get("links")).size());*/

        DiagramRectangle diagramRectangle = new DiagramRectangle(80, 90, "");
        diagramRectangle.setId(30);
        scheme.addToQueue(diagramRectangle);
        scheme.diagramObjects.put(diagramRectangle.getId(), diagramRectangle);

        DiagramRhombus diagramRhombus = new DiagramRhombus(80, 90, "");
        diagramRectangle.setId(31);
        scheme.addToQueue(diagramRhombus);
        scheme.diagramObjects.put(diagramRhombus.getId(), diagramRhombus);


        DiagramGeneralization diagramGeneralization = new DiagramGeneralization(diagramRectangle, diagramRhombus);
        diagramRectangle.setId(32);
        scheme.addToQueue(diagramGeneralization);
        scheme.diagramObjects.put(diagramGeneralization.getId(), diagramGeneralization);

        scheme.diagramObjects.get(2).removeFromQueue();
        scheme.diagramObjects.remove(2);
        scheme.diagramObjects.get(12).removeFromQueue();
        scheme.diagramObjects.remove(12);
        scheme.diagramObjects.get(14).removeFromQueue();
        scheme.diagramObjects.remove(14);


        /*jsonObject = diagramPanel.createJSON();
        assertEquals(9, ((JSONArray)jsonObject.get("blocks")).size());
        assertEquals(7, ((JSONArray)jsonObject.get("links")).size());*/

        diagramPanel.saveFile();
        diagramPanel.saveFile();




    }

}