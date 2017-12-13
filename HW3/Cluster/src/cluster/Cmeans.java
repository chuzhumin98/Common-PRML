package cluster;

import java.util.ArrayList;
import java.util.Random;

import file.FileIO;
import file.PersonEntry;

public class Cmeans {
	public double[][] clusterTotalVectors = new double [20][10]; 
			//给一个足够大的数组用来各类均值向量
	public int[] clusterNumber = new int [20]; //每一类的元素个数
	
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
		
	}
	
	public static void main(String[] args) {
		FileIO file = FileIO.getInstance();
		Cmeans cm = new Cmeans();
		cm.doCmeans(file.personInital, 8);
		file.outputCluster(file.personInital, "testPut.txt");
	}
}
