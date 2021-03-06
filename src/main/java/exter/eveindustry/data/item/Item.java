package exter.eveindustry.data.item;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public final class Item extends IdData
{
  public final String name;
  public final int group_id;
  public final double volume;
  public final boolean marketable;
  public final int metagroup_id;
  public final int icon_id;
  
  public final String name_lowercase;
  
  Item(TSLObject tsl)
  {
    super(tsl);
    name = tsl.getString("name", null);
    group_id = tsl.getStringAsInt("gid", -1);
    volume = tsl.getStringAsFloat("vol", -1);
    marketable = tsl.getStringAsInt("market",0) != 0;
    metagroup_id = tsl.getStringAsInt("mg", -1);
    icon_id = tsl.getStringAsInt("icon", -1);
    name_lowercase = name.toLowerCase();
  }
  
  static public class Data extends DirectoryData<Item>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "item");
    }

    @Override
    protected Item createObject(TSLObject tsl)
    {
      return new Item(tsl);
    }
  }
}
