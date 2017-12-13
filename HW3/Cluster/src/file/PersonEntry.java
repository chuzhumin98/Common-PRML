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
	
	
}
