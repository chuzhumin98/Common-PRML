package select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SelectMethod {
	/*
	 * 暴力搜索算法，针对所有的三维特征进行暴力搜索，找到最优解，复杂度C(10,3) = 120
	 * standard: 0表示类内类间距离的J4，1表示基于概率分布的散度
	 */
	public void violentSearch(int standard) { //暴力搜索
		FileIO fio = FileIO.getInstance();
		double maxScore = Double.MIN_VALUE;
		int[] maxAttri = new int [3];
		for (int i = 0; i < FileIO.attriNum; i++) {
			for (int j = i+1; j < FileIO.attriNum; j++) {
				for (int k = j+1; k < FileIO.attriNum; k++) {
					fio.setAllUsed(false);
					fio.beUsed.set(i, true);
					fio.beUsed.set(j, true);
					fio.beUsed.set(k, true);
					double score = getScore(standard);
					if (score > maxScore) {
						maxScore = score;
						maxAttri[0] = i;
						maxAttri[1] = j;
						maxAttri[2] = k;
					}
				}
			}
		}
		System.out.println("maxScore:"+maxScore);
		System.out.println("the max pairs using voilentSearch is:"+maxAttri[0]+" "+ maxAttri[1]+" "+maxAttri[2]);
		
		//设置为最佳组合
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			fio.beUsed.set(maxAttri[i], true);
		} 
		TrainAndTestByLibSVM.main(null);		
	}
	
	/*
	 * 分支定界算法，采用树结构的思想，并用数组来代替其实现，属于最优算法
	 */
	public void fenZhiDingJie(int standard) {
		FileIO fio = FileIO.getInstance();
		int[] hasDelete = new int [FileIO.attriNum];
		MaxInfo maxScore = new MaxInfo();
		int[] maxAttri = new int [3];
		for (int i = 0; i < FileIO.attriNum; i++) {
			hasDelete[i] = 0; //0表示未使用，1表示使用过，2表示高过
		}
		Map<Integer,Double> scores = new HashMap<Integer, Double>();
		for (int i = 0; i < FileIO.attriNum; i++) {
			fio.setAllUsed(true);
			fio.beUsed.set(i, false);
			double score = getScore(standard);
			maxScore.countCal++;
			scores.put(i, score);
		}
		int[] attriIndex = new int [FileIO.attriNum];
		for (int i = 0; i < FileIO.attriNum; i++) {
			int index = 0; //找到最大的元素，表明它最不重要
			double maxScore1 = Double.MIN_VALUE;
			for (Entry<Integer, Double> item: scores.entrySet()) {
				if (item.getValue() > maxScore1) {
					maxScore1 = item.getValue();
					index = item.getKey();
				}
			}
			attriIndex[i] = index;
			scores.remove(index);
		}
		for (int i = 0; i < 4; i++) {
			hasDelete[i] = 1;
			this.BFS(attriIndex, hasDelete, maxScore, maxAttri, 1, standard, i);
		}
		maxScore.print();
		System.out.print("the best pairs using fenzhidingjie is: ");
		for (int i = 0; i < 3; i++) {
			System.out.print(maxAttri[i]+" ");
		}
		System.out.println();	
		//设置为最佳组合
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			fio.beUsed.set(maxAttri[i], true);
		} 
		TrainAndTestByLibSVM.main(null);
	}
	
	/*
	 * 获取某项准则下的评分的方法，通过standard决定使用的方法
	 */
	private double getScore(int standard) {
		double score = 0.0;
		switch (standard) {
		case 0:
			score = Feature.getJ4();
			break;
		case 1:
			score = Feature.getJD();
			break;
		default:
			score = Feature.getJ4(); //默认采用J4
		}
		return score;
	}
	
	/*
	 * 在分支定界算法中所使用的主干算法
	 */
	public void BFS(int[] attriIndex, int[] hasDelete, MaxInfo maxScore, int[] maxAttri,
			int count, int standard, int thisAdd) {
		FileIO fio = FileIO.getInstance();
		for (int i = 0; i < FileIO.attriNum; i++) {
			if (hasDelete[i] != 1) {
				fio.beUsed.set(attriIndex[i], true);
			} else {
				fio.beUsed.set(attriIndex[i], false);
			}
		}
		double score = getScore(standard);
		maxScore.countCal++;
		if (count == 7) maxScore.count7++;
		if (score <= maxScore.maxNum) {
			hasDelete[thisAdd] = 0;
			return;
		}
		if (count == 7) {		
			maxScore.countX++;
			System.out.println(maxScore.countX+":"+maxScore.maxNum+" "+score);
			maxScore.maxNum = score;	
			int index = 0;
			for (int i = 0; i < FileIO.attriNum; i++) {
				if (hasDelete[i] != 1) {
					maxAttri[index] = attriIndex[i];
					index++;
				}
			}
			hasDelete[thisAdd] = 0;
			return;
		}
		for (int i = thisAdd+1; i < count+4; i++) {
			hasDelete[i] = 1;
			BFS(attriIndex, hasDelete, maxScore, maxAttri, count+1, standard, i);
		}
		hasDelete[thisAdd] = 0;
	}
	
	/*
	 * 单独最优特征的组合，次优算法，贪心算法
	 */
	public void tanXinMethod(int standard) {
		FileIO fio = FileIO.getInstance();
		int[] maxAttri = new int [3];
		Map<Integer,Double> scores = new HashMap<Integer, Double>();
		for (int i = 0; i < FileIO.attriNum; i++) {
			fio.setAllUsed(false);
			fio.beUsed.set(i, true);
			double score = getScore(standard);
			scores.put(i, score);
		}
		for (int i = 0; i < 3; i++) {
			int index = 0; //找到最大的元素，表明它最好
			double maxScore1 = Double.MIN_VALUE;
			for (Entry<Integer, Double> item: scores.entrySet()) {
				if (item.getValue() > maxScore1) {
					maxScore1 = item.getValue();
					index = item.getKey();
				}
			}
			maxAttri[i] = index;
			scores.remove(index);
		}
		
		System.out.println("the max pairs using tanxinMethod is:"+
						maxAttri[0]+" "+ maxAttri[1]+" "+maxAttri[2]);
		
		//设置为最佳组合
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			fio.beUsed.set(maxAttri[i], true);
		} 
		TrainAndTestByLibSVM.main(null);	
	}
	
	/*
	 * 顺序前进法，次优算法
	 */
	public void forwardSelection(int standard) {
		ArrayList<Integer> candidate = new ArrayList<Integer>();
		for (int i = 0; i < FileIO.attriNum; i++) {
			candidate.add(i);
		}
		int[] maxAttri = new int [3];
		FileIO fio = FileIO.getInstance();
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			int maxIndex = 0;
			double maxScore = Double.MIN_VALUE;
			for (int j = 0; j < candidate.size(); j++) {
				fio.beUsed.set(candidate.get(j), true);
				double score = this.getScore(standard);
				if (score > maxScore) {
					maxIndex = j;
					maxScore = score;
				}
				fio.beUsed.set(candidate.get(j), false);
			}
			fio.beUsed.set(candidate.get(maxIndex), true);
			maxAttri[i] = candidate.get(maxIndex);
			candidate.remove(maxIndex);
		}
		
		System.out.println("the max pairs using tanxinMethod is:"+
				maxAttri[0]+" "+ maxAttri[1]+" "+maxAttri[2]);
		//设置为最佳组合
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			fio.beUsed.set(maxAttri[i], true);
		} 
		TrainAndTestByLibSVM.main(null);	
	}
	
	/*
	 * 顺序后退法，次优算法
	 */
	public void backwardSelection(int standard) {
		ArrayList<Integer> candidate = new ArrayList<Integer>();
		for (int i = 0; i < FileIO.attriNum; i++) {
			candidate.add(i);
		}
		FileIO fio = FileIO.getInstance();
		fio.setAllUsed(false);
		while (candidate.size() > 3) {
			int worstIndex = 0;
			double maxScore = Double.MIN_VALUE; //除去某个元素外的分数
			fio.setAllUsed(false);
			for (int j = 0; j < candidate.size(); j++) { //一轮开始之前的准备工作
				fio.beUsed.set(candidate.get(j), true);
			}
			for (int i =  0; i < candidate.size(); i++) {
				fio.beUsed.set(candidate.get(i), false);
				double score = this.getScore(standard);
				if (score > maxScore) {
					maxScore = score;
					worstIndex = i;
				}
				fio.beUsed.set(candidate.get(i), true);
			}
			candidate.remove(worstIndex);
		}
		
		System.out.println("the max pairs using tanxinMethod is:"+
				candidate.get(0)+" "+candidate.get(1)+" "+candidate.get(2));
		//设置为最佳组合
		fio.setAllUsed(false);
		for (int i = 0; i < 3; i++) {
			fio.beUsed.set(candidate.get(i), true);
		} 
		TrainAndTestByLibSVM.main(null);	
	}
	
	public static void main(String[] args) {
		SelectMethod sm1 = new SelectMethod();
		sm1.tanXinMethod(1);
		sm1.backwardSelection(1);
		sm1.forwardSelection(1);
	}
	
	class MaxInfo {
		public double maxNum = Double.MIN_VALUE;
		public int countX = 0; //记录max更迭的次数
		public int count7 = 0; //记录访问到第7层的次数
		public int countCal = 0; //记录运算的次数
		public void print() {
			System.out.println("countX:" + countX);
			System.out.println("count7:" + count7);
			System.out.println("countCal:" + countCal);
		}
	}
}
