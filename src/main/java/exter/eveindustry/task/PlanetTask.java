package exter.eveindustry.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.planet.Planet;
import exter.eveindustry.data.planet.PlanetBuilding;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Planetary interaction task
 */
public final class PlanetTask extends Task
{
  private int runtime;
  private Planet planet;
  private float tax_percent;
  private List<PlanetBuilding> buildings;

  static private final int PARAMETER_BUILDINGS = 0;

  PlanetTask(TaskFactory factory,Planet p)
  {
    super(factory);
    planet = p;
    buildings = new ArrayList<PlanetBuilding>();
    tax_percent = 15;
    runtime = 1;
    updateMaterials();
  }

  PlanetTask(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    super(factory,tsl);
    int pid = tsl.getStringAsInt("planet", -1);
    planet = factory.planets.get(pid);
    if(planet == null)
    {
      throw new TaskLoadException("Planet with ID " + pid + " not found");
    }
    tax_percent = Utils.clamp(tsl.getStringAsFloat("tax", 15),0,100);
    runtime = Utils.clamp(tsl.getStringAsInt("runtime",1),1,Integer.MAX_VALUE);
    
    buildings = new ArrayList<PlanetBuilding>();
    for(int id:tsl.getStringAsIntegerList("building"))
    {
      PlanetBuilding p = factory.planetbuildings.get(id);
      if(p != null && (p.materials.size() > 0 || planet.resources.contains(p.product.item)))
      {
        buildings.add(p);
      }
    }
    updateMaterials();
  }

  protected List<ItemStack> getRawProducedMaterials()
  {
    int runs = runtime * 24;
    List<ItemStack> prods = new ArrayList<ItemStack>();
    for(PlanetBuilding b : buildings)
    {
      ItemStack p = b.product;
      prods.add(p.scaled(runs));
    }
    return prods;
  }

  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> materials = new ArrayList<ItemStack>();
    int runs = runtime * 24;
    for(PlanetBuilding b : buildings)
    {
      if(b != null)
      {
        for(ItemStack mat:b.materials)
        {
          materials.add(mat.scaled(runs));
        }
      }
    }
    return materials;
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

  public Planet getPlanet()
  {
    return planet;
  }

  public void setPlanet(int planet_id)
  {
    Planet newplanet = factory.planets.get(planet_id);
    if(newplanet == null)
    {
      return;
    }
    planet = newplanet;
    int i;
    boolean removed = false;
    for(i = buildings.size() - 1; i >= 0; i--)
    {
      PlanetBuilding p = buildings.get(i);
      if(p.materials.size() == 0)
      {
        if(!planet.resources.contains(p.product.item))
        {
          buildings.remove(i);
          removed = true;
        }
      }
      if(p.level == 4 && !planet.advanced)
      {
        buildings.remove(i);
        removed = true;
      }
    }
    if(removed)
    {
      notifyParameterChange(PARAMETER_BUILDINGS);
    }
    updateMaterials();
  }
  
  public List<PlanetBuilding> getBuildings()
  {
    return Collections.unmodifiableList(buildings);
  }

  public void addBuilding(int building_id)
  {
    PlanetBuilding building = factory.planetbuildings.get(building_id);
    if(building == null)
    {
      return;
    }
    if(building.materials.size() == 0 && !planet.resources.contains(building.product.item))
    {
      return;
    }
    if(building.level == 4 && !planet.advanced)
    {
      return;
    }
    buildings.add(building);
    updateMaterials();
  }

  public void removeBuilding(int index)
  {
    buildings.remove(index);
    updateMaterials();
  }
  
  public void removeBuilding(PlanetBuilding building)
  {
    buildings.remove(building);
    updateMaterials();
  }


  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);

    tsl.putString("planet", planet.id);
    tsl.putString("tax", tax_percent);
    tsl.putString("runtime", runtime);
    for(PlanetBuilding p:buildings)
    {
      if(p != null)
      {
        tsl.putString("building", p.product.item.id);
      }
    }
  }
    
  @Override
  public BigDecimal getExtraExpense()
  {
    BigDecimal expense = BigDecimal.ZERO;
    
    for(ItemStack m:getProducedMaterials())
    {
      PlanetBuilding p = factory.planetbuildings.get(m.item.id);
      expense = expense.add(new BigDecimal((double)(p.tax * m.amount) * (tax_percent / 100)));
    }
    for(ItemStack m:getRequiredMaterials())
    {
      PlanetBuilding p = factory.planetbuildings.get(m.item.id);
      expense = expense.add(new BigDecimal((double)(p.tax * m.amount) * 0.5 * (tax_percent / 100)));
    }
    return expense;
  }
  
  public float getCustomsOfficeTax()
  {
    return tax_percent;
  }
  
  public void setCustomsOfficeTax(float percent)
  {
    tax_percent = Utils.clamp(percent,0,100);
  }

  @Override
  public int getDuration()
  {
    return runtime * 24 * 60 * 60;
  }
}
