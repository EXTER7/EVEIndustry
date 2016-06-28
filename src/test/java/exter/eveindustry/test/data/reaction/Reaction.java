package exter.eveindustry.test.data.reaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.reaction.IReaction;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.test.data.inventory.InventoryDA;
import exter.eveindustry.test.data.inventory.Item;
import exter.tsl.TSLObject;

public class Reaction implements IReaction
{
  public final List<ItemStack> Inputs;
  public final List<ItemStack> Outputs;
  public final int ID;

  public Reaction(TSLObject tsl)
  {
    ID = tsl.getStringAsInt("id",-1);
    
    List<ItemStack> matlist = new ArrayList<ItemStack>();
    List<TSLObject> materials_tsl = tsl.getObjectList("in");
    for(TSLObject mat:materials_tsl)
    {
      Item mat_id = InventoryDA.items.get(mat.getStringAsInt("id",-1));
      int mat_amount = mat.getStringAsInt("amount",-1);
      matlist.add(new ItemStack(mat_id,mat_amount));
    }
    Inputs = Collections.unmodifiableList(matlist);

    matlist = new ArrayList<ItemStack>();
    materials_tsl = tsl.getObjectList("out");
    for(TSLObject mat:materials_tsl)
    {
      Item mat_id = InventoryDA.items.get(mat.getStringAsInt("id",-1));
      int mat_amount = mat.getStringAsInt("amount",-1);
      matlist.add(new ItemStack(mat_id,mat_amount));
    }
    Outputs = Collections.unmodifiableList(matlist);
  }

  @Override
  public List<ItemStack> getInputs()
  {
    return Inputs;
  }

  @Override
  public List<ItemStack> getOutputs()
  {
    return Outputs;
  }

  @Override
  public int getID()
  {
    return ID;
  }
  
  public long GetMainOutputAmount()
  {
    for(ItemStack m:Outputs)
    {
      if(m.item_id.getID() == ID)
      {
        return m.amount;
      }
    }
    return 0;
  }
}
