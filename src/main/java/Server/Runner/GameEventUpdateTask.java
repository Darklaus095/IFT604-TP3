package Server.Runner;

import Common.Models.Game;
import Common.Models.GameInfo;
import Common.Models.Goal;
import Common.Models.Penalty;
import Server.Factory.GameFactory;
import Server.Server;
import Servlets.NotificationServlet;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class GameEventUpdateTask implements Runnable {
    private final Server server;

    public GameEventUpdateTask(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        server.LockForUpdate();
        System.out.println("Event update : Obtained lock");

        try {
            for (Game g : server.GetNonCompletedGames()) {
                GameInfo info = server.GetGameInfo(g.getGameID());

                Goal go = TryAddGoal(info);
                if (go != null) {
                    System.out.println("Added goal " + go.toString() + " in game " + g.toString());
                }

                Penalty p = TryAddPenalty(info);
                if (p != null) {
                    System.out.println("Added penalty " + p.toString() + " in game " + g.toString());
                }
            }
        }
        finally {
            System.out.println("Event update : Releasing lock");
            server.UnlockUpdates();
        }
    }

    private Goal TryAddGoal(GameInfo info) {
        Goal g = GameFactory.TryCreateGoal(info);
        if (g == null) return null;

        //TODO: Created a goal for a team => Prepare a notification for Android client
        //NotificationServlet.AddMessage();
        return g;
    }

    private Penalty TryAddPenalty(GameInfo info) {
        Penalty p = GameFactory.TryCreatePenalty(info);
        if (p == null) return null;

        //TODO: Created a penalty for a team => Prepare a notification for Android client
        return p;
    }
}
