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

import static org.hamcrest.Matchers.equalTo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.plugin.analysis.phonetic.ptBR.AnalysisMetaphonePlugin;
import org.elasticsearch.test.ESTestCase;
import org.junit.Test;

/**
 * @author anaelcarvalho
 */
public class MetaphoneTokenFilterTests extends ESTestCase {
	
    @Test
    public void testMetaphoneWords() throws Exception {
        Index index = new Index("test", "_na_");
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("index.analysis.filter.myStemmer.type", "br_metaphone")
                .build();

        AnalysisService analysisService = createAnalysisService(index, settings, new AnalysisMetaphonePlugin());

        TokenFilterFactory filterFactory = analysisService.tokenFilter("br_metaphone");

        Tokenizer tokenizer = new KeywordTokenizer();
        
        Map<String,String> words = buildWordList();
        
        Set<String> inputWords = words.keySet();
        for(String word : inputWords) {
            tokenizer.setReader(new StringReader(word));
            TokenStream ts = filterFactory.create(tokenizer);

            CharTermAttribute term1 = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term1.toString(), equalTo(words.get(word)));
            ts.close();
        }
    }
    
    @Test
    public void testMetaphonePhrases() throws Exception {
        Index index = new Index("test", "_na_");
        Settings settings = Settings.builder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .put("index.analysis.analyzer.myAnalyzer.type", "custom")
                .put("index.analysis.analyzer.myAnalyzer.tokenizer", "standard")
                .put("index.analysis.analyzer.myAnalyzer.filter", "br_metaphone")
                .build();

        AnalysisService analysisService = createAnalysisService(index, settings, new AnalysisMetaphonePlugin());

        Analyzer analyzer = analysisService.analyzer("myAnalyzer");
        
        Map<String,List<String>> phrases = buildPhraseList();
        
        for(String phrase : phrases.keySet()) {
	        List<String> outputWords = phrases.get(phrase);
	        
	        TokenStream ts = analyzer.tokenStream("test", phrase);
	
	        CharTermAttribute term1 = ts.addAttribute(CharTermAttribute.class);
	        ts.reset();
	
	        for (String expected : outputWords) {
	            assertThat(ts.incrementToken(), equalTo(true));
	            assertThat(term1.toString(), equalTo(expected));
	        }
	        ts.close();
        
        }
    }

    private Map<String,String> buildWordList() {
    	Map<String,String> testWords = new HashMap<String,String>();
    	testWords.put("DILIGÊNCIA", "DLJNS");
    	testWords.put("REINICIAR", "2NS2");
    	testWords.put("TROPEIRO", "TRPR");
    	testWords.put("CLASSIFIQUEI", "KLSFK");
    	testWords.put("TOPONÍMICO", "TPNMK");
    	testWords.put("FROUXO", "FRX");
    	testWords.put("ARQUIVAR", "ARKV2");
    	testWords.put("AQUARELISTA", "AKRLST");
    	testWords.put("PARTICIPANTE", "PRTSPNT");
    	testWords.put("PROTUBERANTE", "PRTBRNT");
    	testWords.put("BRIGADEIRO-DO-AR", "BRGDR-D-A2");
    	testWords.put("ESTALAGEM", "ESTLJM");
    	testWords.put("ESTREPE", "ESTRP");
    	testWords.put("CRATERA", "KRTR");
    	testWords.put("PAÍS", "PS");
    	testWords.put("CANDEEIRO", "KNDR");
    	testWords.put("INTENDER", "INTND2");
    	testWords.put("CÊ-CEDILHA", "S-SD1");
    	testWords.put("SIMPÓSIO", "SMPZ");
    	testWords.put("RECOMENDAR", "2KMND2");
    	testWords.put("ENCORAJAR", "ENKRJ2");
    	testWords.put("SALVE-SE-QUEM-PUDER", "SV-S-KM-PD2");
    	testWords.put("DESTROÇOS", "DSTRSS");
    	testWords.put("REATIVO", "2TV");
    	testWords.put("ESCURA", "ESKR");
    	testWords.put("BOCA-DE-SINO", "BK-D-SN");
    	testWords.put("APÓLICE", "APLS");
    	testWords.put("HOLOCAUSTO", "OLKST");
    	testWords.put("SUBCONTINENTE", "SBKNTNNT");
    	testWords.put("ADAPTÁVEL", "ADPTV");
    	testWords.put("DIMINUENDO", "DMNND");
    	testWords.put("HIPERTIREOIDISMO", "IPRTRDSM");
    	testWords.put("ESPADA", "ESPD");
    	testWords.put("REBELADO", "2BLD");
    	testWords.put("PREGA", "PRG");
    	testWords.put("CAÇADOR", "KSD2");
    	testWords.put("CONSOLAÇÃO", "KNSLS");
    	testWords.put("DIRETRIZ", "DRTRS");
    	testWords.put("TEMPLO", "TMPL");
    	testWords.put("FANTOCHE", "FNTX");
    	testWords.put("MALTOSE", "MTZ");
    	testWords.put("PRECONCEBIDO", "PRKNSBD");
    	testWords.put("REBORDOSA", "2BRDZ");
    	testWords.put("FERRAGEM", "F2JM");
    	testWords.put("PROCRASTINADO", "PRKRSTND");
    	testWords.put("NEONAZISTA", "NNZST");
    	testWords.put("MEDROSO", "MDRZ");
    	testWords.put("LACAR", "LK2");
    	testWords.put("APELATIVA", "APLTV");
    	testWords.put("CONCEPCAO", "KNSPS");
    	testWords.put("TRABUCO", "TRBK");
    	testWords.put("ÁRVORES", "ARVRS");
    	testWords.put("MEDITAÇÃO", "MDTS");
    	testWords.put("CATEDRÁTICO", "KTDRTK");
    	testWords.put("AGREGAÇÃO", "AGRGS");
    	testWords.put("BIPE", "BP");
    	testWords.put("AGACHAR-SE", "AGX2-S");
    	testWords.put("LIBRIANO", "LBRN");
    	testWords.put("ANCORADOURO", "ANKRDR");
    	testWords.put("DECORAÇÃO", "DKRS");
    	testWords.put("PEQUENOS", "PKNS");
    	testWords.put("ADEQUADO", "ADKD");
    	testWords.put("ZANGÃO", "ZNG");
    	testWords.put("BUQUE", "BK");
    	testWords.put("CONTRATO", "KNTRT");
    	testWords.put("RESSENTIR", "2SNT2");
    	testWords.put("DESPROTEGER", "DSPRTJ2");
    	testWords.put("DISPARADO", "DSPRD");
    	testWords.put("NENUFAR", "NNF2");
    	testWords.put("RENDIMENTO", "2NDMNT");
    	testWords.put("ONÇA-PINTADA", "ONS-PNTD");
    	testWords.put("REPLANTAÇÃO", "2PLNTS");
    	testWords.put("CATORZE", "KTRZ");
    	testWords.put("PINACOTECA", "PNKTK");
    	testWords.put("VACINAR", "VSN2");
    	testWords.put("CALABOUÇO", "KLBS");
    	testWords.put("CONTINUAR", "KNTN2");
    	testWords.put("COALHAR", "K12");
    	testWords.put("TENTADOR", "TNTD2");
    	testWords.put("FAQUEIRO", "FKR");
    	testWords.put("AROMATIZADO", "ARMTZD");
    	testWords.put("DIRIMIR", "DRM2");
    	testWords.put("ABILOLADO", "ABLLD");
    	testWords.put("ANTECEDENTES", "ANTSDNTS");
    	testWords.put("DINAMARQUÊS", "DNMRKS");
    	testWords.put("DEDICATÓRIA", "DDKTR");
    	testWords.put("CHEFE-DE-ESQUADRA", "XF-D-ESKDR");
    	testWords.put("BRIGA", "BRG");
    	testWords.put("BRASILEIRISMO", "BRZLRSM");
    	testWords.put("MARMÓREO", "MRMR");
    	testWords.put("CONTRAPESO", "KNTRPZ");
    	testWords.put("PILÓRICO", "PLRK");
    	testWords.put("GAMBITO", "GMBT");
    	testWords.put("BÓRAX", "BRKS");
    	testWords.put("NEODARWINISMO", "NDRNSM");
    	testWords.put("REGULAR", "2GL2");
    	testWords.put("DESTACAMENTO", "DSTKMNT");
    	testWords.put("HORISTA", "ORST");
    	testWords.put("BENZEDEIRO", "BNZDR");
    	testWords.put("ARMADAS", "ARMDS");
    	testWords.put("MOLÉCULA", "MLKL");
    	testWords.put("RECRUTADOR", "2KRTD2");
    	testWords.put("SAMAMBAIA", "SMMB");
    	testWords.put("INTERCEPÇÃO", "INTRSPS");
    	testWords.put("PERISCÓPIO", "PRSKP");
    	testWords.put("MACHO", "MX");
    	testWords.put("ESPERMA", "ESPRM");
    	testWords.put("SISTEMA", "SSTM");
    	testWords.put("CONSANGÜINIDADE", "KNSNGNDD");
    	testWords.put("LOCAÇÃO", "LKS");
    	testWords.put("GRELHA", "GR1");
    	testWords.put("BURRADA", "B2D");
    	testWords.put("ALFORJE", "AFRJ");
    	testWords.put("CAVALHEIRESCO", "KV1RSK");
    	testWords.put("ENFEITIÇADO", "ENFTSD");
    	testWords.put("INCONSTITUCIONAL", "INKNSTTSN");
    	testWords.put("CHAUVINISTA", "XVNST");
    	testWords.put("CALCANHAR", "KK32");
    	testWords.put("BICHO-DE-SETE-CABEÇAS", "BX-D-ST-KBSS");
    	testWords.put("ELETIVO", "ELTV");
    	testWords.put("RADICADO", "2DKD");
    	testWords.put("DISSOCIACAO", "DSSS");
    	testWords.put("MASCULO", "MSKL");
    	testWords.put("OBTEMPERAR", "OBTMPR2");
    	testWords.put("MANIFESTACAO", "MNFSTS");
    	testWords.put("DESVENTURA", "DSVNTR");
    	testWords.put("PREVENÇÃO", "PRVNS");
    	testWords.put("DILATADO", "DLTD");
    	testWords.put("DELONGA", "DLNG");
    	testWords.put("REMONTAR", "2MNT2");
    	testWords.put("ESPREGUIÇAMENTO", "ESPRGSMNT");
    	testWords.put("POLINÉSIO", "PLNZ");
    	testWords.put("GENITAL", "JNT");
    	testWords.put("DESAPONTAMENTO", "DZPNTMNT");
    	testWords.put("COLONO", "KLN");
    	testWords.put("TRUCO", "TRK");
    	testWords.put("EXTENUADO", "ESTND");
    	testWords.put("HOMÔNIMO", "OMNM");
    	testWords.put("ENGRAÇADO", "ENGRSD");
    	testWords.put("MAGO", "MG");
    	testWords.put("INFERNAR", "INFRN2");
    	testWords.put("MARCAR", "MRK2");
    	testWords.put("REELEITO", "2LT");
    	testWords.put("CÁUSTICO", "KSTK");
    	testWords.put("DESENCAIXE", "DZNKX");
    	testWords.put("FIEL", "F");
    	testWords.put("ALCANÇAR", "AKNS2");
    	testWords.put("MARITICIDA", "MRTSD");
    	testWords.put("AXADREZAR", "AXDRZ2");
    	testWords.put("EFUSÃO", "EFZ");
    	testWords.put("BRITADEIRA", "BRTDR");
    	testWords.put("BALDE", "BD");
    	testWords.put("COMANDANTE", "KMNDNT");
    	testWords.put("AUDITORIA", "ADTR");
    	testWords.put("RESMA", "2SM");
    	testWords.put("EMOLDURADO", "EMDRD");
    	testWords.put("CAMINHÃO", "KM3");
    	testWords.put("REPOSIÇÃO", "2PZS");
    	testWords.put("AGREDIR", "AGRD2");
    	testWords.put("ENSACAR", "ENSK2");
    	testWords.put("ELAS", "ELS");
    	testWords.put("QUÁ-QUÁ-QUÁ", "K-K-K");
    	testWords.put("PÚNICO", "PNK");
    	testWords.put("INSATISFAÇÃO", "INSTSFS");
    	testWords.put("CANÔNICO", "KNNK");
    	testWords.put("OCO", "OK");
    	testWords.put("SUTURAR", "STR2");
    	testWords.put("BONÍSSIMO", "BNSM");
    	testWords.put("HISTORIOGRAFIA", "ISTRGRF");
    	testWords.put("OSSIFICAÇÃO", "OSFKS");
    	testWords.put("EMPILHAMENTO", "EMP1MNT");
    	testWords.put("FICAM", "FKM");
    	testWords.put("EXCRESCÊNCIA", "ESKRSNS");
    	testWords.put("CAVALO-VAPOR", "KVL-VP2");
    	testWords.put("EXEQUÍVEL", "EZKV");
    	testWords.put("PREDICAR", "PRDK2");
    	testWords.put("NUTRICIONAL", "NTRSN");
    	testWords.put("DESIMPEDIDO", "DZMPDD");
    	testWords.put("DISPENSAR", "DSPNS2");
    	testWords.put("INTERINIDADE", "INTRNDD");
    	testWords.put("ARRUACEIRO", "A2SR");
    	testWords.put("COLÔNIA", "KLN");
    	testWords.put("BIRRETÂNGULO", "B2TNGL");
    	testWords.put("REBAIXAMENTO", "2BXMNT");
    	testWords.put("SEMI-RETA", "SM-2T");
    	testWords.put("HUMANIDADE", "UMNDD");
    	testWords.put("SANEÁVEL", "SNV");
    	testWords.put("CARTÃO", "KRT");
    	testWords.put("PREESTABELECER", "PRSTBLS2");
    	testWords.put("EXTRAPOLAÇÃO", "ESTRPLS");
    	testWords.put("ASCENDÊNCIA", "ASNDNS");
    	testWords.put("PRÉ-PRIMÁRIO", "PR-PRMR");
    	testWords.put("CENTELHA", "SNT1");
    	testWords.put("URTICÁRIA", "URTKR");
    	testWords.put("COMIDA", "KMD");
    	testWords.put("MICRONÉSIO", "MKRNZ");
    	testWords.put("CLAMOR", "KLM2");

    	return testWords;
    }
    
    private Map<String,List<String>> buildPhraseList() {
    	Map<String,List<String>> testPhrases = new HashMap<String,List<String>>();
    	List<String> outputTokens = new ArrayList<String>();
    	outputTokens.add("VS");
    	outputTokens.add("J");
    	outputTokens.add("2PR");
    	outputTokens.add("NS");
    	outputTokens.add("O1S");
    	outputTokens.add("DL");
    	outputTokens.add("S");
    	outputTokens.add("ASM");
    	outputTokens.add("D");
    	outputTokens.add("SGN");
    	outputTokens.add("OBLK");
    	outputTokens.add("E");
    	outputTokens.add("DSMLD");
    	testPhrases.put("Você já reparou nos olhos dela? São assim de cigana oblíqua e dissimulada.", outputTokens);
    	
    	return testPhrases;
    }
}
