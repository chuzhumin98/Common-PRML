package file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FileIO {
	public int attriNumInital = 10; //初始特征维数为10
	public int attriNumPCA = 2; //PCA变换后的特征维数为2(PCA变换后才确定的)
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
	
	/*
	 * 构造函数时导入数据
	 */
	FileIO() {
		try {
			Scanner input = new Scanner(new File("input/dataset3.txt"));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] splits = line.split("\t");
				PersonEntry person = new PersonEntry(this.attriNumInital);
				for (int i = 0; i < this.attriNumInital; i++) {
					person.attris[i] = Double.parseDouble(splits[i]);
				}
				//System.out.println(person.toString());
				this.personInital.add(person);
			}
			System.out.println("the sample size:"+this.personInital.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FileIO file = FileIO.getInstance();
		
	}
}
