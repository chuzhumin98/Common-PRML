C = 6;
infile = strcat('PCAC=',num2str(C),'hiersinglelink.txt');
[num] = textread(infile);
attri1 = 1;
attri2 = 2;
data1 = num(:,attri1);
data2 = num(:,attri2);
[row, column] = size(num);
label = ['cluster 1'; 'cluster 2';'cluster 3';'cluster 4';'cluster 5';'cluster 6'];
color = ['r.';'g.';'b.';'c.';'m.';'k.'];
for (cluster = 1:C) 
    mydata = [];
    for (i = 1:row) 
        mycluster = int16(num(i,column))+1;
        if (mycluster == cluster) 
            mydata = [mydata; data1(i), data2(i)];
        end
    end
    scatter(mydata(:,1), mydata(:,2), 40, color(cluster,:))
    hold on
end
legend(label(1:C,:),'Location','NorthEastOutside')
xlabel('主特征')
ylabel('次特征')
title(strcat('分级聚类对PCA后提取出的属性的',num2str(C),'-聚类效果'))
box on
file1 = strcat('img/hiersinglelinkattri1-2PCAC=',num2str(C),'.png');
saveas(gcf,file1)