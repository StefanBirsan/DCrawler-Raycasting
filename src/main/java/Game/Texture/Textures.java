package Game.Texture;

import Game.Equipment.Weapon;
import Game.Equipment.RangedWeapons;
import NPCs.NPC;

import java.io.IOException;
import java.util.Hashtable;

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
            initNPCs();
            initWeapons();
            initHealthAndManabar();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initHealthAndManabar() {
        healthbar.put(0, Sprite.Sprites.H0);
        healthbar.put(1, Sprite.Sprites.H1);
        healthbar.put(2, Sprite.Sprites.H2);
        healthbar.put(3, Sprite.Sprites.H3);
        healthbar.put(4, Sprite.Sprites.H4);
        healthbar.put(5, Sprite.Sprites.H5);
        healthbar.put(6, Sprite.Sprites.H6);
        healthbar.put(7, Sprite.Sprites.H7);
        healthbar.put(8, Sprite.Sprites.H8);

        manabar.put(0, Sprite.Sprites.M0);
        manabar.put(1, Sprite.Sprites.M1);
        manabar.put(2, Sprite.Sprites.M2);
        manabar.put(3, Sprite.Sprites.M3);
        manabar.put(4, Sprite.Sprites.M4);
        manabar.put(5, Sprite.Sprites.M5);
        manabar.put(6, Sprite.Sprites.M6);
        manabar.put(7, Sprite.Sprites.M7);
        manabar.put(8, Sprite.Sprites.M8);
    }

    private static void initWeapons() {
        weapons.put(Weapon.Weapons.S_SWORD, Sprite.Sprites.S_SWORD);
    }

    private static void initNPCs() {
        Hashtable<NPC.Position, Sprite.Sprites> tempBaldric = new Hashtable<>();
        tempBaldric.put(NPC.Position.STANDING, Sprite.Sprites.B_STANDING);
        tempBaldric.put(NPC.Position.FALLING, Sprite.Sprites.B_FALLING);
        tempBaldric.put(NPC.Position.FALLED, Sprite.Sprites.B_FALLED);
        tempBaldric.put(NPC.Position.CASTING, Sprite.Sprites.B_CASTING);
        tempBaldric.put(NPC.Position.WALKING, Sprite.Sprites.B_WALKING);

        NPCs.put(NPC.NPCs.BALDRIC, tempBaldric);
    }

    private static void initCeilings() {
        for (int i = 0; i < 7; i++)
            ceilings.put(i, Sprite.Sprites.CEILING0);
    }

    private static void initFloors() {
        for (int i = 0; i < 7; i++)
            floors.put(i, Sprite.Sprites.FLOOR0);
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

    public static Hashtable<RangedWeapons.Bullets, Sprite.Sprites> getBullets() {
        return bullets;
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
        //TODO: Get sprites from files
    }
}
