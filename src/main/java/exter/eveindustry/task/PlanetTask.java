package exter.eveindustry.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.planet.IPlanet;
import exter.eveindustry.data.planet.IPlanetBuilding;
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
  private IPlanet planet;
  private float tax_percent;
  private List<IPlanetBuilding> buildings;

  static private final int PARAMETER_BUILDINGS = 0;

  PlanetTask(TaskFactory factory,IPlanet p)
  {
    super(factory);
    planet = p;
    buildings = new ArrayList<IPlanetBuilding>();
    tax_percent = 15;
    runtime = 1;
    updateMaterials();
  }

  PlanetTask(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    super(factory,tsl);
    int pid = tsl.getStringAsInt("planet", -1);
    planet = factory.static_data.getPlanet(pid);
    if(planet == null)
    {
      throw new TaskLoadException("Planet with ID " + pid + " not found");
    }
    tax_percent = Utils.clamp(tsl.getStringAsFloat("tax", 15),0,100);
    runtime = Utils.clamp(tsl.getStringAsInt("runtime",1),1,Integer.MAX_VALUE);
    
    buildings = new ArrayList<IPlanetBuilding>();
    for(int id:tsl.getStringAsIntegerList("building"))
    {
      IPlanetBuilding p = factory.static_data.getPlanetBuilding(id);
      if(p != null && (p.getMaterials().size() > 0 || planet.getResources().contains(p.getProduct().item_id)))
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
    for(IPlanetBuilding b : buildings)
    {
      ItemStack p = b.getProduct();
      prods.add(p.scaled(runs));
    }
    return prods;
  }

  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> materials = new ArrayList<ItemStack>();
    int runs = runtime * 24;
    for(IPlanetBuilding b : buildings)
    {
      if(b != null)
      {
        for(ItemStack mat:b.getMaterials())
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

  public IPlanet getPlanet()
  {
    return planet;
  }

  public void setPlanet(IPlanet value)
  {
    planet = value;
    int i;
    boolean removed = false;
    for(i = buildings.size() - 1; i >= 0; i--)
    {
      IPlanetBuilding p = buildings.get(i);
      if(p.getMaterials().size() == 0)
      {
        if(!planet.getResources().contains(p.getProduct().item_id))
        {
          buildings.remove(i);
          removed = true;
        }
      }
      if(p.getLevel() == 4 && !planet.isAdvanced())
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
  
  public List<IPlanetBuilding> getBuildings()
  {
    return Collections.unmodifiableList(buildings);
  }

  public void addBuilding(int building_id)
  {
    IPlanetBuilding building = factory.static_data.getPlanetBuilding(building_id);
    if(building == null)
    {
      return;
    }
    if(building.getMaterials().size() == 0 && !planet.getResources().contains(building.getProduct().item_id))
    {
      return;
    }
    if(building.getLevel() == 4 && !planet.isAdvanced())
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
  
  public void removeBuilding(IPlanetBuilding building)
  {
    buildings.remove(building);
    updateMaterials();
  }


  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);

    tsl.putString("planet", planet.getID());
    tsl.putString("tax", tax_percent);
    tsl.putString("runtime", runtime);
    for(IPlanetBuilding p:buildings)
    {
      if(p != null)
      {
        tsl.putString("building", p.getID());
      }
    }
  }
    
  @Override
  public BigDecimal getExtraExpense()
  {
    BigDecimal expense = BigDecimal.ZERO;
    
    for(ItemStack m:getProducedMaterials())
    {
      IPlanetBuilding p = factory.static_data.getPlanetBuilding(m.item_id);
      expense = expense.add(new BigDecimal((double)(p.getCustomsOfficeTax() * m.amount) * (tax_percent / 100)));
    }
    for(ItemStack m:getRequiredMaterials())
    {
      IPlanetBuilding p = factory.static_data.getPlanetBuilding(m.item_id);
      expense = expense.add(new BigDecimal((double)(p.getCustomsOfficeTax() * m.amount) * 0.5 * (tax_percent / 100)));
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
