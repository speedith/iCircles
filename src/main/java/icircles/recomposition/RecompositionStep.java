package icircles.recomposition;

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
import java.util.Iterator;

import icircles.abstractDescription.AbstractDescription;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class RecompositionStep {

    static Logger logger = Logger.getLogger(RecompositionStep.class.getName());

    AbstractDescription m_from;
    AbstractDescription m_to;
    ArrayList<RecompData> m_added_contour_data;

    public RecompositionStep(AbstractDescription from,
            AbstractDescription to,
            ArrayList<RecompData> added_contour_data) {
        m_from = from;
        m_to = to;
        m_added_contour_data = added_contour_data;
        assert (added_contour_data.size() > 0 ): "no added curve in recomp";
        String cl = added_contour_data.get(0).added_curve.getLabel();
        for (RecompData rp : added_contour_data) {
            assert (rp.added_curve.getLabel().equals(cl)) : "mixed curves added in recomp";
        }

        assert (!from.includesLabel(cl)) : "added curve already present";
        assert (to.includesLabel(cl)) : "added curve wasn't added";
    }

    public String debug() {
        if (logger.getEffectiveLevel().equals(Level.ALL)) {
            return "";
        }
        // otherwise

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        sb.append(" from ");
        sb.append(m_from.debugAsSentence());
        sb.append("\n");

        sb.append(" to ");
        sb.append(m_to.debugAsSentence());
        sb.append("\n");

        return sb.toString();
    }

    public AbstractDescription to() {
        return m_to;
    }

    public static double checksum(ArrayList<RecompositionStep> rSteps) {
        double scaling = 11.23;
        double result = 0.0;
        for (RecompositionStep step : rSteps) {
            result += step.checksum() * scaling;
            scaling += 0.1;
        }
        return result;
    }

    private double checksum() {
        return 7.1 * m_from.checksum() + 7.3 * m_to.checksum();
    }

    public Iterator<RecompData> getRecompIterator() {
        return m_added_contour_data.iterator();
    }
}
