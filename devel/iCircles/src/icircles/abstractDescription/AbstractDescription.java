package icircles.abstractDescription;
/*
 * @author Jean Flower <jeanflower@rocketmail.com>
 * Copyright (c) 2012
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the iCircles Project.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * An AbstractDescription encapsulates the elements of a diagram, with no drawn information.
 * A diagram comprises a set of AbstractCurves (the contours).
 * A set of AbstractBasicRegions is given (zones which must be present.
 * <p>
 * An AbstractDiagram is consistent if
 * <ol>
 * <li>The contours in each of the AbstractBasicRegions match those
 * in m_contours.</li>
 * <li>Every valid diagram includes the "outside" zone.</li>
 * <li>Every shaded zone is also a zone.</li>
 * <li>Every contour must have a zone inside it.</li>
 * </ol>
 * </p>
 * Currently, there is no checking done to ensure that conditions 1--4 are
 * adhered to.  As such, you can create invalid diagrams.
 *
 * TODO: add a coherence check on these internal checks.
 */
public class AbstractDescription {

    static Logger logger = Logger.getLogger(AbstractDescription.class.getName());

    TreeSet<AbstractCurve> m_contours;
    Set<AbstractBasicRegion> m_zones;
    Set<AbstractBasicRegion> m_shaded_zones;

    ArrayList<AbstractSpider> m_spiders;

    public AbstractDescription(Set<AbstractCurve> contours,
			       Set<AbstractBasicRegion> zones,
			       Set<AbstractBasicRegion> shaded_zones) {
        m_contours = new TreeSet<AbstractCurve>(contours);
        m_zones = new TreeSet<AbstractBasicRegion>(zones);
        m_shaded_zones = new TreeSet<AbstractBasicRegion>(shaded_zones);
        m_spiders = new ArrayList<AbstractSpider>();
    }

    public AbstractDescription(Set<AbstractCurve> contours,
			       Set<AbstractBasicRegion> zones) {
	m_contours = new TreeSet<AbstractCurve>(contours);
	m_zones = new TreeSet<AbstractBasicRegion>(zones);
	m_shaded_zones = new TreeSet<AbstractBasicRegion>();
        m_spiders = new ArrayList<AbstractSpider>();
	}

    //TODO
    public boolean checksOk()
    {
    	// do some validity checks
    	// is every contour in a zone? etc.
    	return true;
    }

    public void addSpider(AbstractSpider s){
    	// TODO : check that feet are indeed AbstractBasicRegions of the diagram
    	m_spiders.add(s);
    }

    public AbstractCurve getFirstContour() {
        if (m_contours.size() == 0) {
            return null;
        }
        return m_contours.first();
    }

    public AbstractCurve getLastContour() {
        if (m_contours.size() == 0) {
            return null;
        }
        return m_contours.last();
    }

    public Iterator<AbstractCurve> getContourIterator() {
        return m_contours.iterator();
    }

    public int getNumContours() {
        return m_contours.size();
    }

    public Iterator<AbstractBasicRegion> getZoneIterator() {
        return m_zones.iterator();
    }
    // expensive - do not use just for querying
    public TreeSet<AbstractCurve> getCopyOfContours() {
        return new TreeSet<AbstractCurve>(m_contours);
    }
    // expensive - do not use just for querying
    public TreeSet<AbstractBasicRegion> getCopyOfZones() {
        return new TreeSet<AbstractBasicRegion>(m_zones);
    }

    public Iterator<AbstractSpider> getSpiderIterator() {
        return m_spiders.iterator();
    }

    public String debug() {

	final int l = logger.getLevel().toInt();

        StringBuilder b = new StringBuilder();
        b.append("labels:");
        boolean first = true;
        if (l >= Level.DEBUG.toInt()) {
            b.append("{");
        }
        for (AbstractCurve c : m_contours) {
            if (!first) {
                b.append(",");
            }
            b.append(c.debug());
            first = false;
        }
        if (l >= Level.DEBUG.toInt()) {
            b.append("}");
        }
        b.append("\n");
        b.append("zones:");
        if (l >= Level.DEBUG.toInt()) {
            b.append("{");
        }
        first = true;
        for (AbstractBasicRegion z : m_zones) {
            if (!first) {
                b.append(",");
            }
            if (l >= Level.DEBUG.toInt()) {
                b.append("\n");
            }
            b.append(z.debug());
            first = false;
        }
        if (l >= Level.DEBUG.toInt()) {
            b.append("}");
        }
        b.append(" shading:");
        first = true;
        for (AbstractBasicRegion z : m_shaded_zones) {
            if (!first) {
                b.append(",");
            }
            if (l >= Level.DEBUG.toInt()) {
                b.append("\n");
            }
            b.append(z.debug());
            first = false;
        }
        if (l >= Level.DEBUG.toInt()) {
            b.append("}");
        }
        b.append("\n");

	if(l >= Level.DEBUG.toInt())
	    return b.toString();

	// Level.ALL
	return new String();
    }

    public String debugAsSentence() {
        HashMap<AbstractCurve, String> printable = new HashMap<AbstractCurve, String>();
        for (AbstractCurve c : m_contours) {
            printable.put(c, printContour(c));
        }
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (AbstractBasicRegion z : m_zones) {
            if (!first) {
                b.append(",");
            }
            Iterator<AbstractCurve> c_it = z.getContourIterator();
            boolean printed_something = false;
            while (c_it.hasNext()) {
                AbstractCurve c = c_it.next();
                b.append(printable.get(c));
                printed_something = true;
            }
            if (!printed_something) {
                b.append("0");
            }
            first = false;
        }
        return b.toString();
    }

    public String printContour(AbstractCurve c) {
        if (oneOfMultipleInstances(c)) {
            return c.debugWithId();
        } else {
            return c.debug();
        }
    }

    /**
     * Is the passed in AbstractCurve a split contour in the diagram.
     * Alternatively stated as, are there multiple curves with the same label
     * in the diagram.
     *
     * @param c The AbstractCurve to test whether split or not.
     * @return False if there is only one AbstractCurve of the same label as c 
     *         in the diagram, true otherwise.
     */
    boolean oneOfMultipleInstances(AbstractCurve c) {
        for (AbstractCurve cc : m_contours) {
            if (cc != c && cc.matchesLabel(c)) {
                return true;
            }
        }
        return false;
    }

    public int getNumZones() {
        return m_zones.size();
    }

    public double checksum() {
        double scaling = 2.1;
        double result = 0.0;
        for (AbstractCurve c : m_contours) {
            result += c.checksum() * scaling;
            scaling += 0.07;
            scaling += 0.05;
            for (AbstractBasicRegion z : m_zones) {
                if (z.isIn(c)) {
                    result += z.checksum() * scaling;
                    scaling += 0.09;
                }
            }
        }
        return result;
    }

    public boolean includesLabel(CurveLabel l) {
        for (AbstractCurve c : m_contours) {
            if (c.getLabel() == l) {
                return true;
            }
        }
        return false;
    }

    public AbstractBasicRegion getLabelEquivalentZone(AbstractBasicRegion z) {
        for (AbstractBasicRegion zone : m_zones) {
            if (zone.isLabelEquivalent(z)) {
                return zone;
            }
        }
        return null;
    }

    public boolean hasShadedZone(AbstractBasicRegion z){
    	return m_shaded_zones.contains(z);
    }
}
