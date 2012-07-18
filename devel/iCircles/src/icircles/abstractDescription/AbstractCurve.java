package icircles.abstractDescription;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * This class allows multiple different circles to have the same label.
 * <p>Remark: we could still consider having String instead of CurveLabel.</p>
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public class AbstractCurve implements Comparable<AbstractCurve> {

    static Logger logger = Logger.getLogger(CurveLabel.class.getName());

    static int id = 0;
    CurveLabel m_label;
    int m_id;

    public AbstractCurve(CurveLabel label) {
        id++;
        m_id = id;
        m_label = label;
    }

    public CurveLabel getLabel() {
        return m_label;
    }

    public AbstractCurve clone() {
        return new AbstractCurve(m_label);
    }

    public int compareTo(AbstractCurve o) {
    	if(null == o) {
    		return 1; // null is less than anything
    	}

        int tmp = m_label.compareTo(o.m_label);
        if (tmp != 0) {
            return tmp;
        }
        int this_id = m_id;
        int other_id = o.m_id;
        return (this_id < other_id) ? -1 : (this_id == other_id) ? 0 : 1;
    }

    public String debug() {
	// Abuse of log4j

        StringBuilder sb = new StringBuilder();
	sb.append("contour(");
        sb.append(m_label.debug());
	sb.append("_" + m_id + ")@");
	sb.append(hashCode());

	if(logger.getLevel() == Level.DEBUG)
	    return sb.toString();

	// Level.ALL
        return new String();
    }

    public boolean matches_label(AbstractCurve c) {
        return m_label == c.m_label;
    }

    public String debugWithId() {
        return debug() + "_" + m_id;
    }

    public double checksum() {
	logger.debug("build checksum from " + m_label.getLabel()
		     + " (and not " + m_id + ")\ngiving "+m_label.checksum());
        return m_label.checksum() /* * m_id */;
    }

    /** 
     * Only ever used by test code
    public static void reset_id_counter() {
        id = 0;
        AbstractBasicRegion.clearLibrary();
        CurveLabel.clearLibrary();
    }
    */

    public String toString() {
	return m_label.getLabel();
    }

}
