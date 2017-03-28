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

/*
 * This is an implementation of the Metaphone algorithm for Brazilian Portuguese  
 * inspired in the sources below:
 * 
 *  Lawrence Philips. 1990. Hanging on the Metaphone. Computer Language Magazine. 7, 12 (December 1990), 38-??.
 *  Lawrence Philips. 2000. The double metaphone search algorithm. C/C++ Users Journal. 18, 6 (June 2000), 38-43.
 *  Stephen Woodbridge. 2001. PHP DoubleMetaPhone. http://swoodbridge.com/DoubleMetaPhone/
 *  Geoff Caplan. 2002. DoubleMetaphone Functional. http://swoodbridge.com/DoubleMetaPhone/
 *  Apache Commons Codec. 2003. DoubleMetaphone. https://commons.apache.org/proper/commons-codec/
 *  Israel J. Sustaita. 2005. Spanish Metaphone. http://www.geocities.com/isloera/
 *  Prefeitura Municipal de Várzea Paulista. 2008. Portuguese Metaphone. http://informatica.varzeapaulista.sp.gov.br/metaphone/
 *  Ronie Uliana. 2009. MTFN – MeTaPHoNe. https://github.com/ruliana/MTFN/
 *  
 * It also uses the following works for further improvements:
 * 
 * Chbane, D. 1994. Desenvolvimento de sistema para Conversão de Textos em Fonemas no Idioma Português. 
 * Dissertação para o título de Mestre em Engenharia - Escola Politécnica da Universidade de São Paulo. São Paulo-SP, Brazil.
 * 
 * Braga, D.; Coelho, L.; Resende Jr., F. 2006. A Rule-Based Grapheme-to-Phone Converter for TTS Systems in European Portuguese, 
 * VI Int. Telecommunications Symposium, pp. 328-333. Fortaleza-CE, Brazil. 
 * 
 */
package org.apache.lucene.analysis.phonetic.ptBR;

import java.util.Locale;


/**
 * Metaphone for Brazilian Portuguese.
 * 
 * @author anaelcarvalho
 */
public class Metaphone {
	public static String encode(String word) throws IllegalArgumentException {
		if(word == null || word.isEmpty()) {
			throw new IllegalArgumentException("Word cannot be null or empty");
		}
		String lowerCaseWord = word.toLowerCase(new Locale("br")).trim();
		String[] words = lowerCaseWord.split("-");
		StringBuffer sb = new StringBuffer(lowerCaseWord.length());

		if(words.length > 0) {
			for(int i=0; i<words.length; i++) {
				if(!words[i].isEmpty()) {
					char[] wordCharArray = words[i].toCharArray();
					char nextWordInitialLetter = ' ';
					if(i < words.length-1) {
						nextWordInitialLetter = words[i+1].toCharArray()[0];
					}
					char[] transcription = getTranscription(wordCharArray, nextWordInitialLetter);
					sb.append((new String(transcription)).trim());
					if(i < words.length-1) {
						sb.append("-");
					}
				}
			}
		} else {
			throw new IllegalArgumentException("Word cannot be null or empty");
		}
		return sb.toString();
	}

	private static char[] getTranscription(char[] word, char nextWordInitialLetter) {
		int lastWordIndex = word.length-1;
		int transcriptionPosition = 0;
		char[] transcription = new char[word.length*2];
		boolean hasNextChar = false, hasNextNextChar = false, hasPreviousChar = false, hasPreviousPreviousChar = false;
		char nextChar = ' ', nextNextChar = ' ', previousChar = ' ', previousPreviousChar = ' ';
		boolean skipIncrement = false;
		
		for(int i=0; i<word.length; i++) {
			skipIncrement = false;
			if(i+2 <= lastWordIndex) {
				hasNextChar = true; 
				nextChar = word[i+1];
				hasNextNextChar = true;
				nextNextChar = word[i+2];
			} else if(i+1 <= lastWordIndex) {
				hasNextChar = true; 
				nextChar = word[i+1];
				hasNextNextChar = false;
			} else {
				hasNextChar = false;
				hasNextNextChar = false;
			}
			if(i-2 >= 0) {
				hasPreviousChar = true;
				previousChar = word[i-1];
				hasPreviousPreviousChar = true;
				previousPreviousChar = word[i-2];
			} else if(i-1 >= 0) {
				hasPreviousChar = true;
				previousChar = word[i-1];
				hasPreviousPreviousChar = false;
			} else {
				hasPreviousChar = false;
				hasPreviousPreviousChar = false;
			}
			switch(word[i]) {
				case 'a': 
				case 'á': 
				case 'ã':
				case 'à': 
				case 'â': {
					if(!hasPreviousChar || (!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'h')) {
						transcription[transcriptionPosition] = 'A';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'b': {
					transcription[transcriptionPosition] = 'B';
					if(hasNextChar && nextChar == 'b') {
						i++;
					}
					break;
				}
				case 'c': {
					if(hasNextChar && (nextChar == 'e' || nextChar == 'ê' || nextChar == 'é'
							|| nextChar == 'i' || nextChar == 'í' || nextChar == 'y')) {
						transcription[transcriptionPosition] = 'S';
						i++;
					} else if(hasNextNextChar && nextChar == 'a' && nextNextChar == 'o') {
						transcription[transcriptionPosition] = 'S'; //typo correction
						i+=2;
					} else if(hasNextChar && nextChar == 'h') {
						transcription[transcriptionPosition] = 'X';
						i++;
					} else if(hasNextChar && (nextChar == 'k' || nextChar == 'q')) {
						transcription[transcriptionPosition] = 'K';
						i++;
					} else if(hasNextChar && nextChar == 'c') {
						transcription[transcriptionPosition++] = 'K';
						transcription[transcriptionPosition] = 'S';
						i++;
					} else {
						transcription[transcriptionPosition] = 'K';
					}
					break;
				}
				case 'ç': {
					transcription[transcriptionPosition] = 'S';
					break;
				}
				case 'd': {
					transcription[transcriptionPosition] = 'D';
					if(hasNextChar && nextChar == 'd') {
						i++;
					}
					break;
				}
				case 'e': 
				case 'é': 
				case 'ê': {
					if(!hasPreviousChar || (!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'h')) {
						transcription[transcriptionPosition] = 'E';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'f': {
					transcription[transcriptionPosition] = 'F';
					if(hasNextChar && nextChar == 'f') {
						i++;
					}
					break;
				}
				case 'g': {
					if(hasNextNextChar && (nextChar == 'u') && (nextNextChar == 'e' || nextNextChar == 'é'
							|| nextNextChar == 'ê' || nextNextChar == 'i' || nextNextChar == 'í' || nextNextChar == 'y')) {
						transcription[transcriptionPosition] = 'G';
						i+=2;
					} else if(hasNextChar && (nextChar == 'e' || nextChar == 'é' || nextChar == 'ê' || 
							nextChar == 'i' || nextChar == 'í' || nextChar == 'y')) {
						transcription[transcriptionPosition] = 'J';
						i++;
					} else if(hasNextChar && nextChar == 'g') {
						transcription[transcriptionPosition] = 'G';
						i++;
					} else {
						transcription[transcriptionPosition] = 'G';
					}
					break;
				}
				case 'h': {
					//unpronounced; do nothing
					skipIncrement = true;
					break;
				}
				case 'i': 
				case 'í': 
				case 'y': {
					if(!hasPreviousChar || (!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'h')) {
						transcription[transcriptionPosition] = 'I';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'j': {
					transcription[transcriptionPosition] = 'J';
					break;
				}
				case 'k': {
					transcription[transcriptionPosition] = 'K';
					if(hasNextChar && nextChar == 'k') {
						i++;
					}
					break;
				}
				case 'l': {
					if(hasNextChar && nextChar == 'h') {
						transcription[transcriptionPosition] = '1';
						i++;
					} else if(hasNextChar && (isVowel(nextChar))) {
						transcription[transcriptionPosition] = 'L';
					} else {
						//ignore; treat as a vowel
						skipIncrement = true;
					}
					break;
				}
				case 'm': {
					transcription[transcriptionPosition] = 'M';
					if(hasNextChar && nextChar == 'm') {
						i++;
					}
					break;
				}
				case 'n': {
					if(hasNextChar && nextChar == 'h') {
						transcription[transcriptionPosition] = '3';
						i++;
					} else if(hasNextChar && nextChar == 'n') {
						transcription[transcriptionPosition] = 'N';
						i++;
					} else if (!hasNextChar){
						transcription[transcriptionPosition] = 'M';
					} else {
						transcription[transcriptionPosition] = 'N';
					}
					break;
				}
				case 'o': 
				case 'ó': 
				case 'ô': 
				case 'õ': {
					if(!hasPreviousChar || (!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'h')) {
						transcription[transcriptionPosition] = 'O';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'p': {
					if(hasNextChar && nextChar == 'h') {
						transcription[transcriptionPosition] = 'F';
						i++;
					} else if(hasNextChar && nextChar == 'p') {
						transcription[transcriptionPosition] = 'P';
						i++;
					} else {
						transcription[transcriptionPosition] = 'P';
					}
					break;
				}
				case 'q': {
					if(hasNextNextChar && nextChar == 'u' 
							&& (nextNextChar == 'e' || nextNextChar == 'ê' || nextNextChar == 'é'
							|| nextNextChar == 'i' || nextNextChar == 'í' || nextNextChar == 'y')) {
						transcription[transcriptionPosition] = 'K';
						i+=2;
					} else {
						transcription[transcriptionPosition] = 'K';
					}
					break;
				}
				case 'r': {
					if(!hasPreviousChar) {
						transcription[transcriptionPosition] = '2';
					} else if(hasNextChar && nextChar == 'r') {
						transcription[transcriptionPosition] = '2';
						i++;
					} else if(!hasNextChar) {
						transcription[transcriptionPosition] = '2';
					} else {
						transcription[transcriptionPosition] = 'R';
					}
					break;
				}
				case 's': {
					if((hasNextNextChar && nextChar == 'c' && (nextNextChar == 'e' || nextNextChar == 'ê' || nextNextChar == 'é' 
							|| nextNextChar == 'i' || nextNextChar == 'í' || nextNextChar == 'y'))
							|| (hasNextChar && (nextChar == 'ç' || nextChar == 's'))) {
						transcription[transcriptionPosition] = 'S';
						i+=2;
					} else if(((hasPreviousChar && isVowel(previousChar))
							|| (hasPreviousPreviousChar && previousPreviousChar == 'e' && previousChar == 'x'))
							&& hasNextChar && isVowel(nextChar)) {
						transcription[transcriptionPosition] = 'Z';
					} else if(hasNextChar && nextChar == 'h') {
						transcription[transcriptionPosition] = 'X';
						i++;
					} else {
						transcription[transcriptionPosition] = 'S';
					}
					break;
				}
				case 't': {
					if(hasNextChar && (nextChar == 'h' || nextChar == 't')) {
						transcription[transcriptionPosition] = 'T';
						i++;
					} else {
						transcription[transcriptionPosition] = 'T';
					}
					break;
				}
				case 'u': 
				case 'ú': 
				case 'ü': {
					if(!hasPreviousChar || (!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'h')) {
						transcription[transcriptionPosition] = 'U';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'v': {
					transcription[transcriptionPosition] = 'V';
					if(hasNextChar && nextChar == 'v') {
						i++;
					}
					break;
				}
				case 'w': {
					//preferred to go with the english rule; possible improvement point -
					//detect words with germanic origin to apply a different rule or go
					//with a double metaphone approach
					if(!hasPreviousChar) {
						transcription[transcriptionPosition] = 'U';
					} else {
						skipIncrement = true;
					}
					break;
				}
				case 'x': {
					if((i == 3) && word[i-3] == 'i' && previousPreviousChar == 'n' 
							&& (previousChar == 'e' || previousChar == 'é' || previousChar == 'ê')
							&& hasNextChar && isVowel(nextChar)){
						transcription[transcriptionPosition] = 'Z';
						i++;
					} else if(!hasPreviousPreviousChar && hasPreviousChar 
							&& (previousChar == 'e' || previousChar == 'é' || previousChar == 'ê') 
							&& hasNextChar && isVowel(nextChar)) {
						transcription[transcriptionPosition] = 'Z';
						i++;
					} else if(!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'e' 
							&& !hasNextChar && isVowel(nextWordInitialLetter)) {
						transcription[transcriptionPosition] = 'Z'; //ex prefix followed by vowel
					} else if(!hasPreviousPreviousChar && hasPreviousChar && previousChar == 'e' && !hasNextChar) {
						transcription[transcriptionPosition] = 'S'; //ex prefix followed by consonant
					} else if((i >= 4) && word[i-4] == 't' && word[i-3] == 'r' && previousPreviousChar == 'o' && previousChar == 'u') {
						transcription[transcriptionPosition] = 'S';
					} else if((i >= 3) && word[i-3] == 'p' && previousPreviousChar == 'r' && 
							(previousChar == 'a' || previousChar == 'á' || previousChar == 'â'
							|| previousChar == 'o' || previousChar == 'ó' || previousChar == 'ô')
							&& hasNextNextChar && nextChar == 'i' && nextNextChar == 'm') {
						transcription[transcriptionPosition] = 'S';
						i++;
					} else if(hasPreviousPreviousChar 
							&& previousPreviousChar == 'm' && (previousChar == 'a' || previousChar == 'á' || previousChar == 'â'
							|| previousChar == 'o' || previousChar == 'ó' || previousChar == 'ô')
							&& hasNextNextChar && nextChar == 'i' && nextNextChar == 'm') {
						transcription[transcriptionPosition] = 'S';
						i++;
					} else if(hasPreviousPreviousChar && previousPreviousChar == 'a' && previousChar == 'u' && hasNextNextChar 
							&& (nextChar == 'i' || nextChar == 'í' || nextChar == 'y') && nextNextChar == 'l') {
						transcription[transcriptionPosition] = 'S';
						i++;
					} else if((i >= 3) && word[i-3] == 'f' && previousPreviousChar == 'l' 
							&& (previousChar == 'e' || previousChar == 'é' || previousChar == 'ê'
							|| previousChar == 'u' || previousChar == 'ú')) {
						transcription[transcriptionPosition++] = 'K';
						transcription[transcriptionPosition] = 'S';
					} else if(hasPreviousPreviousChar 
							&& ((previousPreviousChar == 'n' && (previousChar == 'e' || previousChar == 'é' || previousChar == 'ê')) 
									|| (previousPreviousChar == 'f' && (previousChar == 'i' || previousChar == 'í' || previousChar == 'y'))
									|| (previousPreviousChar == 's' && (previousChar == 'e' || previousChar == 'é' || previousChar == 'ê')))) {
						transcription[transcriptionPosition++] = 'K';
						transcription[transcriptionPosition] = 'S';
					}else if(hasNextNextChar 
							&& nextChar == 'c' && (nextNextChar == 'e' || nextNextChar == 'é' || nextNextChar == 'ê'
							|| nextNextChar == 'i' || nextNextChar == 'í' || nextNextChar == 'y')) {
						transcription[transcriptionPosition] = 'S';
						i+=2;
					} else if(!hasNextChar) {
						transcription[transcriptionPosition++] = 'K';
						transcription[transcriptionPosition] = 'S';
					} else if(hasNextChar && !isVowel(nextChar)) {
						transcription[transcriptionPosition] = 'S';
					} else {
						transcription[transcriptionPosition] = 'X';
					}
					break;
				}
				case 'z': {
					if(!hasNextChar || (hasNextChar && !isVowel(nextChar))) {
						transcription[transcriptionPosition] = 'S';
					} else if(hasNextChar && nextChar == 'z') {
						transcription[transcriptionPosition] = 'Z';
						i++;
					} else {
						transcription[transcriptionPosition] = 'Z';
					}
					break;
				}
				default: {
					skipIncrement = true;
					break; //unrecognized; do nothing
				}
			}
			if(!skipIncrement) transcriptionPosition++;
		}
		return transcription;
	}

	private static boolean isVowel(char letter) {
		if(letter == 'a' ||  letter == 'e' || letter == 'i' || letter ==  'o'
				|| letter == 'u' || letter == 'ã' || letter == 'õ' || letter == 'ü'
				|| letter == 'á' ||  letter == 'é' || letter == 'í' || letter ==  'ó'
				|| letter == 'ú' || letter == 'â' || letter == 'ê' || letter == 'ô' || letter == 'à' || letter == 'y')
			return true;
		else
			return false;
	}
}
