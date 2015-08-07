package exter.eveindustry.data.reaction;

import java.util.List;
import exter.eveindustry.item.ItemStack;

/**
 * @author exter
 * Starbase reactions
 */
public interface IReaction
{
  /**
   * Get the unique reaction ID.
   */
  public int getID();

  /**
   * Get the items required for the reaction.
   */
  public List<ItemStack> getInputs();

  /**
   * Get the items produced by the reaction.
   */
  public List<ItemStack> getOutputs();
}
