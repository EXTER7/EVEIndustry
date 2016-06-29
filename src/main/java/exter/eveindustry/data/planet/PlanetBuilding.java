package exter.eveindustry.data.planet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public final class PlanetBuilding
{

  public final ItemStack product;
  public final int tax;
  public final int level;
  public final List<ItemStack> materials;
 
  PlanetBuilding(TSLObject tsl, Item.Data inventory)
  {
    ArrayList<ItemStack> matlist = new ArrayList<ItemStack>();
    product = new ItemStack(inventory.get(tsl.getStringAsInt("id",-1)),tsl.getStringAsInt("amount",-1));
    level = tsl.getStringAsInt("level",-1);
    tax = tsl.getStringAsInt("tax",-1);
    List<TSLObject> tsl_materials = tsl.getObjectList("in");
    for(TSLObject mat_tsl:tsl_materials)
    {
      int mat_id = mat_tsl.getStringAsInt("id",-1);
      int raw_amount = mat_tsl.getStringAsInt("amount",0);

      matlist.add(new ItemStack(inventory.get(mat_id), raw_amount));
    }
    materials = Collections.unmodifiableList(matlist);
  }
  
  static public class Data extends DirectoryData<PlanetBuilding>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "planet");
      this.items = items;
    }

    @Override
    protected PlanetBuilding createObject(TSLObject tsl)
    {
      return new PlanetBuilding(tsl,items);
    }
  }
}
