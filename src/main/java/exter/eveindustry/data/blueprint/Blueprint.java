package exter.eveindustry.data.blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.item.ItemStack;
import exter.tsl.TSLObject;

public final class Blueprint extends IdData
{
  static final public class Invention
  {
    public class Relic
    {
      public final Item item;
      public final int runs;
      public final double chance;

      Relic(TSLObject tsl,Item.Data inventory)
      {
        item = inventory.get(tsl.getStringAsInt("id",-1));
        runs = tsl.getStringAsInt("runs",0);
        chance = tsl.getStringAsDouble("chance",0);
      }
    }
    
    public final int time;
    public final int runs;
    public final double chance;
    public final List<ItemStack> materials;
    public final int encryption_skill_id;
    public final Set<Integer> datacore_skill_ids;
    public final Map<Integer,Relic> relics;
    public final Relic default_relic;
    
    Invention(TSLObject tsl,Item.Data inventory)
    {
      ArrayList<ItemStack> matlist = new ArrayList<ItemStack>();
      time = tsl.getStringAsInt("time",-1);
      runs = tsl.getStringAsInt("runs",0);
      chance = tsl.getStringAsDouble("chance",0);
      encryption_skill_id = tsl.getStringAsInt("eskill",-1);
      Set<Integer> dskills = new HashSet<Integer>(tsl.getStringAsIntegerList("dskill"));
      datacore_skill_ids = Collections.unmodifiableSet(dskills);
      List<TSLObject> tsl_materials = tsl.getObjectList("material");
      for(TSLObject mat_tsl:tsl_materials)
      {
        Item mat = inventory.get(mat_tsl.getStringAsInt("id",-1));
        int amount = mat_tsl.getStringAsInt("amount",0);
        matlist.add(new ItemStack(mat, amount));
      }
      materials = Collections.unmodifiableList(matlist);
      List<TSLObject> tsl_relics = tsl.getObjectList("relic");
      if(tsl_relics != null && tsl_relics.size() > 0)
      {
        relics = new HashMap<Integer,Relic>();
        Relic min = null;
        for(TSLObject tr: tsl_relics)
        {
          Relic r = new Relic(tr,inventory);
          relics.put(r.item.id, r);
          if(min == null || r.runs < min.runs)
          {
            min = r;
          }
        }
        default_relic = min;
      } else
      {
        relics = null;
        default_relic = null;
      }
    }

  }
  
  public final ItemStack product;
  public final List<ItemStack> materials;
  public final int manufacture_time;
 
  public final Invention invention;
  public final Set<Integer> skills;
  
  
  Blueprint(TSLObject tsl,Item.Data inventory)
  {
    super(tsl);
    List<ItemStack> matlist = new ArrayList<ItemStack>();

    product = new ItemStack(inventory.get(this.id),tsl.getStringAsInt("amount",-1));
    manufacture_time = tsl.getStringAsInt("time",-1);
    List<TSLObject> tsl_materials = tsl.getObjectList("material");

    for(TSLObject mat_tsl:tsl_materials)
    {
      Item mat = inventory.get(mat_tsl.getStringAsInt("id",-1));
      int amount = mat_tsl.getStringAsInt("amount",0);

      matlist.add(new ItemStack(mat, amount));
    }
    materials = Collections.unmodifiableList(matlist);
    
    
    TSLObject tsl_inv = tsl.getObject("invention");
    if(tsl_inv != null)
    {
      invention = new Invention(tsl_inv,inventory);
    } else
    {
      invention = null;
    }
    
    List<Integer> sk = tsl.getStringAsIntegerList("skill");
    if(sk != null)
    {
      skills = Collections.unmodifiableSet(new HashSet<Integer>(sk));
    } else
    {
      skills = Collections.emptySet();
    }
  }
  
  static public class Data extends DirectoryData<Blueprint>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "blueprint");
      this.items = items;
    }

    @Override
    protected Blueprint createObject(TSLObject tsl)
    {
      return new Blueprint(tsl,items);
    }
  }

}
