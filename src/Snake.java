/*
成员：1.蛇身与头（每一节坐标）
     2.蛇移动方向：string Direction；（UP RIGHT DOWN LEFT）
     3，蛇身长度：int length（初始为3）
     4.速度暂移动到游戏逻辑以刷新率控制，此处不添加为成员
方法：initSnake：初始化蛇    【 起始位置为地图中央即（10，16）
                            身体向左延伸至（10，15），（10，14）】
    move：移动逻辑（只涉及打印，即加头删尾）
        ***移动机制拓展：
 1.通过‘w‘a’‘s’‘d’或‘上’‘下’‘左’‘右’控制；
 2.禁止180度转向
 3.按键立即生效于下一次移动（时间间隔内仅记录最后一次有效方向且必须合法（非180度））
 4，每吃到食物移动间隔减少10ms，下限为100ms
    grew：吃食物变长逻辑【加头不删尾（以碰撞食物时位置为新头）】
    isHitSelf：自撞检测
    get/set方法

蛇初始化：外观，*为头身
 起始位置为地图中央即（10，16）
 身体向左延伸至（10，15），（10，14）
 初始方向向右（下一步移向17列）
 初始长度：3节
 初始方向：向右
 移动边界：行1-20；列1-30
 方法：蛇移动机制：自动移动；初始以固定间隔（初始为200ms/s）
 移动机制拓展：
 1.通过‘上’‘下’‘左’‘右’控制；
 2.禁止180度转向
 3.按键立即生效于下一次移动（时间间隔内仅记录最后一次有效方向且必须合法（非180度））
 4，每吃到食物移动间隔减少10ms，下限为100ms
*/

import java.util.LinkedList;

public class Snake {
    int speed = 200;// 速度，单位为毫秒
    private boolean isLiving = true; // 蛇是否存活
    private Direction direction = Direction.RIGHT;
    private LinkedList<Node> body;

    public boolean isLiving() {
        return isLiving;
    }

    public int getSpeed() {
        return speed;
    }

    public int getLength() {
        return body.size();
    }

    public LinkedList<Node> getBody() {
        return body;
    }

    public void setBody(LinkedList<Node> body) {
        this.body = body;
    }

    public Snake() {
        initSnake();
    }

    private void initSnake() {
        body = new LinkedList<>();
        // 初始位置：地图中央（10列，16行），向左延伸
        body.add(new Node(16, 10));
        body.add(new Node(15, 10));
        body.add(new Node(14, 10));
    }

    // 蛇沿蛇头方向移动
    public void move() {
        if (!isLiving) return;

        Node head = body.getFirst();
        // 新增头节点
        switch (direction) {
            case UP:
                body.addFirst(new Node(head.getX(), head.getY() - 1));
                break;
            case DOWN:
                body.addFirst(new Node(head.getX(), head.getY() + 1));
                break;
            case LEFT:
                body.addFirst(new Node(head.getX() - 1, head.getY()));
                break;
            case RIGHT:
                body.addFirst(new Node(head.getX() + 1, head.getY()));
                break;
        }
        // 删除尾节点（移动逻辑：加头删尾）
        body.removeLast();

        // 撞墙判断（0-29列，0-19行）
        head = body.getFirst();
        if (head.getX() < 0 || head.getY() < 0 || head.getX() >= 30 || head.getY() >= 20) {
            isLiving = false;
            return;
        }

        // 自撞判断
        for (int i = 1; i < body.size(); i++) {
            Node node = body.get(i);
            if (head.getX() == node.getX() && head.getY() == node.getY()) {
                isLiving = false;
                break;
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void eat() {
        // 吃食物后加头不删尾（变长）
        Node head = body.getFirst();
        switch (direction) {
            case UP:
                body.addFirst(new Node(head.getX(), head.getY() - 1));
                break;
            case DOWN:
                body.addFirst(new Node(head.getX(), head.getY() + 1));
                break;
            case LEFT:
                body.addFirst(new Node(head.getX() - 1, head.getY()));
                break;
            case RIGHT:
                body.addFirst(new Node(head.getX() + 1, head.getY()));
                break;
        }
        // 速度提升（下限100ms）
        if (this.speed > 100) {
            this.speed -= 10;
        }
    }
}