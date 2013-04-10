package com.testgame.Models;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;

public class Area {
	
	private Player owner;
	private Question question;

	private Image image;
	private Color color;

	private int value, xPosition, yPosition;
	private ArrayList<Area> neighbors;

	
	public Area(int xPosition, int yPosition, int value) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		
		this.value = value;
		
		loadAreaImage();
	}
	
	protected void loadAreaImage() {
		image = new Image(new Texture( new FileHandle("data/maps/mo/area001.png")));
	}
	
	/**
	 * Returns standard color if no users owns area, and
	 * returns the owners color if an owner exists
	 * 
	 * @return Color
	 */
	public Color getColor() {
		if(getOwner() == null) {
			return color;
		}
		return getOwner().getColor();
	}
	
	/**
	 * Sets provided player as owner
	 * 
	 * @param player
	 */
	public void setOwner(Player player) {
		owner = player;
	}
	
	/**
	 * Provides the owner of the area
	 * 
	 * @return Player
	 */
	public Player getOwner() {
		return owner;
	}
	
	/**
	 * Sets provided question as question of area
	 * 
	 * @param question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	/**
	 * Provides the question of the area
	 * 
	 * @return Question
	 */
	public Question getQuestion() {
		return question;
	}
	
	/**
	 * The value of the area
	 * 
	 * @return int
	 */
	public int getValueOfArea() {
		return value;
	}
	
	public Image getImage() {
		return image;
	}

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	
	public ArrayList<Area> getNeighbors() {
		return neighbors;
	}
}
