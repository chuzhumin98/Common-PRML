package cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import file.FileIO;
import file.PersonEntry;

public class Cmeans {
	public double[][] clusterTotalVectors = new double [20][10]; 
			//给一个足够大的数组用来各类均值向量
	public double[][] clusterMVectors = new double [20][10]; //对应的m向量
	public int[] clusterNumber = new int [20]; //每一类的元素个数
	public static final int MAX_ITER = 100000; //最大迭代次数
	public static final int STOP_N = 20; //N步不变判停准则
	public int SPLIT_NUM = 10; //每十轮关注一次值的变化
	public int matrixColumn;
	public double[][] matrixJe; //不同C迭代不同次数后的表单
	private boolean needWritten = false; //是否需要书写表单
	
	Cmeans() {
		matrixColumn = MAX_ITER / SPLIT_NUM;
		matrixJe = new double [6][this.matrixColumn];
	}
	
	/*
	 * 针对people进行类别数为C的聚类
	 */
	public void doCmeans(ArrayList<PersonEntry> people, int C) {
		if (C < 1 || C > 20) {
			System.err.println("the cluster number "+C+" is out of range!");
			return;
		}
		/*
		 * 步骤1，初始划分c个聚类
		 */
		int attriNum = people.get(0).attriNum;
		int[] indexes = new int [C]; //记录下来C类的代表点下标
		this.resetCluster(people);
		for (int i = 0; i < C; ) {
			Random random = new Random(); //生成一个随机数种子
			int index = random.nextInt(people.size());    
			if (people.get(index).cluster == -1) { //还未划分类别时
				people.get(index).cluster = i;
				indexes[i] = index;
				i++;
			}
		}
		for (PersonEntry item: people) {
			if (item.cluster == -1) { //还未分类的话找一个最近的分类
				int minIndex = 0;
				double minDist = Double.MAX_VALUE;
				for (int i = 0; i < C; i++) {
					PersonEntry base = people.get(indexes[i]);
					double myDist = item.getDistance(base);
					if (myDist < minDist) {
						minIndex = i;
						minDist = myDist;
					}
				}
				item.cluster = minIndex;
			}
		}
		
		this.initTotalVector(people, C);
		/*
		 * 步骤2：迭代调整
		 */
		int size = people.size();
		for (int i = 0; i < Cmeans.MAX_ITER; i++) {
			Random random = new Random();
			int index = random.nextInt(size);
			PersonEntry person = people.get(index);
			int i0 = person.cluster;
			if (this.clusterNumber[i0] == 1) continue; //某一类中只有一个元素时，跳过该轮
			int j0 = -1;
			double roujmin = Double.MAX_VALUE;
			for (int j = 0; j < C; j++) {
				if (j == i0) continue;
				double rouj = (double)this.clusterNumber[j]*
						person.getDistance(this.clusterMVectors[j])/(double)(this.clusterNumber[j]+1);
				if (rouj < roujmin) {
					roujmin = rouj;
					j0 = j;
				}
			}
			double roui0 = (double)this.clusterNumber[i0]*
					person.getDistance(this.clusterMVectors[i0])/(double)(this.clusterNumber[i0]-1);
			if (roui0 > roujmin) { //当需要改变时，则发生改变
				person.cluster = j0;
				this.clusterNumber[i0]--;
				this.clusterNumber[j0]++;
				for (int j = 0; j < attriNum; j++) {
					this.clusterTotalVectors[i0][j] -= person.attris[j];
					this.clusterMVectors[i0][j] = this.clusterTotalVectors[i0][j] / (double)this.clusterNumber[i0];
					this.clusterTotalVectors[j0][j] += person.attris[j];
					this.clusterMVectors[j0][j] = this.clusterTotalVectors[j0][j] / (double)this.clusterNumber[j0];
				}
			}
			if ((i+1)%1000 == 0) {
				//System.out.println("iter "+(i+1)+":"+this.getJe(people, C));
			}
			if (i % SPLIT_NUM == 0 && this.needWritten) {
				int indexWriten = i / SPLIT_NUM;
				this.matrixJe[C-1][indexWriten] = this.getJe(people, C);
			}
		}
		
	}
	
	/*
	 * 重置化people的类别
	 */
	public void resetCluster(ArrayList<PersonEntry> people) {
		for (int i = 0; i < people.size(); i++) {
			people.get(i).cluster = -1;
		}
	}
	
	/*
	 * 初始化各类的所有向量之和
	 */
	private void initTotalVector(ArrayList<PersonEntry> people, int C) {
		/*
		 * 首先要将向量进行初始化
		 */
		int attriNum = people.get(0).attriNum;
		for (int i = 0; i < C; i++) {
			for (int j = 0; j < attriNum; j++) {
				this.clusterTotalVectors[i][j] = 0.0;
				this.clusterMVectors[i][j] = 0.0;
			}
		}
		for (int i = 0; i < C; i++) {
			this.clusterNumber[i] = 0;
		}
		/*
		 * 在此基础上进行计数
		 */
		for (PersonEntry item: people) {
			int index = item.cluster;
			this.clusterNumber[index]++;
			for (int k = 0; k < attriNum; k++) {
				this.clusterTotalVectors[index][k] += item.attris[k];
			}
		}
		for (int i = 0; i < C; i++) {
			for (int j = 0; j < attriNum; j++) {
				this.clusterMVectors[i][j] = this.clusterTotalVectors[i][j] / (double)this.clusterNumber[i];
			}
		}
		
	}
	
	/*
	 * 计算聚类平方和聚类准则
	 */
	public double getJe(ArrayList<PersonEntry> people, int C) {
		double Je = 0.0;
		for (PersonEntry item: people) {
			Je += item.getDistance(this.clusterMVectors[item.cluster]);
		}
		return Je;
	}
	
	/*
	 * 将不同C的Je变化曲线写入文件中
	 */
	public void writtenJeCurve(String path, ArrayList<PersonEntry> people) {
		this.needWritten = true; //置为可以书写
		for (int i = 1; i <= 6; i++) {
			this.doCmeans(people, i);
		}
		try (PrintStream out = new PrintStream(new File("output/"+path))) {
			for (int i = 0; i < 6; i++) {
				String string = "";
				for (int j = 0; j < this.matrixColumn; j++) {
					if (j != this.matrixColumn-1) {
						string += this.matrixJe[i][j]+" ";
					} else {
						string += this.matrixJe[i][j];
					}
				}
				out.println(string);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.needWritten = false;
	}
	
	public static void main(String[] args) {
		FileIO file = FileIO.getInstance();
		Cmeans cm = new Cmeans();
		cm.writtenJeCurve("CmeansiPCACurve", file.personPCA);
		/*int C = 6;
		cm.doCmeans(file.personPCA, C);
		String outfile = "CmeansPCAC="+C+".txt";
		file.outputCluster(file.personPCA, outfile); */
	}
}
