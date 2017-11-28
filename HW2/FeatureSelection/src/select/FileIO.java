package select;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;
import net.sf.json.JSONObject;

public class FileIO {
	public static String trainTxt = "input/dataset3svm.txt";
	public static String testTxt = "input/dataset4svm.txt";
	public ArrayList<JSONObject> trainData = new ArrayList<JSONObject>();
	public ArrayList<JSONObject> testData = new ArrayList<JSONObject>();
	public ArrayList<Boolean> beUsed = new ArrayList<Boolean>();
	public static FileIO fileIO = null;
	FileIO() {
		for (int i = 0; i < 10; i++) {
			beUsed.add(true);
		}
	}
	
	static FileIO getInstance() {
		if (fileIO == null) {
			fileIO = new FileIO();
		}
		return fileIO;
	}
	
	public void readFile() {
		this.readTrainFile();
		this.readTestFile();
	}
	
	public void readTrainFile() {
		try {
			Scanner input = new Scanner(new File(this.trainTxt));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] splits = line.split(" ");
				JSONObject json1 = new JSONObject();
				json1.put("label", Integer.valueOf(splits[0]));
				ArrayList<Double> arl1 = new ArrayList<Double>();
				for (int i = 1; i < 11; i++) {
					String[] attri = splits[i].split(":");
					arl1.add(Double.valueOf(attri[1]));
				}
				json1.put("attri", arl1);
				this.trainData.add(json1);
			}
			System.out.println("size of train data:"+this.trainData.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readTestFile() {
		try {
			Scanner input = new Scanner(new File(this.testTxt));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				String[] splits = line.split(" ");
				JSONObject json1 = new JSONObject();
				json1.put("label", Integer.valueOf(splits[0]));
				ArrayList<Double> arl1 = new ArrayList<Double>();
				for (int i = 1; i < 11; i++) {
					String[] attri = splits[i].split(":");
					arl1.add(Double.valueOf(attri[1]));
				}
				json1.put("attri", arl1);
				this.testData.add(json1);
			}
			System.out.println("size of test data:"+this.testData.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeAttriForSVM() {
		
	}
	
	
	public static void main(String[] args) {
		FileIO fio = FileIO.getInstance();
		fio.readFile();
		Matrix m1 = Matrix.random(4, 5);
		m1.print(4, 5);
		System.out.println(m1.trace());
	}
	
}
