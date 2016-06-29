package exter.eveindustry.data.starmap;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public class Region
{
  public final int id;
  public final String name;
  
  Region(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    name = tsl.getString("name", null);
  }
  
  static public class Data extends DirectoryData<Region>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "solarsystem/region");
    }

    @Override
    protected Region createObject(TSLObject tsl)
    {
      return new Region(tsl);
    }
  }
}
