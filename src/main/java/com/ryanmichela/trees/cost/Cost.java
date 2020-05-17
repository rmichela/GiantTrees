package com.ryanmichela.trees.cost;

import org.bukkit.entity.Player;

import java.util.List;

public abstract class Cost {
    private final double quantity;

    protected Cost(double quantity) {
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    /**
     * Charge a list of costs to the given player.
     *
     * @param player the player to apply the costs to
     * @param costs	a list of costs
     */
    public static void apply(Player player, List<Cost> costs) {
        for (Cost c : costs) {
            c.apply(player);
        }
    }
    /**
     * Check if the costs are applicable to the given player.
     *
     * @param player the player to check
     * @param costs a list of costs
     * @return true if the costs are applicable, false otherwise
     */
    public static boolean isApplicable(Player player, List<Cost> costs) {
        for (Cost c : costs) {
            if (!c.isApplicable(player))
                return false;
        }
        return true;
    }

    /**
     * Check if the player can afford to pay the costs.
     *
     * @param player the player to check
     * @param costs a list of costs
     * @return true if the costs are affordable, false otherwise
     */
    public static boolean isAffordable(Player player, List<Cost> costs) {
        for (Cost c : costs) {
            if (!c.isAffordable(player))
                return false;
        }
        return true;
    }

    /**
     * Check if this cost is affordable to the player.
     *
     * @param player the player to check for
     * @return true if the player can afford this cost; false otherwise
     */
    public abstract boolean isAffordable(Player player);

    /**
     * Apply this cost to the player.  This will take whatever resources the
     * implementing costs specifies.  A check for affordability is not done here;
     * {@link #isAffordable(org.bukkit.entity.Player)} should be called just before
     * this call is made.
     *
     * @param player the player to take the cost from
     */
    public abstract void apply(Player player);

    /**
     * Check if applying this cost to the given player actually makes sense.
     * For example a durability cost would not make sense if the item specified
     * does not have a damage bar.
     *
     * @param player the player to check for
     * @return true if the cost makes sense; false otherwise
     */
    public boolean isApplicable(Player player) {
        return true;
    }
}
