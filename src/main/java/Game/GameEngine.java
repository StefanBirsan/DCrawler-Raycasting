package Game;

import Game.Menus.Serialization;
import Game.Menus.SettingHelper.Settings;
import Game.Menus.Menu;
import Game.Texture.Sprite;
import Game.Texture.Textures;
import Game.Equipment.Weapon;
import NPCs.NPC;
import Player.Character;
import Player.CameraView;
import Player.Player;
import javafx.util.Pair;
import javafx.geometry.Point2D;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class GameEngine extends JFrame  {

    public boolean getPlayer() {
        return false;
    }

    public enum State {
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
    private LinkedList<NPC> NPCs = new LinkedList<>();
    private static final int PORT = 12345;

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

        menu = new Menu(settings.isFullscreen(), this, settings, settingsSerialization);
        getContentPane().add(menu);
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

    public void applySettings(Menu.Mode mode) {
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

    public void refresh() {
        remove(menu);
        menu = new Menu(menu.isFullscreen(), this, settings, settingsSerialization);
        add(menu);
        validate();

        if (state == State.PAUSE)
            camera = new CameraView(resX, resY, renderedResX, renderedResY, player, maps.get(level), NPCs);

        setSize(resX, resY);
    }

    private void readSettings() throws Exception {
        settings = (Settings) settingsSerialization.deserialize();

        for (Menu.Mode mode : Menu.getSettings())
            applySettings(mode);
    }

    public void pause() {
        Audio.stop(Audio.Sound.BG);
        Audio.resetAndStart(Audio.Sound.MENU);
        state = State.PAUSE;
        getContentPane().remove(camera);
        menu.pause();
        getContentPane().add(menu);
        getContentPane().validate();
    }

    public void resume() {
        Audio.stop(Audio.Sound.MENU);
        Audio.resetAndStart(Audio.Sound.BG);
        state = State.GAME;
        removeKeyListener(menu.getInput());
        input.resume(System.currentTimeMillis());
        getContentPane().remove(menu);
        getContentPane().add(camera);
        getContentPane().validate();
    }

    public void restart() {
        newGame();
    }

    public void newGame() {
        int[][] map = maps.get(level);

        Character.setMap(map);
        initNPCs();
        Weapon.initWeapons();

        player = new Player(0.03, 0.06, 100, 100, 100, 100, 100, 100, new Point2D(4.5, 4.5),
                new Point2D(0, 1), new LinkedList<>());

        input = new InputListener(this, player);
        addMouseListener(input);
        addKeyListener(input);

        camera = new CameraView(resX, resY, renderedResX, renderedResY, player, map, NPCs);

        resume();
    }

    public void newGame(int level, int difficulty) {
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

    public void exit() {
        System.exit(0);
    }

    private void initNPCs() {
        Random r = new Random();
        int nrOfEnemies = difficulty + r.nextInt(1) * (r.nextBoolean() ? 1 : -1);       // 1 in r.nextInt(int) is arbitrary
        int[][] map = maps.get(level);
        nrOfEnemies = nrOfEnemies < 0 ? 0 : nrOfEnemies;

        for (int i = 0; i < nrOfEnemies; i++) {
            int x = r.nextInt(map[0].length), y = r.nextInt(map.length);
            boolean collides = true;

            while (collides) {
                collides = map[y][x] != 0;

                for (NPC j : NPCs)
                    if (!collides)
                        collides = x == (int) j.getPos().getX() && y == (int) j.getPos().getY();

                if (collides) {
                    x = r.nextInt(map[0].length);
                    y = r.nextInt(map.length);
                }
            }

            NPCs.add(new NPC(0.03, 0.06, 10, 10, 10, 10, 10, 10, new Point2D(x + 0.5, y + 0.5),
                    new Point2D(0, 1), new LinkedList<>(), NPC.Attitude.EVIL, NPC.NPCs.BALDRIC));
        }
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}