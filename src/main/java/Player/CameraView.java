package Player;

import NPCs.NPC;
import javafx.geometry.Point2D;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class CameraView extends JFrame{

    private final int wallHeight, floorSize = 4, ceilingSize = 4, halfResY, visibility = 8, fogRGB = Color.black.getRGB();
    private int resX, resY, renderResX, renderResY, weaponSizeConst = 2, barsXMargin = 11, barsYMargin = 23;
    private double ratioX, ratioY;

    private BufferedImage rendered;

    private Player  player;
    private LinkedList<NPC> NPCs;

    private int[][] map;

    public CameraView(int resX, int resY, int renderResX, int renderResY, Player player, int[][] map, LinkedList<NPC> NPCs) {
        this.resX = resX;
        this.resY = resY;
        this.renderResX = renderResX;
        this.renderResY = renderResY;
        ratioX = (double) resX / 1366;
        ratioY = (double) resY / 768;
        wallHeight = renderResY;
        halfResY = renderResY / 2;
        rendered = new BufferedImage(renderResX, renderResY, BufferedImage.TYPE_INT_RGB);
        this.player = player;
        this.map = map;
        this.NPCs = NPCs;
    }

    public void drawGraphics(Graphics g) {
        render(g);
        drawWeapon(g);
        drawViewfinder(g);
        drawHealthAndManabar(g);
    }

    private void render(Graphics g) {
        int wallCenterZ = (int) (halfResY * player.getZDir());
        double fovRatio = player.getDefaultFov() / player.getFov();
        Point2D dir = player.getDir(), plane = new Point2D(-dir.getY(), dir.getX()).multiply(Math.tan(player.getFov() / 2) * dir.magnitude()), vec = dir.add(plane),
                deltaPlane = plane.multiply((double) 2 / renderResX), pos = player.getPos();

        for (int i = 0; i < renderResX; i++, vec = vec.subtract(deltaPlane)) {
            Iterator<Pair<Pair<Point2D, Boolean>, Point2D>> iterator = player.collisionInfo(vec).iterator();

            Pair<Point2D, Boolean> collisionInfo = player.collisionInfo(vec).getFirst().getKey();
            Point2D collisionPoint = collisionInfo.getKey();

            //TODO: Add Textures when you wake up

            BufferedImage img = Textures.getSprites().get(Textures.getBlocks().get(player.block(vec, collisionPoint))).getImage();
            int x = (int) (((collisionInfo.getValue() ? collisionPoint.getY() : collisionPoint.getX()) % 1) * img.getWidth()), j = 0,
                    h = (int) (wallHeight * fovRatio * vec.magnitude() / pos.distance(collisionPoint)), emptyH = wallCenterZ - h / 2;

            float fogRatio = (float) pos.distance(collisionPoint);
            fogRatio /= fogRatio < visibility ? visibility : 1;

            for (; j < emptyH; j++) {
                double d = halfResY * vec.magnitude() / (wallCenterZ - j) * fovRatio;
                Point2D p = player.getPos().add(vec.multiply(d / vec.magnitude()));
                int tile = map[(int) p.getY()][(int) p.getX()];

                if (d < visibility) {
                    BufferedImage ceiling = Textures.getSprites().get(Textures.getCeilings().getOrDefault(tile, Sprite.Sprites.CEILING0)).getImage();
                    rendered.setRGB(i, j, mix(ceiling.getRGB((int) ((p.getX() % ceilingSize) / ceilingSize * ceiling.getWidth()),
                            (int) ((p.getY() % ceilingSize) / ceilingSize * ceiling.getHeight())), fogRGB, (float) d / visibility));
                }
                else
                    rendered.setRGB(i, j, fogRGB);
            }
            for (; j < renderResY && j < emptyH + h; j++)
                rendered.setRGB(i, j, fogRatio < 1 ? mix(img.getRGB(x, (j - emptyH) * img.getHeight() / h), fogRGB, fogRatio) : fogRGB);
            for (; j < renderResY; j++) {
                double d = halfResY * vec.magnitude() / (j - wallCenterZ) * fovRatio;
                Point2D p = player.getPos().add(vec.multiply(d / vec.magnitude()));
                int tile = map[(int) p.getY()][(int) p.getX()];

                if (d < visibility) {
                    BufferedImage floor = Textures.getSprites().get(Textures.getFloors().getOrDefault(tile, Sprite.Sprites.FLOOR0)).getImage();
                    rendered.setRGB(i, j, mix(floor.getRGB((int) ((p.getX() % floorSize) / floorSize * floor.getWidth()),
                            (int) ((p.getY() % floorSize) / floorSize * floor.getHeight())), fogRGB, (float) d / visibility));
                }
                else
                    rendered.setRGB(i, j, fogRGB);
            }
        }

        g.drawImage(rendered, 0, 0, resX, resY, null);
    }

    private int mix (int c0, int c1, float ratio) {
        float iRatio = 1.0f - ratio;

        int a = (int) ((c0 >> 24 & 0xff) * iRatio + (c1 >> 24 & 0xff) * ratio);
        int r = (int) (((c0 & 0xff0000) >> 16) * iRatio + ((c1 & 0xff0000) >> 16) * ratio);
        int g = (int) (((c0 & 0xff00) >> 8) * iRatio + ((c1 & 0xff00) >> 8) * ratio);
        int b = (int) ((c0 & 0xff) * iRatio + (c1 & 0xff) * ratio);

        return a << 24 | r << 16 | g << 8 | b;
    }

    private void drawViewfinder(Graphics g) {
        BufferedImage viewfinder = Textures.getSprites().get(Sprite.Sprites.VIEWFINDER).getImage();
        int w = (int) (viewfinder.getWidth() * ratioX), h = (int) (viewfinder.getHeight() * ratioY);
        g.drawImage(viewfinder, (resX - w) / 2, (resY - h) / 2, w, h, null);
    }

    private void drawHealthAndManabar(Graphics g) {
        BufferedImage healthbar = Textures.getSprites().get(Textures.getHealthbar().get(nrOfBarSprites * hero.getHealth() / hero.getMaxHealth())).getImage();
        BufferedImage manabar = Textures.getSprites().get(Textures.getManabar().get(nrOfBarSprites * hero.getMana() / hero.getMaxMana())).getImage();
        int w = (int) (healthbar.getWidth() * ratioX), h = (int) (healthbar.getHeight() * ratioY), marginX = (int) (barsXMargin * ratioX), marginY = (int) (barsYMargin * ratioY);
        g.drawImage(healthbar, marginX, resY - h + marginY, w, h, null);
        g.drawImage(manabar, resX - w - marginX, resY - h + marginY, w, h, null);
    }

    private void drawWeapon(Graphics g) {
        BufferedImage img = Textures.getSprites().get(Textures.getWeapons().get(hero.getWeapon())).getImage();
        if (img != null) {
            int w = img.getWidth(), h = img.getHeight(), x = (int) (resX * .256), y = (int) (resY * .456);
            AffineTransform at = new AffineTransform();
            at.translate(w / 2, h / 2);
            at.rotate(hero.getWeaponAngle(), w / 5, h / 2);
            AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            g.drawImage(op.filter(img, null), x, y, (int) (w * ratioX * weaponSizeConst), (int) (h * ratioY * weaponSizeConst), null);
        }
    }

    private LinkedList<Pair<NPC, Integer>> checkNPCs() {
        LinkedList<Pair<NPC, Integer>> NPCsToDraw = new LinkedList<>();

        for (NPC i : NPCs) {
            Point2D vec = i.getPos().subtract(player.getPos()), dir = player.getDir(), zero = new Point2D(1, 0), perp = new Point2D(-vec.getY(), vec.getX());
            double vecAngle = vec.angle(zero), dirAngle = dir.angle(zero);
            vecAngle = vec.getY() < 0 ? 360 - vecAngle : vecAngle;
            dirAngle = dir.getY() < 0 ? 360 - dirAngle : dirAngle;
            perp = perp.multiply(1 / vec.magnitude() * Textures.getSprites().get(Textures.getNPCs().get(i.getType()).get(i.getPosition())).getImage().getWidth() /
                    Textures.getSprites().get(Textures.getBlocks().get(1)).getImage().getWidth());
            vec = vec.add(perp.multiply(vecAngle < dirAngle ? 1 : -1));

            if (vec.angle(dir) < player.getFov() * 90 / Math.PI)
                NPCsToDraw.add(new Pair<>(i, 2137));
        }

        NPCsToDraw.sort(new Comparator<Pair<NPC, Integer>>() {
            @Override
            public int compare(Pair<NPC, Integer> o1, Pair<NPC, Integer> o2) {
                return o1.getValue() < o2.getValue() ? -1 : 1;
            }
        });

        return NPCsToDraw;
    }

}
