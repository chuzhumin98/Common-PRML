#include <iostream>
#include <fstream>
#include <cmath>
#define Zero(x) (x < 1e-8 && x > -1e-8)
using namespace std;
double ABS(double x) {
	if (x < 0) return -x;
	return x;
} 
double Determinate (double** A, int n);
struct Parameter {
	double* mu;
	double** sigma;
	double** sigmani;
	double deter; 
	int size;
	Parameter(int s) {
		size = s;
		mu = new double [size];
		sigma = new double* [size];
		sigmani = new double* [size];
		for (int i = 0; i < size; i++) {
			sigma[i] = new double [size];
			sigmani[i] = new double [size];
		}
	}
	void InitPara() {
		for (int i = 0; i < size; i++) {
			mu[i] = 0.0;
			for (int j = 0; j < size; j++) {
				sigma[i][j] = 0.0;
				sigmani[i][j] = 0.0;
			}
		}
	}
	~Parameter() {
		delete []mu;
		for (int i = 0; i < size; i++) {
			delete []sigma[i];
			delete []sigmani[i];
		}
		delete []sigma;
		delete []sigmani;
	}
	void QiuNi();
};
class Bayesian {
public:
	bool needNiHandle;
	int attrinum;
	Parameter* male;
	Parameter* female;
	Bayesian(int size) {
		needNiHandle = false; //判断是否遇到行列式为零的情况 
		attrinum = size;
		male = new Parameter(size);
		female = new Parameter(size);
	}
	~Bayesian() {
		//delete male;
		//delete female;
	}
	void trainParameter(const char* infile, int row);
};
double ercixing(double* x, double** A, int n) {
	double sums = 0.0;
	double* tmpArrow = new double [n];
	for (int j = 0; j < n; j++) {
		tmpArrow[j] = 0.0;
	}
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			tmpArrow[i] += x[j] * A[j][i]; 
		}
	}
	for (int i = 0; i < n; i++) {
		sums += x[i] * tmpArrow[i];
	}
	delete []tmpArrow;
	return sums;
}
void TestBayesian(double** data, bool* label, int attrinum, int row, Bayesian bys) {
	int countRight = 0;
	double* tmp = new double [attrinum];
	for (int i = 0; i < row; i++) {		
		double sums = -log(bys.male->deter) + log(bys.female->deter);
		for (int j = 0; j < attrinum; j++) {
			tmp[j] = data[i][j] - bys.male->mu[j];
		}
		sums -= ercixing(tmp, bys.male->sigmani, attrinum);
		for (int j = 0; j < attrinum; j++) {
			tmp[j] = data[i][j] - bys.female->mu[j];
		}
		sums += ercixing(tmp, bys.female->sigmani, attrinum);
		if (sums * (label[i] - 0.5) > 0) {
			countRight++;
		}
	}
	double ita = countRight * 1.0 / row;
	cout << "the right percentage is " << ita << endl;
	delete []tmp;
}

void readTestFile(const char* infile, int row, int attrinum, double** datas, bool* label);
void Curve2Attri(const char* outfile, Bayesian bys) {
	ofstream fout(outfile);
	const double stepX = 0.01;
	const double stepY = 0.05;
	const int sizeX = 2200;
	const int sizeY = 1000;
	const double X0 = 10;
	const double Y0 = 140;
	double* tmp = new double [2];
	for (int i = 0; i < sizeX; i++) {
		double nowX = X0 + i * stepX;
		double minYsign = Y0;
		double minY = 1e100;
		for (int j = 0; j < sizeY; j++) {
			double nowY = Y0 + j * stepY;
			double sums = -log(bys.male->deter) + log(bys.female->deter);
			tmp[0] = nowX - bys.male->mu[0];
			tmp[1] = nowY - bys.male->mu[1];
			sums -= ercixing(tmp, bys.male->sigmani, 2);
			tmp[0] = nowX - bys.female->mu[0];
			tmp[1] = nowY - bys.female->mu[1];
			sums += ercixing(tmp, bys.female->sigmani, 2);
			if (ABS(sums) < minY) {
				minYsign = nowY;
				minY = ABS(sums);
			}
		}
		fout << nowX << " " << minYsign << endl;
	}
	delete []tmp;
	fout.close();
} 

int main() {
	const int attrinum = 2; 
	Bayesian bys(attrinum);
	bys.trainParameter("dataset3_20.txt", 20);	
	const int testrow = 328;
	//const int testrow = 954;
	//const int testrow = 20;
	const int testattri = attrinum;
	double** testdata = new double* [testrow];
	for (int i = 0; i < testrow; i++) {
		testdata[i] = new double [testattri];
	}
	bool* testlabel = new bool [testrow];
	readTestFile("dataset4.txt", testrow, testattri, testdata, testlabel);
	TestBayesian(testdata, testlabel, testattri, testrow, bys);
	//Curve2Attri("bayes2attri.txt", bys);
	for (int i = 0; i < testrow; i++) {
		delete []testdata[i];
	}
	delete []testlabel;
	delete []testdata;
//	Bayesian bys2(2);
//	bys2.trainParameter("dataset3.txt", 954);
//	Bayesian bys3(10);
//	bys3.trainParameter("dataset4.txt", 328);
	return 0;
} 

void readTestFile(const char* infile, int row, int attrinum, double** datas, bool* label) {
	ifstream fin(infile);
	double data[10];
	for (int i = 0; i < row; i++) {
		for (int j = 0; j < 10; j++) {
			fin >> data[j];
		}
		char tmp = ' ';
		while (true) {
			fin >> tmp;
			if (tmp == 'M' || tmp == 'F' || tmp == 'm' || tmp == 'f') {
				if (tmp == 'M' || tmp == 'm') {
					label[i] = true;
				} else {
					label[i] = false;
				}
				break;
			}
		}
		if (attrinum == 10) {
			for (int j = 0; j < 10; j++) {
				datas[i][j] = data[j];
			}
		} else {
			datas[i][0] = data[3];
			datas[i][1] = data[4];
		}
		
	}
}


void Bayesian::trainParameter(const char* infile, int row) {
	if (row == 20 && attrinum == 10) {
		needNiHandle = true;
	} 
	ifstream fin(infile);
	double data[10];
	bool* isMale = new bool [row];
	double** datas = new double* [row];
	for (int i = 0; i < row; i++) {
		datas[i] = new double [this->attrinum];
	}
	for (int i = 0; i < row; i++) {
		for (int j = 0; j < 10; j++) {
			fin >> data[j];
		}
		char tmp = ' ';
		while (true) {
			fin >> tmp;
			if (tmp == 'M' || tmp == 'F' || tmp == 'm' || tmp == 'f') {
				if (tmp == 'M' || tmp == 'm') {
					isMale[i] = true;
				} else {
					isMale[i] = false;
				}
				break;
			}
		}
		if (this->attrinum == 10) {
			for (int j = 0; j < 10; j++) {
				datas[i][j] = data[j];
			}
		} else {
			datas[i][0] = data[3];
			datas[i][1] = data[4];
		}
		
	}
	int countM = 0, countF = 0;
	this->male->InitPara();
	this->female->InitPara();
	for (int i = 0; i < row; i++) {
		//cout << i << ": " << datas[i][0] << endl;
		if (isMale[i]) {
			countM++;
			for (int j = 0; j < this->attrinum; j++) {
				this->male->mu[j] += datas[i][j];
			}
		} else {
			countF++;
			for (int j = 0; j < this->attrinum; j++) {
				this->female->mu[j] += datas[i][j];
			}
		}
	}
	for (int i = 0; i < this->attrinum; i++) {
		this->male->mu[i] = this->male->mu[i] * 1.0 / countM;
		this->female->mu[i] = this->female->mu[i] * 1.0 / countF;
		cout << "attri " << i+1 << " " << this->male->mu[i] << " " << this->female->mu[i] << endl; 
	}
	for (int i = 0; i < row; i++) {
		for (int j = 0; j < this->attrinum; j++) {
			for (int k = 0; k < this->attrinum; k++) {
				if (isMale[i]) {
					this->male->sigma[j][k] += (datas[i][j] - this->male->mu[j]) * (datas[i][k] - this->male->mu[k]);
				} else {
					this->female->sigma[j][k] += (datas[i][j] - this->female->mu[j]) * (datas[i][k] - this->male->mu[k]);
				}
			}
		}
	}
	cout << "Sigma for male\n";
	for (int i = 0; i < this->attrinum; i++) {
		for (int j = 0; j < this->attrinum; j++) {
			this->male->sigma[i][j] = this->male->sigma[i][j] * 1.0 / countM;
			cout << this->male->sigma[i][j] << " ";
		}
		cout << endl;
	}
	
	cout << "Sigma for female\n";
	for (int i = 0; i < this->attrinum; i++) {
		for (int j = 0; j < this->attrinum; j++) {
			this->female->sigma[i][j] = this->female->sigma[i][j] * 1.0 / countM;
			cout << this->female->sigma[i][j] << " ";
		}
		cout << endl;
	}
	if (needNiHandle) {
		int itas = 1;
		for (int i = 0; i < this->attrinum; i++) {
			this->male->sigma[i][i] *= (1+itas);
			this->female->sigma[i][i] *= (1+itas);
		}
	}
	this->male->deter = Determinate(this->male->sigma, this->attrinum);
	this->female->deter = Determinate(this->female->sigma, this->attrinum);
	this->male->QiuNi();
	this->female->QiuNi();
	Determinate(this->male->sigmani, this->attrinum);
	Determinate(this->female->sigmani, this->attrinum); 
	delete []isMale;
	for (int i = 0; i < row; i++) {
		delete []datas[i];
	}
	delete []datas;
	fin.close();	
}

double Determinate (double** B, int n) { // 计算矩阵A的行列式 
	double** A = new double* [n];
	for (int i = 0; i < n; i++) {
		A[i] = new double [n];
	}
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			A[i][j] = B[i][j];
		}
	}
	for (int i = 0; i < n-1; i++) {
		for (int j = i+1; j < n; j++) {
			double ita = A[j][i] * 1.0 / A[i][i];
			for (int k = 0; k < n; k++) {
				A[j][k] -= ita * A[i][k];
			}
		}
	}
/*	cout << "After determinate:\n";
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			if (Zero(A[i][j])) {
				cout << "0 " ;
			} else {
				cout << A[i][j] << " ";
			}
		}
		cout << endl;
	} */
	double deter = A[0][0];
	for (int i = 1; i < n; i++) {
		deter *= A[i][i];
	}
	for (int i = 0; i < n; i++) {
		delete []A[i];
	}
	delete []A;
	cout << "deter = " << deter << endl;
	return deter;
}

void Parameter::QiuNi() {
	int n = size;
	double** A = new double* [n];
	for (int i = 0; i < n; i++) {
		A[i] = new double [n];
	}
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			A[i][j] = sigma[i][j];
			sigmani[i][j] = 0.0;
		}
	}
	for (int i = 0; i < n; i++) {
		sigmani[i][i] = 1.0;
	}
	for (int k = 0; k < n; k++) {
		if (Zero(A[k][k])) cerr << "Stop!" << endl;
		//cout << "A[k][k]:" << A[k][k] << endl; 
		for (int j = k+1; j < n; j++) {
			A[k][j] = A[k][j] * 1.0 / A[k][k];
			//cout << "j" << j << endl;
		}
		for (int j = 0; j <= k; j++) {
			sigmani[k][j] = sigmani[k][j] * 1.0 / A[k][k];
			//cout << "J" << j << endl;
		}
		for (int i = 0; i < n; i++) {
			if (i != k) {
				for (int j = k+1; j < n; j++) {
					A[i][j] = A[i][j] - A[i][k]*A[k][j];
				}
				for (int j = 0; j <= k; j++) {
					sigmani[i][j] = sigmani[i][j] - A[i][k]*sigmani[k][j];
				}
			} 
			
		} 
	} 
	for (int i = 0; i < n; i++) {
		delete []A[i];
	}
	delete []A;
}
