package similarities;

import java.util.Arrays;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
public class Similarity {

	public Similarity() {
		// TODO Auto-generated constructor stub
		
	}
	public static void main(String args[]) {
		Word2Vec w2vModel = WordVectorSerializer.readWord2VecModel("model_w2v.bin");
 		System.out.println(w2vModel.similarity("Container", "LKW"));
		/*continue: immigrate the python similarity measures of sentences **/
	}
	public static int Levenshtein(String x, String y) {
		return calculate(x,y);
	}
	static int calculate(String x, String y) {
	    int[][] dp = new int[x.length() + 1][y.length() + 1];
	 
	    for (int i = 0; i <= x.length(); i++) {
	        for (int j = 0; j <= y.length(); j++) {
	            if (i == 0) {
	                dp[i][j] = j;
	            }
	            else if (j == 0) {
	                dp[i][j] = i;
	            }
	            else {
	                dp[i][j] = min(dp[i - 1][j - 1] 
	                 + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
	                  dp[i - 1][j] + 1, 
	                  dp[i][j - 1] + 1);
	            }
	        }
	    }
	 
	    return dp[x.length()][y.length()];
	}
	 public static int costOfSubstitution(char a, char b) {
	        return a == b ? 0 : 1;
	    }
	 
	    public static int min(int... numbers) {
	        return Arrays.stream(numbers)
	          .min().orElse(Integer.MAX_VALUE);
	    }

}
