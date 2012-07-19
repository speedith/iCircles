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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import icircles.abstractDescription.AbstractDescription;
import icircles.abstractDescription.AbstractCurve;
import icircles.abstractDescription.AbstractBasicRegion;

import icircles.decomposition.DecompositionStep;

import org.apache.log4j.Logger;

public class Recomposer {

    static Logger logger = Logger.getLogger(Recomposer.class.getName());

    RecompositionStrategy strategy;

    public Recomposer(RecompositionStrategy recompStrategy) {
        strategy = recompStrategy;
    }

    public Recomposer() {
        strategy = RecompositionStrategy.getStrategy();
    }

    public ArrayList<RecompositionStep> recompose(
            ArrayList<DecompositionStep> decomp_steps) {
        TreeMap<AbstractBasicRegion, AbstractBasicRegion> matched_zones =
                new TreeMap<AbstractBasicRegion, AbstractBasicRegion>(new ABRComparator());
        int n = decomp_steps.size();
        ArrayList<RecompositionStep> result =
                new ArrayList<RecompositionStep>(n);
        for (int i = n - 1; i >= 0; i--) {
            if (i < n - 1) {
                result.add(recompose_step(
                        decomp_steps.get(i), result.get(n - 2 - i), matched_zones));
            } else {
                result.add(recompose_step(
                        decomp_steps.get(i), null, matched_zones));
            }
        }

        logger.info("recomposition begin : ");
        for (RecompositionStep step : result) {
            logger.info("step : " + step.debug());
        }
        logger.info("recomposition end ");

        return result;
    }

    RecompositionStep recompose_step(
            DecompositionStep decomp_step,
            RecompositionStep previous,
            TreeMap<AbstractBasicRegion, AbstractBasicRegion> matched_zones) {
        AbstractDescription from = null;
        AbstractDescription to = null;

        AbstractCurve was_removed = decomp_step.removed();
        ArrayList<RecompData> added_contour_data = new ArrayList<RecompData>();
        if (previous != null) {
            from = previous.to();

            // find the resulting zones in the previous step got to
            ArrayList<AbstractBasicRegion> zones_to_split =
                    new ArrayList<AbstractBasicRegion>();

            TreeMap<AbstractBasicRegion, AbstractBasicRegion> zones_moved_during_decomp = decomp_step.zones_moved();
            Collection<AbstractBasicRegion> zones_after_moved =
                    zones_moved_during_decomp.values();

            HashMap<AbstractBasicRegion, AbstractBasicRegion> matched_inverse =
                    new HashMap<AbstractBasicRegion, AbstractBasicRegion>();
            Iterator<AbstractBasicRegion> moved_it = zones_after_moved.iterator();
            while (moved_it.hasNext()) {
                AbstractBasicRegion moved = moved_it.next();
                AbstractBasicRegion to_split = matched_zones.get(moved);
                //System.out.println("split this zone : "+to_split.debug());
                matched_inverse.put(to_split, moved);
                if (to_split != null) {
                    zones_to_split.add(to_split);
                } else {
                    throw new Error("match not found");
                }
            }
            // Partition zones_to_split
            ArrayList<Cluster> clusters = strategy.make_clusters(zones_to_split);

            for (Cluster c : clusters) {
                logger.info("cluster for recomposition is " + c.debug());
            }

            TreeSet<AbstractBasicRegion> new_zone_set = from.getCopyOfZones();
            TreeSet<AbstractCurve> new_cont_set = from.getCopyOfContours();
            // for each cluster, make a Contour with label
            for (Cluster cluster : clusters) {
                AbstractCurve new_cont = was_removed.clone();
                ArrayList<AbstractBasicRegion> split_zones = new ArrayList<AbstractBasicRegion>();
                ArrayList<AbstractBasicRegion> added_zones = new ArrayList<AbstractBasicRegion>();
                new_cont_set.add(new_cont);
                ArrayList<AbstractBasicRegion> cluster_zones = cluster.zones();
                for (AbstractBasicRegion z : cluster_zones) {
                    split_zones.add(z);
                    AbstractBasicRegion new_zone = z.movedIn(new_cont);
                    new_zone_set.add(new_zone);
                    added_zones.add(new_zone);
                    AbstractBasicRegion decomp_z = matched_inverse.get(z);
                    //System.out.println("zone "+z.debug()+" has matched inverse "+decomp_z.debug());
                    matched_zones.put(decomp_z.movedIn(was_removed), new_zone);
                }
                added_contour_data.add(new RecompData(new_cont, split_zones, added_zones));
            }
            to = new AbstractDescription(new_cont_set, new_zone_set);

        } else {
            from = decomp_step.to()/*.copy()*/;

            // make a new Abstract Description
            TreeSet<AbstractCurve> cs = new TreeSet<AbstractCurve>();
            AbstractBasicRegion outside_zone = AbstractBasicRegion.get(cs);

            ArrayList<AbstractBasicRegion> split_zone = new ArrayList<AbstractBasicRegion>();
            ArrayList<AbstractBasicRegion> added_zone = new ArrayList<AbstractBasicRegion>();
            split_zone.add(outside_zone);
            added_contour_data.add(new RecompData(was_removed, split_zone, added_zone));

            cs.add(was_removed);
            AbstractBasicRegion new_zone = AbstractBasicRegion.get(cs);
            TreeSet<AbstractBasicRegion> new_zones = new TreeSet<AbstractBasicRegion>();
            new_zones.add(new_zone);
            new_zones.add(outside_zone);
            added_zone.add(new_zone);
            to = new AbstractDescription(cs, new_zones);
            matched_zones.put(outside_zone, outside_zone);
            matched_zones.put(new_zone, new_zone);
        }
        RecompositionStep result = new RecompositionStep(from, to, added_contour_data);
        return result;
    }
}
