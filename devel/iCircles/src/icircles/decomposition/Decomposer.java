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
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import icircles.abstractDescription.AbstractDescription;
import icircles.abstractDescription.AbstractCurve;
import icircles.abstractDescription.AbstractBasicRegion;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class Decomposer {

    static Logger logger = Logger.getLogger(Decomposer.class.getName());

    DecompositionStrategy s;
    ArrayList<AbstractCurve> toRemove = new ArrayList<AbstractCurve>(); // some utility data

    public Decomposer(DecompositionStrategy decompStrategy) {
        s = decompStrategy;
    }

    public Decomposer() {
        s = DecompositionStrategy.getStrategy();
    }

    private DecompositionStep take_step(AbstractDescription ad, AbstractCurve c) {
        if (c == null) {
            return null;
        }

        // otherwise, make a new AbstractDescription
        TreeSet<AbstractCurve> contours = ad.getCopyOfContours();
        contours.remove(c);

        Iterator<AbstractBasicRegion> zoneIt = ad.getZoneIterator();
        TreeSet<AbstractBasicRegion> zones = new TreeSet<AbstractBasicRegion>();
        TreeMap<AbstractBasicRegion, AbstractBasicRegion> zones_moved = new TreeMap<AbstractBasicRegion, AbstractBasicRegion>();
        while (zoneIt.hasNext()) {
            AbstractBasicRegion z = zoneIt.next();
            AbstractBasicRegion znew = z.moveOutside(c);
            zones.add(znew);
            if (z != znew) {
                zones_moved.put(z, znew);
            }
        }
        AbstractDescription target_ad = new AbstractDescription(contours, zones);
        DecompositionStep result = new DecompositionStep(
                ad, target_ad, zones_moved, c);
        return result;
    }

    public ArrayList<DecompositionStep> decompose(AbstractDescription ad) {
        if (!ad.getZoneIterator().hasNext()) {
            throw new Error("decompose empty description?");
        }

        ArrayList<DecompositionStep> result = new ArrayList<DecompositionStep>();
        while_loop:
        while (true) {
            s.getContoursToRemove(ad, toRemove);

            if (toRemove.size() == 0) {
                break while_loop;
            }

            for (AbstractCurve c : toRemove) {
                DecompositionStep step = take_step(ad, c);
                if (step == null) {
                    break while_loop;
                }
                result.add(step);
                ad = step.target();
            }
        }

        logger.debug("decomposition begin : ");
        for (DecompositionStep step : result) {
                logger.debug("step : " + step.debug());
        }
        logger.debug("decomposition end ");

        return result;
    }
    /*
    public static void main(String args[])
    {
    Decomposer d = new Decomposer();
    DEB.level = 1;

    System.out.println("example 1: ____________ a b ab");
    ArrayList<DecompositionStep> steplist = d.decompose(
    AbstractDescription.makeForTesting("a b ab"));
    for(DecompositionStep step : steplist)
    System.out.println("step : "+step.debug());

    System.out.println("example 1: ____________ a b ab ac ad de");
    steplist = d.decompose(
    AbstractDescription.makeForTesting("a b ab ac ad de"));
    for(DecompositionStep step : steplist)
    System.out.println("step : "+step.debug());

    System.out.println("example 1: ____________ a(1) b a(2)b");
    // an example with multiple curves with the same label
    CurveLabel a = CurveLabel.get("a");
    CurveLabel b = CurveLabel.get("b");

    TreeSet<AbstractCurve> tsc = new TreeSet<AbstractCurve>();
    TreeSet<AbstractBasicRegion> tsz = new TreeSet<AbstractBasicRegion>();
    AbstractCurve ca1 = new AbstractCurve(a);
    AbstractCurve ca2 = new AbstractCurve(a);
    AbstractCurve cb = new AbstractCurve(b);
    tsz.add(AbstractBasicRegion.get(tsc)); // empty
    tsc.add(ca1);
    tsz.add(AbstractBasicRegion.get(tsc)); // in a(1)
    tsc.clear();
    tsc.add(cb);
    tsz.add(AbstractBasicRegion.get(tsc)); // in b
    tsc.add(ca2);
    tsz.add(AbstractBasicRegion.get(tsc)); // in a(2) and b
    tsc.add(ca1);
    AbstractDescription ad = new AbstractDescription(tsc, tsz);
    steplist = d.decompose(ad);
    for(DecompositionStep step : steplist)
    System.out.println("step : "+step.debug());
    }
     */
}
