package cn.yyx;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.ucdavis.cacheca.CachecaComputer;
import edu.ucdavis.cacheca.Word;

public class Evaluate {
	
	public static final CachecaComputer INSTANCE = new CachecaComputer();
	public static final int[] top_ks = new int[] {1, 3, 6, 10};
	
	public Evaluate() {
		try {
			INSTANCE.init(new File("train.3grams").toURI().toURL(), 3);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Integer> EvaluateAccurate(String context, String expected_token) {
		INSTANCE.ClearCache();
		INSTANCE.build(context);
		ArrayList<Word> cads = INSTANCE.getCandidates(context);
		int index = Integer.MAX_VALUE;
		for (int i=0;i<cads.size();i++) {
			Word w = cads.get(i);
			if (w.mToken.equals(expected_token)) {
				index = i;
				break;
			}
		}
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i=0;i<top_ks.length;i++) {
			int acc = 0;
			if (top_ks[i] > index) {
				acc = 1;
			}
			result.add(acc);
		}
		return result;
	}
	
	public ArrayList<Double> EvaluateAccuracy(String one_example) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i=0;i<top_ks.length;i++) {
			result.add(0.0);
		}
		String[] oes = one_example.split(" ");
		for (int i=1;i<oes.length;i++) {
			String expected_token = oes[i];
			String[] s_a = Arrays.copyOfRange(oes, 0, i);
			String context = String.join(" ", s_a);
			ArrayList<Integer> as = EvaluateAccurate(context, expected_token);
			for (int j=0;j<top_ks.length;j++) {
				result.set(j, result.get(j) + as.get(j)*1.0);
			}
		}
		for (int j=0;j<top_ks.length;j++) {
			result.set(j, result.get(j)/(oes.length*1.0));
		}
		return result;
	}
	
}
