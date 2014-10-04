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

import icircles.abstractDescription.AbstractBasicRegion;
import icircles.abstractDualGraph.AbstractDualEdge;
import icircles.abstractDualGraph.AbstractDualGraph;
import icircles.abstractDualGraph.AbstractDualNode;

import org.apache.log4j.Logger;

public class RecompositionStrategySinglyPierced extends RecompositionStrategy {
    static Logger logger = Logger.getLogger(RecompositionStrategySinglyPierced.class.getName());

    public ArrayList<Cluster> make_clusters(
                                            ArrayList<AbstractBasicRegion> zonesToSplit) {

        logger.info("recomposition stratgey is singly peirced");

        // Look for pairs of AbstractBasicRegions which differ by just a
        // single AbstractCurve - these pairs are potential double-clusters

        AbstractDualGraph adg = new AbstractDualGraph(zonesToSplit);
        ArrayList<Cluster> result = seekSinglePiercings(adg);
        return result;
    }

    public static ArrayList<Cluster> seekSinglePiercings(AbstractDualGraph adg) {
        ArrayList<Cluster> result = new ArrayList<Cluster>();
        for (AbstractDualEdge e = adg.getLowDegreeEdge();
                e != null;
                e = adg.getLowDegreeEdge()) {
            Cluster c = new Cluster(e.from.abr, e.to.abr);
            result.add(c);

            logger.debug("made single-peirced cluster " + (c.debug()) + "\n");
            logger.debug("graph before trimming for cluster " + (adg.debug()) + "\n");

            adg.remove(e.from);
            adg.remove(e.to);

            logger.debug("graph after trimming for cluster " + adg.debug() + "\n");

        }
        assert (adg.getNumEdges() == 0) : "non-empty adg edge set";
        result.addAll(seekNestedPiercings(adg));
        return result;
    }

    public static ArrayList<Cluster> seekNestedPiercings(AbstractDualGraph adg) {
        ArrayList<Cluster> result = new ArrayList<Cluster>();
        Iterator<AbstractDualNode> nIt = adg.getNodeIterator();
        while (nIt.hasNext()) {
            AbstractDualNode n = nIt.next();
            result.add(new Cluster(n.abr));

            logger.debug("adding nested cluster " + n.abr.debug());
        }
        return result;
    }
}
