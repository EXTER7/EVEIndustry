package exter.eveindustry.data.blueprint;

/**
 * @author exter
 * Installation used for invention.
 */
public interface IInventionInstallation
{
  /**
   * Get the unique invention installation ID.
   */
  public int getID();
  
  /**
   * Get the invention time bonus in the [0, 1] range.
   */
  public double getTimeBonus();

  /**
   * Get the invention install cost bonus in the [0, 1] range.
   */
  public double getCostBonus();

  /**
   * True, if this installation only accepts relics for T3 invention.
   */
  public boolean isForRelics();
}
