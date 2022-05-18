package com.arcastudio.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Menu {
	
	
	public String[] options = { "novo jogo", "carregar jogo", "sair" };		

	public int currentOption = 0;
	
	public int maxOption = options.length - 1;

	public boolean baixo = false, cima = false,enter = false;
	public boolean pause = false;
	public void tick() {
		if(cima) {
			cima = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;			}
		}if(baixo) {
			baixo = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;			}
		}
		if(enter) {
			enter = false;
			
			if(options[currentOption] == "novo jogo") {
				
				Game.gameState = "normal";
			pause = false;
			}if(options[currentOption] == "sair") {
			
			System.exit(1);
			}
		}
		
	}
	
	public static void SaveGame(String[] val1,int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length;i++) {
			String current = val1[i];
			current+=":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for(int n = 0; n < value.length; n++) {
				value[n]+=encode;
				current+=value[n];
			}
			try {
				write.write(current);
				if(i < val1.length - 1) {
					write.newLine();
					
				}
				
			} catch (IOException e) {
				// TODO: handle exception
			}
			try {
				write.flush();
				write.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}
	}
	
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor( new Color(0,0,0,200));
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD,36));
		g.drawString(">Zelda<", (Game.WIDTH*Game.SCALE / 2 - 50), (Game.HEIGHT*Game.SCALE / 2 - 200));
		//opções de menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD,20));
		if(pause == false) {
		g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE / 2 - 40), (Game.HEIGHT*Game.SCALE / 2 - 50));
		}else {
			g.drawString("resumir", (Game.WIDTH*Game.SCALE / 2 - 30), (Game.HEIGHT*Game.SCALE / 2 - 50));
		}
		g.drawString("carregar jogo", (Game.WIDTH*Game.SCALE / 2 - 30), (Game.HEIGHT*Game.SCALE / 2 - 25));
		g.drawString("sair", (Game.WIDTH*Game.SCALE / 2 - 30), (Game.HEIGHT*Game.SCALE / 2));
		
		if(options[this.currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE / 2 - 70), (Game.HEIGHT*Game.SCALE / 2 - 50));
		}if(options[this.currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE / 2 - 70), (Game.HEIGHT*Game.SCALE / 2 - 25));
		}if(options[this.currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE / 2 - 70), (Game.HEIGHT*Game.SCALE / 2));
		}
	}
	
	
}
