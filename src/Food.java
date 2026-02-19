/*
成员：
    食物位置int x，y；
    食物无法生成boolean GameEndException（初始为false）
方法：randomPosition：随机生成位置
     printFood：打印食物于食物位置，形状暂定，必须与边界区分
补充：食物生成时需判定且若无法满足生成条件重新生成时次数应小于五避免死循环，【疑惑】
     若已无位置
     触发GameEndException（“地图已满”）显示“恭喜通关！地图已无空位”
*/
public class Food {
    private int x;
    private int y;
}
