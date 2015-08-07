package exter.eveindustry.data.reaction;

import exter.eveindustry.item.ItemStack;

/**
 * @author exter
 * Starbase control tower.
 */
public interface IStarbaseTower
{
  /**
   * Get the unique starbase tower ID.
   */
  public int getID();
  
  /**
   * Get the required type and amount of fuel per hour.
   */
  public ItemStack getRequiredFuel();
}
