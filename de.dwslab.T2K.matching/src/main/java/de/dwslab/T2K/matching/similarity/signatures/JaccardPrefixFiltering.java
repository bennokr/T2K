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
package de.dwslab.T2K.matching.similarity.signatures;

import java.util.Collection;

import de.dwslab.T2K.similarity.functions.SimilarityFunction;

public class JaccardPrefixFiltering<T extends Comparable<T>> extends PrefixFiltering<T> {

    @Override
    protected int getPrefixLength(Collection<T> signature, double threshold,
            SimilarityFunction<T> sim) {
        return signature.size() - (int)Math.ceil(threshold * signature.size()) + 1;
    }

}
