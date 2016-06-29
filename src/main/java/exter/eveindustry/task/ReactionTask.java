package exter.eveindustry.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.reaction.IReaction;
import exter.eveindustry.data.reaction.IStarbaseTower;
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
  private IStarbaseTower tower;
  private List<IReaction> reactions;
  private boolean sovereignty;

  ReactionTask(TaskFactory factory,IStarbaseTower t)
  {
    super(factory);
    tower = t;
    reactions = new ArrayList<IReaction>();
    sovereignty = false;
    runtime = 1;
    updateMaterials();
  }

  ReactionTask(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    super(factory,tsl);
    int tid = tsl.getStringAsInt("tower", -1);
    tower = factory.static_data.getStarbaseTower(tid);
    if(tower == null)
    {
      throw new TaskLoadException("Starbase Tower with ID " + tid + " not found");
    }
    runtime = Utils.clamp(tsl.getStringAsInt("runtime",1),1,Integer.MAX_VALUE);
    sovereignty = tsl.getStringAsInt("sovereignty",0) != 0;
    
    reactions = new ArrayList<IReaction>();
    List<Integer> rval = tsl.getStringAsIntegerList("reaction");
    for(int i:rval)
    {
      IReaction r = factory.static_data.getReaction(i);
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
    for(IReaction r : reactions)
    {
      if(r != null)
      {
        for(ItemStack mat:r.getOutputs())
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
    for(IReaction r:reactions)
    {
      if(r != null)
      {
        for(ItemStack mat:r.getInputs())
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
    ItemStack fuel = tower.getRequiredFuel().scaled(runtime * 24);
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

  public IStarbaseTower getStarbaseTower()
  {
    return tower;
  }

  public void setStarbaseTower(IStarbaseTower value)
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
  
  public List<IReaction> getReactions()
  {
    return Collections.unmodifiableList(reactions);
  }

  public void removeReaction(int index)
  {
    reactions.remove(index);
    updateMaterials();
  }
  
  public void removeReaction(IReaction reaction)
  {
    reactions.remove(reaction);
    updateMaterials();
  }

  public void addReaction(int reaction_id)
  {
    IReaction reaction = factory.static_data.getReaction(reaction_id);
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

    tsl.putString("tower", tower.getID());
    tsl.putString("runtime", runtime);
    tsl.putString("sovereignty", sovereignty?1:0);
    for(IReaction r:reactions)
    {
      if(r != null)
      {
        tsl.putString("reaction", r.getID());
      }
    }
  }

  @Override
  public int getDuration()
  {
    return runtime * 24 * 60 * 60;
  }
}
