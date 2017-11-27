% size = 954;
% [num] = xlsread('dataset3.xlsx');
size = 328;
[num] = xlsread('dataset4.xlsx');
[line] = textread('etaSVM2attri.txt');
attri1 = num(:,4);
attri2 = num(:,5);
tmp(1:size) = NaN;
for i = 1:size
    if (num(i,11) == 1)
        tmp(i) = 1;
    end
end
males1 = tmp' .* attri1;
males2 = tmp' .* attri2;
scatter(males1, males2, 80, 'b.')
hold on
tmp(1:size) = NaN;
for i = 1:size
    if (num(i,11) == 0)
        tmp(i) = 1;
    end
end
females1 = tmp' .* attri1;
females2 = tmp' .* attri2;
scatter(females1, females2, 80, 'r.')
hold on
plot(line(:,1), line(:,2),'k','linewidth',0.8)
xlabel('attribute 4')
ylabel('attribute 5')
title('测试集上两个属性的男女生线性SVM判别效果图')
legend('male','female')
xlim([10,35])
ylim([140,210])
box on
saveas(gcf,'SVMcurveTest.png')