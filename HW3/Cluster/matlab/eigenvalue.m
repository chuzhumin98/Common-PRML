[eigenvalues] = textread('eigenvalue.txt');
plot(eigenvalues)
hold on 
plot(eigenvalues, '.', 'MarkerSize',20)
xlabel('����ֵ���')
ylabel('����ֵ��С')
title('�����ɷ�����ֵ(����)��С')
box on
saveas(gcf,'eigenvalue.png')