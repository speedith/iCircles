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

import icircles.abstractDescription.AbstractDescription;
import icircles.abstractDescription.AbstractCurve;

public abstract class DecompositionStrategy {

    public static final int SORT_ORDER = 0;
    public static final int SORT_ORDER_REV = 1;
    public static final int INNERMOST = 2;
    public static final int PIERCEDFIRST = 3;
    private static String[] names = {"SORT_ORDER", "SORT_ORDER_REV", "INNERMOST", "PIERCEDFIRST"};
    private static String[] nice_names = {
        "decompose in alphabetic order",
        "decompose in reverse alphabetic order",
        "decompose using fewest-zone contours first",
        "decompose using piercing curves first"};
    public static int strategy = PIERCEDFIRST;

    abstract void getContoursToRemove(AbstractDescription ad, ArrayList<AbstractCurve> toRemove);

    public static DecompositionStrategy getStrategy() {
        return getStrategy(strategy);
    }

    public static DecompositionStrategy getStrategy(
            int decompStrategy) {
        if (decompStrategy == SORT_ORDER) {
            return new DecompositionStrategyUseSortOrder(true);
        } else if (decompStrategy == SORT_ORDER_REV) {
            return new DecompositionStrategyUseSortOrder(false);
        } else if (decompStrategy == INNERMOST) {
            return new DecompositionStrategyInnermost();
        } else if (decompStrategy == PIERCEDFIRST) {
            return new DecompositionStrategyPiercing();
        } else {
            throw new Error("unrecognised decomposition strategy");
        }
    }

    public static String text_for(int decompStrategy) {
        // Unecessary assertion as Java will throw ArrayIndexOutOfBoundsException
        // which will also provide more information than an assertion failure
        // assert (decompStrategy >= 0
        //        && decompStrategy < names.length) //  "out of bounds"
        return names[decompStrategy];
    }

    public static String[] getDecompStrings() {
        return nice_names;
    }
}
