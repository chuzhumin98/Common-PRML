C = 6;
infile = strcat('initialC=',num2str(C),'hieraveragelink.txt');
[num] = textread(infile);
attri1 = 3;
attri2 = 4;
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
xlabel('初始的属性3')
ylabel('初始的属性4')
title(strcat('分级聚类对原始属性的',num2str(C),'-聚类效果'))
box on
file1 = strcat('img/hieraveragelinkattri3-4InitialC=',num2str(C),'.png');
saveas(gcf,file1)

figure
attri1 = 9;
attri2 = 10;
data1 = num(:,attri1);
data2 = num(:,attri2);
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
xlabel('初始的属性9')
ylabel('初始的属性10')
title(strcat('分级聚类对原始属性的',num2str(C),'-聚类效果'))
box on
file2 = strcat('img/hieraveragelinkattri9-10InitialC=',num2str(C),'.png');
saveas(gcf,file2)