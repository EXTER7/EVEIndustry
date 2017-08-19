package exter.eveindustry.data.structure;

import exter.eveindustry.data.IdData;
import exter.tsl.TSLObject;

public class StructureService extends IdData
{
  public final String name;
  public final double manufacturing_time;
  public final double manufacturing_material;
  public final double manufacturing_cost;
  public final double invention_time;
  public final double invention_material;
  public final double invention_cost;
  public final double reporosessing_base;
  public final double reporosessing_multiplier_high;
  public final double reporosessing_multiplier_low;
  public final double reporosessing_multiplier_null;
  public final int structure_group_id;
  
  StructureService(TSLObject tsl)
  {
    super(tsl);
    name = tsl.getString("name", null);
    manufacturing_time = tsl.getStringAsDouble("manufacturing_time", 1.0);
    manufacturing_material = tsl.getStringAsDouble("manufacturing_material", 1.0);
    manufacturing_cost = tsl.getStringAsDouble("manufacturing_cost", 1.0);
    invention_time = tsl.getStringAsDouble("invention_time", 1.0);
    invention_material = tsl.getStringAsDouble("invention_material", 1.0);
    invention_cost = tsl.getStringAsDouble("invention_cost", 1.0);
    reporosessing_base = tsl.getStringAsDouble("reporosessing_base", 0.5);
    reporosessing_multiplier_high = tsl.getStringAsDouble("reporosessing_multiplier_high", 1.0);
    reporosessing_multiplier_low = tsl.getStringAsDouble("reporosessing_multiplier_low", 1.0);
    reporosessing_multiplier_null = tsl.getStringAsDouble("reporosessing_multiplier_null", 1.0);
    structure_group_id = tsl.getStringAsInt("structure_group_id", -1);
  }
}
