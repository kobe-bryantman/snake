/*
逻辑：1.蛇移动
     2.判断是否吃到食物
       吃到--》长度加一+刷新食物+得分加10
     3.碰撞判断--》撞墙/自撞
     4.重绘界面【移动速度提升（刷新间隔-10ms）】
成员：
    1.得分int score：得分等于食物刷新次数（若未通关则为界面重绘次数）×10
    【理论最大值约等于6000（蛇占满地图）】
    【0<=score<=10000】
    2.蛇Snake snake
    3.食物Food food
    4。刷新率（速度）Timer timer
    5，撞墙boolean isHitWall
    6.计时int time
    7.游戏结束判断：boolean isGameOver（snake自撞snake。isHitSelf==true||撞墙isHitWall==true
    ||满足食物生成不了胜利GameEndException==true）
=================================================================
方法：
    1：绘制 paintComponent（待定）“打印’背景‘’蛇身体‘’食物‘’分数‘计时’游戏结束文字（满足条件时）‘”
    【初始化蛇，食物（实现方式待定）】
    【外观，*为头身】
    【起始位置为地图中央即（10，16）】
    【身体向左延伸至（10，15），（10，14）】
    【初始方向向右（下一步移向17列）】
    【初始长度：3节】
    【初始方向：向右】
======================================================================
    地图绘制：
            1.规格：20行×30列
            2。墙壁占用边缘网格：即第0行，第21行，第0列，第31列为墙壁（用’*‘号渲染）】
    2，开启键盘监听（键盘录入）
    3.定时器掌管刷新（控制游戏底层逻辑）
    实现    1.蛇移动
           2.判断是否吃到食物
           吃到--》变长+刷新食物+得分加10
           3.碰撞判断--》撞墙/自撞--》游戏结束
           4.重绘界面【移动速度提升（刷新间隔-10ms）】
   4.结束动作：GameOver
                1.停止移动与计时
                2.显示最终得分，游戏总时长（秒）
                3.提供”重新开始“按钮（重置所有状态）
    优化（若时间充足）：添加q键退出游戏。
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JFrame {
    // 改用Swing专用定时器（线程安全）
    private javax.swing.Timer timer;
    private Snake snake;
    public int score = 0;
    private JPanel jPanel;
    private Node food;
    // 计时相关
    private long startTime;
    private int gameTime; // 游戏时长（秒）

    public GamePanel() throws HeadlessException {
        // 1. 先初始化jPanel（创建对象）
        initGamePenel();
        // 2. 再初始化窗体（jPanel已存在，无空指针）
        initFrame();
        // 3. 后续初始化
        initSnake();
        initFood();
        initTimer();
        setKeyListener();
        startTime = System.currentTimeMillis();
    }

    private void initFood() {
        food = new Node();
        Random r = new Random();
        boolean isOnSnake; // 标记食物是否在蛇身上
        do {
            food.setX(r.nextInt(30)); // 0-29列
            food.setY(r.nextInt(20)); // 0-19行
            isOnSnake = false;
            // 检查食物是否与蛇身重叠
            for (Node node : snake.getBody()) {
                if (food.getX() == node.getX() && food.getY() == node.getY()) {
                    isOnSnake = true;
                    break;
                }
            }
        } while (isOnSnake); // 重叠则重新生成
    }

    private void setKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 游戏结束后不响应方向键
                if (!snake.isLiving()) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (snake.getDirection() != Direction.DOWN) {
                            snake.setDirection(Direction.UP);
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (snake.getDirection() != Direction.UP) {
                            snake.setDirection(Direction.DOWN);
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (snake.getDirection() != Direction.RIGHT) {
                            snake.setDirection(Direction.LEFT);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (snake.getDirection() != Direction.LEFT) {
                            snake.setDirection(Direction.RIGHT);
                        }
                        break;
                    case KeyEvent.VK_Q: // 新增Q键退出
                        System.exit(0);
                        break;
                }
            }
        });
    }

    private void initSnake() {
        snake = new Snake();
    }

    private void initTimer() {
        // 取消旧定时器（若存在）
        if (timer != null) {
            timer.stop();
        }
        // Swing定时器（EDT线程执行，安全）
        timer = new javax.swing.Timer(snake.getSpeed(), e -> {
            if (snake.isLiving()) {
                // 计算游戏时长
                gameTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
                snake.move();
                Node head = snake.getBody().getFirst();
                // 判断是否吃到食物
                if (head.getX() == food.getX() && head.getY() == food.getY()) {
                    snake.eat();
                    score += 10;
                    initFood();
                    // 速度变化后重启定时器（更新间隔）
                    initTimer();
                }
                // 重绘（EDT中执行）
                jPanel.repaint();
            } else {
                // 游戏结束
                ((javax.swing.Timer) e.getSource()).stop();
                gameOver();
            }
        });
        timer.start();
    }

    // 游戏结束处理
    private void gameOver() {
        if (timer != null) {
            timer.stop();
        }
        // 计算最终时长
        gameTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        JButton restartBtn = new JButton("重新开始");
        restartBtn.setBounds(250, 300, 100, 30);
        restartBtn.addActionListener(e -> restartGame());
        jPanel.add(restartBtn);
        jPanel.repaint();
    }

    // 重新开始游戏
    private void restartGame() {
        jPanel.removeAll();
        initSnake();
        initFood();
        score = 0;
        startTime = System.currentTimeMillis(); // 重置计时
        initTimer(); // 重启定时器
        jPanel.repaint();
        requestFocusInWindow(); // 重新获取焦点
    }

    private void initGamePenel() {
        jPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // 必须调用父类方法
                // 绘制网格
                for (int i = 0; i <= 20; i++) {
                    g.drawLine(0, i * 30, 600, i * 30);
                }
                for (int i = 0; i <= 30; i++) {
                    g.drawLine(i * 20, 0, i * 20, 600);
                }
                // 绘制蛇（加空指针防护，避免snake未初始化）
                if (snake != null && snake.getBody() != null) {
                    LinkedList<Node> body = snake.getBody();
                    for (Node node : body) {
                        g.fillRect(node.getX() * 20, node.getY() * 30, 20, 30);
                    }
                }
                // 绘制食物（加空指针防护）
                if (food != null) {
                    g.setColor(Color.RED);
                    g.fillRect(food.getX() * 20, food.getY() * 30, 20, 30);
                    g.setColor(Color.BLACK);
                }
                // 绘制得分、时长等（加空指针防护）
                if (snake != null) {
                    g.setColor(Color.RED);
                    g.setFont(new Font("宋体", Font.BOLD, 20));
                    g.drawString("得分：" + score, 10, 20);
                    g.drawString("蛇长度：" + snake.getLength(), 100, 20);
                    g.drawString("时长：" + gameTime + "s", 200, 20);

                    // 游戏结束信息
                    if (!snake.isLiving()) {
                        g.drawString("游戏结束！最终得分：" + score, 200, 300);
                        g.drawString("最终蛇长度：" + snake.getLength(), 200, 330);
                        g.drawString("总时长：" + gameTime + "s", 200, 360);
                    }
                }
            }
        };
        jPanel.setLayout(null);
        this.add(jPanel); // 关键：将jPanel添加到窗体
    }

    private void initFrame() {
        setSize(610, 640);
        setLocation(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        if (jPanel != null) { // 空指针防护
            jPanel.setFocusable(false);
        }
        requestFocusInWindow();
    }

    // 设置程序主入口
    public static void main(String[] args) {
        // Swing组件必须在EDT中创建
        SwingUtilities.invokeLater(() -> new GamePanel().setVisible(true));
    }
}