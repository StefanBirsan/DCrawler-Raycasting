package Player;

import javafx.geometry.Point2D;

import Game.Equipment.Weapon;

import java.util.LinkedList;

public class Player extends Character  {
    private double defaultFov = 66 * Math.PI / 180, aimFov = 45 * Math.PI / 180, fov = defaultFov, deltaFov, zDir = 1;

    public Player(double speed, double sprintSpeed, int health, int mana, int stamina, int maxHealth, int maxMana, int maxStamina, Point2D pos, Point2D dir, LinkedList<Weapon.Weapons> weapons) {
        super(speed, sprintSpeed, health, mana, stamina, maxHealth, maxMana, maxStamina, pos, dir, weapons);
        this.weapons.add(Weapon.Weapons.S_SWORD);
        weapon = weapons.getFirst();
    }

    public void update() {
        super.update();

        if (deltaFov != 0)
            updateFov();
    }

    public void lookVertical(double delta) {
        zDir += delta;
        zDir = zDir < -2 ? -2 : zDir > 2 ? 2 : zDir;
    }

    private void updateFov() {
        if (deltaFov < 0) {
            if (fov > aimFov)
                fov += deltaFov;
            else {
                fov = aimFov;
                deltaFov = 0;
            }
        }
        else {
            if (fov < defaultFov)
                fov += deltaFov;
            else {
                fov = defaultFov;
                deltaFov = 0;
            }
        }
    }

    public double getFov() {
        return fov;
    }

    public double getDefaultFov() {
        return defaultFov;
    }

    public double getZDir() {
        return zDir;
    }

    public void aim() {
        deltaFov = fov == defaultFov ? -0.035 : fov == aimFov ? 0.035 : deltaFov;
    }
}
