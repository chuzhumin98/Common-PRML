package select;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class ExtractMethod {
	public static final int attriNum = 10;
	public static final int extractNum = 2;
	/*
	 * 从类均值中提取判别信息
	 */
	public void basedOnClassAverage() {
		Feature.getInstance();
		Matrix Sw1 = new Matrix(Feature.Sw);
		Matrix Sb1 = new Matrix(Feature.Sb);
		EigenvalueDecomposition eigens = Sw1.eig();
		double[] eigenValue = eigens.getRealEigenvalues();
		Matrix eigenVector = eigens.getV();
		Map<Integer,Double> map = new HashMap<Integer,Double>(); //记录index与J(y)的对应关系
		for (int i = 0; i < attriNum; i++) {
			Matrix ui = eigenVector.getMatrix(0, attriNum-1, i, i);
			Matrix numerator = ui.transpose().times(Sb1).times(ui);
			double Jyi = numerator.get(0, 0) / eigenValue[i];
			map.put(i, Jyi);
			System.out.println(i+":"+Jyi);
		}
		//下面找出最佳的两维特征
		int[] maxIndex = new int [extractNum];
		for (int i = 0; i < extractNum; i++) {
			int bestIndex = 0;
			double maxValue = Double.MIN_VALUE;
			for (Entry<Integer,Double> item: map.entrySet()) {
				if (item.getValue() > maxValue) {
					bestIndex = item.getKey();
					maxValue = item.getValue();
				}
			}
			maxIndex[i] = bestIndex;
			map.remove(bestIndex);
		}
		
		System.out.print("the best dim:");
		for (int i = 0; i < extractNum; i++) {
			System.out.print(maxIndex[i]+" ");
		}
		System.out.println();
		
		FileIO fio = FileIO.getInstance();
		fio.writeExtractAttriForSVM(eigenVector, maxIndex); //向文件中写入提取特征
		TrainAndTestByLibSVM tat = TrainAndTestByLibSVM.getInstance();  
        System.out.println("正在训练分类模型。。。。");  
        tat.trainByLibSVM(FileIO.trainExport);  
        System.out.println("正在应用分类模型进行分类。。。。");  
        tat.tellByLibSVM(FileIO.testExport);  
		
		/*eigenVector.print(10, 10);
		eigenVector.getMatrix(0, 9, 0, 0).print(10, 10); */
		/*for (int i = 0; i < eigenValue.length; i++){
			System.out.print(eigenValue[i]+" ");
		}
		System.out.println("\n");		
		eigenVector.print(10, 10);
		System.out.println("\n");
		Sw1.times(eigenVector).print(10, 10); */
	}
	 
	public static void main(String[] args) {
		ExtractMethod em = new ExtractMethod();
		em.basedOnClassAverage();
	}
}
