package com.arcastudio.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class som {
	private AudioClip clip;
	
	public static final som musicBackground = new som("/music.wav");
	public static final som hurtEffect = new som("/hurt.wav");
	
	private som(String name) {
		try {
			clip = Applet.newAudioClip(som.class.getResource(name));
		}catch(Throwable e) {}
	}
	public void play() {
		try {
			new Thread() {
				public void run() {
					clip.play();
				}
			}.start();
		}catch(Throwable e) {}
	}
	public void loop() {
		try {
			new Thread() {
				public void run() {
					clip.loop();
				}
			}.start();
		}catch(Throwable e) {}
	}
	}
	
	
	
	
	
