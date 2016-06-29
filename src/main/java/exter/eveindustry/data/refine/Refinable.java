package exter.eveindustry.data.refine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public class Refinable
{
  public final List<ItemStack> products;
  public final ItemStack item;
  public final int skill_id;

  Refinable(TSLObject tsl,Item.Data inventory)
  {

    ArrayList<ItemStack> prodlist = new ArrayList<ItemStack>();

    item = new ItemStack(inventory.get(tsl.getStringAsInt("id",-1)),tsl.getStringAsInt("batch",-1));
    skill_id = tsl.getStringAsInt("sid",-1);
    
    List<TSLObject> products_tsl = tsl.getObjectList("product");
    for(TSLObject prod:products_tsl)
    {
      Item prod_item = inventory.get(prod.getStringAsInt("id",-1));
      int min_amount = prod.getStringAsInt("amount",-1);
      prodlist.add(new ItemStack(prod_item,min_amount));
    }
    products = Collections.unmodifiableList(prodlist);
  }
  
  static public class Data extends DirectoryData<Refinable>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "refine");
      this.items = items;
    }

    @Override
    protected Refinable createObject(TSLObject tsl)
    {
      return new Refinable(tsl,items);
    }
  }
}
