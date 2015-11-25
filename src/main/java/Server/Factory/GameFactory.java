package Server.Factory;

import Common.Models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class GameFactory {
    private static final double P_GOAL = 1. / 10;
    private static final double P_EVEN = 1. / 2;
    private static final double P_PENALTY = 1. / 15;
    private static final double P_PENALTY_LENGTH = 4. / 5;
    private static final int NB_PLAYER_PER_TEAM = 10;
    private static final Random generator = new Random();
    private static final String[] TEAM_NAMES =
            new String[]{"Montreal", "San-Jose", "Toronto", "Washington", "Chicago", "Ottawa",
                    "Edmonton", "New-York", "Florida", "Tampa Bay", "Minnesota", "Columbus", "Vancouver",
                    "Los Angeles", "Calgary", "Nashville", "Dallas", "Anaheim", "Buffalo", "Detroit",
                    "Philadelphia", "Boston", "Winnipeg", "Colorado", "Arizona"};

    private static int gameId = 0;
    private static int periodLength = 20;  // in minutes
    private static int playerNo = 0;

    private static List<String> teams;

    private static int GetNextID() {
        return ++gameId;
    }

    private static int GetNextPlayerNo() {
        return ++playerNo;
    }

    public static Game GenerateGame() {
        Game newGame = new Game(GetNextID(), TakeRandomTeam(), TakeRandomTeam());
        return newGame;
    }

    private static String TakeRandomTeam() {
        int index = generator.nextInt(teams.size());
        String team = teams.get(index);
        teams.remove(index);
        return team;
    }

    public static GameInfo GenerateGameInfo(Game game) {
        return new GameInfo(game.getGameID(), periodLength);
    }

    public static void UpdatePeriodLength(int minutes) {
        periodLength = minutes;
    }

    public static Side GetSide() {
        return (generator.nextDouble() > P_EVEN) ? Side.Host : Side.Visitor;
    }

    public static Goal TryCreateGoal(GameInfo info) {
        if (generator.nextDouble() > P_GOAL) return null;

        //We add a goal to the team
        Side side = GetSide();
        Goal g = null;

        if ((generator.nextDouble() > P_EVEN) && info.getSideGoals(side).size() < NB_PLAYER_PER_TEAM) {
            //Create a new goal for side
            g = new Goal("Player " + GetNextPlayerNo());
            info.addSideGoal(g, side);
        } else {
            //Add a goal value to a random goal
            List<Goal> goals = info.getSideGoals(side);
            if (goals.size() > 0) {
                int index = generator.nextInt(goals.size());
                g = goals.get(index);
                info.addSideGoal(g, side);
            }
        }

        return g;
    }

    public static Penalty TryCreatePenalty(GameInfo info) {
        if (generator.nextDouble() > P_PENALTY) return null;

        // We add penalty to the team;
        Side side = GetSide();
        int duration = ((generator.nextDouble() > P_PENALTY_LENGTH) ? Penalty.LONG_PENALTY : Penalty.SHORT_PENALTY) * 60;
        Penalty p = new Penalty("Player Name " + GetNextPlayerNo(), duration);
        info.addSidePenalty(p, side);

        return p;
    }

    public static void Initialize() {
        teams = new ArrayList<>(Arrays.asList(TEAM_NAMES));
    }
}
