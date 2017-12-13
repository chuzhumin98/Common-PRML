package cluster;

import java.util.ArrayList;

import file.PersonEntry;

public class Cluster {
	public ArrayList<PersonEntry> peopleCluster = new ArrayList<PersonEntry>();
	
	/*
	 * 获取与其他类别之间的最近距离
	 */
	public double getSingleLink(Cluster cluster) {
		double minDist = Double.MAX_VALUE;
		for (PersonEntry item1: this.peopleCluster) {
			for (PersonEntry item2: cluster.peopleCluster) {
				double myDist = item1.getDistance(item2);
				if (myDist < minDist) {
					minDist = myDist;
				}
			}
		}
		return minDist;
	}
	
	/*
	 * 获取与其他类别之间的最远距离
	 */
	public double getCompleteLink(Cluster cluster) {
		double maxDist = Double.MIN_VALUE;
		for (PersonEntry item1: this.peopleCluster) {
			for (PersonEntry item2: cluster.peopleCluster) {
				double myDist = item1.getDistance(item2);
				if (myDist > maxDist) {
					maxDist = myDist;
				}
			}
		}
		return maxDist;
	}
	
	/*
	 * 获取与其他类别之间的均值距离
	 */
	public double getAverageLink(Cluster cluster) {
		PersonEntry myAverage = this.getAveragePerson();
		PersonEntry otherAverage = cluster.getAveragePerson();
		double dist = myAverage.getDistance(otherAverage);
		return dist;
	}
	
	/*
	 * 获取类别内部的均值人口
	 */
	public PersonEntry getAveragePerson() {
		int attriNum = this.peopleCluster.get(0).attriNum;
		PersonEntry average = new PersonEntry(attriNum);
		for (int i = 0; i < attriNum; i++) {
			double total = 0.0;
			for (PersonEntry item: this.peopleCluster) {
				total += item.attris[i];
			}
			total /= (double)this.peopleCluster.size();
			average.attris[i] = total;
		}
		return average;
	}
}
