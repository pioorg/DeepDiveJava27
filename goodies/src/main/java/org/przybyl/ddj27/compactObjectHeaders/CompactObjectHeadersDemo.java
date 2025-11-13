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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.przybyl.ddj27.compactObjectHeaders;

/*
 Java 27, same program and heap, traditional headers vs compact headers:

   java -Xms512m -Xmx512m -XX:+UnlockExperimentalVMOptions \
     -XX:+UseEpsilonGC -XX:-UseCompactObjectHeaders \
     -XX:StartFlightRecording=filename=coh-off.jfr,settings=profile,dumponexit=true \
     CompactObjectHeadersDemo.java

   java -Xms512m -Xmx512m -XX:+UnlockExperimentalVMOptions \
     -XX:+UseEpsilonGC -XX:+UseCompactObjectHeaders \
     -XX:StartFlightRecording=filename=coh-on.jfr,settings=profile,dumponexit=true \
     CompactObjectHeadersDemo.java

 Epsilon never reclaims memory, so the heap increase directly shows how much
 the object graph allocated; no explicit GC or settling delay is involved.

 Tiny has no fields. With the usual 8-byte alignment, each Tiny is normally
 16 bytes, but only 8 bytes with compact object headers. The reference array
 costs the same in both runs, so expect roughly 190 MiB vs 114 MiB for the
 retained graph below: about 75 MiB saved.
 */

import module java.base;
import com.sun.management.HotSpotDiagnosticMXBean;

import java.lang.management.ManagementFactory;

@SuppressWarnings("all")
public class CompactObjectHeadersDemo {

    void main() {
        var vm = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        var compact = vm.getVMOption("UseCompactObjectHeaders").getValue();

        System.out.printf("Compact object headers: %s%n", compact);
        var before = usedHeap();

        int count = 10_000_000;
        final class Tiny {}
        var objects = new Tiny[count];
        for (int i = 0; i < count; i++) {
            objects[i] = new Tiny();
        }

        var retained = usedHeap() - before;
        Reference.reachabilityFence(objects);

        System.out.printf("%,d tiny objects retained: %.1f MiB (%.1f bytes/object incl. array reference)%n",
            count, retained / 1_048_576.0, (double) retained / count);
    }

    long usedHeap() {
        var runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
