/**
 * Copyright (C) 2015 T2K-Team, Data and Web Science Group, University of
							Mannheim (t2k@dwslab.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dwslab.T2K.units;

/**
 * @author petar
 *
 */
public class UnitParser {

    UnitManager mgr;

    public UnitParser() {
        mgr = new UnitManager();
    }

    public SubUnit parseUnit(String text) {
        //Long startTime = System.nanoTime();
        text = text.replaceAll(" ", "");
        try {
            SubUnit sub = mgr.parseUnit(text);
            if (sub != null && !sub.getName().equals("normalized number")) {
                return sub;
            }
         //Long time = System.nanoTime()-startTime;   
        // System.out.println(text  + " parsing: " + time);
        } catch (Exception e) {
        }
        return null;
    }
}
