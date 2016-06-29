package exter.eveindustry.task;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.data.IDynamicDataProvider;
import exter.eveindustry.data.blueprint.Blueprint;
import exter.eveindustry.data.blueprint.Installation;
import exter.eveindustry.data.blueprint.InstallationGroup;
import exter.eveindustry.data.blueprint.InventionInstallation;
import exter.eveindustry.data.decryptor.Decryptor;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.data.item.ItemCategory;
import exter.eveindustry.data.item.ItemGroup;
import exter.eveindustry.data.planet.Planet;
import exter.eveindustry.data.planet.PlanetBuilding;
import exter.eveindustry.data.reaction.Reaction;
import exter.eveindustry.data.reaction.StarbaseTower;
import exter.eveindustry.data.refine.Refinable;
import exter.eveindustry.data.starmap.Region;
import exter.eveindustry.data.starmap.SolarSystem;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Used to create Task instances
 */
public final class TaskFactory
{
  public final Item.Data items;
  public final ItemGroup.Data item_groups;
  public final ItemCategory.Data item_categories;
  public final Blueprint.Data blueprints;
  public final Installation.Data installations;
  public final InstallationGroup.Data installation_groups;
  public final InventionInstallation.Data invention_installations;
  public final Decryptor.Data decryptors;
  public final PlanetBuilding.Data planetbuildings;
  public final Planet.Data planets;
  public final Reaction.Data reactions;
  public final Refinable.Data refinables;
  public final StarbaseTower.Data towers;
  public final SolarSystem.Data solarsystems;
  public final Region.Data regions;
  public final IDynamicDataProvider dynamic_data;
  
  // Used for loading/saving from/to TSL Objects.
  static private Map<String, Class<? extends Task>> task_types;
  static private Map<Class<? extends Task>, String> task_names;
  
  
  public TaskFactory(IFileSystemHandler fs,IDynamicDataProvider dynamic_data)
  {
    items = new Item.Data(fs);
    item_groups = new ItemGroup.Data(fs);
    item_categories = new ItemCategory.Data(fs);
    installations = new Installation.Data(fs);
    installation_groups = new InstallationGroup.Data(fs);
    invention_installations = new InventionInstallation.Data(fs);
    blueprints = new Blueprint.Data(fs,items,installation_groups);
    decryptors = new Decryptor.Data(fs,items);
    planetbuildings = new PlanetBuilding.Data(fs,items);
    planets = new Planet.Data(fs,items);
    reactions = new Reaction.Data(fs,items);
    refinables = new Refinable.Data(fs,items);
    towers = new StarbaseTower.Data(fs,items);
    solarsystems = new SolarSystem.Data(fs);
    regions = new Region.Data(fs);
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
      task = clazz.getDeclaredConstructor(TaskFactory.class,TSLObject.class).newInstance(this,tsl);
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
    Blueprint bp = blueprints.get(blueprint_id);
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
    Refinable ref = refinables.get(refinable_id);
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
    StarbaseTower tower = towers.get(tower_id);
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
    Planet planet = planets.get(planet_id);
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
