package Player;

import Game.GameEngine;
import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;

public class Character {
    double weaponAngle = 0, deltaWeaponAngle, attackingWeaponAngle = -Math.PI / 2;
    double speed, sprintSpeed, currentSpeed, minDistToBlock = 0.1;
    int health, mana, stamina, maxHealth, maxMana, maxStamina;

    Point2D pos, dir, zero = new Point2D(0, 0), movingDir = zero, lastMovingDir = zero;

    static int[][] map;

    private HashSet<Point2D> dirs = new HashSet<>();

    public Character(double speed, double sprintSpeed, int health, int mana, int stamina, int maxHealth, int maxMana, int maxStamina, Point2D pos, Point2D dir) {
        this.speed = speed;
        this.sprintSpeed = sprintSpeed;
        currentSpeed = speed;
        this.health = health;
        this.mana = mana;
        this.stamina = stamina;
        this.maxHealth = maxHealth;
        this.maxMana = maxMana;
        this.maxStamina = maxStamina;
        this.pos = pos;
        this.dir = normalize(dir);
    }

    void update() {
        movingDir = new Point2D(0, 0);
        for (Point2D p : dirs)
            movingDir = movingDir.add(p);
        movingDir = normalize(movingDir);
        go();

        currentSpeed = speed;
        dirs = new HashSet<>();
    }

    private Point2D normalize(Point2D vec) {
        double d = vec.magnitude();
        return vec.multiply(d == 0 ? 0 : (1 / d));
    }

    public Point2D getPos() {
        return pos;
    }

    public Point2D getDir() {
        return dir;
    }

    public static void setMap(int[][] map) {
        Character.map = map;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    void turn(double angle) {
        double sin = Math.sin(angle), cos = Math.cos(angle);
        dir = new Mtx2x2(cos, -sin, sin, cos).apply(dir);
    }

    void attack() {
        if (deltaWeaponAngle != -.2) {
            deltaWeaponAngle = -.2;
            Audio.resetAndStart(Audio.Sound.SWORD);
        }
    }

    void forward() {
        dirs.add(dir);
    }

    void backward() {
        dirs.add(new Mtx2x2(-1, 0, 0, -1).apply(dir));
    }

    void left() {
        dirs.add(new Mtx2x2(0, -1, 1, 0).apply(dir));
    }

    void right() {
        dirs.add(new Mtx2x2(0, 1, -1, 0).apply(dir));
    }

    void sprint() {
        if (stamina > 0) {
            currentSpeed = sprintSpeed;
            stamina -= 2;
        }
    }

    private void go() {
        pos = pos.add(movingDir.multiply(currentSpeed));
        Point2D safePos = pos.add(new Point2D(movingDir.getX() < 0 ? -minDistToBlock : minDistToBlock, movingDir.getY() < 0 ? -minDistToBlock : minDistToBlock));

        if (block(movingDir, safePos) != 0) {
            Pair<Point2D, Boolean> collisionInfo = collisionInfo(movingDir).getFirst().getKey();
            pos = collisionInfo.getValue() ? new Point2D(Math.floor(pos.getX()) + (movingDir.getX() < 0 ? minDistToBlock : 1 - minDistToBlock), pos.getY()) :
                    new Point2D(pos.getX(), Math.floor(pos.getY()) + (movingDir.getY() < 0 ? minDistToBlock : 1 - minDistToBlock));
        }
    }

    LinkedList<Pair<Pair<Point2D, Boolean>, Point2D>> collisionInfo(Point2D vec) {
        boolean found = true;       // better to init this with fake value than check if closer == null in inner while
        double tg = vec.getY() / vec.getX(), diffY = Math.abs(tg) * Math.signum(vec.getY()), diffX = 1 / Math.abs(tg) * Math.signum(vec.getX()),
                x = vec.getX() < 0 ? Math.floor(pos.getX()) : Math.ceil(pos.getX()), y = vec.getY() < 0 ? Math.floor(pos.getY()) : Math.ceil(pos.getY()),
                lastHeight = 0, distSquaredX = 0, distSquaredY = 0;     // same as with bool found
        Point2D closer = null, px = new Point2D(x, (x - pos.getX()) * tg + pos.getY()), py = new Point2D((y - pos.getY()) / tg + pos.getX(), y),
                dPx = new Point2D(vec.getX() < 0 ? -1 : 1, diffY), dPy = new Point2D(diffX, vec.getY() < 0 ? -1 : 1);
        LinkedList<Pair<Pair<Point2D, Boolean>, Point2D>> list = new LinkedList<>();

        while (lastHeight != 1) {
            while (found || block(vec, closer) == 0) {
                if (closer == px)
                    px = px.add(dPx);
                else if (closer == py)
                    py = py.add(dPy);

                found = false;
                distSquaredX = distSquared(pos, px);
                distSquaredY = distSquared(pos, py);
                closer = distSquaredX < distSquaredY ? px : py;
            }

            boolean pxSide = closer == px;
            Point2D px1 = pxSide ? px.add(dPx) : px, py1 = !pxSide ? py.add(dPy) : py, next = distSquared(pos, px1) < distSquared(pos, py1) ? px1 : py1;
            Pair<Double, Double> wallHeight = GameEngine.getWallHeight().get(block(vec, closer));
            double height = wallHeight.getKey();

            if (height >= lastHeight) {
                list.add(new Pair<>(new Pair<>(closer, pxSide), next));
                lastHeight = height;
                found = true;
            }
        }

        return list;
    }

    private static double distSquared(Point2D p0, Point2D p1) {
        double diffX = p0.getX() - p1.getX(), diffY = p0.getY() - p1.getY();
        return diffX * diffX + diffY * diffY;
    }

    int block(Point2D vec, Point2D collisionPoint) {
        double tempY = collisionPoint.getY(), tempX = collisionPoint.getX();
        int y = (int) tempY - (vec.getY() < 0 && tempY == Math.round(tempY) ? 1 : 0), x = (int) tempX - (vec.getX() < 0 && tempX == Math.round(tempX) ? 1 : 0);
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length ? map[y][x] : 1;
    }
}