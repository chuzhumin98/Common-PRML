package select;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Feature {
	public static Feature[] feature = null; 
	public static final int attriNum = 10;
	public double[] mu;
	public double[][] sigma;
	Feature() {
		mu = new double [attriNum];
		sigma = new double [attriNum][attriNum];
	}
	
	public static Feature[] getInstance() {
		if (Feature.feature == null) {
			feature = new Feature [2]; //0表示女生，1表示男生
			for (int i = 0; i < 2; i++) {
				feature[i] = new Feature();
			}
			Feature.initFeature();
		}
		return Feature.feature;
	}
	
	private static void initFeature() {
		if (Feature.feature == null) {
			System.err.println("Error for use method initFeature illegaly");
		}
		FileIO fio = FileIO.getInstance();
		int[] count = new int [2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < attriNum; j++) {
				feature[i].mu[j] = 0.0;
				for (int k = 0; k < attriNum; k++) {
					feature[i].sigma[j][k] = 0.0;
				}
			}
		}
		for (int i = 0; i < fio.trainData.size(); i++) {
			JSONObject json1 = fio.trainData.get(i);
			int index = json1.getInt("label");
			JSONArray attries = json1.getJSONArray("attri");
			count[index]++; //计数各类的个数
			for (int j = 0; j < attriNum; j++) {
				feature[index].mu[j] += attries.getDouble(j);
			}
		}
		for (int i = 0; i < attriNum; i++) {
			for (int j = 0; j < 2; j++) {
				feature[j].mu[i] /= (double)count[j];
			}
		}
		for (int i = 0; i < fio.trainData.size(); i++) {
			JSONObject json1 = fio.trainData.get(i);
			int index = json1.getInt("label");
			JSONArray attries = json1.getJSONArray("attri");
			for (int j = 0; j < attriNum; j++) {
				for (int k = 0; k < attriNum; k++) {
					feature[index].sigma[j][k] += (attries.getDouble(j) - feature[index].mu[j])
							* (attries.getDouble(k) - feature[index].mu[k]);
				}
			}
		}
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < attriNum; j++) {
				for (int k = 0; k < attriNum; k++) {
					feature[i].sigma[j][k] /= (double)count[i];
				}
			}
		}
		System.out.println("female:"+count[0]);
		System.out.println("male:"+count[1]);
		for (int i = 0; i < attriNum; i++) {
			System.out.print(feature[0].mu[i]+" ");
		}
		System.out.println();
		for (int i = 0; i < attriNum; i++) {
			for (int j = 0; j < attriNum; j++) {
				System.out.print(feature[0].sigma[i][j]+" ");
			}
			System.out.println();
		} 
		
	}
	public static void main(String[] args) {
		Feature[] ft = Feature.getInstance();
	}
}
