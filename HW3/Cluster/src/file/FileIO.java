package file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class FileIO {
	public int attriNumInital = 10; //初始特征维数为10
	public int attriNumPCA = 2; //PCA变换后的特征维数为2(PCA变换后才确定的)
	public ArrayList<PersonEntry> personInital = new ArrayList<PersonEntry>(); //建立一个所有样本的数组
	public ArrayList<PersonEntry> personPCA = new ArrayList<PersonEntry>(); //存储PCA变换之后的特征
	public Matrix sigma = null; //协方差矩阵
	public double[] mu = null; //均值mu向量 
	
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
		mu = new double [this.attriNumInital];
		this.sigma = new Matrix(this.attriNumInital, this.attriNumInital);
		try {
			@SuppressWarnings("resource")
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
			this.calculateSigma();
			this.doPCA();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 计算mu均值向量和协方差矩阵
	 */
	private void calculateSigma() {
		/*
		 * 首先计算mu均值向量
		 */
		for (int i = 0; i < this.attriNumInital; i++) {
			this.mu[i] = 0.0; //首先初始化为0
			for (PersonEntry item: this.personInital) {
				this.mu[i] += item.attris[i];
			}
			this.mu[i] /= this.personInital.size();
		}
		
		/*
		 * 在此基础上计算sigma协方差矩阵
		 */
		for (int i = 0; i < this.attriNumInital; i++) {
			for (int j = 0; j < this.attriNumInital; j++) {
				double total = 0.0;
				for (PersonEntry item: this.personInital) {
					total += (item.attris[i]-this.mu[i]) * (item.attris[j]-this.mu[j]);
				}
				total /= this.personInital.size();
				this.sigma.set(i, j, total);
			}
		}
		//System.out.println("following is the sigma matrix:");
		//this.sigma.print(10, 10);
		
	}
	
	private void doPCA() {
		EigenvalueDecomposition eigens = this.sigma.eig();
		Matrix eigenVector = eigens.getV();
		
		Matrix vector1 = eigenVector.getMatrix(0, this.attriNumInital-1, this.attriNumInital-1, this.attriNumInital-1);
		eigenVector.print(10, 10);
		vector1.print(10, 10);
		this.sigma.times(vector1).print(10, 10);
		
		//eigens.getD().print(10, 10);
	}
	
	public static void main(String[] args) {
		FileIO file = FileIO.getInstance();
		
	}
}
