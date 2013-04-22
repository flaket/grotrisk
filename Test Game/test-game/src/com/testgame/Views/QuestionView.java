package com.testgame.Views;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.testgame.Takeover;
import com.testgame.Models.Area;
import com.testgame.Models.Constants;
import com.testgame.Models.Question;
import com.testgame.Models.SkinSingleton;

public class QuestionView extends AbstractPanelView {

	private final Sound correct;
	private final Sound wrong;
	private Question currentQuestion;
	private Label questionText, countDownText;
	private LabelStyle labelStyle;
	// How many seconds you get to answer a question.
	private int countDownTime = 20;
	private int currentTime = 0;
	protected Area area;
	private boolean hasAnswered = false;

	/**
	 * Constructor keeping a reference to the main Game class
	 * 
	 * @param game
	 */
	public QuestionView(Takeover game) {
		super(game);
		correct = Gdx.audio.newSound(Gdx.files.internal("data/correct.wav"));
		wrong = Gdx.audio.newSound(Gdx.files.internal("data/wrong.wav"));
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
						// Count down label must update each render
						countDownText.setText("" + (countDownTime - currentTime));
						startTimer();
					} else {
						wrongAnswer();
					}
				}
			}

		}, 1);
	}

	/**
	 * Helper method setting the next screen. If the game is considered finished
	 * it sets the end game screen.
	 */
	protected void nextPlayer() {
		if (isGameFinished()) {
			game.setScreen(new EndGameView(game));
		} else {
			game.setScreen(new NextPlayerView(game));
		}
	}

	/**
	 * Currently: Asks if the game has run for # moves and is not in a duel
	 * state.
	 * 
	 * @return
	 */
	private boolean isGameFinished() {
		if (game.getPlaysCounter() >= Constants.QUESTION_MAXPLAYROUNDS
				&& !game.getDuelState().isDuel()) {
			return true;
		}
		return false;
	}

	/**
	 * Updates and draws stuff.
	 */
	@Override
	public void render(float delta) {
		super.render(delta);

		batch.begin();
		questionText.draw(batch, 1.0f);
		countDownText.draw(batch, 1.0f);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		Gdx.app.log("TAKEOVER-TIME", "END - Question resize time: "+ new SimpleDateFormat("HH:mm:ss.SSSS").format(new Date()));
	}
	/**
	 * Button initialization
	 */
	public void initializeButtons() {
		// Retrieve question from question pool
		currentQuestion = game.getQuestionPool().random();
		
		// Question text field
		labelStyle = SkinSingleton.getInstance().getLabelStyle();

		questionText = new Label(currentQuestion.getQuestionText(),
				labelStyle);
		questionText.setFontScale(0.7f);
			
		questionText.setAlignment(2);
		questionText.setColor(0.647059f, 0.164706f, 0.164706f, 1.0f);
		questionText.setWidth(0.8f * Gdx.graphics.getWidth());
		questionText.setWrap(true);
		
		questionText.setX(Gdx.graphics.getWidth() / 2 - questionText.getWidth()
				/ 2);
		questionText.setY(Gdx.graphics.getHeight() / 2
				- questionText.getHeight() / 2 + Gdx.graphics.getHeight() * 0.22f);
		
		
		
		
		countDownText = new Label("" + countDownTime, labelStyle);
		countDownText.setColor(0.647059f, 0.164706f, 0.164706f, 1.0f);
		countDownText.setX((4 * Gdx.graphics.getWidth()) / 5);
		countDownText.setY((3 * Gdx.graphics.getHeight()) / 4);
		
	}

	/**
	 * Called when the current screen changes from this to a different screen.
	 * Remember to dispose objects.
	 */
	public void hide() {
		super.hide();
		SkinSingleton.getInstance().resetFontSize();
		// Stop the count down clock. Not 100% sure if this is working
		// correctly.
		Timer.instance.clear();
		currentTime = 0;
		hasAnswered = false;
//		altGroup.dispose();
	}
	
	public void dispose() {
		correct.dispose();
		wrong.dispose();
	}
	
	protected void setAnswerd(boolean bol) {
		hasAnswered = bol;
	}
	
	/**
	 * Provides current question to its children
	 * 
	 * @return Question
	 */
	protected Question getCurrentQuestion() {
		return currentQuestion;
	}
	
	/**
	 * Called when the answer was correct.
	 */
	protected void correctAnswer() {
		// Duel active
		if (game.getDuelState().isDuel()) {
			// Award correct player in duel
			if (game.getCurrentPlayer() == game.getDuelState().getDefendant()) {
				game.getDuelState().increaseDefenantScore();
			} else {
				game.getDuelState().increaseInitiatorScore();
			}
			// Check if duel is finished, and handle it appropriately
			handleDuel();
			// No active duel
		} else {
			// Update score and set the new owner of the area.
			game.getCurrentPlayer().getScore()
					.updateScore(area.getValueOfArea());
			area.setOwner(game.getCurrentPlayer());
		}

		// Move on to the next player screen.
		game.switchCurrentPlayer();
		nextPlayer();
	}

	/**
	 * Called when the answer was wrong.
	 */
	protected void wrongAnswer() {
		// Duel active
		if (game.getDuelState().isDuel()) {
			// Check if duel is finished, and handle it appropriately
			handleDuel();
		}
		// Move on to the next player screen.
		game.switchCurrentPlayer();
		nextPlayer();
	}
	
	/**
	 * Checks if duel is finished, and assigns the area to correct player
	 * 
	 */
	protected void handleDuel() {
		if (game.getDuelState().isFinished()
				&& (game.getCurrentPlayer() == game.getDuelState()
						.getDefendant())) {
			if (game.getDuelState().getWinner() == game.getDuelState()
					.getInitiator()) {
				// Update score of winner
				game.getDuelState().getWinner().getScore()
						.updateScore(area.getValueOfArea());
				// Update owner of area
				area.setOwner(game.getDuelState().getWinner());
			}
			// Set current player to initiator of duel, so that correct next
			// player
			// is set afterwards
			game.setCurrentPlayer(game.getDuelState().getInitiator());
			// Clean up after duel
			game.getDuelState().finishDuel();
		}
	}
	
	/**
	 * Plays the sound of the correct answer
	 */
	protected void playCorrectAnswerSound() {
		correct.play();
	}
	
	/**
	 * Plays the sound of the wrong answer
	 */
	protected void playWrongAnswerSound() {
		wrong.play();
	}

	public Area getArea() {
		return area;
	}

	/**
	 * Sets which area the questions might give you
	 * @param area
	 */
	public void setArea(Area area) {
		this.area = area;
	}
	
	@Override
	public void show() {
		super.show();
		startTimer();
		Gdx.app.log("TAKEOVER-TIME", "Question show time: "+ new SimpleDateFormat("HH:mm:ss.SSSS").format(new Date()));
	}
}
