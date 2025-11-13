/*
 *  Copyright (C) 2023 Piotr Przybył
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
package org.przybyl.ddj27.concurrency.scopedValues;


public class WeHaveToGoDeeper {

    static final ScopedValue<String> MESSAGE = ScopedValue.newInstance();

    void spin() {
        IO.print(MESSAGE.get() + " ");
    }

    void unspin() {
        IO.println();
        IO.print(MESSAGE.get() + " ");
    }

    void main() {
        ScopedValue.where(MESSAGE, "we").run(() -> {
            spin();
            ScopedValue.where(MESSAGE, "have").run(() -> {
                spin();
                ScopedValue.where(MESSAGE, "to").run(() -> {
                    spin();
                    ScopedValue.where(MESSAGE, "go").run(() -> {
                        spin();
                        ScopedValue.where(MESSAGE, "deeper").run(this::spin);
                        unspin();
                    });
                });
            });
        });
    }
}
