import tensorflow as tf
import numpy as np

def getABSmin(predictions):
    sign = 1
    min = 100000000000
    for i in range(len(predictions)):
        if (abs(predictions[i][0]) < min):
            sign = i
            min = abs(predictions[i][0])
    return sign

def add_layer(inputs, in_size, out_size, activation_function=None):
    # add one more layer and return the output of this layer
    Weights = tf.Variable(tf.random_normal([in_size, out_size]))
    biases = tf.Variable(tf.zeros([1, out_size]))
    Wx_plus_b = tf.matmul(inputs, Weights) + biases
    if activation_function is None:
        outputs = Wx_plus_b
    else:
        outputs = activation_function(Wx_plus_b)
    return outputs

train_data = []
train_labels = [] # male for 1, female for -1
test_data = []
test_labels = []
getsplit_data = []
getsplit_labels = []



# import data part
f = open("dataset3.txt")  # 返回一个文件对象
line = f.readline()  # 调用文件的 readline()方法
while line:
    tmp = line.split('\t')
    dataperline = []
    for i in range(10):
        if (i == 3 or i == 4):
            dataperline.append(float(tmp[i])/100)

    train_data.append(dataperline)
    if ('M' in tmp[10]):
        train_labels.append([1])
    else:
        train_labels.append([-1])
    line = f.readline()

print(train_data)
print(train_labels)
f.close()

f2 = open("dataset4.txt")  # 返回一个文件对象
line = f2.readline()  # 调用文件的 readline()方法
while line:
    tmp = line.split('\t')
    dataperline = []
    for i in range(10):
        if (i == 3 or i == 4):
            dataperline.append(float(tmp[i])/100)

    test_data.append(dataperline)
    if ('M' in tmp[10] or 'm' in tmp[10]):
        test_labels.append([1])
    else:
        test_labels.append([-1])
    line = f2.readline()

f2.close()
# define placeholder for inputs to network
xs = tf.placeholder(tf.float32, [None, 2])
ys = tf.placeholder(tf.float32, [None, 1])


# 3.定义神经层：隐藏层和预测层
# add hidden layer 输入值是 xs，在隐藏层有 10 个神经元
l1 = add_layer(xs, 2, 10, activation_function=tf.nn.tanh)
# add output layer 输入值是隐藏层 l1，在预测层输出 1 个结果
prediction = add_layer(l1, 10, 1, activation_function=tf.nn.tanh)

# 4.定义 loss 表达式
# the error between prediciton and real data
loss = tf.reduce_mean(tf.reduce_sum(tf.square(ys - prediction),
                     reduction_indices=[1]))

# 5.选择 optimizer 使 loss 达到最小
# 这一行定义了用什么方式去减少 loss，学习率是 0.1
lam = 0.1
train_step = tf.train.GradientDescentOptimizer(0.1).minimize(loss)
optimizer = tf.train.MomentumOptimizer(lam, 0.1).minimize(loss)

# important step 对所有变量进行初始化
init = tf.initialize_all_variables()
sess = tf.Session()
# 上面定义的都没有运算，直到 sess.run 才会开始运算
sess.run(init)
fita = open('itaann2attri.txt', 'w')
# 迭代 1000 次学习，sess.run optimizer
for i in range(10000):
    sess.run(optimizer, feed_dict={xs: train_data, ys: train_labels})
    if (i + 1) % 1000 == 0:
        # to see the step improvement
        # print((sess.run(loss, feed_dict={xs: train_data, ys: train_labels})))
        count = 0
        predictions = (sess.run(prediction, feed_dict={xs: train_data, ys: train_labels}))
        for j in range(len(predictions)):
            if (predictions[j][0] * train_labels[j][0] > 0):
                count += 1
        print("iter %(1)d:" % {'1': i + 1}, end=' ')
        print(count / len(predictions), end=' ')
        count2 = 0
        predictions2 = (sess.run(prediction, feed_dict={xs: test_data, ys: test_labels}))
        for j in range(len(predictions2)):
            if (predictions2[j][0] * test_labels[j][0] > 0):
                count2 += 1
        print(count2 / len(predictions2))
        fita.write("%(1)f %(2)f\n" % {'1': count / len(predictions), '2': count2 / len(predictions2)})

fita.close()


#predictions = (sess.run(prediction, feed_dict={xs: train_data, ys: train_labels}))
#print(predictions)

# 输出curve部分
"""
fita2 = open('curveann2attri.txt', 'w')
stepX = 0.01
stepY = 0.05
sizeX = 2200
sizeY = 1000
X0 = 10
Y0 = 140
getsplit_labels = []
for i in range(sizeX):
    getsplit_labels.append([0])
for i in range(sizeX):
    nowX = (X0+i*stepX)/100
    getsplit_data = []
    for j in range(sizeY):
        getsplit_data.append([nowX, (Y0+j*stepY)/100])
    predictions = (sess.run(prediction, feed_dict={xs: getsplit_data, ys: getsplit_labels}))
    sign = getABSmin(predictions)
    nowY = (Y0+sign*stepY)/100
    fita2.write("%(1)f %(2)f\n" % {'1': nowX*100, '2': nowY*100})
    if ((i+1)%50==0):
        print(i+1)
"""
