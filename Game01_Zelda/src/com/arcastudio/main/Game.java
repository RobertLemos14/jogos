package com.arcastudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.arcastudio.entities.BulletShoot;
//Importação dos Packages
import com.arcastudio.entities.Enemy;
import com.arcastudio.entities.Entity;
import com.arcastudio.entities.Player;
import com.arcastudio.graficos.Spritesheet;
import com.arcastudio.graficos.UI;
import com.arcastudio.world.Camera;
import com.arcastudio.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener{

	private static final long serialVersionUID = 1L;
	// Variables
	// Janela e Run Game
	public static JFrame frame;
	private boolean isRunning = true;
	private Thread thread;
	public static final int WIDTH = 240, HEIGHT = 160, SCALE = 3;

	public int cur_level = 1, maxlevel = 2;
	
	// Imagens e Gráficos
	
	private BufferedImage image;
	
	private Graphics g;
	
	// Entities
	
	public static List<Entity> entities;
	
	public static List<Enemy> enemies;
	
	public static List<BulletShoot> bulletShoot;
	
	public static Spritesheet spritesheet;
	public static World world;
	
	public static Player player;
	
	
	public static Random random;
	
	public UI ui;
	

	
	public static String gameState = "menu";
	
	private static Boolean ShowMessageGameOver = true;
	
	private int framesGameOver = 0;
	
	private boolean restartGame = false;
	
	public Menu menu;
	
	
	/***/
	
	

	// Construtor
	public Game() {
		som.musicBackground.loop();
		random = new Random();

		// Para que os eventos de teclado e mouse funcionem
		addKeyListener(this);
		addMouseListener(this);

		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando Objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bulletShoot = new ArrayList<BulletShoot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		menu = new Menu();
	}

	// Criação da Janela
	public void initFrame() {
		frame = new JFrame("New Window");
		frame.add(this);
		frame.setResizable(false);// Usuário não irá ajustar janela
		frame.pack();
		frame.setLocationRelativeTo(null);// Janela inicializa no centro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Fechar o programa por completo
		frame.setVisible(true);// Dizer que estará visível
	}

	// Threads
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Método Principal
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	// Ticks do Jogo
	public void tick() {
		if(gameState == "normal"){
			this.restartGame = false;
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e instanceof Player) {
				// Ticks do Player
			}
			e.tick();
		}

		for (int i = 0; i < bulletShoot.size(); i++) {
			bulletShoot.get(i).tick();
		}
		
		if(enemies.size() == 0) {
			//proximo mapa
			this.cur_level++;
			if(this.cur_level > maxlevel) {
				cur_level = 1;
			}
			String newWorld = "level"+cur_level+".png";
			World.restartGame(newWorld);
		}
		}else if (gameState == "game_Over") {
			this.framesGameOver++;
			if(this.framesGameOver == 35) {
				this.framesGameOver = 0;
				
				if(this.ShowMessageGameOver) {
					ShowMessageGameOver = false;
				}else
					ShowMessageGameOver = true;
			}
			
			if (restartGame){
				this.restartGame = false;
				this.gameState = "menu";
				cur_level = 1;
				String newWorld = "level"+cur_level+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState ==  "menu") {
			
			menu.tick();
			
		}
		}

	// O que será mostrado em tela
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();// Sequência de buffer para otimizar a renderização, lidando com
														// performace gráfica
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		g = image.getGraphics();// Renderizar imagens na tela
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

		/* Render do jogo */
		world.render(g);

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bulletShoot.size(); i++) {
			bulletShoot.get(i).render(g);
		}

		ui.render(g);
		/***/

		g.dispose();// Limpar dados de imagem não usados
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 17));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 600, 40);
		if(this.gameState == "game_Over") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 28));
			g.setColor(Color.white);
			g.drawString("Você perdeu, tente novamente.", WIDTH*SCALE / 4, HEIGHT*SCALE / 2 + 25);
			g.setFont(new Font("arial", Font.BOLD, 28));
			if(ShowMessageGameOver) {
			g.drawString(">Aperte Enter para recomeçar<.", WIDTH*SCALE / 4, HEIGHT*SCALE / 2 + 70);
			}
			
			}else if(gameState == "menu") {
				menu.render(g);
			}
		bs.show();
	}

	// Controle de FPS
	public void run() {
		// Variables
		long lastTime = System.nanoTime();// Usa o tempo atual do computador em nano segundos, bem mais preciso
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;// Calculo exato de Ticks
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		// Runner Game
		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop(); // Garante que todas as Threads relacionadas ao computador foram terminadas,
				// para garantir performance.

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {

			player.right = true;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {

			player.left = true;

		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {

			player.up = true;
			
			if(gameState == "menu") {
				menu.cima = true;
			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			
			if(gameState == "menu") {
				menu.baixo = true;
				
			}

		}

		if (e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_M) {
				player.life = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(this.gameState == "menu") {
				menu.enter = true;
			}
		}if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "menu";
			menu.pause = true;
	}
	if(e.getKeyCode() == KeyEvent.VK_SPACE) {
		player.jump = true;
	}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {

			player.right = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {

			player.left = false;

		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {

			player.up = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
