#include <iostream>
#include <fstream>
using namespace std;
void FormatTrans(const char* infile, const char* outfile, int row) {
	ifstream fin(infile);
	ofstream fout(outfile);
	double data[10];
	for (int i = 0; i < row; i++) {
		for (int j = 0; j < 10; j++) {
			fin >> data[j];
		}
		char tmp = ' ';
		while (true) {
			fin >> tmp;
			if (tmp == 'M' || tmp == 'F' || tmp == 'm' || tmp == 'f') {
				if (tmp == 'M') {
					fout << "1 ";
				} else {
					fout << "0 ";
				}
				break;
			}
		}
		for (int j = 0; j < 10; j++) {
			fout << j+1 << ":" << data[j] << " ";
		}
		fout << endl;
	}
	
	fin.close();
	fout.close();
}
void FormatTransfor2(const char* infile, const char* outfile, int row) {
	ifstream fin(infile);
	ofstream fout(outfile);
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
					fout << "1 ";
				} else {
					fout << "0 ";
				}
				break;
			}
		}
		for (int j = 3; j < 5; j++) {
			fout << j+1 << ":" << data[j] << " ";
		}
		fout << endl;
	}
	
	fin.close();
	fout.close();
}
int main() {
	FormatTrans("dataset3_20.txt", "dataset3_20svm.txt", 20);
	//FormatTrans("dataset3.txt", "dataset3svm.txt", 954);
	//FormatTrans("dataset4.txt", "dataset4svm.txt", 328);
	//FormatTransfor2("dataset3.txt", "dataset3svmfor2.txt", 954);
	//FormatTransfor2("dataset4.txt", "dataset4svmfor2.txt", 328);
	FormatTransfor2("dataset3_20.txt", "dataset3_20svmfor2.txt", 20);
	return 0;
} 
