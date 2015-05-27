package cg.group4.view.screen;

import cg.group4.game_logic.StandUp;
import cg.group4.util.timer.TimeKeeper;
import cg.group4.util.timer.Timer;
import cg.group4.view.screen_mechanics.ScreenLogic;
import cg.group4.view.screen_mechanics.ScreenStore;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public final class HomeScreen extends ScreenLogic {
    /**
     * Container group used for the layout of the view.
     */
    protected Table cTable;

    /**
     * Buttons for the stroll, settings.
     */
    protected TextButton cStrollButton, cSettingsButton;

    /**
     * Labels for title, timer.
     */
    protected Label title, timer;

    /**
     * Observer that gets called on the start of a new stroll.
     */
    protected Observer cNewStrollObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            ScreenStore.getInstance().addScreen("Stroll", new StrollScreen());
        }
    };

    /**
     * Observer to subscribe to the tick subject of the interval timer.
     */
    protected Observer cIntervalTickObserver;

    /**
     * The interval timer of the game.
     */
    protected Timer cIntervalTimer;

    /**
     * Creates the home screen.
     */
    public HomeScreen() {
        StandUp.getInstance().getNewStrollSubject().addObserver(cNewStrollObserver);
    }

    @Override
    protected WidgetGroup createWidgetGroup() {
        cTable = new Table();
        cTable.setFillParent(true);


        initHomeScreenTitle();
        initStrollIntervalTimer();

        initStrollButton();
        initSettingsButton();

        return cTable;
    }

    /**
     * Initializes the title on the home screen.
     */
    public final void initHomeScreenTitle() {
        title = new Label("Super StarFish Mania", cGameSkin.get("default_labelStyle", Label.LabelStyle.class));
        cTable.row().expandY();
        cTable.add(title);
    }

    /**
     * Initializes the stroll interval timer to the view.
     * The default label is "3600".
     * If the timer is started, its label will change to the time left on the timer.
     */
    public final void initStrollIntervalTimer() {
        timer = new Label(
                Integer.toString(Timer.Global.INTERVAL.getDuration()),
                cGameSkin.get("default_labelStyle", Label.LabelStyle.class));

        cIntervalTickObserver = new Observer() {

            @Override
            public void update(Observable o, Object arg) {
                timer.setText(arg.toString());
            }
        };

        cIntervalTimer = TimeKeeper.getInstance().getTimer(Timer.Global.INTERVAL.name());
        cIntervalTimer.getTickSubject().addObserver(cIntervalTickObserver);

        cTable.row().expandY();
        cTable.add(timer);
    }

    /**
     * Initializes the stroll buttons on the home screen.
     */
    public final void initStrollButton() {
        cStrollButton = cGameSkin.generateDefaultMenuButton("Stroll");
        cStrollButton.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                StandUp.getInstance().startStroll();
                ScreenStore.getInstance().setScreen("Stroll");
            }
        });
        cTable.row().expandY();
        cTable.add(cStrollButton);
    }

    /**
     * Initializes the settings button on the home screen.
     */
    public final void initSettingsButton() {
        cSettingsButton = cGameSkin.generateDefaultMenuButton("Settings");
        cSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                ScreenStore.getInstance().setScreen("Settings");
            }
        });
        cTable.row().expandY();
        cTable.add(cSettingsButton);
    }
}
