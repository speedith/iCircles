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

import icircles.abstractDescription.AbstractBasicRegion;
import icircles.abstractDualGraph.AbstractDualGraph;
import icircles.abstractDualGraph.AbstractDualNode;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class RecompositionStrategyDoublyPierced extends RecompositionStrategy {

    static Logger logger = Logger.getLogger(RecompositionStrategyDoublyPierced.class.getName());

    public ArrayList<Cluster> make_clusters(
            ArrayList<AbstractBasicRegion> zonesToSplit) {

        logger.info("recomposition stratgey is doubly peirced");

        // Look for four-tuples of AbstractBasicRegions which differ by
        // two AbstractCurves - these four-tuples are potential double-clusters

        ArrayList<Cluster> result = new ArrayList<Cluster>();

        AbstractDualGraph adg = new AbstractDualGraph(zonesToSplit);
        logger.debug("zonesToSplit is ");
        for (AbstractBasicRegion abr : zonesToSplit) {
            logger.debug("abr:" + abr.debug());
        }

        for (ArrayList<AbstractDualNode> nodes = adg.getFourTuple();
                nodes != null;
                nodes = adg.getFourTuple()) {
            if (nodes.size() == 0) {
                break;
            }
            Cluster c = new Cluster(nodes.get(0).abr,
                    nodes.get(1).abr,
                    nodes.get(2).abr,
                    nodes.get(3).abr);
            result.add(c);

            logger.debug("made cluster " + (c.debug()) + "\n");
            logger.debug("graph before trimming for cluster " + (adg.debug()) + "\n");

            adg.remove(nodes.get(0));
            adg.remove(nodes.get(1));
            adg.remove(nodes.get(2));
            adg.remove(nodes.get(3));

            logger.debug("graph after trimming for cluster " + adg.debug() + "\n");
        }

        result.addAll(RecompositionStrategySinglyPierced.seekSinglePiercings(adg));

        return result;
    }
}
