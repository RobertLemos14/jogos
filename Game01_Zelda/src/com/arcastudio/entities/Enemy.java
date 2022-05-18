package com.arcastudio.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.arcastudio.main.Game;
import com.arcastudio.main.som;
import com.arcastudio.world.Camera;
import com.arcastudio.world.World;

public class Enemy extends Entity {

	private int speed = 1;
	
	private int maskX = 8, maskY = 8, maskW = 10, maskH = 10;
	
	private int frames = 0, maxFrames = 5, index =	0, maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int lifeenemy = 5;
	private boolean isDamaged = false;
	private int damageFrames = 10, DamageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		//Frames de Animação
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112+16, 16, 16, 16);
	}

	public void tick() {
		/*
		//Random
		if (Game.random.nextInt(100) < 30) {
			if (x < Game.player.getX() && World.isFree(x + speed, this.getY())) {
				x += speed;
			} else if (x > Game.player.getX() && World.isFree(x - speed, this.getY())) {
				x -= speed;
			}

			if (y < Game.player.getY() && World.isFree(this.getX(), y + speed)) {
				y += speed;
			} else if (y > Game.player.getY() && World.isFree(this.getX(), y - speed)) {
				y -= speed;
			}
		}
		*/
		if (this.isColliddingWithPlayer() == false) {

			if (x < Game.player.getX() && World.isFree(x + speed, this.getY())
					&& !isCollidding(x + speed, this.getY())) {
				x += speed;
			} else if (x > Game.player.getX() && World.isFree(x - speed, this.getY())
					&& !isCollidding(x - speed, this.getY())) {
				x -= speed;
			}

			if (y < Game.player.getY() && World.isFree(this.getX(), y + speed)
					&& !isCollidding(this.getX(), y + speed)) {
				y += speed;
			} else if (y > Game.player.getY() && World.isFree(this.getX(), y - speed)
					&& !isCollidding(this.getX(), y - speed)) {
				y -= speed;
			}
		} else {
			//Estamos colidindo
			if(Game.random.nextInt(100) < 10) {
				Game.player.life-= Game.random.nextInt(3);
				Game.player.isDamage = true;
				som.hurtEffect.play();
			}
		}
		
		//Animation
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			if (index > maxIndex) {
				index = 0;
			}
		}
		colidingBullet();
		if(lifeenemy <= 0) {
			destroySelf();
			return;
		}
		
		
		if(isDamaged) {
			this.DamageCurrent++;
			if(this.DamageCurrent == this.damageFrames) {
				this.DamageCurrent = 0;
				this.isDamaged = false;
			}
		}
		
	}
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void colidingBullet() {
	for(int i = 0; i < Game.bulletShoot.size(); i++) {
		Entity e = Game.bulletShoot.get(i);
		if(e instanceof BulletShoot) {
			
			if(Entity.isCollidding(this, e)) {	
				isDamaged = true;
				Game.bulletShoot.remove(i);
				this.lifeenemy--;
				return;
			}
	
		}
	}
	}
	
	
	public boolean isColliddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isCollidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskX, ynext + maskY, maskW, maskH); //Classe que cria retangulos fictícios para testar colisões.
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			
			if(e == this) {
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskW, maskH);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
			
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if(!isDamaged) { 
		g.drawImage(sprites[index], this.getX()- Camera.x, this.getY()- Camera.y, null);
		}else {
		g.drawImage(Entity.ENEMY_FEEDBACK, this.getX()- Camera.x, this.getY()- Camera.y, null);
		}
	}
}
