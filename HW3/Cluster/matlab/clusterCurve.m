[curves] = textread('CmeansiPCACurve.txt');
% [row, column] = size(curves);
% X = linspace(1, 100000-9, column);
% for (C = 1:6)
%     plot(X, curves(C,:))
%     hold on
% end
% legend('C=1','C=2','C=3','C=4','C=5','C=6','Location','NorthEastOutside')
% box on
% xlabel('��������')
% ylabel('J_e')
% title('ԭʼ���Ը����������J_e����������ı仯��ϵ')
% saveas(gcf,'initialCurve.png')
% xlim([0 20000])
% saveas(gcf,'initialCurvecutoff.png')
% 
% figure
% for (C = 1:6)
%     normalcurves = curves(C,:) ./ curves(C,1);
%     plot(X, normalcurves)
%     hold on
% end
% legend('C=1','C=2','C=3','C=4','C=5','C=6','Location','NorthEastOutside')
% box on
% xlabel('��������')
% ylabel('J_e')
% title('��һ�����ԭʼ���Ը����������J_e����������ı仯��ϵ')
% saveas(gcf,'normalinitialCurve.png')
% xlim([0 20000])
% saveas(gcf,'normalinitialCurvecutoff.png')
% 
% figure
% change = [];
% for (C = 1:6)
%     change = [change, curves(C,10000)];
% end
% X1 = linspace(1,6,6);
% plot(X1,change)
% hold on 
% plot(X1,change,'.', 'MarkerSize',20);
% box on
% xlabel('������C')
% ylabel('�ȶ����J_e')
% title('ԭʼ����J_e��������ı仯��ϵ')
% saveas(gcf,'initialJe-CCurve.png')