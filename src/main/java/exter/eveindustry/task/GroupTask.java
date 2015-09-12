package exter.eveindustry.task;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.item.ItemStack;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Groups multiple tasks together
 */
public final class GroupTask extends Task
{
  protected Map<String,Task> tasks;
  
  private int scale;

  static public final int PARAMETER_TASK = 0;
  
  public GroupTask()
  {
    tasks = new HashMap<String,Task>();
    listener = new TaskMaterialChangeListener(this);
    scale = 1;
    updateMaterials();
  }

  private TaskMaterialChangeListener listener;

  public Map<String,Task> getTaskList()
  {
    return Collections.unmodifiableMap(tasks);
  }

  static private class TaskMaterialChangeListener implements ITaskListener
  {
    private WeakReference<GroupTask> group_task;
    
    public TaskMaterialChangeListener(GroupTask group)
    {
      group_task = new WeakReference<GroupTask>(group);
    }

    @Override
    public void onMaterialSetChanged(Task task)
    {
      GroupTask group = group_task.get();
      if(group == null)
      {
        task.unregisterListener(this);
      } else
      {
        group.updateMaterials();
      }
    }

    @Override
    public void onParameterChanged(Task task, int parameter)
    {
      GroupTask group = group_task.get();
      if(group == null)
      {
        task.unregisterListener(this);
      } else
      {
        group.notifyParamaterChange(PARAMETER_TASK);
      }
    }
  }

  protected List<ItemStack> getRawProducedMaterials()
  {
    List<ItemStack> list = new ArrayList<ItemStack>();
    for(Task t:tasks.values())
    {
      for(ItemStack mat:t.getProducedMaterials())
      {
        list.add(mat.scaled(scale));
      }
    }    
    return list;
  }

  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> list = new ArrayList<ItemStack>();
    for(Task t:tasks.values())
    {
      for(ItemStack mat:t.getRequiredMaterials())
      {
        list.add(mat.scaled(scale));
      }
    }
    return list;
  }

  @Override
  protected void onLoadDataFromTSL(TSLObject tsl)
  {
    tasks.clear();
    scale = Utils.clamp(tsl.getStringAsInt("scale", 1),1,Integer.MAX_VALUE);
    List<TSLObject> task_list = tsl.getObjectList("task");
    if(task_list != null)
    {
      for(TSLObject task_tsl:task_list)
      {
        String name = task_tsl.getString("name", null);
        Task t;
        try
        {
          t = Task.loadPromTSL(task_tsl);
          if(t != null)
          {
            t.registerListener(listener);
            tasks.put(name, t);
          }
        } catch(TaskLoadException e)
        {
          e.printStackTrace();
        }
      }
    }
    updateMaterials();
  }
  
  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);
    tsl.putString("scale", scale);
    for(Map.Entry<String, Task> e:tasks.entrySet())
    {
      TSLObject task_tsl = new TSLObject();
      task_tsl.putString("name", e.getKey());
      e.getValue().writeToTSL(task_tsl);
      tsl.putObject("task", task_tsl);
    }
  }

  public void removeTask(String name)
  {
    tasks.get(name).unregisterListener(listener);
    tasks.remove(name);
    updateMaterials();
  }
    
  @Override
  public BigDecimal getExtraExpense()
  {
    BigDecimal sum = BigDecimal.ZERO;
    for(Task t:tasks.values())
    {
      sum = sum.add(t.getExtraExpense());
    }
    return sum.multiply(new BigDecimal(scale));
  }
  
  public void addTask(String name,Task task)
  {
    if(task == null)
    {
      return;
    }
    int i = 2;
    String newname = name.replace('_', '-').trim();
    while(true)
    {
      if(tasks.get(newname) == null)
      {
        tasks.put(newname, task);
        task.registerListener(listener);
        updateMaterials();
        return;
      }
      newname = name + " (" + String.valueOf(i++) + ")";
    }
  }
  
  public int getScale()
  {
    return scale;
  }
  
  public void setScale(int value)
  {
    scale = Utils.clamp(value,1,Integer.MAX_VALUE);
    updateMaterials();
  }
  
  public Task getTask(String name)
  {
    return tasks.get(name);
  }

  @Override
  public int getDuration()
  {
    int max = 0;
    for(Task t:tasks.values())
    {
      int time = t.getDuration();
      if(max < 1 || time > max)
      {
        max = time;
      }
    }
    return max * scale;
  }
}
