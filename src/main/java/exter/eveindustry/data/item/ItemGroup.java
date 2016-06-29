package exter.eveindustry.data.item;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public class ItemGroup
{
  public final int id;
  public final int category_id;
  public final String name;
  public final int icon_id;
  
  public ItemGroup(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    name = tsl.getString("name", null);
    category_id = tsl.getStringAsInt("cid", -1);
    icon_id = tsl.getStringAsInt("icon", -1);
  }
  
  static public class Data extends DirectoryData<ItemGroup>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "item/group");
    }

    @Override
    protected ItemGroup createObject(TSLObject tsl)
    {
      return new ItemGroup(tsl);
    }
  }
}
