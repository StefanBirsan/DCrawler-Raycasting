package Game;

import Game.Menus.Serialization;
import Game.Menus.SettingHelper.Settings;
import Player.CameraView;
import Player.Player;
import javafx.util.Pair;
import javafx.geometry.Point2D;

import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.LinkedList;

public class GameEngine extends JFrame implements KeyListener, MouseMotionListener {
    enum State {
        MENU, GAME, PAUSE
    }

    private int resX, resY, renderedResX, renderedResY, fps = 60, msPerFrame = 1000 / fps;
    private int level, difficulty;

    private CameraView camera;
    private Player player;
    private InputListener input;
    private Menu menu;
    private State state = State.MENU;
    private Serialization settingsSerialization = new Serialization("settings.ser", Settings.class);
    private Settings settings;

    private LinkedList<int[][]> maps = new LinkedList<>();

    private static Hashtable<Integer, Pair<Double, Double>> wallHeight = new Hashtable<>();

    public GameEngine() {
        setTitle("Raycasting Game");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(true);
        setResizable(false);

        Menu.initHashSets();
        Menu.initModeStack();

        try {
            readSettings();
            Audio.init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        getContentPane().setPreferredSize(new Dimension(resX, resY));

        Textures.init();
        initMaps();
        initWallHeight();
        getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TRANSLUCENT),
                new Point(0, 0), "blank"));

        menu = new Menu(); //TODO: add settings later if needed
        getContentPane().add((PopupMenu) menu); //TODO: debyg later if needed

        if (settings.isFullscreen()) {
            setUndecorated(true);
            setExtendedState(MAXIMIZED_BOTH);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        Audio.resetAndStart(Audio.Sound.MENU);
        run();
    }

    public void start() {
        setVisible(true);
        createBufferStrategy(3);
        gameLoop();
    }

    private void gameLoop() {
        while (true) {
            render();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, resX, resY);
        renderScene(g);
        g.dispose();
        bs.show();
    }

    private void renderScene(Graphics g) {
        for (int x = 0; x < resX; x++) {
            double cameraX = 2 * x / (double) resX - 1;
            double rayDirX = playerDir + playerPlaneX * cameraX;
            double rayDirY = playerPlaneY * cameraX;

            //kap position
            int mapX = (int) playerX;
            int mapY = (int) playerY;

            double sideDistX;
            double sideDistY;

            double deltaDistX = Math.abs(1 / rayDirX);
            double deltaDistY = Math.abs(1 / rayDirY);
            double perpWallDist;

            int stepX;
            int stepY;

            boolean hit = false;
            int side = 0;

            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (playerX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - playerX) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (playerY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - playerY) * deltaDistY;
            }

            while (!hit) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //check if the ray has hit a wall
                if (mapX < 0 || mapY < 0 || mapX >= 24 || mapY >= 24) {
                    hit = true;
                }
            }

            //calculate distance projected on camera direction
            if (side == 0) perpWallDist = (mapX - playerX + (1 - stepX) / 2) / rayDirX;
            else perpWallDist = (mapY - playerY + (1 - stepY) / 2) / rayDirY;

            int lineHeight = (int) (resY / perpWallDist);

            int drawStart = -lineHeight / 2 + resY / 2;
            if (drawStart < 0) drawStart = 0;
            int drawEnd = lineHeight / 2 + resY / 2;
            if (drawEnd >= resY) drawEnd = resY - 1;

            Color color;
            if (side == 0) color = Color.RED;
            else color = Color.DARK_GRAY;

            g.setColor(color);
            g.drawLine(x, drawStart, x, drawEnd);
        }
    }

    private void initWallHeight() {
        wallHeight.put(0, new Pair<>(0d, 0d));

        for (int i = 1; i < 5; i++)
            wallHeight.put(i, new Pair<>(1d, 0d));

        wallHeight.put(5, new Pair<>(.3, .7));
        wallHeight.put(6, new Pair<>(.6, .4));
    }

    public static Hashtable<Integer, Pair<Double, Double>> getWallHeight() {
        return wallHeight;
    }

    public String[] getHighscores() {
        return new String[0];
    }

    private void initMaps() {
        maps.add(new int[][]{{1,1,1,1,1,1,1,1,2,2,2,2,2,2,2},
                {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
                {1,0,3,3,3,3,3,0,0,0,0,0,0,0,2},
                {1,0,3,5,0,6,3,0,2,0,0,0,0,0,2},
                {1,0,3,0,0,0,3,0,2,2,2,0,2,2,2},
                {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
                {1,0,3,3,0,3,3,0,2,0,0,0,0,0,2},
                {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
                {1,1,1,1,1,1,1,1,4,4,4,0,4,4,4},
                {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
                {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
                {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
                {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                {1,1,1,1,1,1,1,4,4,4,4,4,4,4,4}});
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        double moveSpeed = 0.1;
        double rotSpeed = 0.05;

        if (e.getKeyCode() == KeyEvent.VK_W) {
            playerX += playerDir * moveSpeed;
            playerY += playerPlaneY * moveSpeed;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            playerX -= playerDir * moveSpeed;
            playerY -= playerPlaneY * moveSpeed;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            playerX -= playerPlaneY * moveSpeed;
            playerY += playerDir * moveSpeed;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            playerX += playerPlaneY * moveSpeed;
            playerY -= playerDir * moveSpeed;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            double oldDirX = playerDir;
            playerDir = playerDir * Math.cos(-rotSpeed) - playerPlaneX * Math.sin(-rotSpeed);
            playerPlaneX = oldDirX * Math.sin(-rotSpeed) + playerPlaneX * Math.cos(-rotSpeed);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            double oldDirX = playerDir;
            playerDir = playerDir * Math.cos(rotSpeed) - playerPlaneX * Math.sin(rotSpeed);
            playerPlaneX = oldDirX * Math.sin(rotSpeed) + playerPlaneX * Math.cos(rotSpeed);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int centerX = resX / 2;
        double rotationSpeed = 0.005 * (mouseX - centerX);
        double oldDirX = playerDir;

        playerDir = playerDir * Math.cos(rotationSpeed) - playerPlaneX * Math.sin(rotationSpeed);
        playerPlaneX = oldDirX * Math.sin(rotationSpeed) + playerPlaneX * Math.cos(rotationSpeed);

        try {
            Robot robot = new Robot();
            robot.mouseMove(centerX, resY / 2);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    void applySettings(Menu.Mode mode) {
        if (mode == Menu.Mode.GRAPHICS) {
            if (settings.isFullscreen()) {
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                resX = d.width;
                settings.setResX(resX);
                resY = d.height;
                settings.setResY(resY);
            }
            else {
                resX = settings.getResX();
                resY = settings.getResY();
            }

            renderedResX = settings.getRenderResX();
            renderedResY = settings.getRenderResY();
        }
    }

    void refresh() {
        remove(menu);
        menu = new Menu(menu.isFullscreen(), this, settings, settingsSerialization);
        add(menu);
        validate();

        if (state == State.PAUSE)
            camera = new Camera(resX, resY, renderedResX, renderedResY, hero, maps.get(level), NPCs);

        setSize(resX, resY);
    }

    private void readSettings() throws Exception {
        settings = (Settings) settingsSerialization.deserialize();

        for (Menu.Mode mode : Menu.getSettings())
            applySettings(mode);
    }

    void pause() {
        Audio.stop(Audio.Sound.BG);
        Audio.resetAndStart(Audio.Sound.MENU);
        state = State.PAUSE;
        getContentPane().remove(camera);
        menu.pause();
        getContentPane().add(menu);
        getContentPane().validate();
    }

    void resume() {
        Audio.stop(Audio.Sound.MENU);
        Audio.resetAndStart(Audio.Sound.BG);
        state = State.GAME;
        removeKeyListener(menu.getInput());
        input.resume(System.currentTimeMillis());
        getContentPane().remove(menu);
        getContentPane().add(camera);
        getContentPane().validate();
    }

    void restart() {
        newGame();
    }

    void newGame() {
        int[][] map = maps.get(level);

        Character.setMap(map);
        initNPCs();
        Weapon.initWeapons();

        hero = new Hero(0.03, 0.06, 100, 100, 100, 100, 100, 100, new Point2D(4.5, 4.5),
                new Point2D(0, 1), new LinkedList<>());

        input = new InputListener(this, player);
        addMouseListener(input);
        addKeyListener(input);

        camera = new CameraView(resX, resY, renderedResX, renderedResY, hero, map, NPCs);

        resume();
    }

    void newGame(int level, int difficulty) {
        this.level = level;
        this.difficulty = difficulty;
        newGame();
    }

    private void run() {
        while (true) {
            long time = System.currentTimeMillis();

            if (state != State.GAME)
                menu.update();
            else {
                input.update();
                player.update();
            }

            Sprite.update();
            repaint();

            try {
                Thread.sleep(msPerFrame - (System.currentTimeMillis() - time));
            } catch (Exception e) { }
        }
    }

    void exit() {
        System.exit(0);
    }



}