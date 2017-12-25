[single] = textread('initialhierdistsinglelink.txt');
[complete] = textread('initialhierdistcompletelink.txt');
[average] = textread('initialhierdistaveragelink.txt');
plot(log(single),'b')
hold on
plot(log(complete),'r')
hold on 
plot(log(average),'k')
legend('single','complete','average','location','SouthEast')
xlabel('merge�����Ľ�չ')
xlim([0,952])
ylabel('log(distance)')
title('ԭʼ�������ϲ������ľ���Ķ�����merge������չ�ı仯')
box on
saveas(gcf,'initialdistance.png')