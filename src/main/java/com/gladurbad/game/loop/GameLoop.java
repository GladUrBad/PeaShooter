package com.gladurbad.game.loop;

import com.gladurbad.game.GameWindow;
import com.gladurbad.game.entity.*;
import com.gladurbad.game.keyboard.Keyboard;
import com.gladurbad.game.mouse.Mouse;
import com.gladurbad.game.util.Vector;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class GameLoop extends JPanel implements ActionListener {

    public static final int GRID_SIZE = 20;
    public static final int FPS = 120;
    public static final double EXPECTED_DELTA = 1000.0 / 60.0;
    public final Mouse mouse;
    public final Keyboard keyboard;

    private SentientOrb sentientOrb;
    private JLabel healthBar;
    private static GameLoop instance;

    private final Timer timer = new Timer(1000 / FPS, this);
    private final List<Entity> entities = new ArrayList<>();

    private boolean running;
    private long lastTick = System.currentTimeMillis();
    private int currentTick;

    public GameLoop() {
        instance = this;
        mouse = new Mouse();
        keyboard = new Keyboard();

        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(keyboard);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        init();
    }

    public void init() {
        running = true;
        timer.start();
        sentientOrb = new SentientOrb(new Vector(GameWindow.WIDTH / 2.0, GameWindow.HEIGHT / 2.0));
        entities.add(sentientOrb);

        healthBar = new JLabel();
        healthBar.setSize(50, 50);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (running) {
            long now = System.currentTimeMillis();



            graphics.setColor(new Color(50, 50, 50));

            healthBar.setText("Health: " + sentientOrb.getHealth());

            for (int i = 0; i < GameWindow.HEIGHT / GRID_SIZE; i++) {
                graphics.drawLine(0, i * GRID_SIZE, GameWindow.WIDTH, i * GRID_SIZE);
            }

            for (int i = 0; i < GameWindow.WIDTH / GRID_SIZE; i++) {
                graphics.drawLine(i * GRID_SIZE, GameWindow.HEIGHT, i * GRID_SIZE, 0);
            }

            double ticks = Math.ceil((now - lastTick) / EXPECTED_DELTA);

            for (int i = 0; i < ticks; i++) {
                currentTick++;
                tick();
            }

            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("Monospaced", Font.BOLD, 22));
            graphics.drawString("Health: " + (int) (sentientOrb.getHealth()), GameWindow.WIDTH / 2 - 115, GameWindow.HEIGHT - 75);
            graphics.drawString("Kills: " + sentientOrb.getKills(), GameWindow.WIDTH / 2 - 115, GameWindow.HEIGHT - 100);
            graphics.drawRect(GameWindow.WIDTH / 2 - 130, GameWindow.HEIGHT - 130, 170, 75);

            entities.forEach(entity -> {
                if (entity.getId() != sentientOrb.getId()) {
                    entity.render(graphics);
                }
            });

            sentientOrb.render(graphics);

            lastTick = now;
        }
    }

    public void tick() {
        if (mouse.isPressed() && currentTick % sentientOrb.fireTicks == 0) {
            addEntity(new Bullet(sentientOrb.getPosition().add(sentientOrb.size() / 2.0), sentientOrb.inaccuracy, sentientOrb.damage));
        }

        System.out.println(entities.size());

        int tick = 20 - (sentientOrb.getKills() / 100);

        if (tick < 1) tick = 1;

        if (currentTick % 15 == 0) {
            int spawnX = ThreadLocalRandom.current().nextInt(GameWindow.WIDTH);
            int spawnY = ThreadLocalRandom.current().nextInt(GameWindow.HEIGHT);

            addEntity(new Orb(new Vector(spawnX, spawnY)));
        }

        if (currentTick % 1000 == 0) {
            int spawnX = ThreadLocalRandom.current().nextInt(GameWindow.WIDTH);
            int spawnY = ThreadLocalRandom.current().nextInt(GameWindow.HEIGHT);

            addEntity(new PowerOrb(new Vector(spawnX, spawnY)));
        }

        if (currentTick % 1000 == 0) {
            int spawnX = ThreadLocalRandom.current().nextInt(GameWindow.WIDTH);
            int spawnY = ThreadLocalRandom.current().nextInt(GameWindow.HEIGHT);

            addEntity(new HealthOrb(new Vector(spawnX, spawnY)));
        }

        entities.forEach(entity -> entity.move(keyboard.getStrafe(), keyboard.getForward()));

        for (Entity bob : entities) {
            for (Entity joe : entities) {
                double deltaX = bob.getCenter().x - joe.getCenter().x;
                double deltaY = bob.getCenter().y - joe.getCenter().y;

                if (Math.abs(deltaX) < joe.size() && Math.abs(deltaY) < joe.size()) {
                        bob.collideWithEntity(joe);
                }
            }
        }

        entities.removeIf(Entity::canRemove);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static GameLoop getInstance() {
        return instance;
    }

    public void addEntity(Entity entity) {
        if (entities.size() < 1000 || entity instanceof Bullet) {
            entities.add(entity);
        }
    }

    public SentientOrb getOrb() {
        return sentientOrb;
    }

    public Mouse getMouseListener() {
        return mouse;
    }

    public Keyboard getKeyboardListener() {
        return keyboard;
    }
}
