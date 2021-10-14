package code;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiagramRectangleTest {

    DiagramRectangle diagramRectangle;

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void internalTestHit() {
        diagramRectangle = new DiagramRectangle(50, 70, "");
        assertEquals(diagramRectangle.internalTestHit(60, 60), true);
        assertEquals(diagramRectangle.internalTestHit(200, -15), false);
        assertEquals(diagramRectangle.internalTestHit(195, 75), false);
    }
}