[single] = textread('initialhierdistsinglelink.txt');
[complete] = textread('initialhierdistcompletelink.txt');
[average] = textread('initialhierdistaveragelink.txt');
plot(log(single),'b')
hold on
plot(log(complete),'r')
hold on 
plot(log(average),'k')
legend('single','complete','average','location','SouthEast')
xlabel('merge操作的进展')
xlim([0,952])
ylabel('log(distance)')
title('原始特征所合并的类别的距离的对数随merge操作进展的变化')
box on
saveas(gcf,'initialdistance.png')