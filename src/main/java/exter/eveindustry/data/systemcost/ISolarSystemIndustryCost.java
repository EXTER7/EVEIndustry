package exter.eveindustry.data.systemcost;


/**
 * @author exter
 * Per solar system industry cost.
 * These values can be obtained from CCP's CREST API.
 */
public interface ISolarSystemIndustryCost
{
  /**
   * Get the manufacturing cost of the solar system.
   */
  public double getManufacturingCost();

  /**
   * Get the invention cost of the solar system.
   */
  public double getInventionCost();
}
