package exter.eveindustry.data.starmap;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.tsl.TSLObject;

public class SolarSystem
{
  public final int id;
  public final String name;
  public final int region;
  
  SolarSystem(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    name = tsl.getString("name", null);
    region = tsl.getStringAsInt("region", -1);
  }
  
  static public class Data extends DirectoryData<SolarSystem>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "solarsystem");
    }

    @Override
    protected SolarSystem createObject(TSLObject tsl)
    {
      return new SolarSystem(tsl);
    }
  }
}
