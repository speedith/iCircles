package icircles.decomposition;
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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import icircles.abstractDescription.AbstractDescription;
import icircles.abstractDescription.AbstractCurve;
import icircles.abstractDescription.AbstractBasicRegion;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class DecompositionStep {

    static Logger logger = Logger.getLogger(DecompositionStep.class.getName());

    AbstractDescription m_from;
    AbstractDescription m_to;
    TreeMap<AbstractBasicRegion, AbstractBasicRegion> m_zones_moved;
    AbstractCurve m_removed;                  // in m_from but not m_to

    public DecompositionStep(
            AbstractDescription from,
            AbstractDescription to,
            TreeMap<AbstractBasicRegion, AbstractBasicRegion> zones_moved, // could be derived from from + to
            AbstractCurve removed) // could be derived from from + to
    {
        m_from = from;
        m_to = to;
        m_zones_moved = zones_moved;
        m_removed = removed;
    }

    public AbstractDescription target() {
        return m_to;
    }

    public String debug() {
        if (logger.getEffectiveLevel().equals(Level.ALL)) {
            return "";
        }
        // otherwise we want output

        StringBuilder sb = new StringBuilder();
        sb.append("remove ");
        sb.append(m_from.printContour(m_removed));
        sb.append("\n");

        sb.append(" from ");
        sb.append(m_from.debugAsSentence());
        sb.append("\n");

        sb.append(" to ");
        sb.append(m_to.debugAsSentence());
        sb.append("\n");

        sb.append(" zonesMoved: ");
        Set<Map.Entry<AbstractBasicRegion, AbstractBasicRegion>> entries = m_zones_moved.entrySet();
        for (Map.Entry<AbstractBasicRegion, AbstractBasicRegion> z_map : entries) {
            sb.append("<");
            sb.append(z_map.getKey().debug());
            sb.append("->");
            sb.append(z_map.getValue().debug());
            sb.append(">");
        }
        return sb.toString();
    }

    public AbstractDescription to() {
        return m_to;
    }

    public AbstractDescription from() {
        return m_from;
    }

    public TreeMap<AbstractBasicRegion, AbstractBasicRegion> zones_moved() {
        return m_zones_moved;
    }

    public AbstractCurve removed() {
        return m_removed;
    }

    private double checksum() {
        return 1.1 * m_from.checksum() + 1.3 * m_to.checksum();
    }

    public static double checksum(ArrayList<DecompositionStep> d_steps) {
        double scaling = 1.11;
        double result = 0.0;
        for (DecompositionStep step : d_steps) {
            result += step.checksum() * scaling;
            scaling += 0.1;
        }
        return result;
    }
}
