package exter.eveindustry.data.reaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public final class Reaction extends IdData
{
  public final List<ItemStack> inputs;
  public final List<ItemStack> outputs;

  public Reaction(TSLObject tsl,Item.Data inventory)
  {
    super(tsl);
    
    List<ItemStack> matlist = new ArrayList<ItemStack>();
    List<TSLObject> materials_tsl = tsl.getObjectList("in");
    for(TSLObject mat:materials_tsl)
    {
      Item mat_item = inventory.get(mat.getStringAsInt("id",-1));
      int mat_amount = mat.getStringAsInt("amount",-1);
      matlist.add(new ItemStack(mat_item,mat_amount));
    }
    inputs = Collections.unmodifiableList(matlist);

    matlist = new ArrayList<ItemStack>();
    materials_tsl = tsl.getObjectList("out");
    for(TSLObject mat:materials_tsl)
    {
      Item mat_id = inventory.get(mat.getStringAsInt("id",-1));
      int mat_amount = mat.getStringAsInt("amount",-1);
      matlist.add(new ItemStack(mat_id,mat_amount));
    }
    outputs = Collections.unmodifiableList(matlist);
  }

  public ItemStack getMainOutput()
  {
    for(ItemStack m:outputs)
    {
      if(m.item.id == id)
      {
        return m;
      }
    }
    return null;
  }
  
  static public class Data extends DirectoryData<Reaction>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "reaction");
      this.items = items;
    }

    @Override
    protected Reaction createObject(TSLObject tsl)
    {
      return new Reaction(tsl,items);
    }
  }
}
