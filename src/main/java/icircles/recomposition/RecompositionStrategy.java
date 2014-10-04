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

public abstract class RecompositionStrategy {

    public static final int RECOMPOSE_NESTED = 0;
    public static final int RECOMPOSE_SINGLY_PIERCED = 1;
    public static final int RECOMPOSE_DOUBLY_PIERCED = 2;

    public static RecompositionStrategy getStrategy() {
        return new RecompositionStrategyDoublyPierced();
    }

    public static RecompositionStrategy getStrategy(int strategy) {
        if (strategy == RECOMPOSE_NESTED) {
            return new RecompositionStrategyNested();
        } else if (strategy == RECOMPOSE_SINGLY_PIERCED) {
            return new RecompositionStrategySinglyPierced();
        } else {
            return new RecompositionStrategyDoublyPierced();
        }
    }
    private static String[] names = {
        "RECOMPOSE_NESTED",
        "RECOMPOSE_SINGLY_PIERCED",
        "RECOMPOSE_DOUBLY_PIERCED"
    };
    private static String[] nice_names = {
        "recompose using zero-piercing (nesting)",
        "recompose using single piercings",
        "recompose using double piercings"
    };

    public static String text_for(int recompStrategy) {
        // Unnecessary assert
        // assert (recompStrategy >= 0
        //        && recompStrategy < names.length) : "out of bounds";
        return names[recompStrategy];
    }

    public static String[] getRecompStrings() {
        return nice_names;
    }

    public abstract ArrayList<Cluster> make_clusters(
            ArrayList<AbstractBasicRegion> zones_to_split);
}
