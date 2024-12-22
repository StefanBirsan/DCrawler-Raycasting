package Player;

import javafx.geometry.Point2D;

import java.util.LinkedList;

public class Player extends Character  {
    private double defaultFov = 66 * Math.PI / 180, aimFov = 45 * Math.PI / 180, fov = defaultFov, deltaFov, zDir = 1;

    public Player(double speed, double sprintSpeed, int health, int mana, int stamina, int maxHealth, int maxMana, int maxStamina, Point2D pos, Point2D dir) {
        super(speed, sprintSpeed, health, mana, stamina, maxHealth, maxMana, maxStamina, pos, dir);
    }

    void update() {
        super.update();

        if (deltaFov != 0)
            updateFov();
    }

    void lookVertical(double delta) {
        zDir += delta;
        zDir = zDir < -2 ? -2 : zDir > 2 ? 2 : zDir;
    }

    private void updateFov() {
        if (deltaFov < 0) {
            fov = Math.max(fov + deltaFov, aimFov);
        } else {
            fov = Math.min(fov + deltaFov, defaultFov);
        }

        if (fov == aimFov || fov == defaultFov) {
            deltaFov = 0;
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

    void aim() {
        deltaFov = fov == defaultFov ? -0.035 : fov == aimFov ? 0.035 : deltaFov;
    }
}
