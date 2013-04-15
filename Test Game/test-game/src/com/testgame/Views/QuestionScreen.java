package com.testgame.Views;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.testgame.MyGame;
import com.testgame.Models.Alternative;
import com.testgame.Models.Area;
import com.testgame.Models.Quiz;

public class QuestionScreen extends AbstractMenuScreen {

	// private final Sound correct;
	// private final Sound wrong;
	Quiz currentQuestion;
	TextButton alt1Button, alt2Button, alt3Button, alt4Button;
	Label questionText, responseText, countDownText;
	LabelStyle labelStyleHeader;
	// How many seconds you get to answer a question.
	int countDownTime = 20;
	int currentTime = 0;
	Area area;

	boolean hasAnswered = false;

	/**
	 * Constructor keeping a reference to the main Game class
	 * 
	 * @param game
	 */
	public QuestionScreen(MyGame game, Area area) {
		super(game);
		this.area = area;
		// correct = Gdx.audio.newSound(Gdx.files.internal("data/correct.wav"));
		// wrong = Gdx.audio.newSound(Gdx.files.internal("data/wrong.wav"));
		startTimer();
	}

	/**
	 * The count down timer.
	 */
	private void startTimer() {
		Timer.schedule(new Task() {

			@Override
			public void run() {
				if (!hasAnswered) {
					if (countDownTime - currentTime != 0) {
						currentTime++;
						startTimer();
					} else {
						wrongAnswer();
					}
				}
			}

		}, 1);
	}

	/**
	 * Helper method setting the next screen.
	 */
	private void nextPlayer() {
		game.setScreen(new NextPlayerScreen(game));
	}

	/**
	 * Updates and draws stuff.
	 */
	@Override
	public void render(float delta) {
		super.render(delta);

		// Count down label must update each render
		countDownText = new Label("" + (countDownTime - currentTime),
				labelStyleHeader);
		countDownText.setX((4 * Gdx.graphics.getWidth()) / 5);
		countDownText.setY((3 * Gdx.graphics.getHeight()) / 4);

		batch.begin();
		questionText.draw(batch, 1.0f);
		countDownText.draw(batch, 1.0f);
		batch.end();
	}

	/**
	 * Button initialization
	 */
	public void initializeButtons() {
		// Retrieve question from question pool
		currentQuestion = (Quiz) game.getQuestionPool().random();

		AlternativeGroupView altGroup = new AlternativeGroupView(currentQuestion, buttonStyle, game);
		ArrayList<AlternativeView> alternatives = altGroup.getList();

		// Question text field
		labelStyleHeader = new LabelStyle();
		labelStyleHeader.font = font;
		labelStyleHeader.fontColor = new Color(0.647059f, 0.164706f, 0.164706f,
				1.0f);
		questionText = new Label(currentQuestion.getQuestionText(),
				labelStyleHeader);
		questionText.setX(Gdx.graphics.getWidth() / 2 - questionText.getWidth()
				/ 2);
		questionText.setY(Gdx.graphics.getHeight() / 2
				- questionText.getHeight() / 2 + Gdx.graphics.getHeight()
				* 0.19f);
		questionText.setWidth(0.6f * Gdx.graphics.getWidth());

		// Add the buttons to the stage.
		for (AlternativeView av : alternatives) {
			av.getView().addListener(new InputEventListener());
			this.stage.addActor(av.getView());
		}
	}

	/**
	 * Called when the current screen changes from this to a different screen.
	 * Remember to dispose objects.
	 */
	public void hide() {
		super.hide();
		// Stop the count down clock. Not 100% sure if this is working
		// correctly.
		Timer.instance.clear();

		// correct.dispose();
		// wrong.dispose();
	}

	/**
	 * Unified InputListener
	 * 
	 */
	class InputEventListener extends InputListener {
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			return true;
		}

		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			Label altButton = (Label) event.getTarget();
			String altName = altButton.getText().toString();

			if (altName.equals(currentQuestion.getAlt1().getName())) {
				handleEvent(altButton, currentQuestion.getAlt1());
			} else if (altName.equals(currentQuestion.getAlt2().getName())) {
				handleEvent(altButton, currentQuestion.getAlt2());
			} else if (altName.equals(currentQuestion.getAlt3().getName())) {
				handleEvent(altButton, currentQuestion.getAlt3());
			} else if (altName.equals(currentQuestion.getAlt4().getName())) {
				handleEvent(altButton, currentQuestion.getAlt4());
			}
		}

	}

	/**
	 * Takes an alternative, checks if it is correct and handles the given
	 * situation appropriately
	 * 
	 * @param Alternative
	 */
	private void handleEvent(Label lab, Alternative alt) {
		hasAnswered = true;

		if (alt.isCorrectAnswer()) {
			lab.setColor(Color.GREEN);

			Timer.schedule(new Task() {

				@Override
				public void run() {
					correctAnswer();
				}

			}, 2);
			
		} else {
			lab.setColor(Color.RED);

			Timer.schedule(new Task() {

				@Override
				public void run() {
					wrongAnswer();
				}

			}, 2);

		}
	}
	
	/**
	 * Checks if duel is finished, and assigns the area to correct player
	 * 
	 */
	private void handleDuel() {
		if(game.getDuelState().isFinished() && (game.getCurrentPlayer() == game.getDuelState().getDefendant())) {
			game.getDuelState().getWinner().getScore().updateScore(area.getValueOfArea());
			game.getDuelState().finishDuel();
		}
	}

	/**
	 * Called when the answer was correct.
	 */
	private void correctAnswer() {
		// Duel active
		if(game.getDuelState().isDuel()) {
			// Award correct player in duel
			if(game.getCurrentPlayer() == game.getDuelState().getDefendant()) {
				game.getDuelState().increaseDefenantScore();
			} else {
				game.getDuelState().increaseInitiatorScore();
			}
			// Check if duel is finished, and handle it appropriately
			handleDuel();
		// No active duel
		} else {			
			// Update score and set the new owner of the area.
			game.getCurrentPlayer().getScore().updateScore(area.getValueOfArea());
			area.setOwner(game.getCurrentPlayer());
		}
		
		// Move on to the next player screen.
		game.switchCurrentPlayer();
		nextPlayer();
	}

	/**
	 * Called when the answer was wrong.
	 */
	private void wrongAnswer() {
		// Duel active
		if(game.getDuelState().isDuel()) {
			// Check if duel is finished, and handle it appropriately
			handleDuel();
		}
		// Move on to the next player screen.
		game.switchCurrentPlayer();
		nextPlayer();
	}
}
