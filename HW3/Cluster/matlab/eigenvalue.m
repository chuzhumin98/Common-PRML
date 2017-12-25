[eigenvalues] = textread('eigenvalue.txt');
plot(eigenvalues)
hold on 
plot(eigenvalues, '.', 'MarkerSize',20)
xlabel('特征值编号')
ylabel('特征值大小')
title('各主成分特征值(方差)大小')
box on
saveas(gcf,'eigenvalue.png')