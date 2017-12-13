package cluster;

import java.util.ArrayList;
import java.util.Random;

import file.PersonEntry;

public class Cmeans {
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
				double minIndex = 0;
				double minValue = Double.MAX_VALUE;
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
}