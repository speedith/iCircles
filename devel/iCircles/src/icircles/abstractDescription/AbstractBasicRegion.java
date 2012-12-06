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

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * {@link AbstractBasicRegion} maintains a collection of {@link AbstractBasicRegion} objects.
 *
 * You normally create an {@link AbstractBasicRegion} by calling {@link AbstractBasicRegion #get}
 * and pass in the set of {@link AbstractCurve} objects.  Normally you use {@link AbstractBasicRegion #get}
 * in conjunction with {@link AbstractCurve} as follows:
 * <pre>
 * {@code
 * CurveLabel cl             = new CurveLabel("Example");
 * AbstractCurve ac          = new AbstractCurve(cl);
 * Set<AbstractCurve> ts     = new TreeSet<AbstractCurve>();
 * ts.add(ac);
 * AbstractBasicRegion abr   = AbstractBasicRegion.get(ts);
 * }
 * </pre>
 */
public class AbstractBasicRegion implements Comparable<AbstractBasicRegion> {

    static Logger logger = Logger.getLogger(AbstractBasicRegion.class.getName());

    @JsonProperty(value="inSet")
    TreeSet<AbstractCurve> m_in_set;
    static TreeSet<AbstractBasicRegion> m_library = new TreeSet<AbstractBasicRegion>();

    /**
     * Default constructor is needed for Jackson Databinding.
     */
    public AbstractBasicRegion() {
    }

    private AbstractBasicRegion(TreeSet<AbstractCurve> in_set) {
        m_in_set = in_set;
    }

    public static AbstractBasicRegion get(Set<AbstractCurve> in_set) {
        // TODO: This is quite an expensive way to look up already existing
        // objects. This should definitely be replaced by 'contains'.
        //
        // I would suggest not to use the library at all.
        for (AbstractBasicRegion alreadyThere : m_library) {
            if (alreadyThere.m_in_set.equals(in_set)) {
                return alreadyThere;
            }
        }

        TreeSet<AbstractCurve> tmp = new TreeSet<AbstractCurve>(in_set);
        AbstractBasicRegion result = new AbstractBasicRegion(tmp);
        m_library.add(result);
        return result;
    }

    /**
     * Given an {@link AbstractBasicRegion} inside an {@link AbstractCurve} it returns
     * a copy of an {@link AbstractBasicRegion} where the passed {@link AbstractCurve} is
     * removed.  In the case that the passed {@link AbstractCurve} is not a curve that
     * this {@link AbstractBasicRegion} is inside then this {@link AbstractBasicRegion} is
     * returned.
     *
     * Does not modify the object on which this method is called.
     *
     * @param c An {@link AbstractCurve} which this {@link AbstractBasicRegion} is inside
     * @return an {@link AbstractBasicRegion} which has been moved outside c
     */
    public AbstractBasicRegion moveOutside(AbstractCurve c) {
        if (m_in_set.contains(c)) {
            TreeSet<AbstractCurve> contours = new TreeSet<AbstractCurve>(m_in_set);
            contours.remove(c);
            return get(contours);
        } else {
            return this;
        }
    }

    /**
     * Allows two {@link AbstractBasicRegion} objects to be compared {@link Comparable #compareTo}
     * @param other The {@link AbstractBasicRegion} to compareTo
     * @return 1  if this {@link AbstractBasicRegion} contains more contours than other,
     *         -1 if other contains more contours than this, and
     *         the comparison of the interal contour sets otherwise.
     */
    public int compareTo(AbstractBasicRegion other) {
        if (other.m_in_set.size() < m_in_set.size()) {
            return 1;
        } else if (other.m_in_set.size() > m_in_set.size()) {
            return -1;
        }

        // same sized in_set
        Iterator<AbstractCurve> this_it = m_in_set.iterator();
        Iterator<AbstractCurve> other_it = other.m_in_set.iterator();

        while (this_it.hasNext()) {
            AbstractCurve this_c = this_it.next();
            AbstractCurve other_c = other_it.next();
            int comp = this_c.compareTo(other_c);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }

    public String debug() {
        // log4j abuse
        final Level l = logger.getEffectiveLevel();

        StringBuilder b = new StringBuilder();
        if (l.isGreaterOrEqual(Level.DEBUG)) {
            b.append("(");
        }
        boolean first = true;
        for (AbstractCurve c : m_in_set) {
            if (!first && l.isGreaterOrEqual(Level.DEBUG)) {
                b.append(",");
            }
            b.append(c.debug());
            first = false;
        }
        if (l.isGreaterOrEqual(Level.DEBUG)) {
            b.append(")");
        }
        if (l.isGreaterOrEqual(Level.TRACE)) {
            b.append(hashCode());
        }

        if (l.isGreaterOrEqual(Level.DEBUG)) {
            return b.toString();
        }

        // Level.ALL
        return new String();
    }
    public String toString() {
        if(m_in_set.isEmpty())
            return ".";

        StringBuilder b = new StringBuilder();
        for (AbstractCurve c : m_in_set) {
            b.append(c.toString());
        }
        return b.toString();
    }

    /**
     * An Iterator over the {@link AbstractCurve} objects contained in this {@link AbstractBasicRegion}.
     *
     * @return The iterator.
     */
    @JsonIgnore
    public Iterator<AbstractCurve> getContourIterator() {
        return m_in_set.iterator();
    }

    /**
     * Returns the number of contours within which this {@link AbstractBasicRegion} is contained.
     *
     * @return the size of the internal store of {@link AbstractCurve} objects
     */
    @JsonIgnore
    public int getNumContours() {
        return m_in_set.size();
    }

    /**
     * A "straddled contour" is a contour which distinguishes two AbstractBasicRegion 
     * objects.  Effectively, it is the difference between two contour sets, if
     * and only if the difference is a single contour.
     *
     * @param other The AbstractBasicRegion to compare to.
     * @return The straddled contour or null.
     */
    public AbstractCurve getStraddledContour(AbstractBasicRegion other) {
        int nc = getNumContours();
        int othernc = other.getNumContours();
        if (Math.abs(nc - othernc) != 1) {
            return null;
        } else if (nc < othernc) {
            return other.getStraddledContour(this);
        } else {
            // we have one more contour than other - are we neighbours?
            AbstractCurve result = null;
            Iterator<AbstractCurve> it = getContourIterator();
            while (it.hasNext()) {
                AbstractCurve ac = it.next();
                if (!other.isIn(ac)) {
                    if (result != null) {
                        return null; // found two contours here absent from other
                    } else {
                        result = ac;
                    }
                }
            }

            logger.debug("straddle : " + debug() + "->" + other.debug() + "=" + result.debug());

            return result;
        }
    }

    /**
     * Moves an AbstractBasicRegion inside a given AbstractCurve.  It is the
     * of {@link #moveOutside}.
     *
     * It's worth noting that this method does not modify the object that it
     * is called on.
     *
     * @param newCont The curve to move an AbstractBasicRegion inside.
     * @return a new AbstractBasicRegion inside all the AbstractCurve objects
     *         that this is inside and also inside the passed AbstractCurve.
     */
    public AbstractBasicRegion movedIn(AbstractCurve newCont) {
        TreeSet<AbstractCurve> conts = new TreeSet<AbstractCurve>(m_in_set);
        conts.add(newCont);
        return AbstractBasicRegion.get(conts);
    }

    public boolean isIn(AbstractCurve c) {
        return m_in_set.contains(c);
    }

    public double checksum() {
        double result = 0.0;
        double scaling = 3.1;
        for (AbstractCurve c : m_in_set) {
            result += c.checksum() * scaling;
            scaling += 0.09;
        }
        return result;
    }

    /**
     * Compares the label equivalence of two AbstractBasicRegion objects.  The
     * label equivalence ensures that the labels in this AbstractBasicRegion are
     * exactly those which are contained in the passed in AbstractBasicRegion.
     *
     * This is necessarily O(n^2) + the complexity to access the internal data
     * structure, where n is the number of AbsractCurve objects in the region.
     *
     * @param z The AbstractBasicRegion to compare label equivalence.
     * @return True if the regions are label equivalent, false otherwise.
     */
    public boolean isLabelEquivalent(AbstractBasicRegion z) {
        if (getNumContours() == z.getNumContours()) {
            if (z.getNumContours() == 0) {
                return true;
            } else {
                //System.out.println(" compare zones "+debug()+" and "+z.debug());
                Iterator<AbstractCurve> acIt = getContourIterator();
                AcItLoop:
                while (acIt.hasNext()) {
                    AbstractCurve thisAC = acIt.next();
                    // look for an AbstractCurve in z with the same label
                    Iterator<AbstractCurve> acIt2 = z.getContourIterator();
                    while (acIt2.hasNext()) {
                        AbstractCurve thatAC = acIt2.next();
                        //System.out.println(" compare abstract contours "+thisAC.debug()+" and "+thatAC.debug());
                        if (thisAC.matchesLabel(thatAC)) {
                            //System.out.println(" got match ");
                            continue AcItLoop;
                        }
                    }
                    //System.out.println(" no match for "+thisAC.debug());
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
