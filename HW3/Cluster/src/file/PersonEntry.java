package file;

public class PersonEntry {
	public int attriNum = 10; //需要设定特征的维数
	public double[] attris; //记录各特征的属性值
	public int cluster; //分类的类别号
	PersonEntry(int attriNum) {
		this.attriNum = attriNum;
		attris = new double [attriNum];
		cluster = -1; //初始化还未进行分类
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * override toString method
	 */
	public String toString() {
		String string = "";
		for (int i = 0; i < this.attriNum; i++) {
			string += String.valueOf(this.attris[i])+" ";
		}
		string += "cluster:"+String.valueOf(this.cluster);
		return string;
	}
	
	/*
	 * 获得和某个同类对象之间的距离(采用传统的欧式距离)
	 */
	public double getDistance(PersonEntry other) {
		if (other.attriNum != this.attriNum) {
			return Double.MAX_VALUE;
		}
		double sums = 0.0;
		for (int i = 0; i < this.attriNum; i++) {
			double delta = this.attris[i] - other.attris[i];
			sums += delta * delta;
		}
		return sums;
	}
	
	/*
	 * 获得和某个向量之间的距离(采用传统的欧式距离)
	 */
	public double getDistance(double[] other) {
		double sums = 0.0;
		for (int i = 0; i < this.attriNum; i++) {
			double delta = this.attris[i] - other[i];
			sums += delta * delta;
		}
		return sums;
	}
}
