package com.testgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.testgame.Models.Player;
import com.testgame.Models.QuestionPool;
import com.testgame.Views.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;

public class MyGame extends Game implements ApplicationListener {

	MainMenuScreen mainMenuScreen;
	private Music music;
	QuestionPool questionPool;

	@Override
	public void create() {
		// Load music
		music = Gdx.audio.newMusic(Gdx.files.getFileHandle(
				"data/Riskspillet.mp3", FileType.Internal));
		music.setLooping(true);
		music.play();
		
		//TODO:  Generate question pool.
		questionPool = new QuestionPool();
		
		// Launch main menu screen.
		mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
	}
	
	public QuestionPool getQuestionPool() {
		return questionPool;
	}

	@Override
	public void dispose() {
		music.dispose();
	}
}
