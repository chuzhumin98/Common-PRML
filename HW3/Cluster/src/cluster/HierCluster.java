package cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import file.FileIO;
import file.PersonEntry;

public class HierCluster {
	public ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	
	/*
	 * 构造函数选定聚类的对象们
	 */
	HierCluster(ArrayList<PersonEntry> people) {
		for (PersonEntry item: people) {
			Cluster myItem = new Cluster();
			myItem.peopleCluster.add(item);
			clusters.add(myItem);
		}
	}
	
	/*
	 * 根据不同方法获得类间距离：
	 * 0 - 最近距离
	 * 1 - 最远距离
	 * 2 - 均值距离
	 * default - 均值距离
	 */
	public double getDistance(int method, Cluster c1, Cluster c2) {
		double dist = 0.0;
		switch (method) {
		case 0:
			dist = c1.getSingleLink(c2);
			break;
		case 1:
			dist = c1.getCompleteLink(c2);
			break;
		case 3:
			dist = c1.getAverageLink(c2);
			break;
		default:
			dist = c1.getAverageLink(c2);
		}
		return dist;
	}
	
	
	/*
	 * 将类别数将至C类，采用某种评价方式
	 */
	public void mergeCluster(int C, int standard) {
		while (clusters.size() > C) {
			int i0 = -1, j0 = -1; //记录最可能merge的组合
			double minDist = Double.MAX_VALUE;
			for (int i = 0; i < this.clusters.size(); i++) {
				for (int j = i+1; j < this.clusters.size(); j++) {
					double myDist = this.getDistance(standard, this.clusters.get(i), this.clusters.get(j));
					if (myDist < minDist) {
						i0 = i;
						j0 = j;
						minDist = myDist;
					}
				}
			}
			System.out.println(clusters.size()+":"+i0+" "+j0);
			for (PersonEntry item: this.clusters.get(j0).peopleCluster) { //实现merge操作
				this.clusters.get(i0).peopleCluster.add(item);
			}
			this.clusters.remove(j0);
		}
	}
	
	/*
	 * 将聚类结果输出到output文件夹中
	 * 输出格式：前attriNum列为更属性的值，最后一列为分类结果
	 */
	public void outputCluster(String path) {
		try (PrintStream out = new PrintStream(new File("output/"+path))) {
			for (int i = 0; i < this.clusters.size(); i++) {
				Cluster item = this.clusters.get(i);
				for (PersonEntry items: item.peopleCluster) {
					String outString = "";
					for (int j = 0; j < items.attriNum; j++) {
						outString += String.valueOf(items.attris[j])+" ";
					}
					outString += String.valueOf(i);
					out.println(outString);
				}
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FileIO file = FileIO.getInstance();
		HierCluster hc = new HierCluster(file.personPCA);
		for (int C = 6; C >= 1; C--) {
			hc.mergeCluster(C, 2);
			String path = "PCAC="+C+"hieraveragelink.txt";
			hc.outputCluster(path);
		}
	}
}
