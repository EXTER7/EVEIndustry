package exter.eveindustry.data.item;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class ItemCategory
{
  public final int id;
  public final String name;
  public final int icon_id;
  
  public ItemCategory(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    name = tsl.getString("name", null);
    icon_id = tsl.getStringAsInt("icon", -1);
  }
  
  static public class Data extends DirectoryData<ItemCategory>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "item/category");
    }

    @Override
    protected ItemCategory createObject(TSLObject tsl)
    {
      return new ItemCategory(tsl);
    }
  }
}
