package exter.eveindustry.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.reaction.Reaction;
import exter.eveindustry.data.reaction.StarbaseTower;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Reaction starbase task.
 */
public final class ReactionTask extends Task
{
  private int runtime;
  private StarbaseTower tower;
  private List<Reaction> reactions;
  private boolean sovereignty;

  ReactionTask(TaskFactory factory,StarbaseTower t)
  {
    super(factory);
    tower = t;
    reactions = new ArrayList<Reaction>();
    sovereignty = false;
    runtime = 1;
    updateMaterials();
  }

  ReactionTask(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    super(factory,tsl);
    int tid = tsl.getStringAsInt("tower", -1);
    tower = factory.towers.get(tid);
    if(tower == null)
    {
      throw new TaskLoadException("Starbase Tower with ID " + tid + " not found");
    }
    runtime = Utils.clamp(tsl.getStringAsInt("runtime",1),1,Integer.MAX_VALUE);
    sovereignty = tsl.getStringAsInt("sovereignty",0) != 0;
    
    reactions = new ArrayList<Reaction>();
    List<Integer> rval = tsl.getStringAsIntegerList("reaction");
    for(int i:rval)
    {
      Reaction r = factory.reactions.get(i);
      if(r != null)
      {
        reactions.add(r);
      }
    }
    updateMaterials();
  }

  protected List<ItemStack> getRawProducedMaterials()
  {
    int runs = runtime * 24;
    List<ItemStack> list = new ArrayList<ItemStack>();
    for(Reaction r : reactions)
    {
      if(r != null)
      {
        for(ItemStack mat:r.outputs)
        {
          list.add(mat.scaled(runs));
        }
      }
    }
    return list;
  }

  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> list = new ArrayList<ItemStack>();
    int runs = runtime * 24;
    for(Reaction r:reactions)
    {
      if(r != null)
      {
        for(ItemStack mat:r.inputs)
        {
          list.add(mat.scaled(runs));
        }
      }
    }
    list.add(getFuelUsed());
    return list;
  }

  private ItemStack getFuelUsed()
  {
    ItemStack fuel = tower.fuel.scaled(runtime * 24);
    if(sovereignty)
    {
      return fuel.scaledFloor(0.75);
    }
    return fuel;
  }

  /**
   * Get the run-time of the task in days.
   */
  public int getRunTime()
  {
    return runtime;
  }
  
  /**
   * Set the run-time of the task in days.
   */
  public void setRunTime(int days)
  {
    runtime = Utils.clamp(days,1,Integer.MAX_VALUE);
    updateMaterials();
  }

  public StarbaseTower getStarbaseTower()
  {
    return tower;
  }

  public void setStarbaseTower(StarbaseTower value)
  {
    tower = value;
    updateMaterials();
  }
  
  /**
   * @return true, if the tower receives sovereignty bonus
   */
  public boolean hasSovereignty()
  {
    return sovereignty;
  }

  /**
   * Set to true if the tower receives sovereignty bonus.
   */
  public void setSovereignty(boolean value)
  {
    sovereignty = value;
    updateMaterials();
  }
  
  public List<Reaction> getReactions()
  {
    return Collections.unmodifiableList(reactions);
  }

  public void removeReaction(int index)
  {
    reactions.remove(index);
    updateMaterials();
  }
  
  public void removeReaction(Reaction reaction)
  {
    reactions.remove(reaction);
    updateMaterials();
  }

  public void addReaction(int reaction_id)
  {
    Reaction reaction = factory.reactions.get(reaction_id);
    if(reaction == null)
    {
      return;
    }
    reactions.add(reaction);
    updateMaterials();
  }

  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);

    tsl.putString("tower", tower.item.id);
    tsl.putString("runtime", runtime);
    tsl.putString("sovereignty", sovereignty?1:0);
    for(Reaction r:reactions)
    {
      if(r != null)
      {
        tsl.putString("reaction", r.id);
      }
    }
  }

  @Override
  public int getDuration()
  {
    return runtime * 24 * 60 * 60;
  }
}
