package Common.Models;

import java.io.Serializable;

/**
 * Micha�l Beaulieu         13048132
 * Benoit Jeunehomme        13055392
 * Bruno-Pier Touchette     13045732
 */
public class Goal implements Serializable {
    private String GoalHolder;
    private int amount;

    public Goal(String player) {
        this.GoalHolder = player;
        amount = 1;
    }

    public String getGoalHolder() {
        return GoalHolder;
    }

    public void setGoalHolder(String goalHolder) {
        GoalHolder = goalHolder;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void incAmount() {
        this.amount++;
    }

    public void decAmount() {
        this.amount--;
    }

    public String toString() {
        return String.format("%s,   %d", GoalHolder, amount);
    }
}
