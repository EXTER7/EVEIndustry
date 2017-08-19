package exter.eveindustry.data.starmap;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class SolarSystem extends IdData
{
  public final String name;
  public final int region;
  
  SolarSystem(TSLObject tsl)
  {
    super(tsl);
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
