package Game.Equipment;

public class RangedWeapons extends Weapon{

    public enum Bullets {
        BULLET, L_BULLET, R_BULLET, SHOT
    }

    private double bulletSpeed;

    public RangedWeapons(int power, int accuracy, double bulletSpeed) {
        super(power, accuracy);
        this.bulletSpeed = bulletSpeed;
    }

}
