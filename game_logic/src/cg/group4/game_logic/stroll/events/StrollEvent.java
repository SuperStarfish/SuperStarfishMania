package cg.group4.game_logic.stroll.events;

import cg.group4.data_structures.subscribe.Subject;
import cg.group4.game_logic.StandUp;
import cg.group4.util.timer.Timer;
import cg.group4.util.timer.TimerStore;
import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Disposable;

import java.util.Observable;
import java.util.Observer;

/**
 * Interface that gets implemented by every event.
 */
public abstract class StrollEvent extends InputAdapter implements Disposable, Observer, InputProcessor {

    /**
     * Timer to constrain the amount of time spent on an event.
     */
    protected final Observer cEventStopObserver = new Observer() {
        @Override
        public void update(final Observable o, final Object arg) {
            clearEvent();
        }
    };
    
    /**
     * The amount of time in seconds an event takes. 
     */
    protected int EVENT_DURATION = 60;
    
    /**
     * Every strollEvent has a respective timer.
     */
    protected Timer cEventTimer = new Timer("EVENT", EVENT_DURATION);
    /**
     * Subject to detect event changes.
     */
    protected Subject cDataSubject;

    /**
     * Inputprocessor of the strollevent.
     */
    protected InputProcessor cProcessor;

    /**
     * Constructor, creates a new stroll event.
     */
    public StrollEvent() {
        cDataSubject = new Subject();

        Gdx.app.log(this.getClass().getSimpleName(), "Event started!");
        StandUp.getInstance().getUpdateSubject().addObserver(this);

        TimerStore.getInstance().addTimer(cEventTimer);
        cEventTimer.getStopSubject().addObserver(cEventStopObserver);
        cEventTimer.reset();

        cProcessor = Gdx.input.getInputProcessor();
        if (cProcessor instanceof InputMultiplexer) {
            ((InputMultiplexer) cProcessor).addProcessor(this);
        }
    }

    /**
     * Cleanup after the event.
     */
    protected abstract void clearEvent();

    /**
     * Starts the event.
     */
    public abstract void start();

    /**
     * Getter for the event subject. Detects event changes.
     *
     * @return The event data subject.
     */
    public Subject getEventChangeSubject() {
        return cDataSubject;
    }

    /**
     * Calls dispose using true, helper method for clearing events.
     */
    public final void dispose() {
        dispose(true);
    }

    /**
     * Method that gets called to dispose of the event.
     *
     * @param eventCompleted If the event is completed or not.
     */
    public void dispose(boolean eventCompleted) {
        StandUp.getInstance().getUpdateSubject().deleteObserver(this);
        Gdx.app.log(this.getClass().getSimpleName(), "Event completed!");
        cEventTimer.getStopSubject().deleteObserver(cEventStopObserver);
        cEventTimer.dispose();
        if (eventCompleted) {
        	StandUp.getInstance().getStroll().eventFinished(getReward());
        } else {
        	StandUp.getInstance().getStroll().cancelEvent();
        }
        
        TimerStore.getInstance().removeTimer(cEventTimer);
    }

    /**
     * Returns the reward accumulated by completing the event.
     *
     * @return the reward.
     */
    public abstract int getReward();

    @Override
    public final boolean keyDown(final int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.F1) {
            final InputAdapter myself = this;
            if (cProcessor instanceof InputMultiplexer) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMultiplexer) cProcessor).removeProcessor(myself);
                    }
                });
            }
            this.dispose(false);
        }
        return false;
    }

}
