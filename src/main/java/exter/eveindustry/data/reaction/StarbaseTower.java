package exter.eveindustry.data.reaction;

import exter.eveindustry.data.access.FileData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public final class StarbaseTower
{
  public final Item item;
  public final ItemStack fuel;
  public final String name;

  public StarbaseTower(TSLObject tsl,Item.Data inventory)
  {
    item = inventory.get(tsl.getStringAsInt("id",-1));
    fuel = new ItemStack(inventory.get(tsl.getStringAsInt("fuel_id",-1)),tsl.getStringAsInt("fuel_amount",-1));
    name = tsl.getString("name",null);
  }
  
  static public class Data extends FileData<StarbaseTower>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "reaction/starbases.tsl");
      this.items = items;
    }

    @Override
    protected StarbaseTower createObject(TSLObject tsl)
    {
      return new StarbaseTower(tsl,items);
    }

    @Override
    protected int getID(StarbaseTower obj)
    {
      return obj.item.id;
    }
  }

}
