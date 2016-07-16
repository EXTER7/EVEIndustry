package exter.eveindustry.data.systemcost;


/**
 * @author exter
 * Per solar system industry cost.
 * These values can be obtained from CCP's CREST API.
 */
public class SolarSystemIndustryCost
{
  public final double manufacturing;
  public final double invention;
  
  public SolarSystemIndustryCost(double manufacturing,double invention)
  {
    this.manufacturing = manufacturing;
    this.invention = invention;
  }
}
