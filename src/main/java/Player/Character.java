package Player;

import Game.Equipment.Weapon;
import Game.GameEngine;
import Game.Audio;
import Player.Helper.RenderVect;
import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;

public class Character {
    double weaponAngle = 0, deltaWeaponAngle, attackingWeaponAngle = -Math.PI / 2;
    double speed, sprintSpeed, currentSpeed, minDistToBlock = 0.1;
    int health, mana, stamina, maxHealth, maxMana, maxStamina;

    Point2D pos, dir, zero = new Point2D(0, 0), movingDir = zero, lastMovingDir = zero;
    Weapon.Weapons weapon;

    static int[][] map;

    private HashSet<Point2D> dirs = new HashSet<>();
    LinkedList<Weapon.Weapons> weapons = new LinkedList<>();

    public Character(double speed, double sprintSpeed, int health, int mana, int stamina, int maxHealth, int maxMana, int maxStamina, Point2D pos, Point2D dir,
                     LinkedList<Weapon.Weapons> weapons) {
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
        this.weapons = weapons;
    }

    void update() {
        movingDir = new Point2D(0, 0);
        for (Point2D p : dirs)
            movingDir = movingDir.add(p);
        movingDir = normalize(movingDir);
        go();

        if (lastMovingDir.equals(zero) && !movingDir.equals(zero))
            Audio.start(Audio.Sound.STEPS);
        else if (movingDir.equals(zero))
            Audio.stop(Audio.Sound.STEPS);

        if (stamina < maxStamina) {
            stamina++;
        }

        if (deltaWeaponAngle != 0)
            updateWeaponAngle();

        currentSpeed = speed;
        dirs = new HashSet<>();
    }

    private void updateWeaponAngle() {
        if (deltaWeaponAngle > 0) {
            if (weaponAngle < 0)
                weaponAngle += deltaWeaponAngle;
            else {
                weaponAngle = 0;
                deltaWeaponAngle = 0;
            }
        }
        else {
            if (weaponAngle > attackingWeaponAngle)
                weaponAngle += deltaWeaponAngle;
            else {
                weaponAngle = attackingWeaponAngle;
                deltaWeaponAngle *= -1;
            }
        }
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

    public Weapon.Weapons getWeapon() {
        return weapon;
    }

    public double getWeaponAngle() {
        return weaponAngle;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void turn(double angle) {
        double sin = Math.sin(angle), cos = Math.cos(angle);
        dir = new RenderVect(cos, -sin, sin, cos).apply(dir);
    }

    public void attack() {
        if (deltaWeaponAngle != -.2) {
            deltaWeaponAngle = -.2;
            Audio.resetAndStart(Audio.Sound.SWORD);
        }
    }

    public void forward() {
        dirs.add(dir);
    }

    public void backward() {
        dirs.add(new RenderVect(-1, 0, 0, -1).apply(dir));
    }

    public void left() {
        dirs.add(new RenderVect(0, -1, 1, 0).apply(dir));
    }

    public void right() {
        dirs.add(new RenderVect(0, 1, -1, 0).apply(dir));
    }

    public void sprint() {
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

    public LinkedList<Pair<Pair<Point2D, Boolean>, Point2D>> collisionInfo(Point2D vec) {
        boolean found = true;
        double tg = vec.getY() / vec.getX(), diffY = Math.abs(tg) * Math.signum(vec.getY()), diffX = 1 / Math.abs(tg) * Math.signum(vec.getX()),
                x = vec.getX() < 0 ? Math.floor(pos.getX()) : Math.ceil(pos.getX()), y = vec.getY() < 0 ? Math.floor(pos.getY()) : Math.ceil(pos.getY()),
                lastHeight = 0, distSquaredX = 0, distSquaredY = 0;
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

    public int block(Point2D vec, Point2D collisionPoint) {
        double tempY = collisionPoint.getY(), tempX = collisionPoint.getX();
        int y = (int) tempY - (vec.getY() < 0 && tempY == Math.round(tempY) ? 1 : 0), x = (int) tempX - (vec.getX() < 0 && tempX == Math.round(tempX) ? 1 : 0);
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length ? map[y][x] : 1;
    }
}
