package exter.eveindustry.test.data.systemcost;

import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;


public class TestSystemCost implements ISolarSystemIndustryCost
{
  public final int SystemID;
  
  public TestSystemCost(int sys)
  {
    SystemID = sys;
  }

  @Override
  public double getManufacturingCost()
  {
    return 0;
  }

  @Override
  public double getInventionCost()
  {
    return 0;
  }
}
