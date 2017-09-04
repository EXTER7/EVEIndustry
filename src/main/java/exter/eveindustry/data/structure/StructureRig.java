package exter.eveindustry.data.structure;

import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.starmap.SolarSystem;
import exter.tsl.TSLObject;

public class StructureRig extends IdData
{
  public final double time;
  public final double material;
  public final double cost;

  public final double highsec_bonus;
  public final double lowsec_bonus;
  public final double nullsec_bonus;
  
  public final List<Integer> fit_groups;

  StructureRig(TSLObject tsl)
  {
    super(tsl);

    material = tsl.getStringAsDouble("material", 1.0);
    time = tsl.getStringAsDouble("time", 1.0);
    cost = tsl.getStringAsDouble("cost", 1.0);
    
    highsec_bonus = tsl.getStringAsDouble("highsec_bonus", 1.0);
    lowsec_bonus = tsl.getStringAsDouble("lowsec_bonus", 1.0);
    nullsec_bonus = tsl.getStringAsDouble("nullsec_bonus", 1.0);
    
    fit_groups = Collections.unmodifiableList(tsl.getStringAsIntegerList("fit_group"));
  }
  
  public double getSolarSystemBonus(SolarSystem system)
  {
    if(system.sec_status < 5.0)
    {
      return lowsec_bonus;
    } else if(system.sec_status <= 0.0)
    {
      return nullsec_bonus;
    } else
    {
      return highsec_bonus;
    }
  }

  public double getMaterialBonus(SolarSystem system)
  {
    double sec_bonus = getSolarSystemBonus(system);
    return 1.0 + material*sec_bonus;
  }

  public double getTimeBonus(SolarSystem system)
  {
    double sec_bonus = getSolarSystemBonus(system);
    return 1.0 + time*sec_bonus;
  }

  public double getCostBonus(SolarSystem system)
  {
    double sec_bonus = getSolarSystemBonus(system);
    return 1.0 + cost*sec_bonus;
  }
}
