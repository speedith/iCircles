/*
 *   Project: iCircles
 * 
 * File name: CirclesPanelEx.java
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.ArrayList;

import java.io.StringWriter;

import javax.swing.JPanel;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

/**
 * This panel takes a {@link ConcreteDiagram concrete diagram} and draws it.
 *
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public class CirclesPanelEx extends JPanel {

    // Get a DOMImplementation.
    private DOMImplementation domImpl =
        GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    private String svgNS = "http://www.w3.org/2000/svg";
    private Document document = domImpl.createDocument(svgNS, "svg", null);

    // Create an instance of the SVG Generator.
    private SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

    // <editor-fold defaultstate="collapsed" desc="Private Fields">
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
    private static final Color HIGHLIGHT_ZONE_COLOUR = new Color(0x70ff0000, true); // Color.RED;
    private static final double HIGHLIGHTED_FOOT_SCALE = 1.4;
    private CircleContour highlightedContour = null;
    private ConcreteZone highlightedZone = null;
    private ConcreteSpiderFoot highlightedFoot = null;
    private static final long serialVersionUID = 0x5b7fd085e1dff1a0L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Creates new panel that will draw the given diagram.
     *
     * @param diagram the diagram to be drawn by this panel.
     */
    public CirclesPanelEx(ConcreteDiagram diagram) {
        initComponents();
        resetDiagram(diagram);
        resizeContents();
    }

    /**
     * Creates new panel with no diagram.
     */
    public CirclesPanelEx() {
        this(null);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(null);
    }
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
     * Returns the factor by which the {@link ConcreteDiagram drawn concrete
     * diagram} is scaled. The scaling is done so that the diagram fits the panel
     * nicely.
     * @return the factor by which the {@link ConcreteDiagram drawn concrete
     * diagram} is scaled.
     */
    public final double getScaleFactor() {
        return scaleFactor;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">
    /**
     * Converts the given point from the coordinate system of this panel to the
     * coordinate system of the {@link CirclesPanelEx#getDiagram() displayed
     * diagram}. <p>Use this method to issue queries on which diagrammatic
     * element is located under the given point.</p>
     *
     * @param p the coordinates which to convert.
     * @return the coordinates of the corresponding point in the coordinate
     * system of the {@link CirclesPanelEx#getDiagram() displayed diagram}.
     */
    public Point toDiagramCoordinates(Point p) {
        p.x -= getCenteringTranslationX();
        p.x /= scaleFactor;
        p.y -= getCenteringTranslationY();
        p.y /= scaleFactor;
        return p;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Highlighting">
    protected ConcreteZone getHighlightedZone() {
        return highlightedZone;
    }

    protected void setHighlightedZone(ConcreteZone highlightedZone) {
        if (this.highlightedZone != highlightedZone) {
            setHighlightedContour(null);
            setHighlightedFoot(null);
//            repaintShape(this.highlightedZone);
            this.highlightedZone = highlightedZone;
//            repaintShape(this.highlightedZone);
            repaint();
        }
    }

    protected CircleContour getHighlightedContour() {
        return highlightedContour;
    }

    protected void setHighlightedContour(CircleContour highlightedContour) {
        if (this.highlightedContour != highlightedContour) {
            setHighlightedZone(null);
            setHighlightedFoot(null);
//            repaintShape(this.highlightedContour);
            this.highlightedContour = highlightedContour;
//            repaintShape(this.highlightedContour);
            repaint();
        }
    }

    protected ConcreteSpiderFoot getHighlightedFoot() {
        return highlightedFoot;
    }

    protected void setHighlightedFoot(ConcreteSpiderFoot foot) {
        if (this.highlightedFoot != foot) {
            setHighlightedZone(null);
            setHighlightedContour(null);
            this.highlightedFoot = foot;
            repaint();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Drawing">
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
                transformCircle(scaleFactor, cc.getCircle(), tmpCircle);
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

                g2d.drawString(cc.ac.getLabel(),
                        (int) (cc.getLabelXPosition() * trans.getScaleX()) + 5,
                        (int) (cc.getLabelYPosition() * trans.getScaleY()) + 5);
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

                // TODO: Do not scale the feet. Let them be of fixed size. But fix the positioning!
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
                    translateCircleCentre(scaleFactor, tmpCircle, tmpCircle);
                    if (getHighlightedFoot() == foot) {
                        oldColor2 = g2d.getColor();
                        g2d.setColor(HIGHLIGHTED_FOOT_COLOUR);
                        scaleCircleCentrally(tmpCircle, HIGHLIGHTED_FOOT_SCALE);
                    }
                    g2d.fill(tmpCircle);
                    if (getHighlightedFoot() == foot) {
                        g2d.setColor(oldColor2);
                    }
                }
                if (s.as.getName() == null) {
                    continue;
                }
                // TODO a proper way to place labels - it can't be a method in ConcreteSpider,
                // we need the context in the ConcreteDiagram
                g2d.drawString(s.as.getName(),
                        (int) ((s.feet.get(0).getX()) * trans.getScaleX()) - 5,
                        (int) ((s.feet.get(0).getY()) * trans.getScaleY()) - 10);

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
                transformCircle(scaleFactor, getHighlightedContour().getCircle(), tmpCircle);
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
     * refreshes the {@link CirclesPanelEx#setPreferredSize(java.awt.Dimension)
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
    private static void transformCircle(double scaleFactor, Ellipse2D.Double inCircle, Ellipse2D.Double outCircle) {
        translateCircle(scaleFactor, inCircle, outCircle);
        scaleCircle(scaleFactor, inCircle, outCircle);
    }

    /**
     * Translates the circle to match the scale factor. The changed
     * coordinates are written to the {@code outCircle} (without changing
     * {@code inCircle}).
     *
     * @param scaleFactor
     * @param inCircle
     * @param outCircle
     */
    private static void translateCircle(double scaleFactor, Ellipse2D.Double inCircle, Ellipse2D.Double outCircle) {
        outCircle.x = inCircle.x * scaleFactor;
        outCircle.y = inCircle.y * scaleFactor;
    }

    /**
     * Translates the circle without scaling so that its centre coincides with
     * the centre if the circle was translated and then scaled. The new
     * coordinates are written to the {@code outCircle} (without changing
     * {@code inCircle}).
     *
     * @param scaleFactor
     * @param inCircle
     * @param outCircle
     */
    private static void translateCircleCentre(double scaleFactor, Ellipse2D.Double inCircle, Ellipse2D.Double outCircle) {
        final double correctionFactor = (scaleFactor - 1) / 2;
        outCircle.x = inCircle.x * scaleFactor + inCircle.width * correctionFactor;
        outCircle.y = inCircle.y * scaleFactor + inCircle.height * correctionFactor;
    }

    /**
     * Scales the circle to match the scale factor. The changed
     * width and height are written to the {@code outCircle} (without changing
     * {@code inCircle}).
     *
     * @param scaleFactor
     * @param inCircle
     * @param outCircle
     */
    private static void scaleCircle(double scaleFactor, Ellipse2D.Double inCircle, Ellipse2D.Double outCircle) {
        outCircle.width = inCircle.width * scaleFactor;
        outCircle.height = inCircle.height * scaleFactor;
    }

    /**
     * Scales the given circle while preserving the location of its centre.
     *
     * @param circle
     * @param scale
     */
    private static void scaleCircleCentrally(Double circle, double scale) {
        circle.x -= circle.width * (scale - 1) / 2;
        circle.y -= circle.height * (scale - 1) / 2;
        circle.width *= scale;
        circle.height *= scale;
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
    // </editor-fold>

    @Override
    protected Graphics getComponentGraphics(Graphics g) {
	return svgGenerator;
    }

    @Override
    public String toString() {
        // We've got to force the Component to paint
        // particularly when we're calling toString from a non GUI app
        paint(svgGenerator);

        StringWriter  w = new StringWriter();

        try {
            svgGenerator.stream(w);
        } catch (SVGGraphics2DIOException sg2ie) {
            return new String("<!-- SVG Generation Failed -->");
        }
        return w.toString();
    }
}
