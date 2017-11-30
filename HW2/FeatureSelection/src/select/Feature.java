package select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	static int[] count = new int [2];
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
	
	/*
	 * 类内类间距判据2
	 */
	public static double getJ2() {
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		Matrix Sb1 = Feature.getForSb();
		Matrix Sw1 = Feature.getForSw();
		Matrix pro = Sw1.inverse().times(Sb1);
		double J2 = pro.trace();
		return J2;
	}
	
	public static double getJ4() { //获取类内类间距离判据4
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		double J4 = Feature.trace(Feature.Sb) / Feature.trace(Feature.Sw);
		return J4;
	}
	
	/*
	 * 类内类间距判据5
	 */
	public static double getJ5() {
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		Matrix Sb1 = Feature.getForSb();
		Matrix Sw1 = Feature.getForSw();
		Sb1 = Sb1.minus(Sw1);
		double J5 = (Sb1.det() / Sw1.det());
		//System.out.println(Sb1.det()+"/"+Sw1.det()+"="+J5);
		return J5;
	}
	
	public static Matrix getForSb() { 
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
				mtmp.set(i, j, Sb[useIndex.get(i)][useIndex.get(j)]);
			}
		}
		return mtmp;
	}
	
	public static Matrix getForSw() { 
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
				mtmp.set(i, j, Sw[useIndex.get(i)][useIndex.get(j)]);
			}
		}
		return mtmp;
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
	
	/*
	 * 基于t分布的方法，通过比较其t-检验的大小来决定排名
	 */
	public static double getTDistribution(int index) { 
		if (Feature.feature == null) {
			Feature.getInstance();
		}
		double numerator = Math.abs(feature[0].mu[index] - feature[1].mu[index]);
		int m_1 = count[0] - 1;
		int n_1 = count[1] - 1;
		double sp2 = (m_1*feature[0].sigma[index][index] + 
				n_1*feature[1].sigma[index][index]) / 
				(double)(n_1+m_1);
		double den = Math.sqrt(sp2) * Math.sqrt(1.0/(double)count[0] + 1.0/(double)count[1]);
		double t = numerator / den;
		return t;
	}
	
	/*
	 * 基于秩参数的方法，通过观察秩参数所在取值的区域
	 */
	public static double getRankDistribution(int index) {
		Map<Integer, AttriData> map = new HashMap<Integer, AttriData>();
		FileIO fio = FileIO.getInstance();
		for (int i = 0; i < fio.trainData.size(); i++) {
			int label = fio.trainData.get(i).getInt("label");
			double value = fio.trainData.get(i).getJSONArray("attri").getDouble(index);
			AttriData atd = new AttriData(label, value);
			map.put(i, atd);
		}
		//这里将map.entrySet()转换成list
        List<Map.Entry<Integer, AttriData>> list = new ArrayList<Map.Entry<Integer,AttriData>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<Integer,AttriData>>() {
            //升序排序
            public int compare(Entry<Integer, AttriData> o1,
                    Entry<Integer, AttriData> o2) {
                if (o1.getValue().value - o2.getValue().value < 0) {
                	return -1;
                } else {
                	return 1;
                }
            }   
        });
        double countX = 0.0;
        for (int i = 0; i < list.size(); i++) {
        	if (list.get(i).getValue().label == 0) {
        		countX += i + 1;
        	}
        }
        double num = Math.abs(count[0]*(count[0]+count[1]+1)/2.0 - countX);
        double den = Math.sqrt(count[0]*count[1]*(count[0]+count[1]+1)/12.0);
        double rankValue = num / den;
        return rankValue;
	}
	
	public static void main(String[] args) {
		System.out.println("J4:"+Feature.getJ4());
		System.out.println("JD:"+Feature.getJD());
		Feature.getRankDistribution(4);
	}
	
	
	static class AttriData {
		public int label; //0或1
		public double value; //某个attribute的值
		public AttriData(int label, double value) {
			this.label = label;
			this.value = value;
		}
	}
}
