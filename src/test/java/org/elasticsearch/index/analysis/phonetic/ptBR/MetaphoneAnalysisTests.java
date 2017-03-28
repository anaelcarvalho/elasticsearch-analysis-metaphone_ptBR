/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.elasticsearch.index.analysis.phonetic.ptBR;

import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.phonetic.ptBR.MetaphoneTokenFilterFactory;
import org.elasticsearch.plugin.analysis.phonetic.ptBR.AnalysisMetaphonePlugin;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * @author anaelcarvalho
 */
public class MetaphoneAnalysisTests extends ESTestCase {

    @Test
    public void testDefaultsRSLPAnalysis() throws IOException {
        AnalysisService analysisService = createAnalysisService(new Index("test", "_na_"), Settings.EMPTY, new AnalysisMetaphonePlugin());

        TokenFilterFactory tokenizerFactory = analysisService.tokenFilter("br_metaphone");

        MatcherAssert.assertThat(tokenizerFactory, instanceOf(MetaphoneTokenFilterFactory.class));
    }
}
