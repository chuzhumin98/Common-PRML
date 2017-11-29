package select;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FileIO {
	public static final int attriNum = 10;
	public static String trainExport = "cache/dataset3mysvm.txt";
	public static String testExport = "cache/dataset4mysvm.txt";
	public static String trainTxt = "input/dataset3svm.txt";
	public static String testTxt = "input/dataset4svm.txt";
	public ArrayList<JSONObject> trainData = new ArrayList<JSONObject>();
	public ArrayList<JSONObject> testData = new ArrayList<JSONObject>();
	public ArrayList<Boolean> beUsed = new ArrayList<Boolean>();
	public static FileIO fileIO = null;
	FileIO() {
		for (int i = 0; i < attriNum; i++) {
			beUsed.add(true);
		}
		this.readFile();
	}
	
	static FileIO getInstance() {
		if (fileIO == null) {
			fileIO = new FileIO();
		}
		return fileIO;
	}
	
	public static int countUsed() {
		if (fileIO == null) {
			fileIO = new FileIO();
		}
		int counts = 0;
		for (Boolean item: fileIO.beUsed) {
			if (item) {
				counts++;
			}
		}
		return counts;
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
				JSONArray arl1 = new JSONArray();
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
				JSONArray arl1 = new JSONArray();
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
		this.writeTrainAttriForSVM();
		this.writeTestAttriForSVM();
	}
	
	public void writeTrainAttriForSVM() {
		try {
			PrintStream out = new PrintStream(new File(this.trainExport));
			for (int i = 0; i < this.trainData.size(); i++) {
				out.print(this.trainData.get(i).getInt("label")+" ");
				for (int j = 0; j < attriNum; j++) {
					if (this.beUsed.get(j)) {
						out.print(j+":"+this.trainData.get(i).getJSONArray("attri").getDouble(j)+" ");
					}
				}
				out.println();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeTestAttriForSVM() {
		try {
			PrintStream out = new PrintStream(new File(this.testExport));
			for (int i = 0; i < this.testData.size(); i++) {
				out.print(this.testData.get(i).getInt("label")+" ");
				for (int j = 0; j < attriNum; j++) {
					if (this.beUsed.get(j)) {
						out.print(j+":"+this.testData.get(i).getJSONArray("attri").getDouble(j)+" ");
					}
				}
				out.println();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		FileIO fio = FileIO.getInstance();
		fio.writeAttriForSVM();
	}
	
}
