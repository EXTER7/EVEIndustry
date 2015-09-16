package exter.eveindustry.data.refinable;

import java.util.List;

import exter.eveindustry.item.ItemStack;

/**
 * @author exter
 * Refinable item
 */
public interface IRefinable
{
  /**
   * Get the unique refinable ID.
   */
  public int getID();

  /**
   * Get the item and batch amount required for refining.
   */
  public ItemStack getRequiredItem();

  /**
   * Get the ore specific processing skill ID.
   */
  public int getSkill();

  /**
   * Get the base items produced by the refining.
   */
  public List<ItemStack> getProducts();
}
