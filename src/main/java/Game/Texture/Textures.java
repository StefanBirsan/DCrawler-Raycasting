package Game.Texture;

import Game.Equipment.Weapon;
import Game.Equipment.RangedWeapons;
import NPCs.NPC;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class Textures {

    private static Hashtable<Sprite.Sprites, Sprite> sprites = new Hashtable<>();
    private static Hashtable<Integer, Sprite.Sprites> blocks = new Hashtable<>();
    private static Hashtable<Integer, Sprite.Sprites> floors = new Hashtable<>();
    private static Hashtable<Integer, Sprite.Sprites> ceilings = new Hashtable<>();
    private static Hashtable<NPC.NPCs, Hashtable<NPC.Position, Sprite.Sprites>> NPCs = new Hashtable<>();
    private static Hashtable<Weapon.Weapons, Sprite.Sprites> weapons = new Hashtable<>();
    private static Hashtable<RangedWeapons.Bullets, Sprite.Sprites> bullets = new Hashtable<>();
    private static Hashtable<Integer, Sprite.Sprites> healthbar = new Hashtable<>();
    private static Hashtable<Integer, Sprite.Sprites> manabar = new Hashtable<>();

    public static void init() {
        try {
            initSprites();

            initBlocks();
            initFloors();
            initCeilings();
            initWeapons();
            initHealthAndManabar();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initHealthAndManabar() {
        healthbar.put(8, Sprite.Sprites.H0);

        manabar.put(8, Sprite.Sprites.M0);
    }

    private static void initWeapons() {
        weapons.put(Weapon.Weapons.S_SWORD, Sprite.Sprites.S_SWORD);
    }


    private static void initCeilings() {
        ceilings.put(0, Sprite.Sprites.CEILING0);
    }

    private static void initFloors() {
        floors.put(0, Sprite.Sprites.FLOOR0);
    }

    private static void initBlocks() {
        blocks.put(1, Sprite.Sprites.BG1);
        blocks.put(2, Sprite.Sprites.BG2);
        blocks.put(3, Sprite.Sprites.BG3);
        blocks.put(4, Sprite.Sprites.BG4);
        blocks.put(5, Sprite.Sprites.BG5);
        blocks.put(6, Sprite.Sprites.BG6);
    }


    public static Hashtable<Integer, Sprite.Sprites> getHealthbar() {
        return healthbar;
    }

    public static Hashtable<Integer, Sprite.Sprites> getManabar() {
        return manabar;
    }

    public static Hashtable<Weapon.Weapons, Sprite.Sprites> getWeapons() {
        return weapons;
    }

    public static Hashtable<NPC.NPCs, Hashtable<NPC.Position, Sprite.Sprites>> getNPCs() {
        return NPCs;
    }

    public static Hashtable<Integer, Sprite.Sprites> getCeilings() {
        return ceilings;
    }

    public static Hashtable<Integer, Sprite.Sprites> getFloors() {
        return floors;
    }

    public static Hashtable<Integer, Sprite.Sprites> getBlocks() {
        return blocks;
    }

    public static Hashtable<Sprite.Sprites, Sprite> getSprites() {
        return sprites;
    }

    private static void initSprites() throws IOException {
        sprites.put(Sprite.Sprites.S_SWORD, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/weapons/sword2.png"))}));

        sprites.put(Sprite.Sprites.VIEWFINDER, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/viewfinder.png"))}));

        sprites.put(Sprite.Sprites.BG1, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall5.jpeg"))}));
        sprites.put(Sprite.Sprites.BG2, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall4.jpeg"))}));
        sprites.put(Sprite.Sprites.BG3, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall3.jpeg"))}));
        sprites.put(Sprite.Sprites.BG4, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall2.jpeg"))}));
        sprites.put(Sprite.Sprites.BG5, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall1.jpeg"))}));
        sprites.put(Sprite.Sprites.BG6, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/walls/wall0.jpeg"))}));

        sprites.put(Sprite.Sprites.FLOOR0, new Sprite(new BufferedImage[]{ImageIO.read(new File("./res/floors/floor.jpeg"))}));

        sprites.put(Sprite.Sprites.CEILING0, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/ceilings/0m.jpeg"))}));


        sprites.put(Sprite.Sprites.H0, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/healthbar/0h.png"))}));

        sprites.put(Sprite.Sprites.M0, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/manabar/IGOTNOMANA.png"))}));

        sprites.put(Sprite.Sprites.MENU_BG, new Sprite(new BufferedImage[]{ImageIO.read(new File("res/dungeonbg.jpg"))}));
    }

}
