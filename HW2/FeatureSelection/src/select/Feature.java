package select;

import java.util.ArrayList;

import Jama.Matrix;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Feature {
	public static Feature[] feature = null; 
	public static final int attriNum = 10;
	public double[] mu;
	public double[][] sigma;
	public static double[][] Sb; //Sb矩阵
	public static double[][] Sw; //Sw矩阵
	public static double[] Mu; //整体的mu矩阵
	Feature() {
		mu = new double [attriNum];
		sigma = new double [attriNum][attriNum];
	}
	
	public static Feature[] getInstance() {
		if (Feature.feature == null) {
			feature = new Feature [2]; //0表示女生，1表示男生
			Sb = new double [attriNum][attriNum];
			Sw = new double [attriNum][attriNum];
			Mu = new double [attriNum];
			for (int i = 0; i < 2; i++) {
				feature[i] = new Feature();
			}
			Feature.initFeature();
		}
		return Feature.feature;
	}
	
	private static void initMatrix() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < attriNum; j++) {
				feature[i].mu[j] = 0.0;
				for (int k = 0; k < attriNum; k++) {
					feature[i].sigma[j][k] = 0.0;
				}
			}
		}
		for (int i = 0; i < attriNum; i++) {
			Feature.Mu[i] = 0.0;
			for (int j = 0; j < attriNum; j++) {
				Feature.Sb[i][j] = 0.0;
				Feature.Sw[i][j] = 0.0;
			}
		}
	}
	
	private static void initFeature() {
		if (Feature.feature == null) {
			System.err.println("Error for use method initFeature illegaly");
		}
		FileIO fio = FileIO.getInstance();
		int[] count = new int [2];		
		Feature.initMatrix(); //将矩阵初始化
		
		//计算mu向量
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
		
		//计算sigma矩阵
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
		
		//计算Mu向量
		for (int i = 0; i < attriNum; i++) {
			Mu[i] = (count[0]*feature[0].mu[i] + count[1]*feature[1].mu[i]) 
					/ (double)(count[0]+count[1]);
		}
		
		//计算Sb和Sw矩阵
		for (int i = 0; i < attriNum; i++) {
			for (int j = 0; j < attriNum; j++) {
				Sb[i][j] = 0.5 * ((feature[0].mu[i] - Mu[i])*(feature[0].mu[j] - Mu[j]) +
						(feature[1].mu[i] - Mu[i])*(feature[1].mu[j] - Mu[j]));
				Sw[i][j] = 0.5 * (feature[0].sigma[i][j] + feature[1].sigma[i][j]);
			}
		}
		
		System.out.println("female:"+count[0]);
		System.out.println("male:"+count[1]);
	}
	
	private static double trace(double[][] matrix) {
		double sum = 0;
		FileIO fio = FileIO.getInstance();
		for (int i = 0; i < attriNum; i++) {
			if (fio.beUsed.get(i)) {
				sum += matrix[i][i];
			}
		}
		return sum;
	}
	
	public static double getJ4() { //获取类内类间距离判据4
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		double J4 = Feature.trace(Feature.Sb) / Feature.trace(Feature.Sw);
		return J4;
	}
	
	public static Matrix getForSigma(int index) { //index为0或1
		int size = FileIO.countUsed();
		Matrix mtmp = Matrix.identity(size, size);
		ArrayList<Integer> useIndex = new ArrayList<Integer>();
		for (int i = 0; i < attriNum; i++) {
			if (FileIO.fileIO.beUsed.get(i)) {
				useIndex.add(i);
			}
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mtmp.set(i, j, feature[index].sigma[useIndex.get(i)][useIndex.get(j)]);
			}
		}
		return mtmp;
	}
	
	public static Matrix getForMuMinus() { //mu1-mu2
		int size = FileIO.countUsed();
		Matrix mtmp = Matrix.identity(size, 1);
		ArrayList<Integer> useIndex = new ArrayList<Integer>();
		for (int i = 0; i < attriNum; i++) {
			if (FileIO.fileIO.beUsed.get(i)) {
				useIndex.add(i);
			}
		}
		for (int i = 0; i < size; i++) {
			mtmp.set(i, 0, feature[0].mu[i] - feature[1].mu[i]);
		}
		return mtmp;
	}
	
	public static double getJD() { //获取概率分布的散度
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		Matrix sigma0 = getForSigma(0);
		Matrix sigma1 = getForSigma(1);
		Matrix sigma0ni = sigma0.inverse();
		Matrix sigma1ni = sigma1.inverse();
		int size = FileIO.countUsed();
		Matrix I2 = Matrix.identity(size, size).times(2.0);
		Matrix muMinus = getForMuMinus();
		Matrix part1 = (sigma0ni.times(sigma1)).plus(sigma1ni.times(sigma0)).minus(I2);
		Matrix part2 = (muMinus.transpose()).times(sigma0ni.plus(sigma1ni)).times(muMinus);
		double total = 0.5 * (part1.trace() + part2.get(0, 0));
		return total;
		
	}
	
	
	public static void main(String[] args) {
		System.out.println("J4:"+Feature.getJ4());
		System.out.println("JD:"+Feature.getJD());
	}
}
