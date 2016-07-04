package exter.eveindustry.data.item;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class ItemMetaGroup
{
  public final int id;
  public final String name;

  ItemMetaGroup(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
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
