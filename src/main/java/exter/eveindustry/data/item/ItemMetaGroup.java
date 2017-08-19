package exter.eveindustry.data.item;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class ItemMetaGroup extends IdData
{
  public final String name;

  ItemMetaGroup(TSLObject tsl)
  {
    super(tsl);
    name = tsl.getString("name", null);
  }
  
  static public class Data extends DirectoryData<ItemMetaGroup>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "item/metagroup");
    }

    @Override
    protected ItemMetaGroup createObject(TSLObject tsl)
    {
      return new ItemMetaGroup(tsl);
    }
  }
}
