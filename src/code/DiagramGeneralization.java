package code;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Actor-Actor generalization arrow.
 */
class DiagramGeneralization extends AbstractDiagramLink implements LabelParent {

    protected AbstractDiagramNode nFrom;
    protected AbstractDiagramNode nTo;

    private double ldX;
    private double ldY;

    private double last_lenth = 0;
    private Point2D.Double last_from;
    private Point2D.Double last_to;

    protected AbstractDiagramNode getnFrom() {
        return nFrom;
    }
    protected AbstractDiagramNode getnTo() {
        return nTo;
    }

    public DiagramGeneralization(AbstractDiagramNode nFrom, AbstractDiagramNode nTo, String caption) {
        this.nFrom = nFrom;
        this.nTo = nTo;
        nFrom.get_lines_in().add(this);
        nTo.get_lines_in().add(this);
        this.caption = caption;

        if (caption.length() != 0)
            addToQueue(new DiagramLabel(this));
    }

    @Override
    public String getText() {
        return caption;
    }

    @Override
    public double getLabelX() {
        return (nTo.getmX() + nFrom.getmX()) / 2 + ldX;
    }

    @Override
    public double getLabelY() {
        return (nTo.getmY() + nFrom.getmY()) / 2 + ldY - 2;
    }

    @Override
    public void setLabelX(double newX) {
        ldX = newX - (nTo.getmX() + nFrom.getmX()) / 2;
    }

    @Override
    public void setLabelY(double newY) {
        ldY = newY - (nTo.getmY() + nFrom.getmY()) / 2 + 2;
    }

    @Override
    protected void internalDraw() {

        getCanvas().setColor(Color.BLACK);
        getCanvas().setPaintMode();

           drawFrom(nFrom.getVertices().get(0));
/*
        if(nFrom.lines_out.size()==2 ) {
            drawFrom(nFrom.getLeftVertex());

        }

        if(nFrom.lines_out.size()==3 && already_draw==0) {
            drawFrom(nFrom.getVertices().get(0));
            nFrom.already_draw++;
              }

        if(nFrom.lines_out.size()==3 && already_draw==1) {
            drawFrom(nFrom.getLeftVertex());
            nFrom.already_draw++;
             }

        if(nFrom.lines_out.size()==3 && already_draw==2) {
            drawFrom(nFrom.getRightVertex());
            nFrom.already_draw++;
         }
*/
        // double len = Math.sqrt((nFrom.getVertices().get(0).x - nTo.getVertices().get(0).x) * (nFrom.getVertices().get(0).x - nTo.getVertices().get(0).x) + (nFrom.getVertices().get(0).y - nTo.getVertices().get(0).y) * (nFrom.getVertices().get(0).y - nTo.getVertices().get(0).y));
    }

    private void drawFrom(Vertex vertex) {

        double x1 = vertex.x;
        double y1 = vertex.y;
        double x2 = nTo.getVertices().get(0).x;
        double y2 = nTo.getVertices().get(0).y;
        String text = vertex.name;

        int[] abscissae = new int[3];
        int[] ordinates = new int[3];

        if ((nFrom.getBotMiddleVertex().x == nTo.getBotMiddleVertex().x) && (nFrom.getBotMiddleVertex().y < nTo.getBotMiddleVertex().y)) {
            x1 = nFrom.getTopMiddleVertex().x;
            y1 = nFrom.getTopMiddleVertex().y;
            x2 = nTo.getBotMiddleVertex().x;
            y2 = nTo.getBotMiddleVertex().y;
            getCanvas().drawLine((int)(x1), (int)(y1), (int)(x2), (int)(y2));
            //  getArrowPoints(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), abscissae, ordinates);
            //  getCanvas().fillPolygon(abscissae, ordinates, 3);
            //  getCanvas().drawPolygon(abscissae, ordinates, 1);
            last_from = new Point2D.Double(x1, y1);
            last_to = new Point2D.Double(x2, y2);
            last_lenth = lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getBotMiddleVertex().x, nTo.getBotMiddleVertex().y);
        } else if ((nFrom.getBotMiddleVertex().y + scale(40) < nTo.getBotMiddleVertex().y)) {
//||(Math.abs(nFrom.getBotMiddleVertex().x - nTo.getBotMiddleVertex().x) < scale(200))
            getCanvas().drawLine(
                    (int)nFrom.getTopMiddleVertex().x,
                     (int)nFrom.getTopMiddleVertex().y,
                     (int)nFrom.getTopMiddleVertex().x,
                     (int)nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2);
            // getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y), new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 4), abscissae, ordinates);
            // getCanvas().fillPolygon(abscissae, ordinates, 3);
            // getCanvas().drawPolygon(abscissae, ordinates, 1);

            last_from = new Point2D.Double(nTo.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2);
            last_to = new Point2D.Double(nTo.getBotMiddleVertex().x, nTo.getBotMiddleVertex().y);
            last_lenth = lenth(nTo.getTopMiddleVertex().x,
                    nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                    nTo.getBotMiddleVertex().x,
                    nTo.getBotMiddleVertex().y);

            getCanvas().drawLine(
                     (int)nFrom.getTopMiddleVertex().x,
                     (int)nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                     (int)nTo.getTopMiddleVertex().x,
                     (int)nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2);

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x,
                                nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                        new Point2D.Double(nTo.getTopMiddleVertex().x,
                                nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);


            getCanvas().drawLine(
                    (int) nTo.getTopMiddleVertex().x,
                    (int) nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                    (int) nTo.getBotMiddleVertex().x,
                    (int) nTo.getBotMiddleVertex().y);



           /*             if (nFrom.getTopMiddleVertex().x < nTo.getBotMiddleVertex().x) {
                            if(!(this.last_lenth == max(nTo.get_lines_in(), this))) {
                                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                        new Point2D.Double(nFrom.getTopMiddleVertex().x + lenth(nFrom.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                                                nTo.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), abscissae, ordinates);
                            }
                            else {
                                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                        new Point2D.Double(nFrom.getTopMiddleVertex().x + lenth(nFrom.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                                                nTo.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2) /2, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), abscissae, ordinates);

                            }
                        }
                        else {
                            if (!(this.last_lenth == max(nTo.get_lines_in(), this))) {
                                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                        new Point2D.Double(nFrom.getTopMiddleVertex().x - lenth(nFrom.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                                                nTo.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), abscissae, ordinates);
                            }
                            else{
                                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                        new Point2D.Double(nFrom.getTopMiddleVertex().x - lenth(nFrom.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                                                nTo.getTopMiddleVertex().x,
                                                nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2) /2 , nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2), abscissae, ordinates);
                            }
                        }
                        getCanvas().fillPolygon(abscissae, ordinates, 3);
                        getCanvas().drawPolygon(abscissae, ordinates, 1);
                    }

                    if(this.last_lenth == max(nTo.get_lines_in(), this)){
                       getCanvas().drawLine(
                               nTo.getTopMiddleVertex().x,
                               nFrom.getTopMiddleVertex().y + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2,
                               nTo.getBotMiddleVertex().x,
                               nTo.getBotMiddleVertex().y);
                        getArrowPoints(new Point2D.Double( nTo.getTopMiddleVertex().x,
                                nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                new Point2D.Double(nTo.getBotMiddleVertex().x, nTo.getBotMiddleVertex().y), abscissae, ordinates);

                       getCanvas().fillPolygon(abscissae, ordinates, 3);
                       getCanvas().drawPolygon(abscissae, ordinates, 1);

                } else {
                        getArrowPoints(new Point2D.Double(nTo.getTopMiddleVertex().x,
                                        nFrom.getTopMiddleVertex().y + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nFrom.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y) / 2),
                                new Point2D.Double(nTo.getBotMiddleVertex().x, nTo.getBotMiddleVertex().y), abscissae, ordinates);
                        getCanvas().fillPolygon(abscissae, ordinates, 3);
                        getCanvas().drawPolygon(abscissae, ordinates, 1);
                    }
                    */
        } else if ((nFrom.getBotMiddleVertex().y + scale(40) > nTo.getBotMiddleVertex().y) && (Math.abs(nFrom.getBotMiddleVertex().x - nTo.getBotMiddleVertex().x) > scale(50))) {

            getCanvas().drawLine(
                   (int) nFrom.getTopMiddleVertex().x,
                   (int) nFrom.getTopMiddleVertex().y,
                   (int) nFrom.getTopMiddleVertex().x,
                   (int) nFrom.getTopMiddleVertex().y + scale(20));

            if (nFrom.getTopMiddleVertex().x > nTo.getBotMiddleVertex().x) {

                getCanvas().drawLine(
                     (int)   nFrom.getTopMiddleVertex().x,
                     (int)   nFrom.getTopMiddleVertex().y + scale(20),
                     (int)   nFrom.getTopMiddleVertex().x - (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nFrom.getTopMiddleVertex().y + scale(20));

                getCanvas().drawLine(
                     (int)   nFrom.getTopMiddleVertex().x - (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nFrom.getTopMiddleVertex().y + scale(20),
                     (int)   nFrom.getTopMiddleVertex().x - (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nTo.getBotMiddleVertex().y - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x - lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                nFrom.getTopMiddleVertex().y + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().x - lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                (nTo.getBotMiddleVertex().y - scale(20) + nFrom.getTopMiddleVertex().y + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);


                getCanvas().drawLine(
                     (int)   nFrom.getTopMiddleVertex().x - (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nTo.getBotMiddleVertex().y - scale(20),
                     (int)   nTo.getTopMiddleVertex().x,
                     (int)   nTo.getBotMiddleVertex().y - scale(20));

                      /* if(!(nTo.get_lines_in().size()==1)) {
                           getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x - lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                   nTo.getBotMiddleVertex().y - scale(20)), new Point2D.Double(nFrom.getTopMiddleVertex().x - (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                   nTo.getBotMiddleVertex().y - scale(20)), abscissae, ordinates);
                           getCanvas().fillPolygon(abscissae, ordinates, 3);
                           getCanvas().drawPolygon(abscissae, ordinates, 1);
                       }
                       if(!(nTo.get_lines_in().size()==1)){
                           getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                           nTo.getBotMiddleVertex().y-scale(20)),
                                   new Point2D.Double(nTo.getTopMiddleVertex().x,
                                           nTo.getBotMiddleVertex().y-scale(20)), abscissae, ordinates);
                           getCanvas().fillPolygon(abscissae, ordinates, 3);
                           getCanvas().drawPolygon(abscissae, ordinates, 1);
                       }*/

                    getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                    nTo.getBotMiddleVertex().y - scale(20)),
                            new Point2D.Double(nTo.getTopMiddleVertex().x,
                                    nTo.getBotMiddleVertex().y - scale(20)), abscissae, ordinates);
                    getCanvas().fillPolygon(abscissae, ordinates, 3);
                    getCanvas().drawPolygon(abscissae, ordinates, 1);

            } else {
                getCanvas().drawLine(
                    (int)    nFrom.getTopMiddleVertex().x,
                    (int)    nFrom.getTopMiddleVertex().y + scale(20),
                    (int)    nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                    (int)    nFrom.getTopMiddleVertex().y + scale(20));
                getCanvas().drawLine(
                     (int)   nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nFrom.getTopMiddleVertex().y + scale(20),
                     (int)   nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                     (int)   nTo.getBotMiddleVertex().y - scale(20));
                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                nFrom.getTopMiddleVertex().y + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                (nTo.getBotMiddleVertex().y - scale(20) + nFrom.getTopMiddleVertex().y + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                    (int)    nFrom.getTopMiddleVertex().x + (int) lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                    (int)    nTo.getBotMiddleVertex().y - scale(20),
                    (int)    nTo.getTopMiddleVertex().x,
                    (int)    nTo.getBotMiddleVertex().y - scale(20));


                    getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                    nTo.getBotMiddleVertex().y - scale(20)),
                            new Point2D.Double(nTo.getTopMiddleVertex().x,
                                    nTo.getBotMiddleVertex().y - scale(20)), abscissae, ordinates);
                    getCanvas().fillPolygon(abscissae, ordinates, 3);
                    getCanvas().drawPolygon(abscissae, ordinates, 1);

            }

            last_from = new Point2D.Double(nTo.getTopMiddleVertex().x,
                    nTo.getBotMiddleVertex().y - scale(20));
            last_to = new Point2D.Double(nTo.getBotMiddleVertex().x,
                    nTo.getBotMiddleVertex().y);
            last_lenth = lenth(nTo.getTopMiddleVertex().x,
                    nTo.getBotMiddleVertex().y - scale(20),
                    nTo.getBotMiddleVertex().x,
                    nTo.getBotMiddleVertex().y);

            getCanvas().drawLine(
                 (int)   nTo.getTopMiddleVertex().x,
                 (int)   nTo.getBotMiddleVertex().y - scale(20),
                 (int)   nTo.getBotMiddleVertex().x,
                 (int)   nTo.getBotMiddleVertex().y);
            getArrowPoints(new Point2D.Double(nTo.getTopMiddleVertex().x, nTo.getBotMiddleVertex().y - 20), new Point2D.Double(nTo.getBotMiddleVertex().x, nTo.getBotMiddleVertex().y), abscissae, ordinates);
            getCanvas().fillPolygon(abscissae, ordinates, 3);
            getCanvas().drawPolygon(abscissae, ordinates, 1);


        } else {
            getCanvas().drawLine(
                 (int)   nFrom.getTopMiddleVertex().x,
                 (int)   nFrom.getTopMiddleVertex().y,
                 (int)   nFrom.getTopMiddleVertex().x,
                 (int)   nFrom.getTopMiddleVertex().y + scale(20));

            if (nFrom.getBotMiddleVertex().x >= nTo.getBotMiddleVertex().x) {
                getCanvas().drawLine(
                 (int)       nFrom.getTopMiddleVertex().x,
                 (int)       nFrom.getTopMiddleVertex().y + scale(20),
                 (int)       nFrom.getTopMiddleVertex().x + scale(40),
                 (int)       nFrom.getTopMiddleVertex().y + scale(20));

                getCanvas().drawLine(
                   (int)     nFrom.getTopMiddleVertex().x + scale(40),
                   (int)     nFrom.getTopMiddleVertex().y + scale(20),
                   (int)     nFrom.getTopMiddleVertex().x + scale(40),
                   (int)     nTo.getBotMiddleVertex().y - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + scale(40),
                                nFrom.getTopMiddleVertex().y + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().x + scale(40),
                                (nTo.getBotMiddleVertex().y - scale(20) + nFrom.getTopMiddleVertex().y + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                  (int)      nFrom.getTopMiddleVertex().x + scale(40),
                  (int)      nTo.getBotMiddleVertex().y - scale(20),
                  (int)      nTo.getTopMiddleVertex().x,
                  (int)      nTo.getBotMiddleVertex().y - scale(20));

                    getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                    nTo.getBotMiddleVertex().y - scale(20)),
                            new Point2D.Double(nTo.getTopMiddleVertex().x,
                                    nTo.getBotMiddleVertex().y - scale(20)), abscissae, ordinates);
                    getCanvas().fillPolygon(abscissae, ordinates, 3);
                    getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                 (int)       nTo.getTopMiddleVertex().x,
                 (int)       nTo.getBotMiddleVertex().y - scale(20),
                 (int)       nTo.getBotMiddleVertex().x,
                 (int)       nTo.getBotMiddleVertex().y);
            } else {
                getCanvas().drawLine(
                 (int)       nFrom.getTopMiddleVertex().x,
                 (int)       nFrom.getTopMiddleVertex().y + scale(20),
                 (int)       nFrom.getTopMiddleVertex().x - scale(40),
                 (int)       nFrom.getTopMiddleVertex().y + scale(20));

                getCanvas().drawLine(
                   (int)     nFrom.getTopMiddleVertex().x - scale(40),
                   (int)     nFrom.getTopMiddleVertex().y + scale(20),
                   (int)     nFrom.getTopMiddleVertex().x - scale(40),
                   (int)     nTo.getBotMiddleVertex().y - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x - scale(40),
                                nFrom.getTopMiddleVertex().y + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().x - scale(40),
                                (nTo.getBotMiddleVertex().y - scale(20) + nFrom.getTopMiddleVertex().y + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                   (int)     nFrom.getTopMiddleVertex().x - scale(40),
                   (int)     nTo.getBotMiddleVertex().y - scale(20),
                   (int)     nTo.getTopMiddleVertex().x,
                   (int)     nTo.getBotMiddleVertex().y - scale(20));

                    getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().x + lenth(nFrom.getTopMiddleVertex().x, nFrom.getTopMiddleVertex().y, nTo.getTopMiddleVertex().x, nFrom.getBotMiddleVertex().y) / 2,
                                    nTo.getBotMiddleVertex().y - scale(20)),
                            new Point2D.Double(nTo.getTopMiddleVertex().x,
                                    nTo.getBotMiddleVertex().y - scale(20)), abscissae, ordinates);
                    getCanvas().fillPolygon(abscissae, ordinates, 3);
                    getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                    (int)    nTo.getTopMiddleVertex().x,
                    (int)    nTo.getBotMiddleVertex().y - scale(20),
                    (int)    nTo.getBotMiddleVertex().x,
                    (int)    nTo.getBotMiddleVertex().y);
            }
        }
    }


    private double lenth (double x1, double y1, double x2, double y2){
        return Math.sqrt((x1 - x2) * ( x1 - x2) + ( y1 - y2) * (y1 - y2));
    }
}