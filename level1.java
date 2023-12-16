import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import static java.lang.Character.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class level1 extends Canvas implements KeyListener, Runnable {

  private boolean[] keys;
  private BufferedImage back;
  private Player player;
  private ArrayList<Enemy> enemies;
  private ArrayList<Block> platforms; 
  private ArrayList<Block> walls;
  private boolean isOnPlatform; 
  private boolean gravityDir; 
  private long jumpTimer; 
  private Block coin;
  private Block healthBar;
  private int lives; 

  public level1() {
    setBackground(Color.black);
    keys = new boolean[7];

    player = new Player(50, 570, 30, 30, 2);
    coin = new Block(540, 540, 30, 30);
    healthBar = new Block(20, 20, 235, 59);
    
    gravityDir = true; 
    jumpTimer = 51; 
    lives = 171; 

    enemies = new ArrayList<Enemy>();
    enemies.add(new Enemy(10, 150, 30, 30, 1));
    enemies.add(new Enemy(400, 150, 30, 30, 1));
    enemies.add(new Enemy(400, 540, 30, 30, 1));
    enemies.add(new Enemy(470, 270, 30, 30, 2));

    platforms = new ArrayList<Block>();
    platforms.add(new Block(120, 200, 100, 10));
    platforms.add(new Block(0, 300, 150, 10));
    platforms.add(new Block(120, 400, 100, 10));
    platforms.add(new Block(0, 500, 150, 10));
    platforms.add(new Block(430, 200, 100, 10));
    platforms.add(new Block(350, 300, 250, 10));
    platforms.add(new Block(430, 400, 250, 10));
    platforms.add(new Block(500, 500, 120, 10));

    walls = new ArrayList<Block>();
    walls.add(new Block(300, 200, 50, 600));


    this.addKeyListener(this);
    new Thread(this).start();
    setVisible(true);

  }

  public void update(Graphics window) {
    paint(window);
  }

  public void paint(Graphics window) {
    //set up the double buffering to make the game animation nice and smooth
    Graphics2D twoDGraph = (Graphics2D) window;

    //take a snap shop of the current screen and same it as an image
    //that is the exact same width and height as the current screen
    if (back == null)
      back = (BufferedImage)(createImage(getWidth(), getHeight()));

    //create a graphics reference to the back ground image
    //we will draw all changes on the background image
    Graphics graphToBack = back.createGraphics();
    graphToBack.setColor(Color.WHITE);
    graphToBack.fillRect(0,0,800,600); 

   

    jumpTimer++; 
    if(jumpTimer >= 30) {
      gravityDir = true; 
    }
    if(jumpTimer < 30) {
      gravityDir = false; 
    }

    

    player.draw(graphToBack);
    coin.drawCoin(graphToBack);
    healthBar.drawHealthBar(graphToBack);
    graphToBack.setColor(Color.RED);
    graphToBack.fillRect(79,40,lives,19); 
    
    for (int i = 0; i < enemies.size(); i++) {
      enemies.get(i).draw(graphToBack);
      if (i == 0) {
        enemies.get(i).backAndForth(10, 150);
      }
      if (i == 1) {
        enemies.get(i).backAndForth(400, 540);
      }
      if (i == 2) {
        enemies.get(i).backAndForth(400, 560);
      }
      if (i == 3) {
        enemies.get(i).setDirection(false);
        enemies.get(i).backAndForth(370, 500);
      }
      if (player.collidesEnemy(enemies.get(i)))  {
        lives-=57;
        enemies.remove(i);
        i--;
      }
    }

    for(Block b : platforms){
      b.draw(graphToBack);
    }
    for (Block b : walls) {
      b.draw(graphToBack);
    }

    if (player.collides(coin)) {
      player.setX(120);
      player.setY(200);
      GameRunner run = new GameRunner(2);
    }

    isOnPlatform = false;

    //make it so that the player cant go off the screen 
    if(player.getX() < 0) {
      player.setX(0);
    }
    if(player.getX()+player.getWidth() > 650) {
      player.setX(650-player.getWidth());
    }
    if(player.getY() < 0) {
      player.setY(0);
    }
    if(player.getY()+player.getHeight() > 570) {
      player.setY(570-player.getHeight());
    }

    //check if the player is on any platforms


    //checks for collision on bottom of platofrms 
    for(Block b : platforms) {
      if(player.didCollideTop(b)) {
        isOnPlatform = true;
      }
      if(player.didCollideBottom(b)){
        player.setY(player.getY()+player.getSpeed());
        gravityDir=true;
        jumpTimer = 100000;
      }
      if(player.didCollideRight(b)) {
        player.setX(player.getX()+player.getSpeed());
      }
      if(player.didCollideLeft(b)) {
        player.setX(player.getX()-player.getSpeed());
      }
    }

    // player is on floor
    if (player.getY() + player.getHeight() == 570) {
      isOnPlatform = true;
    }

    //check if the player is touching any walls
    for (Block b: walls) {
      if (player.didCollideWallLeft(b)) {
        player.setX(player.getX()-player.getSpeed());
      }
      if (player.didCollideWallRight(b)) {
        player.setX(player.getX()+player.getSpeed());
      }
      if (player.collides(b)) {
        isOnPlatform = true;
      }
    }

    //gravity  
    if(!(isOnPlatform)){
      if(gravityDir==true){
      player.move("DOWN");  
      }
      if(gravityDir==false){
        player.move("UP");  
      }
    }


    //checks for keypresses 
    if (keys[0]) {
      player.move("LEFT");
    }
    if (keys[1]) {
      player.move("RIGHT");
    }
    if (keys[2] && isOnPlatform) {
      jumpTimer = 0; 
      player.move("UP");
    }
    if (keys[3] && !isOnPlatform) {
      player.move("DOWN");
    }
    if (keys[4]) {

    }


    twoDGraph.drawImage(back, null, 0, 0);
  }

  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      keys[0] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
      keys[1] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_UP) {
      keys[2] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      keys[3] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      keys[4] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_R) {
      keys[5] = true;
    }
    if (e.getKeyCode() == KeyEvent.VK_P) {
      keys[6] = true;
    }
    repaint();
  }

  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      keys[0] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
      keys[1] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_UP) {
      keys[2] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
      keys[3] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      keys[4] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_R) {
      keys[5] = false;
    }
    if (e.getKeyCode() == KeyEvent.VK_P) {
      keys[6] = false;
    }
    repaint();
  }

  public void keyTyped(KeyEvent e) {
    //no code needed here
  }

  public void run() {
    try {
      while (true) {
        Thread.currentThread().sleep(5);
        repaint();
      }
    } catch (Exception e) {}
  }
}