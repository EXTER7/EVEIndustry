package exter.eveindustry.test.data.blueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.test.data.inventory.InventoryDA;
import exter.eveindustry.test.data.inventory.Item;
import exter.tsl.TSLObject;

public class Blueprint implements IBlueprint
{
  static public class Invention implements IBlueprint.IInvention
  {
    public class Relic implements IBlueprint.IInvention.IRelic
    {
      private final Item RelicItem;
      private final int Runs;
      private final double Chance;

      public Relic(TSLObject tsl)
      {
        RelicItem = InventoryDA.items.get(tsl.getStringAsInt("id",-1));
        Runs = tsl.getStringAsInt("runs",0);
        Chance = tsl.getStringAsDouble("chance",0);
      }
      
      @Override
      public IItem getItem()
      {
        return RelicItem;
      }

      @Override
      public int getRuns()
      {
        return Runs;
      }

      @Override
      public double getChance()
      {
        return Chance;
      }

      @Override
      public int getID()
      {
        return RelicItem.ID;
      }
    }

    private class RelicComparator implements Comparator<Integer>
    {
      @Override
      public int compare(Integer lhs, Integer rhs)
      {
        Relic l = Relics.get(lhs);
        Relic r = Relics.get(rhs);
        return l.Runs - r.Runs;
      }
    }
    
    private final int Time;
    private final int Runs;
    private final double Chance;
    private final List<ItemStack> Materials;
    private final int EncryptionSkill;
    private final Set<Integer> DatacoreSkills;
    private final Map<Integer,Relic> Relics;
    private final Set<Integer> RelicList;
    
    public Invention(TSLObject tsl)
    {
      ArrayList<ItemStack> matlist = new ArrayList<ItemStack>();
      Time = tsl.getStringAsInt("time",-1);
      Runs = tsl.getStringAsInt("runs",0);
      Chance = tsl.getStringAsDouble("chance",0);
      EncryptionSkill = tsl.getStringAsInt("eskill",-1);
      Set<Integer> dskills = new HashSet<Integer>(tsl.getStringAsIntegerList("dskill"));
      DatacoreSkills = Collections.unmodifiableSet(dskills);
      List<TSLObject> tsl_materials = tsl.getObjectList("material");
      for(TSLObject mat_tsl:tsl_materials)
      {
        Item mat = InventoryDA.items.get(mat_tsl.getStringAsInt("id",-1));
        int amount = mat_tsl.getStringAsInt("amount",0);
        matlist.add(new ItemStack(mat, amount));
      }
      Materials = Collections.unmodifiableList(matlist);
      List<TSLObject> tsl_relics = tsl.getObjectList("relic");
      if(tsl_relics != null && tsl_relics.size() > 0)
      {
        Relics = new HashMap<Integer,Relic>();
        RelicList = new TreeSet<Integer>(new RelicComparator());
        for(TSLObject tr: tsl_relics)
        {
          Relic r = new Relic(tr);
          Relics.put(r.RelicItem.getID(), r);
          RelicList.add(r.RelicItem.getID());
        }
      } else
      {
        Relics = null;
        RelicList = null;
      }
    }

    @Override
    public int getTime()
    {
      return Time;
    }

    @Override
    public int getRuns()
    {
      return Runs;
    }

    @Override
    public double getChance()
    {
      return Chance;
    }

    @Override
    public List<ItemStack> getMaterials()
    {
      return Materials;
    }

    @Override
    public int getEncryptionSkillID()
    {
      return EncryptionSkill;
    }

    @Override
    public Set<Integer> getDatacoreSkillIDs()
    {
      return DatacoreSkills;
    }

    @Override
    public IRelic getRelic(int id)
    {
      return Relics == null?null:Relics.get(id);
    }

    @Override
    public Set<Integer> getRelicIDs()
    {
      return RelicList == null ? null : (Collections.unmodifiableSet(RelicList));
    }

    @Override
    public IRelic getDefaultRelic()
    {
      return RelicList == null? null : Relics.values().iterator().next();
    }

    @Override
    public boolean usesRelics()
    {
      return Relics != null;
    }
  }
  
  public final ItemStack Product;
  public final List<ItemStack> Materials;
  public final int ManufactureTime;
 
  public final Invention Invention;
  public final Set<Integer> Skills;
  
  
  public Blueprint(TSLObject tsl)
  {
    List<ItemStack> matlist = new ArrayList<ItemStack>();

    Product = new ItemStack(InventoryDA.items.get(tsl.getStringAsInt("id",-1)),tsl.getStringAsInt("amount",-1));
    ManufactureTime = tsl.getStringAsInt("time",-1);

    List<TSLObject> tsl_materials = tsl.getObjectList("material");

    for(TSLObject mat_tsl:tsl_materials)
    {
      Item mat = InventoryDA.items.get(mat_tsl.getStringAsInt("id",-1));
      int amount = mat_tsl.getStringAsInt("amount",0);

      matlist.add(new ItemStack(mat, amount));
    }
    Materials = Collections.unmodifiableList(matlist);
    
    
    TSLObject tsl_inv = tsl.getObject("invention");
    if(tsl_inv != null)
    {
      Invention = new Invention(tsl_inv);
    } else
    {
      Invention = null;
    }
    
    List<Integer> sk = tsl.getStringAsIntegerList("skill");
    if(sk != null)
    {
      Skills = Collections.unmodifiableSet(new HashSet<Integer>(sk));
    } else
    {
      Skills = Collections.emptySet();
    }
  }


  @Override
  public ItemStack getProduct()
  {
    return Product;
  }

  @Override
  public List<ItemStack> getMaterials()
  {
    return Materials;
  }


  @Override
  public int getManufacturingTime()
  {
    return ManufactureTime;
  }


  @Override
  public IInvention getInvention()
  {
    return Invention;
  }


  @Override
  public Set<Integer> getSkills()
  {
    return Skills;
  }

  @Override
  public int getID()
  {
    return Product.item.getID();
  }
}
