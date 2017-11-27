from libsvm.python.svmutil import *
from libsvm.python.svm import *
import numpy as np

def getABSmin(predictions):
    sign = 1
    min = 100000000000
    for i in range(len(predictions)):
        if (abs(predictions[i][0]) < min):
            sign = i
            min = abs(predictions[i][0])
    return sign

y, x = svm_read_problem('dataset3_20svmfor2.txt') # 训练集
yt, xt = svm_read_problem('dataset4svmfor2.txt') # 测试集
model = svm_train(y, x , "-t 0 -h 0")
print('test:')
p_label, p_acc, p_val = svm_predict(yt, xt, model)
print(p_val)

"""
stepX = 0.01
stepY = 0.05
sizeX = 2200
sizeY = 1000
X0 = 10
Y0 = 140
fita = open('etaSVM2attri.txt', 'w')
yyt = np.zeros(sizeY)
for i in range(sizeX):
    nowX = X0 + i*stepX
    test_data = []
    for j in range(sizeY):
        element = {}
        nowY = Y0 + j*stepY
        element[4] = nowX
        element[5] = nowY
        test_data.append(element)
    p_label, p_acc, p_val = svm_predict(yyt, test_data, model)
    sign = getABSmin(p_val)
    nowY = sign*stepY + Y0
    fita.write("%(1)f %(2)f\n" % {'1': nowX,'2': nowY})
"""
