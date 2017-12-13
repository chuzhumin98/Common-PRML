package file;

import java.util.ArrayList;

public class FileIO {
	public ArrayList<PersonEntry> personInital = new ArrayList<PersonEntry>(); //建立一个所有样本的数组
	public ArrayList<PersonEntry> personPCA = new ArrayList<PersonEntry>(); //存储PCA变换之后的特征
	public static FileIO file = null;
	/*
	 * 进行实例化，实现单子模式
	 */
	public static FileIO getInstance() {
		if (file == null) {
			file = new FileIO();
		}
		return file;
	}
	
	
}
