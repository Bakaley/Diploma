package code;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * UML Use Case Diagram prototype.
 */
public class Scheme extends DiagramObject {

    public HashMap <Integer, DiagramObject> diagramObjects = new HashMap<>();


        public Scheme(boolean empty) {
            if(empty){

            }

            else {
                DiagramTerminator t1 = new DiagramTerminatorStart(250, 50, "");
                DiagramParallelogram p1 = new DiagramParallelogram(250, 150, "a, b");
                DiagramRectangle o1 = new DiagramRectangle(250, 250, "a=a+b");
                DiagramRhombus r1 = new DiagramRhombus(250, 350, "a<4");
                DiagramRectangle o2 = new DiagramRectangle(400, 450, "a*4");
                DiagramRectangle o3 = new DiagramRectangle(400, 250, "b-9");
                DiagramParallelogram p2 = new DiagramParallelogram(250, 450, "a, b");
                DiagramTerminator t2 = new DiagramTerminatorEnd(250, 550, "End");



                DiagramGeneralization diagramGeneralization1 = new DiagramGeneralization(t1, p1, "");
                DiagramGeneralization diagramGeneralization2 = new DiagramGeneralization(p1, o1, "");
                DiagramGeneralization diagramGeneralization3 = new DiagramGeneralization(o1, r1, "");
                DiagramGeneralization diagramGeneralization4 = new DiagramGeneralization(r1, o2, "true");
                DiagramGeneralization diagramGeneralization5 = new DiagramGeneralization(o2, o3, "");
                DiagramGeneralization diagramGeneralization6 = new DiagramGeneralization(o3, r1, "");
                DiagramGeneralization diagramGeneralization7 = new DiagramGeneralization(r1, p2, "false");
                DiagramGeneralization diagramGeneralization8 = new DiagramGeneralization(p2, t2, "");


                diagramGeneralization1.setId(9);
                diagramGeneralization2.setId(10);
                diagramGeneralization3.setId(11);
                diagramGeneralization4.setId(12);
                diagramGeneralization5.setId(13);
                diagramGeneralization6.setId(14);
                diagramGeneralization7.setId(15);
                diagramGeneralization8.setId(16);

                addToQueue(t1);
                addToQueue(p1);
                addToQueue(o1);
                addToQueue(r1);
                addToQueue(o2);
                addToQueue(o3);
                addToQueue(p2);
                addToQueue(t2);

                addToQueue(diagramGeneralization1);
                addToQueue(diagramGeneralization2);
                addToQueue(diagramGeneralization3);
                addToQueue(diagramGeneralization4);
                addToQueue(diagramGeneralization5);
                addToQueue(diagramGeneralization6);
                addToQueue(diagramGeneralization7);
                addToQueue(diagramGeneralization8);

                t1.setId(1);
                p1.setId(2);
                o1.setId(3);
                r1.setId(4);
                o2.setId(5);
                o3.setId(6);
                p2.setId(7);
                t2.setId(8);

         //       diagramObjects.put(diagramGeneralization1.id, diagramGeneralization1);
         //       diagramObjects.put(diagramGeneralization2.id, diagramGeneralization2);
         //       diagramObjects.put(diagramGeneralization3.id, diagramGeneralization3);
         //       diagramObjects.put(diagramGeneralization4.id, diagramGeneralization4);
         //       diagramObjects.put(diagramGeneralization5.id, diagramGeneralization5);
         //       diagramObjects.put(diagramGeneralization6.id, diagramGeneralization6);
         //       diagramObjects.put(diagramGeneralization7.id, diagramGeneralization7);
         //       diagramObjects.put(diagramGeneralization8.id, diagramGeneralization8);



                diagramObjects.put(t1.getId(), t1);
                diagramObjects.put(p1.getId(), p1);
                diagramObjects.put(o1.getId(), o1);
                diagramObjects.put(r1.getId(), r1);
                diagramObjects.put(o2.getId(), o2);
                diagramObjects.put(o3.getId(), o3);
                diagramObjects.put(p2.getId(), p2);
                diagramObjects.put(t2.getId(), t2);

                diagramObjects.put(diagramGeneralization1.getId(), diagramGeneralization1);
                diagramObjects.put(diagramGeneralization2.getId(), diagramGeneralization2);
                diagramObjects.put(diagramGeneralization3.getId(), diagramGeneralization3);
                diagramObjects.put(diagramGeneralization4.getId(), diagramGeneralization4);
                diagramObjects.put(diagramGeneralization5.getId(), diagramGeneralization5);
                diagramObjects.put(diagramGeneralization6.getId(), diagramGeneralization6);
                diagramObjects.put(diagramGeneralization7.getId(), diagramGeneralization7);
                diagramObjects.put(diagramGeneralization8.getId(), diagramGeneralization8);

          /*      objects.add(new DiagramGeneralization(t1, p1));
                objects.add(new DiagramGeneralization(p1, o1));
                objects.add(new DiagramGeneralization(o1, r1));
                objects.add(new DiagramGeneralization(r1, o2));
                objects.add(new DiagramGeneralization(o2, o3));
                objects.add(new DiagramGeneralization(o3, r1));
                objects.add(new DiagramGeneralization(r1, p2));
                objects.add(new DiagramGeneralization(p2, t2));

                objects.add(t1);
                objects.add(p1);
                objects.add(o1);
                objects.add(r1);
                objects.add(o2);
                objects.add(o3);
                objects.add(p2);
                objects.add(t2);*/

                //addObjectsToQueue();

            }
        }


    protected void internalDraw(Graphics canvas) {
    }

    @Override
    protected double getMaxX() {
        return 600;
    }

    @Override
    protected double getMinX() {return 0;  }

    @Override
    protected double getMaxY() {
        return 600;
    }

    @Override
    protected double getMinY() {
        return  0;
    }

}
