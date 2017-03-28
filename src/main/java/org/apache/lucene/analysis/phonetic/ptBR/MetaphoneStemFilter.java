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

package org.apache.lucene.analysis.phonetic.ptBR;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

/**
 * @author anaelcarvalho
 */
public final class MetaphoneStemFilter extends TokenFilter {
	protected boolean inject = true;
	protected State save = null;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final PositionIncrementAttribute posAtt = addAttribute(PositionIncrementAttribute.class);

	public MetaphoneStemFilter(TokenStream input, boolean inject) {
		super(input);
		this.inject = inject;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if( save != null ) {
			restoreState(save);
			save = null;
			return true;
		}

		if (input.incrementToken()) {
			if (termAtt.length() == 0) return true;

			String term = termAtt.toString();
			String encoded = null;
			try {
				encoded = Metaphone.encode(term);
			} catch (Exception ignored) {} // use original term

			if (encoded == null || encoded.isEmpty()) return true;

			if (inject) {
				int origOffset = posAtt.getPositionIncrement();
				posAtt.setPositionIncrement(0);
				save = captureState();
				posAtt.setPositionIncrement(origOffset);
				termAtt.setEmpty().append(encoded);
			} else {
				termAtt.setEmpty().append(encoded);
			}
			return true;
		} else {
			return false;
		}
	}
}
