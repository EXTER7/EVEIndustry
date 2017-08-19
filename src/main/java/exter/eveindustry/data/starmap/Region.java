package exter.eveindustry.data.starmap;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class Region extends IdData
{
  public final String name;
  
  Region(TSLObject tsl)
  {
    super(tsl);
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
