/*
 *   Project: iCircles
 * 
 * File name: CirclesPanel2.java
 *    Author: Matej Urbas [matej.urbas@gmail.com]
 * 
 *  Copyright © 2012 Matej Urbas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package icircles.gui;

import icircles.concreteDiagram.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.ArrayList;
import javax.swing.event.MouseInputListener;

/**
 * This panel takes a {@link ConcreteDiagram concrete diagram} and draws it.
 *
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public class CirclesPanel2 extends javax.swing.JPanel {

    // <editor-fold defaultstate="collapsed" desc="Private Fields">
    /**
     * This flag is used in {@link CirclesPanel2#setHighlighting(int)} to
     * indicate that the user may highlight the spiders in the currently
     * displayed diagram with the mouse.
     */
    public static final int Spiders = 0x1;
    /**
     * This flag is used in {@link CirclesPanel2#setHighlighting(int)} to
     * indicate that the user may highlight the zones in the currently
     * displayed diagram with the mouse.
     */
    public static final int Zones = 0x2;
    /**
     * This flag is used in {@link CirclesPanel2#setHighlighting(int)} to
     * indicate that the user may highlight the circle contours in the
     * currently displayed diagram with the mouse.
     */
    public static final int Contours = 0x4;
    /**
     * This flag is used in {@link CirclesPanel2#setHighlighting(int)} to
     * indicate that the user may highlight all the elements in the currently
     * displayed diagram with the mouse.
     */
    public static final int All = Spiders | Zones | Contours;
    /**
     * This set of flag determines which elements of the diagram may be
     * highlighted with the mouse. <p>This flag can be a (binary) combination of the
     * following flags: <ul> <li>{@link CirclesPanel2#Spiders}: which indicates
     * that spiders will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Zones}:
     * which indicates that zones will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Contours}:
     * which indicates that circle contours will be highlighted when the user hovers over them.</li> </ul></p> <p>
     * The {@link CirclesPanel2#All} flag can also be used. It indicates that all
     * diagram elements can be highlighted with the mouse.</p>
     */
    private int highlighting = 0;
    /**
     * The diagram that will actually be drawn in this panel.
     */
    private ConcreteDiagram diagram;
    /**
     * The scale that should be applied to the circles in this diagram (due to
     * the resizing of this panel).
     */
    private double scaleFactor = 1;
    private AffineTransform trans = new AffineTransform();
    /**
     * This stroke is used to draw contours if no special stroke is specified
     * for them.
     */
    private static final BasicStroke DEFAULT_CONTOUR_STROKE = new BasicStroke(2);
    /**
     * This stroke is used to draw highlighted legs, outlines, and contours.
     */
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(3.5f);
    private static final Color HIGHLIGHT_LEG_COLOUR = Color.BLUE;
    private static final Color HIGHLIGHTED_FOOT_COLOUR = Color.RED;
    private static final Color HIGHLIGHT_STROKE_COLOUR = Color.RED;
    private static final Color HIGHLIGHT_ZONE_COLOUR = new Color(0x70ff0000, true);
    private static final double HIGHLIGHTED_FOOT_SCALE = 1.4;
    private static final double HIGHLIGHT_CONTOUR_TOLERANCE = 6;
    private CircleContour highlightedContour = null;
    private ConcreteZone highlightedZone = null;
    private ConcreteSpiderFoot highlightedFoot = null;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates new panel that will draw the given diagram.
     *
     * @param diagram the diagram to be drawn by this panel.
     */
    public CirclesPanel2(ConcreteDiagram diagram) {
        initComponents();
        resetDiagram(diagram);
        resizeContents();
        // Register mouse listeners
        CirclesPanelMouseHandler mouseHandler = new CirclesPanelMouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /**
     * Creates new panel with no diagram.
     */
    public CirclesPanel2() {
        this(null);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Properties">
    /**
     * Sets the diagram that should be displayed by this panel.
     *
     * @param diagram the diagram that should be displayed by this panel.
     */
    public void setDiagram(ConcreteDiagram diagram) {
        if (this.diagram != diagram) {
            resetDiagram(diagram);
        }
    }

    /**
     * Gets the diagram that is currently being displayed by this panel.
     *
     * @return the diagram that is currently being displayed by this panel.
     */
    public ConcreteDiagram getDiagram() {
        return diagram;
    }

    /**
     * Returns the set of flags that determines which elements of the diagram may be
     * highlighted with the mouse. <p>This flag can be a (binary) combination of the
     * following flags: <ul> <li>{@link CirclesPanel2#Spiders}: which indicates
     * that spiders will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Zones}:
     * which indicates that zones will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Contours}:
     * which indicates that circle contours will be highlighted when the user hovers over them.</li> </ul></p> <p>
     * The {@link CirclesPanel2#All} flag can also be used. It indicates that all
     * diagram elements can be highlighted with the mouse.</p>
     *
     * @return the set of flags that determines which elements of the diagram may be
     * highlighted with the mouse.
     */
    public int getHighlighting() {
        return highlighting;
    }

    /**
     * Sets the set of flags that determines which elements of the diagram may be
     * highlighted with the mouse. <p>This flag can be a (binary) combination of the
     * following flags: <ul> <li>{@link CirclesPanel2#Spiders}: which indicates
     * that spiders will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Zones}:
     * which indicates that zones will be highlighted when the user hovers over them.</li> <li>{@link CirclesPanel2#Contours}:
     * which indicates that circle contours will be highlighted when the user hovers over them.</li> </ul></p> <p>
     * The {@link CirclesPanel2#All} flag can also be used. It indicates that all
     * diagram elements can be highlighted with the mouse.</p>
     *
     * @param highlighting the new set of flags that determines which elements of the diagram may be
     * highlighted with the mouse.
     */
    public void setHighlighting(int highlighting) {
        this.highlighting = highlighting & All;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    /**
     * Converts the given point from the coordinate system of this panel to the
     * coordinate system of the {@link CirclesPanel2#getDiagram() displayed
     * diagram}. <p>Use this method to issue queries on which diagrammatic
     * element is located under the given point.</p>
     *
     * @param p the coordinates which to convert.
     * @return the coordinates of the corresponding point in the coordinate
     * system of the {@link CirclesPanel2#getDiagram() displayed diagram}.
     */
    public Point toDiagramCoordinates(Point p) {
        p.x -= getCenteringTranslationX();
        p.x /= scaleFactor;
        p.y -= getCenteringTranslationY();
        p.y /= scaleFactor;
        return p;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Events">
    /**
     * Registers the given {@link DiagramClickListener diagram click listener} to
     * the events which are fired when the user clicks on particular diagram
     * elements.
     *  <p><span style="font-weight:bold">Note</span>: the events are invoked
     * regardless of whether {@link CirclesPanel2#getHighlighting()} flags are
     * set.</p>
     * @param l the event listener to register.
     */
    public void addDiagramClickListener(DiagramClickListener l) {
        this.listenerList.add(DiagramClickListener.class, l);
    }
    
    /**
     * Removes the given {@link DiagramClickListener diagram click listener}
     * from the events which are fired when the user clicks on particular diagram
     * elements.
     *  <p>The given listener will no longer receive these events.</p>
     * @param l the event listener to deregister.
     */
    public void removeDiagramClickListener(DiagramClickListener l) {
        this.listenerList.remove(DiagramClickListener.class, l);
    }
    
    /**
     * Returns the array of all {@link CirclesPanel2#addDiagramClickListener(icircles.gui.DiagramClickListener) registered}
     * {@link DiagramClickListener diagram click listeners}.
     * @return the array of all {@link CirclesPanel2#addDiagramClickListener(icircles.gui.DiagramClickListener) registered}
     * {@link DiagramClickListener diagram click listeners}.
     */
    public DiagramClickListener[] getDiagramClickListeners() {
        return listenerList.getListeners(DiagramClickListener.class);
    }
    
    protected void fireSpiderClickedEvent(ConcreteSpiderFoot foot) {
        DiagramClickListener[] ls = listenerList.getListeners(DiagramClickListener.class);
        if (ls != null && ls.length > 0) {
            SpiderClickedEvent e = new SpiderClickedEvent(this, foot);
            for (int i = 0; i < ls.length; i++) {
                ls[i].spiderClicked(e);
            }
        }
    }
    
    protected void fireZoneClickedEvent(ConcreteZone zone) {
        DiagramClickListener[] ls = listenerList.getListeners(DiagramClickListener.class);
        if (ls != null && ls.length > 0) {
            ZoneClickedEvent e = new ZoneClickedEvent(this, zone);
            for (int i = 0; i < ls.length; i++) {
                ls[i].zoneClicked(e);
            }
        }
    }
    
    protected void fireContourClickedEvent(CircleContour contour) {
        DiagramClickListener[] ls = listenerList.getListeners(DiagramClickListener.class);
        if (ls != null && ls.length > 0) {
            ContourClickedEvent e = new ContourClickedEvent(this, contour);
            for (int i = 0; i < ls.length; i++) {
                ls[i].contourClicked(e);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Highlighting">
    private ConcreteZone getHighlightedZone() {
        return highlightedZone;
    }

    private void setHighlightedZone(ConcreteZone highlightedZone) {
        if (this.highlightedZone != highlightedZone) {
            setHighlightedContour(null);
            setHighlightedFoot(null);
//            repaintShape(this.highlightedZone);
            this.highlightedZone = highlightedZone;
//            repaintShape(this.highlightedZone);
            repaint();
        }
    }

    private CircleContour getHighlightedContour() {
        return highlightedContour;
    }

    private void setHighlightedContour(CircleContour highlightedContour) {
        if (this.highlightedContour != highlightedContour) {
            setHighlightedZone(null);
            setHighlightedFoot(null);
//            repaintShape(this.highlightedContour);
            this.highlightedContour = highlightedContour;
//            repaintShape(this.highlightedContour);
            repaint();
        }
    }

    private ConcreteSpiderFoot getHighlightedFoot() {
        return highlightedFoot;
    }

    private void setHighlightedFoot(ConcreteSpiderFoot foot) {
        if (this.highlightedFoot != foot) {
            setHighlightedZone(null);
            setHighlightedContour(null);
            this.highlightedFoot = foot;
            repaint();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (diagram == null) {
            this.setBackground(Color.red);
            super.paint(g);
        } else {
            // draw the diagram
            super.paint(g);

            // This centers the diagram onto the drawing area.
            g.translate(getCenteringTranslationX(), getCenteringTranslationY());

            // Draw shaded zones:
            g.setColor(Color.lightGray);
            for (ConcreteZone z : diagram.getShadedZones()) {
                if (z.getColor() != null) {
                    g.setColor(z.getColor());
                } else {
                    g.setColor(Color.lightGray);
                }

                // TODO: The box of the diagram should not change. Put the box
                // into the constructor? NOTE: It would not add much to execution
                // speed. The 'getShape' function already caches the calculated
                // shape.
                Area a = z.getShape(diagram.getBox());
                g2d.fill(a.createTransformedArea(trans));
            }

            // Draw the highlighted zone:
            if (getHighlightedZone() != null) {
                Color oldColour = g2d.getColor();
                g2d.setColor(HIGHLIGHT_ZONE_COLOUR);
                g2d.fill(getHighlightedZone().getShape(diagram.getBox()).createTransformedArea(trans));
                g2d.setColor(oldColour);
            }

            // Draw contours:
            g2d.setStroke(DEFAULT_CONTOUR_STROKE);
            ArrayList<CircleContour> circles = diagram.getCircles();
            Ellipse2D.Double tmpCircle = new Ellipse2D.Double();
            for (CircleContour cc : circles) {
                Color col = cc.color();
                if (col == null) {
                    col = Color.black;
                }
                g.setColor(col);
                scaleCircle(scaleFactor, cc.getCircle(), tmpCircle);
                g2d.draw(tmpCircle);
                if (cc.ac.getLabel() == null) {
                    continue;
                }
                g.setColor(col);
                if (cc.stroke() != null) {
                    g2d.setStroke(cc.stroke());
                } else {
                    g2d.setStroke(DEFAULT_CONTOUR_STROKE);
                }
                // TODO a proper way to place labels - it can't be a method in CircleContour,
                // we need the context in the ConcreteDiagram
                Font f = diagram.getFont();
                if (f != null) {
                    g2d.setFont(f);
                }
                /*
                 * //TODO: g2d.getFontMetrics(); // for a string??? // use the
                 * font metrics to adjust the anchor position
                 *
                 * JLabel jl = new JLabel("IGI"); jl.setFont(font);
                 * jl.getWidth(); jl.getHeight(); jl.setLocation(arg0, arg1);
                 */

                g2d.drawString(cc.ac.getLabel().getLabel(),
                        (int) (cc.getLabelXPosition() * trans.getScaleX()),
                        (int) (cc.getLabelYPosition() * trans.getScaleY()));
            }

            ConcreteSpider highlightedSpider = getHighlightedFoot() == null ? null : getHighlightedFoot().getSpider();
            g.setColor(Color.black);
            for (ConcreteSpider s : diagram.getSpiders()) {
                // Reset the stroke and the colour if the spider is highlighted.
                Color oldColor = null;
                Stroke oldStroke = null;
                if (highlightedSpider == s) {
                    oldColor = g2d.getColor();
                    g2d.setColor(HIGHLIGHT_LEG_COLOUR);
                    oldStroke = g2d.getStroke();
                    g2d.setStroke(HIGHLIGHT_STROKE);
                }

                for (ConcreteSpiderLeg leg : s.legs) {

                    g2d.drawLine(
                            (int) (leg.from.getX() * scaleFactor),
                            (int) (leg.from.getY() * scaleFactor),
                            (int) (leg.to.getX() * scaleFactor),
                            (int) (leg.to.getY() * scaleFactor));
                }

                for (ConcreteSpiderFoot foot : s.feet) {
                    foot.getBlob(tmpCircle);
                    Color oldColor2 = g2d.getColor();
                    scaleCircle(scaleFactor, tmpCircle, tmpCircle);
                    if (getHighlightedFoot() == foot) {
                        oldColor2 = g2d.getColor();
                        g2d.setColor(HIGHLIGHTED_FOOT_COLOUR);
                        rescaleCircle(tmpCircle, HIGHLIGHTED_FOOT_SCALE);
                    }
                    g2d.fill(tmpCircle);
                    if (getHighlightedFoot() == foot) {
                        g2d.setColor(oldColor2);
                    }
                }
                if (s.as.get_name() == null) {
                    continue;
                }
                // TODO a proper way to place labels - it can't be a method in ConcreteSpider,
                // we need the context in the ConcreteDiagram
                g2d.drawString(s.as.get_name(),
                        (int) ((s.feet.get(0).getX() + 5) * trans.getScaleX()),
                        (int) ((s.feet.get(0).getY() - 5) * trans.getScaleY()));

                // Reset the stroke and colour appropriatelly.
                if (highlightedSpider == s) {
                    g2d.setColor(oldColor);
                    g2d.setStroke(oldStroke);
                }
            }

            // Draw the highlighted circle contour
            if (getHighlightedContour() != null) {
                // Reset the stroke and the colour of the highlighted outline.
                g2d.setColor(HIGHLIGHT_STROKE_COLOUR);
                g2d.setStroke(HIGHLIGHT_STROKE);
                scaleCircle(scaleFactor, getHighlightedContour().getCircle(), tmpCircle);
                g2d.draw(tmpCircle);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        resizeContents();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Utility Methods">
    /**
     * This method sets the given diagram as the one to be displayed. <p>It
     * refreshes the {@link CirclesPanel2#setPreferredSize(java.awt.Dimension)
     * preferred size} of this panel and requests a refresh of the drawing area
     * accordingly.</p>
     *
     * @param diagram
     */
    private void resetDiagram(ConcreteDiagram diagram) {
        this.diagram = diagram;
        if (diagram == null) {
            // NOTE: Currently we display nothing if there is no diagram
            this.setPreferredSize(null);
        } else {
            this.setPreferredSize(new Dimension(diagram.getSize(), diagram.getSize()));
        }
        // We have to redraw the entire area...
        resizeContents();
    }

    private void resizeContents() {
        if (diagram != null) {
            // Get the current width of this diagram panel and resize contents...
            int size = diagram.getSize();
            if (size > 0) {
                setScaleFactor(Math.min((float) this.getWidth() / size, (float) this.getHeight() / size));
            }
        }
    }

    /**
     * Sets the scale factor of the drawn contents to the new value. <p>This
     * merely scales the drawn contents (without affecting the thickness of
     * curves, size of spiders or fonts).</p> <p>Note: this method does not
     * change the size of the panel (not even the preferred size).</p>
     *
     * @param newScaleFactor the new factor by which to scale the drawn
     * contents.
     */
    private void setScaleFactor(double newScaleFactor) {
        scaleFactor = newScaleFactor;
        recalculateTransform();
        repaint();
    }

    /**
     * Compares the width and height of this panel and tries to scale the
     * concrete diagram's box so that it nicely fits the contents of the panel.
     */
    private void recalculateTransform() {
        this.trans.setToScale(scaleFactor, scaleFactor);
    }

    /**
     * Puts the scaled coordinates, width and height of {@code inCircle} into
     * the {@code outCircle} (without changing {@code inCircle}).
     *
     * @param scaleFactor
     * @param inCircle
     * @param outCircle
     */
    private void scaleCircle(double scaleFactor, Ellipse2D.Double inCircle, Ellipse2D.Double outCircle) {
        outCircle.x = inCircle.x * scaleFactor;
        outCircle.y = inCircle.y * scaleFactor;
        outCircle.width = inCircle.width * scaleFactor;
        outCircle.height = inCircle.height * scaleFactor;
    }

    /**
     * Issues a repaint of the content of this panel within the bounds of the
     * given shape.
     *
     * @param shape
     */
    private void repaintShape(Shape shape) {
        if (shape != null) {
            repaint(shape.getBounds());
        }
    }

    /**
     * Returns the horizontal centring translation of the diagram.
     *
     * @return
     */
    private int getCenteringTranslationX() {
        return (this.getWidth() - (int) Math.round(diagram.getSize() * scaleFactor)) / 2;
    }

    /**
     * Returns the vertical centring translation of the diagram.
     *
     * @return
     */
    private int getCenteringTranslationY() {
        return (this.getHeight() - (int) Math.round(diagram.getSize() * scaleFactor)) / 2;
    }

    /**
     * Scales the given circle while preserving the location of its centre.
     *
     * @param circle
     * @param scale
     */
    private void rescaleCircle(Double circle, double scale) {
        circle.x -= circle.width * (scale - 1) / 2;
        circle.y -= circle.height * (scale - 1) / 2;
        circle.width *= scale;
        circle.height *= scale;


    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Helper Classes">
    private class CirclesPanelMouseHandler implements MouseInputListener {

        public CirclesPanelMouseHandler() {
        }

        public void mouseClicked(MouseEvent e) {
            if (getDiagram() != null) {
                Point p = toDiagramCoordinates(e.getPoint());

                if ((getHighlighting() & Contours) == Contours) {
                    CircleContour contour = getDiagram().getCircleContourAtPoint(p, HIGHLIGHT_CONTOUR_TOLERANCE / scaleFactor);
                    if (contour != null) {
                        fireContourClickedEvent(contour);
                        return;
                    }
                }

                if ((getHighlighting() & Spiders) == Spiders) {
                    ConcreteSpiderFoot foot = getDiagram().getSpiderFootAtPoint(p);
                    if (foot != null) {
                        fireSpiderClickedEvent(foot);
                        return;
                    }
                }

                if ((getHighlighting() & Zones) == Zones) {
                    ConcreteZone zone = getDiagram().getZoneAtPoint(p);
                    if (zone != null) {
                        fireZoneClickedEvent(zone);
                    }
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            if (getHighlighting() != 0 && getDiagram() != null) {
                Point p = toDiagramCoordinates(e.getPoint());

                // Check if the mouse hovers over a contour:
                if ((getHighlighting() & Contours) == Contours) {
                    CircleContour contour = getDiagram().getCircleContourAtPoint(p, HIGHLIGHT_CONTOUR_TOLERANCE / scaleFactor);
                    if (contour != null) {
                        setHighlightedContour(contour);
                        return;
                    }
                }

                // Check if the mouse hovers over a spider:
                if ((getHighlighting() & Spiders) == Spiders) {
                    ConcreteSpiderFoot foot = getDiagram().getSpiderFootAtPoint(p);
                    if (foot != null) {
                        setHighlightedFoot(foot);
                        return;
                    }
                }

                // Check if the mouse hovers over a zone:
                if ((getHighlighting() & Zones) == Zones) {
                    ConcreteZone zone = getDiagram().getZoneAtPoint(p);
                    if (zone != null) {
                        setHighlightedZone(zone);
                        return;
                    }
                }

                setHighlightedZone(null);
                setHighlightedContour(null);
                setHighlightedFoot(null);
            }
        }
    }
//</editor-fold>
}
