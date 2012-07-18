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

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Stores a label that can be applied to an {@link AbsractCurve}.
 */
public class CurveLabel implements Comparable<CurveLabel> {

    static Logger logger = Logger.getLogger(CurveLabel.class.getName());

    private String m_label;
    private static Set<CurveLabel> m_library = new TreeSet<CurveLabel>();
    // or use a WeakReference - then the WeakHashMap will be emptied when
    // there will be no references for the members any more
    // but beware to put the item into the WeakHashMap just after you've 
    // extracted it!

    /*
     * only ever called by test code
    public static void clearLibrary() {
        m_library.clear();
    }
    */

    private CurveLabel(String label) {
        m_label = label;
    }

    public static CurveLabel get(String label) {
        // TODO: This suboptimal existent label lookup should be fixed. This
        //       should be used instead:
        //
        //              m_library.contains(label);
        //
        // The problem seems to stem from the fact that the string is not
        // comparable with the CurveLabel. I would suggest that instead of a
        // CurveLabel we use String.
        for (CurveLabel alreadyThere : m_library) {
            if (alreadyThere.m_label.equals(label)) {
                return alreadyThere;
            }
        }

        CurveLabel result = new CurveLabel(label);
        m_library.add(result);
        return result;
    }

    public String debug() {
	// This is an abuse of log4j, however it's a stop-gap reformulation of
	// the previous code.  FIXME: Do logging correctly
	Level l = logger.getLevel();

	if(Level.DEBUG == l) {
	    return m_label;
	}

	if(Level.TRACE == l) {
	    return new String(m_label + "@"+ hashCode());
	}

	// Level.ALL
	return new String();
    }

    public int compareTo(CurveLabel other) {
        return m_label.compareTo(other.m_label);
    }

    public double checksum() {
        return (double)m_label.hashCode() * 1E-7;
    }

    public boolean isLabelled(String string) {
        return string.equals(m_label);

    }

    public String getLabel() {
        return m_label;
    }
}
