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
import java.util.TreeSet;

import icircles.abstractDescription.AbstractBasicRegion;
import icircles.abstractDescription.AbstractCurve;
import icircles.abstractDescription.AbstractDescription;

public class DecompositionStrategyPiercing extends DecompositionStrategy {

    void getContoursToRemove(AbstractDescription ad, ArrayList<AbstractCurve> toRemove) {
        toRemove.clear();
        int bestNZ = Integer.MAX_VALUE;
        Iterator<AbstractCurve> acIt = ad.getContourIterator();
        while (acIt.hasNext()) {
            AbstractCurve ac = acIt.next();
            if (isPiercingCurve(ac, ad)) {
                int nz = numZonesInside(ac, ad);
                if (nz < bestNZ) {
                    toRemove.clear();
                    toRemove.add(ac);
                    bestNZ = nz;
                } else if (nz == bestNZ) {
                    toRemove.add(ac);
                }
            }
        }
        if (toRemove.size() == 0) {
            acIt = ad.getContourIterator();
            while (acIt.hasNext()) {
                AbstractCurve ac = acIt.next();
                int nz = numZonesInside(ac, ad);
                if (nz < bestNZ) {
                    toRemove.clear();
                    toRemove.add(ac);
                    bestNZ = nz;
                } else if (nz == bestNZ) {
                    toRemove.add(ac);
                }
            }
        }
    }

    private int numZonesInside(AbstractCurve ac,
            AbstractDescription ad) {
        int nz = 0;

        Iterator<AbstractBasicRegion> abrit = ad.getZoneIterator();
        while (abrit.hasNext()) {
            AbstractBasicRegion abr = abrit.next();
            if (abr.isIn(ac)) {
                nz++;
            }
        }
        return nz;
    }

    private boolean isPiercingCurve(AbstractCurve ac,
            AbstractDescription ad) {
        // every abstract basic region in ad which is in ac
        // must have a corresponding abr which is not in ac
        Iterator<AbstractBasicRegion> abrit = ad.getZoneIterator();
        ArrayList<AbstractBasicRegion> zonesInContour =
                new ArrayList<AbstractBasicRegion>();

        abrLoop:
        while (abrit.hasNext()) {
            AbstractBasicRegion abr = abrit.next();
            if (abr.isIn(ac)) {
                zonesInContour.add(abr);
                // look for a partner zone
                Iterator<AbstractBasicRegion> abrit2 = ad.getZoneIterator();
                while (abrit2.hasNext()) {
                    AbstractBasicRegion abr2 = abrit2.next();
                    if (abr.getStraddledContour(abr2) == ac) {
                        continue abrLoop;
                    }
                }
                // never found a partner zone
                return false;
            }
        }
        // check that the zones in C form a cluster - we need 2^n zones
        int power = powerOfTwo(zonesInContour.size());
        if (power < 0) {
            return false;
        }

        // find the smallest zone (one in fewest contours)
        int zoneSize = Integer.MAX_VALUE;
        AbstractBasicRegion smallestZone = null;
        abrit = zonesInContour.iterator();
        while (abrit.hasNext()) {
            AbstractBasicRegion abr = abrit.next();
            int numCs = abr.getNumContours();
            if (numCs < zoneSize) {
                zoneSize = numCs;
                smallestZone = abr;
            }
        }
        // every other zone in ac must be a superset of that zone
        abrit = zonesInContour.iterator();
        while (abrit.hasNext()) {
            AbstractBasicRegion abr = abrit.next();
            Iterator<AbstractCurve> acIt = smallestZone.getContourIterator();
            while (acIt.hasNext()) {
                AbstractCurve ac2 = acIt.next();
                if (!abr.isIn(ac2)) {
                    return false;
                }
            }
        }
        // We have 2^n zones which are all supersets of smallestZone.
        // Check that they use exactly n contours from smallestZone.
        TreeSet<AbstractCurve> addedContours = new TreeSet<AbstractCurve>();
        abrit = zonesInContour.iterator();
        while (abrit.hasNext()) {
            AbstractBasicRegion abr = abrit.next();
            Iterator<AbstractCurve> acIt = abr.getContourIterator();
            while (acIt.hasNext()) {
                AbstractCurve ac2 = acIt.next();
                if (!smallestZone.isIn(ac2)) {
                    addedContours.add(ac2);
                    if (addedContours.size() > power) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int powerOfTwo(int n) // return result where  n = 2^(result)
    {
        if (n <= 0) {
            return -1;
        }
        int result = 0;
        while (n % 2 == 0) {
            result++;
            n /= 2;
        }
        if (n != 1) {
            return -1;
        } else {
            return result;
        }
    }
}
