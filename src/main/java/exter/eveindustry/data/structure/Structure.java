package exter.eveindustry.data.structure;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.tsl.TSLObject;

public class Structure extends IdData
{
  public final double manufacturing_time;
  public final double manufacturing_material;
  public final double manufacturing_cost;
  
  public final int service_slots;
  public final int rig_slots;
  public final int rig_size;
  
  Structure(TSLObject tsl)
  {
    super(tsl);
    manufacturing_time = tsl.getStringAsDouble("time", 1.0);
    manufacturing_material = tsl.getStringAsDouble("material", 1.0);
    manufacturing_cost = tsl.getStringAsDouble("cost", 1.0);
    service_slots = tsl.getStringAsInt("service_slots", 0);
    rig_slots = tsl.getStringAsInt("rig_slots", 0);
    rig_size = tsl.getStringAsInt("rig_size", 0);
  }

  static public class Data extends DirectoryData<Structure>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "refine");
    }

    @Override
    protected Structure createObject(TSLObject tsl)
    {
      return new Structure(tsl);
    }
  }
}
