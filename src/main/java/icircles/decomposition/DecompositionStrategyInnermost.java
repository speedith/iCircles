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

import icircles.abstractDescription.AbstractDescription;
import icircles.abstractDescription.AbstractCurve;
import icircles.abstractDescription.AbstractBasicRegion;

import org.apache.log4j.Logger;

public class DecompositionStrategyInnermost extends DecompositionStrategy {

    static Logger logger = Logger.getLogger(DecompositionStrategyInnermost.class.getName());

    void getContoursToRemove(AbstractDescription ad, ArrayList<AbstractCurve> toRemove) {
        toRemove.clear();
        logger.debug("recomposition stratgey is innermost");

        // an innermost abstract contour has the fewest abstract basic regions inside
        int best_num_zones = ad.getNumZones() + 1;
        AbstractCurve best_contour = null;
        Iterator<AbstractCurve> c_it = ad.getContourIterator();
        while (c_it.hasNext()) {
            AbstractCurve c = c_it.next();
            int num_zones = 0;
            Iterator<AbstractBasicRegion> z_it = ad.getZoneIterator();
            while (z_it.hasNext()) {
                AbstractBasicRegion z = z_it.next();
                if (z.isIn(c)) {
                    num_zones++;
                }
            }
            if (num_zones < best_num_zones) {
                best_num_zones = num_zones;
                best_contour = c;
            }
        }
        toRemove.add(best_contour);
    }
}
