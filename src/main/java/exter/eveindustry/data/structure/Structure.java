package exter.eveindustry.data.structure;

import exter.eveindustry.data.IdData;
import exter.tsl.TSLObject;

public class Structure extends IdData
{
  public final String name;
  public final double manufacturing_time;
  public final double manufacturing_material;
  public final double manufacturing_cost;
  public final double invention_time;
  public final double invention_material;
  public final double invention_cost;
  
  public final int service_slots;
  public final int rig_slots;
  
  Structure(TSLObject tsl)
  {
    super(tsl);
    name = tsl.getString("name", null);
    manufacturing_time = tsl.getStringAsDouble("manufacturing_time", 1.0);
    manufacturing_material = tsl.getStringAsDouble("manufacturing_material", 1.0);
    manufacturing_cost = tsl.getStringAsDouble("manufacturing_cost", 1.0);
    invention_time = tsl.getStringAsDouble("invention_time", 1.0);
    invention_material = tsl.getStringAsDouble("invention_material", 1.0);
    invention_cost = tsl.getStringAsDouble("invention_cost", 1.0);
    service_slots = tsl.getStringAsInt("service_slots", 0);
    rig_slots = tsl.getStringAsInt("rig_slots", 0);
  }

}
