package edu.ucdavis.cacheca;

import java.net.URL;
import java.util.ArrayList;

import cn.yyx.YTokenizer;

public class CachecaComputer {
	
//	private static final CachecaComputer INSTANCE = new CachecaComputer();
//	private static boolean initialized;
//	public static String file;
	
	public CachecaComputer(){
//		initialized = false;
//		file = "";
	}
	
//	public void init(URL u, int order, String sourceFile){
//		init(u, order);
//		build(sourceFile);
//		file = sourceFile;
//		initialized = true;
//	}

//	public static CachecaComputer getInstance(String p){
//		if(!p.equals(file)){
//			INSTANCE.init(Platform.getBundle(Activator.PLUGIN_ID).getEntry("train.3grams"), 3, p);
//		}
//		return INSTANCE;
//	}
	
//	public static boolean isInitialized(){
//		return initialized;
//	}
	
	public boolean init(URL u, int ngramOrder)
    {
        // Initialize the whole procedure
    	return Data.Init(u, ngramOrder);
    }

	public void build(String line)
    {
        // Before editing, build the cache on the current file
        Data.CACHE.build(line);
    }
	
	public void ClearCache()
    {
        // Before editing, build the cache on the current file
        Data.CACHE.clear();
    }

    public ArrayList<Word> getCandidates(String p)
    {
//    	String[] pref = p.split("((?<=\\.)|(?=\\.))| |((?<=\\{)|(?=\\{))|((?<=\\()|(?=\\())|((?<=\\))|(?=\\)))|((?<=\\[)|(?=\\[))|((?<=\\;)|(?=\\;))");
//		String prefix = "";
//		boolean start = true;
//		for (String pre : pref){
//			pre.trim();
//			if(pre == null || pre.equals("") || pre.equals(" "))
//				continue;
//			if(start == false)
//				prefix+= " ";
//			prefix+=pre;
//			start = false;
//		}
		
		ArrayList<String> tks = YTokenizer.GetTokens(p);
		String prefix = "";
		for (String pre : tks){
			prefix += (pre + " ");
		}
		prefix = prefix.trim();
		
    	ArrayList<Word> candidates;
    	
        String ngramPrefix, cachePrefix;
        
        // order = # prefix + 1 (current token)
        // Take 3-gram for example, the prefix contains 2 tokens
        ngramPrefix = Utilities.getLastNWords(prefix, Data.NGRAM_ORDER-1);

        cachePrefix = Utilities.getLastNWords(prefix, Data.CACHE_ORDER-1);
        
        // n-gram word candidates
        candidates = (Data.NGRAM).getCandidates(ngramPrefix, Data.USE_BACKOFF);
        
        if(Data.USE_CACHE)
        {
            // update the candidates according to the cache
            candidates = (Data.CACHE).updateCandidates(cachePrefix, candidates);
        }

        return candidates;
    }
}