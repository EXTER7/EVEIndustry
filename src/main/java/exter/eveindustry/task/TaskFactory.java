package exter.eveindustry.task;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.data.IDynamicDataProvider;
import exter.eveindustry.data.IStaticDataProvider;
import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.planet.IPlanet;
import exter.eveindustry.data.reaction.IStarbaseTower;
import exter.eveindustry.data.refinable.IRefinable;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Used to create Task instances
 */
public final class TaskFactory
{
  public final IStaticDataProvider static_data;
  public final IDynamicDataProvider dynamic_data;
  
  // Used for loading/saving from/to TSL Objects.
  static private Map<String, Class<? extends Task>> task_types;
  static private Map<Class<? extends Task>, String> task_names;
  
  
  public TaskFactory(IStaticDataProvider static_data,IDynamicDataProvider dynamic_data)
  {
    this.static_data = static_data;
    this.dynamic_data = dynamic_data;
  }
  
  /**
   * Load a task from a TSL Object.
   * @param tsl The TSL Object to load the task from.
   * @throws TaskLoadException If there was a fatal error loading the task.
   */
  public Task fromTSL(TSLObject tsl) throws TaskLoadException
  {
    Task task = null;
    try
    {
      String type = tsl.getString("type", null);
      if(type == null)
      {
        throw new TaskLoadException("Missing task type");
      }
      Class<? extends Task> clazz = task_types.get(type);
      if(clazz == null)
      {
        throw new TaskLoadException("Invalid task type: " + type);
      }
      task = clazz.getDeclaredConstructor(TaskFactory.class,TSLObject.class).newInstance(this,static_data);
    } catch(InstantiationException e)
    {
      throw new RuntimeException(e);
    } catch(IllegalAccessException e)
    {
      throw new RuntimeException(e);
    } catch(InvocationTargetException e)
    {
      Throwable target = e.getTargetException();
      if(target instanceof TaskLoadException)
      {
        throw (TaskLoadException)target;
      } else
      {
        throw new RuntimeException(target);
      }
    } catch(NoSuchMethodException e)
    {
      throw new RuntimeException(e);
    }
    return task;
  }
  
  public GroupTask newGroup()
  {
    return new GroupTask(this);
  }
  
  /**
   * Create a new ManufacturingTask with a blueprint
   * @param blueprint_id ID of the blueprint for the task.
   * @return The new ManufacturingTask, or null if the blueprint ID is invalid.
   */
  public ManufacturingTask newManufacturing(int blueprint_id)
  {
    IBlueprint bp = static_data.getBlueprint(blueprint_id);
    if(bp == null)
    {
      return null;
    }
    return new ManufacturingTask(this,bp);
  }

  /**
   * Create a new RefiningTask with a refinable.
   * @param refinable_id ID of the refinable for the task.
   * @return The new RefiningTask, or null if the refinable ID is invalid.
   */
  public RefiningTask newRefining(int refinable_id)
  {
    IRefinable ref = static_data.getRefinable(refinable_id);
    if(ref == null)
    {
      return null;
    }
    return new RefiningTask(this,ref);
  }

  /**
   * Create a new ReactionTask with a starbase tower.
   * @param tower_id ID of the starbase tower for the task.
   * @return The new ReactionTask, or null if the starbase tower ID is invalid.
   */
  public ReactionTask newReaction(int tower_id)
  {
    IStarbaseTower tower = static_data.getStarbaseTower(tower_id);
    if(tower == null)
    {
      return null;
    }
    return new ReactionTask(this,tower);
  }

  /**
   * Create a new PlanetTask with a planet.
   * @param planet_id ID of the planet for the task.
   * @return The new PlanetTask, or null if the planet ID is invalid.
   */
  public PlanetTask newPlanet(int planet_id)
  {
    IPlanet planet = static_data.getPlanet(planet_id);
    if(planet == null)
    {
      return null;
    }
    return new PlanetTask(this,planet);
  }


  static private void registerTaskType(String name, Class<? extends Task> type)
  {
    task_types.put(name, type);
    task_names.put(type, name);
  }

  static String getTaskName(Class<? extends Task> clazz)
  {
    return task_names.get(clazz);
  }
  
  static
  {
    task_types = new HashMap<String, Class<? extends Task>>();
    task_names = new HashMap<Class<? extends Task>, String>();
    registerTaskType("manufacturing", ManufacturingTask.class);
    registerTaskType("refining", RefiningTask.class);
    registerTaskType("reaction", ReactionTask.class);
    registerTaskType("planet", PlanetTask.class);
    registerTaskType("group", GroupTask.class);
  }
}
