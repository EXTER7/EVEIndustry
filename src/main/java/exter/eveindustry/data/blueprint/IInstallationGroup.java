package exter.eveindustry.data.blueprint;

/**
 * @author exter
 * Manufacturing installation-group (pairs installations with item groups)
 */
public interface IInstallationGroup
{
  /**
   * Get the unique ID of the installation-group
   */
  public int getID();

  /**
   * Get the group ID of the items this installation can manufacture.
   * The result should match the group ID of the blueprint's product.
   */
  public int getGroupID();

  /**
   * Get the manufacturing time bonus in the [0, 1] range.
   */
  public double getTimeBonus();

  /**
   * Get the manufacturing material bonus in the [0, 1] range.
   */
  public double getMaterialBonus();
  
  /**
   * Get the manufacturing install cost bonus in the [0, 1] range.
   */
  public double getCostBonus();
}
