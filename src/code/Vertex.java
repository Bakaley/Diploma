package code;

import java.awt.*;

public class Vertex {
    double  x;
    double  y;

    Vertex(double x, double y){
        this.x = x;
        this.y = y;
    }

/*
    final void drawVertex(int x, int y, Graphics canvas){
        canvas.setColor(Color.CYAN);
        canvas.fillOval(x-DiagramObject.scale(shiftout), y-scale(shiftout), scale(radout), scale(radout));
        canvas.setColor(Color.WHITE);
        canvas.setXORMode(Color.CYAN);
        canvas.fillOval(x-scale(shiftin), y-scale(shiftin), scale(radin), scale(radin));
        canvas.setPaintMode();
    }*/

}
