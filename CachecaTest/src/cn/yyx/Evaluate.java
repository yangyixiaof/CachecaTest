package cn.yyx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	
	public Accuracy EvaluateAccuracy(String one_example) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i=0;i<top_ks.length;i++) {
			result.add(0.0);
		}
		one_example = "<s>" + " " + one_example;
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
		return new Accuracy(result, oes.length*1.0-1.0);
	}
	
	public ArrayList<Double> EvaluateAccuracy(File test_file) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i=0;i<top_ks.length;i++) {
			result.add(0.0);
		}
		double r_size = 0;
		List<String> examples = new LinkedList<String>();
		try {
			examples.addAll(FileUtil.ReadFile(test_file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String example : examples) {
			Accuracy acc = EvaluateAccuracy(example);
			for (int j=0;j<top_ks.length;j++) {
				result.set(j, result.get(j) + acc.top_k.get(j)*1.0);
			}
			r_size += acc.size;
		}
		for (int j=0;j<top_ks.length;j++) {
			result.set(j, result.get(j)/r_size);
		}
		return result;
	}
	
	class Accuracy {
		
		ArrayList<Double> top_k = new ArrayList<Double>();
		double size = 0;
		
		public Accuracy(ArrayList<Double> top_k, double size) {
			this.top_k.addAll(top_k);
			this.size = size;
		}
		
	}
	
}
