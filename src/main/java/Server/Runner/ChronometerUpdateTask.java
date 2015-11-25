package Server.Runner;

import Common.Models.Game;
import Common.Models.GameInfo;
import Common.Models.Penalty;
import Common.Models.Side;
import Server.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Michaël Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class ChronometerUpdateTask implements Runnable {
    private final Server server;
    private final int TICK_VALUE = 30;

    public ChronometerUpdateTask(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.LockForUpdate();
        System.out.println("Obtained lock to update");
        try {
            for (Game g : server.GetNonCompletedGames()) {
                System.out.println("Altering " + g.toString());
                GameInfo info = server.GetGameInfo(g.getGameID());

                System.out.println("Chronometer before :" + info.getPeriodChronometer());
                info.decPeriodChronometer(TICK_VALUE);
                System.out.println("Chronometer after :" + info.getPeriodChronometer());

                //Verify if we have completed a period
                int currentChronometer = info.getPeriodChronometer();
                if (currentChronometer <= 0) {
                    // If chronometer is 0 and period is currently 3
                    if (info.getPeriod() == 3) {
                        g.setCompleted(true);
                        System.out.println("Game " + g.toString() + " is completed - No more time");
                        info.setPeriodChronometer(0);
                        server.notifyBets(g);
                    } else {
                        info.incPeriod();
                        System.out.println("Going to period (" + info.getPeriod() + ") for game " + g.toString());
                    }
                }

                // If completed, clear all penalties, update and remove completed penalties otherwise
                if (g.isCompleted()) ClearPenalties(info);
                else UpdateAllPenalties(info);
            }
        }
        finally {
            System.out.println("Releasing lock to update");
            server.UnlockUpdates();
        }
    }

    private void ClearPenalties(GameInfo info) {
        info.getVisitorPenalties().clear();
        info.getHostPenalties().clear();
    }

    private void UpdateAllPenalties(GameInfo info) {
        UpdatePenalties(info, Side.Host);
        UpdatePenalties(info, Side.Visitor);
    }

    private void UpdatePenalties(GameInfo info, Side side) {
        List<Penalty> toRemove = new ArrayList<>();
        for (Penalty p : info.getSidePenalties(side)) {
            p.decTimeLeft(TICK_VALUE);
            int timeLeft = p.getTimeLeft();
            if (timeLeft <= 0)
                toRemove.add(p);
        }

        for (Penalty p : toRemove)
            info.removeSidePenalty(p, side);
    }

}
