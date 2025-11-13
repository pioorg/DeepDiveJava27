/*
 *  Copyright (C) 2026 Piotr Przybył
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.przybyl.ddj27.concurrency.structuredConcurrency;

import java.util.Random;

public class FakeWorker {

    // seed is used here ONLY for demonstration purposes, don't copy and use that in your production code!
    private static final Random rand = new Random(42L);

    public static void sneakySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//       hardWork(d);
    }

    public static long hardWork(long millis) {
        long stop = System.nanoTime() + millis * 1_000_000;
        long mod = -1;
        while (System.nanoTime() < stop) {
//            if (Thread.interrupted()) {
//                throw new RuntimeException("I should have been interrupted exception, sorry!");
//            }
            long a = rand.nextLong(1_000_000_000, 1_000_000_000_000L);
            long b = rand.nextLong(1_000_000, 1_000_000_000);
            mod = a % b;
        }
        return mod;
    }
}
