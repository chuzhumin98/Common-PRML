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
	
}
