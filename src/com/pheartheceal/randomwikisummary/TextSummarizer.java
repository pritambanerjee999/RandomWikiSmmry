package com.pheartheceal.randomwikisummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TextSummarizer {
	   public static final List<String> stopWords = Arrays.asList("a","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","ain’t","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren’t","around","as","aside","ask","asking","associated","at","available","away","awfully","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c’mon","c’s","came","can","can’t","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn’t","course","currently","definitely","described","despite","did","didn’t","different","do","does","doesn’t","doing","don’t","done","down","downwards","during","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","had","hadn’t","happens","hardly","has","hasn’t","have","haven’t","having","he","he’s","hello","help","hence","her","here","here’s","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i", "i’d","i’ll","i’m","i’ve","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isn’t","it","it’d","it’ll","it’s","its","itself","just","keep","keeps","kept","know","knows","known","last","lately","later","latter","latterly","least","less","lest","let","let’s","like","liked","likely","little","look","looking","looks","ltd","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","que","quite","qv","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","shouldn’t","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t’s","take","taken","tell","tends","th","than","thank","thanks","thanx","that","that’s","thats","the","their","theirs","them","themselves","then","thence","there","there’s","thereafter","thereby","therefore","therein","theres","thereupon","these","they","they’d","they’ll","they’re","they’ve","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","value","various","very","via","viz","vs","want","wants","was","wasn’t","way","we","we’d","we’ll","we’re","we’ve","welcome","well","went","were","weren’t","what","what’s","whatever","when","whence","whenever","where","where’s","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","who’s","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","won’t","wonder","would","would","wouldn’t","yes","yet","you","you’d","you’ll","you’re","you’ve","your","yours","yourself","yourselves", "zero");
	   public static final int STEM_SIZE = 5;
	   public static final int summarySize = 4;
	   
	   public static String getSummary(String originalText) {
		   
		   originalText = originalText.replaceAll("\\(.*?\\)", "").replaceAll("\\[.*?\\]", "");
		   
		   HashMap<String, Integer> contentWordFreqs = getWordCounts(originalText);
		   
		   ArrayList<String> contentWordsSortByFreq = sortByFreqThenDropFreq(contentWordFreqs);
		   
		   
		   String[] sentences = getSentences(originalText);
		   
		   HashMap<String, Integer> rankedSentences = new HashMap<String, Integer>();
		   int terms = contentWordFreqs.size();
		   
		   int score;
		   for (String sent : sentences) {
			   score = getSentScore(sent, terms, contentWordFreqs);
			   rankedSentences.put(sent, score);
		   }
		   
		   ArrayList<String> sortedSentences = sortByFreqThenDropFreq(rankedSentences);
		   
		   
		   // always take first sentence from wiki article
		   sortedSentences.remove(sentences[0]);
		   List<String> topSentences = ((List<String>) sortedSentences).subList(0, Math.min(summarySize-1, sortedSentences.size()));
		   topSentences.add(0, sentences[0]);
		   
		   String summary = "";
		   
		   int len = 0;
		   for (String sentence : sentences) {
			   
			   if (topSentences.contains(sentence)) {
				   summary += " " + sentence + " ";
				   len += 1;
			   }
			   
			   if (len == summarySize) {
				   break;
			   }
		   }
		   
		   return summary;
	   }

	private static int getSentScore(String sent, int terms,
			HashMap<String, Integer> contentWordFreqs) {
		int score = 0;
		
		String clnText = cleanText(sent);
		
		String[] words = clnText.split("\\s+");
		
		for (String word : words) {
			if (stopWords.contains(word) == false) {
				word = word.substring(0, Math.min(STEM_SIZE, word.length()));
				if (contentWordFreqs.get(word) != null) {
					score += contentWordFreqs.get(word);
				}
			}
		}
		
		return score;
	}
	
	
	private static String cleanText(String txt) {
		return txt.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
	}

	private static String[] getSentences(String originalText) {
		return originalText.split("(?<=[.!?])\\s+(?=[A-Z])");
	}

	private static ArrayList<String> sortByFreqThenDropFreq(final HashMap<String, Integer> contentWordFreqs) {

		// remember, larger goes first

		List<String> list = new ArrayList<String>(contentWordFreqs.keySet());
		Collections.sort(list, new Comparator<String>() {
		    @Override
		    public int compare(String s1, String s2) {
		        Integer popularity1 = contentWordFreqs.get(s1);
		        Integer popularity2 = contentWordFreqs.get(s2);
		        return popularity1.compareTo(popularity2);
		    }
		});
	    
	    Collections.reverse(list);
	    return (ArrayList<String>) list;
	}

	private static HashMap<String, Integer> getWordCounts(String originalText) {
		
		HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
		
		String clnTxt = cleanText(originalText);
		String[] wordList = clnTxt.split("\\s+");
		
		for (String word : wordList) {
			if (stopWords.contains(word)) {
				continue;
			}
			word = word.substring(0, Math.min(STEM_SIZE, word.length()));
			
			if (wordCount.get(word) == null) {
				wordCount.put(word, 1);
			}
			else {
				
				wordCount.put(word, wordCount.get(word)+1);
			}
		}
		
		return wordCount;
		
	}
}
