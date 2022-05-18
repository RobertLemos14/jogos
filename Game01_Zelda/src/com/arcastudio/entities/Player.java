package com.arcastudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.arcastudio.graficos.Spritesheet;
import com.arcastudio.main.Game;
import com.arcastudio.world.Camera;
import com.arcastudio.world.World;

public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public int speed = 2;

	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;

	private BufferedImage playerDamage;

	private boolean hasGun = false;

	public int ammo = 0;

	public boolean isDamage = false;
	private int damageFrames = 0;

	public boolean shoot = false;
	public boolean mouseShoot = false;

	public double life = 100, maxLife = 100;
	public int mx, my;
	
	public boolean jump;
	public boolean isjump;
	
	public int z = 0;
	public int framesjump = 50,jumpCur = 0;
	
	public int speedjump = 2;
	public boolean jumpUp = false;
	public boolean jumpdown = false;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		// Anima��es quantidade
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}

		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}

	}

	public void tick() {
		if(jump) {
			if(isjump == false) {
			jump = false;
			isjump = true;
			jumpUp = true;
			}
			}
		
		if(isjump == true) {
			
				
				if(jumpUp) {
					
					jumpCur+= 2;
					
				}else if (jumpdown) {
					
				jumpCur -= 2;	
				
				}
				if(jumpCur <= 0) {
					isjump = false;
					jumpdown = false;
					jumpUp = false;
				}
				z = jumpCur;
				if(jumpCur >= framesjump) {
					
					jumpUp = false;
					
					jumpdown = true;
					
				
			}
		}
		
		
		moved = false;
		if (right && World.isFreePlayer((int) (x + speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		} else if (left && World.isFreePlayer((int) (x - speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}

		if (up && World.isFreePlayer(this.getX(), (int) (y - speed))) {
			moved = true;
			y -= speed;
		} else if (down && World.isFreePlayer(this.getX(), (int) (y + speed))) {
			moved = true;
			y += speed;
		}

		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
		}

		checkCollisionAmmo();
		checkCollisionWeapon();
		checkCollisionLifePack();

		// Recebendo Anima��o de Dano
		if (isDamage) {
			this.damageFrames++;
			if (this.damageFrames == 8) {
				this.damageFrames = 0;
				this.isDamage = false;
			}
		}
		/***/

		// Criar bala e atirar com o teclado
		if (shoot) {
			shoot = false;
			if (hasGun && ammo > 0) {
				ammo--;
				int dx = 0;
				int px = 0;
				int py = 8;

				if (dir == right_dir) {
					px = 16;
					dx = 1;
				} else {
					px = -8;
					dx = -1;
				}

				BulletShoot bulletShoot = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
				Game.bulletShoot.add(bulletShoot);
			}
		}
		/***/

		// Criar bala e atirar com o mouse
		if (mouseShoot) {
			mouseShoot = false;

			if (hasGun && ammo > 0) {
				ammo--;

				int px = 0;
				int py = 8;
				double angle = 0;
				if (dir == right_dir) {
					px = 18;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				} else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);

				BulletShoot bulletShoot = new BulletShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
				Game.bulletShoot.add(bulletShoot);
			}
		}
		/***/

		// Resetar Game
		if (life <= 0) {
			//Game Over!
			life = 0;
			Game.gameState = "game_Over";
		}

		// Config Camera
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}

	// Coletar Arma
	public void checkCollisionWeapon() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if (e instanceof Weapon) {
				if (Entity.isCollidding(this, e)) {
					hasGun = true;
					Game.entities.remove(i);
					// return;
				}
			}
		}
	}

	// Coletar balas
	public void checkCollisionAmmo() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if (e instanceof Bullet) {
				if (Entity.isCollidding(this, e)) {
					ammo += 50;
					// System.out.println(ammo);
					if (ammo >= 100) {
						ammo = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	// Coletar vida
	public void checkCollisionLifePack() {
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if (e instanceof Lifepack) {
				if (Entity.isCollidding(this, e) && life < 100) {
					life += 8;
					if (life >= 100) {
						life = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

	public void render(Graphics g) {
		// Ativar o player no JFrame, flip
		if (!isDamage) {

			if (dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (hasGun) {
					// Renderizar Arma na Direita
					g.drawImage(Entity.GUN_RIGHT, this.getX() + 7 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}

			else if (dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if (hasGun) {
					// Renderizar Arma na Esquerda
					g.drawImage(Entity.GUN_LEFT, this.getX() - 7 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
		} else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(this.hasGun && dir == left_dir) {
				g.drawImage(GUN_LEFT_FEEDBACK, this.getX() - 7 - Camera.x, this.getY() - Camera.y - z, null);				
			}if(this.hasGun && dir == right_dir) {
				g.drawImage(GUN_RIGHT_FEEDBACK, this.getX() + 7 - Camera.x, this.getY() - Camera.y - z, null);
			}	
		}
		if(isjump == true) {
			g.setColor(Color.BLACK);
			g.drawOval(this.getX() - Camera.x + 4, this.getY() - Camera.y + 16,8,8);
		}
	}

}
