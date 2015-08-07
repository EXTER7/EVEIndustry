package exter.eveindustry.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.refinable.IRefinable;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Refining task.
 */
public final class RefiningTask extends Task
{
  /**
   * @author exter
   * Slot 8 hardwiring
   */
  public enum Hardwiring
  {
    None(0,1),
    ZainouBeancounterH40(1,1.01),
    ZainouBeancounterH50(2,1.02),
    ZainouBeancounterH60(3,1.04);
    
    public final int value;
    public final double bonus;
    
    Hardwiring(int v,double b)
    {
      value = v;
      bonus = b;
    }
    
    static private Map<Integer,Hardwiring> intmap;
    
    static public Hardwiring fromInt(int i)
    {
      if(intmap == null)
      {
        intmap = new HashMap<Integer,Hardwiring>();
        for(Hardwiring v:values())
        {
          intmap.put(v.value, v);
        }
      }
      return intmap.get(i);
    }
  }

  private int processing_skill;
  private int repreff_skill;
  private int preprocessing_skill;
  private Hardwiring hardwiring;

  private int installation_efficiency;
  private float tax_percent;
  private long amount;
  
  private IRefinable refinable;


  protected RefiningTask()
  {
    super();
  }

  public RefiningTask(IRefinable ref)
  {
    IEVEDataProvider data = getDataProvider();
    refinable = ref;
    installation_efficiency = 50;
    hardwiring = Hardwiring.None;
    preprocessing_skill = data.getDefaultSkillLevel(data.getRefiningSkillID());
    repreff_skill = data.getDefaultSkillLevel(data.getRefineryEfficiencySkillID());
    processing_skill = data.getDefaultSkillLevel(refinable.getSkill());
    tax_percent = 5;
    amount = ref.getRequiredItem().amount;
    updateMaterials();
  }
  
  public int getReprocessingSkillLevel()
  {
    return preprocessing_skill;
  }
  
  public int getReprocessingEfficiencySkillLevel()
  {
    return repreff_skill;
  }
  
  public void setReprocessingSkillLevel(int value)
  {
    preprocessing_skill = Utils.clamp(value,0,5);
    updateMaterials();
  }
  
  public void setReprocessingEfficiencySkillLevel(int value)
  {
    repreff_skill = Utils.clamp(value,0,5);
    updateMaterials();
  }
  
  public Hardwiring getHardwiring()
  {
    return hardwiring;
  }

  public void setHardwiring(Hardwiring value)
  {
    hardwiring = value;
    updateMaterials();
  }
  
  public long getOreAmount()
  {
    return amount;
  }
  
  public void setOreAmount(long value)
  {
    amount = Utils.clamp(value,refinable.getRequiredItem().amount,Long.MAX_VALUE);
    updateMaterials();
  }

  public float getRefineryTax()
  {
    return tax_percent;
  }

  
  public void setRefineryTax(float value)
  {
    tax_percent = Utils.clamp(value,0,100);
    updateMaterials();
  }
  
  public int getProcessingSkillLevel()
  {
    return processing_skill;
  }
  
  public void setProcessingSkill(int value)
  {
    processing_skill = Utils.clamp(value,0,5);
    updateMaterials();
  }
  

  public double getEfficiency()
  {
    double efficiency = ((double)installation_efficiency / 100) * (1 + (double)preprocessing_skill * 0.03) * (1 + (double)repreff_skill * 0.02) * (1 + (double) processing_skill * 0.02) * hardwiring.bonus;
    if(efficiency > 1)
    {
      efficiency = 1;
    }
    return efficiency;
  }

  private ItemStack getEffectiveProduct(ItemStack product, long item_amount)
  {
    double efficiency = getEfficiency();
    long batches = item_amount / refinable.getRequiredItem().amount;
    return product.scaledRounded(Utils.clamp((double)batches * efficiency * (1 - tax_percent / 100),0.0,Double.MAX_VALUE)); 
  }

  
  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);
    tsl.putString("refinable", refinable.getID());
    tsl.putString("refining_skill", preprocessing_skill);
    tsl.putString("refeff_skill", repreff_skill);
    tsl.putString("processing_skill", processing_skill);
    tsl.putString("hardwiring", hardwiring.value);
    tsl.putString("station_efficiency", installation_efficiency);
    tsl.putString("tax_percent", tax_percent);
    tsl.putString("amount", amount);
  }


  @Override
  protected void onLoadDataFromTSL(TSLObject tsl) throws TaskLoadException
  {
    refinable = getDataProvider().getRefinable(tsl.getStringAsInt("refinable", -1));
    if(refinable == null)
    {
      throw new TaskLoadException();
    }
    preprocessing_skill = Utils.clamp(tsl.getStringAsInt("refining_skill", 0),0,5);
    repreff_skill = Utils.clamp(tsl.getStringAsInt("refeff_skill", 0),0,5);
    processing_skill = Utils.clamp(tsl.getStringAsInt("processing_skill", 0),0,5);
    hardwiring = Hardwiring.fromInt(tsl.getStringAsInt("hardwiring", 0));
    installation_efficiency = Utils.clamp(tsl.getStringAsInt("station_efficiency", 50),35,100);
    tax_percent = Utils.clamp(tsl.getStringAsFloat("tax_percent", 5),0,100);
    long batch = refinable.getRequiredItem().amount;
    amount = Utils.clamp(tsl.getStringAsLong("amount", batch),batch,Long.MAX_VALUE);
    if(hardwiring == null)
    {
      hardwiring = Hardwiring.None;
    }
    updateMaterials();
  }
  
  public IRefinable getRefinable()
  {
    return refinable;
  }
  
  public void setInstallationEfficiency(int value)
  {
    installation_efficiency = Utils.clamp(value,35,100);
    updateMaterials();
  }
  
  public int getInstallationEfficiency()
  {
    return installation_efficiency;
  }

  @Override
  public int getDuration()
  {
    return 0;
  }

  @Override
  protected List<ItemStack> getRawProducedMaterials()
  {
    List<ItemStack> list = new ArrayList<ItemStack>();
    for(ItemStack m : refinable.getProducts())
    {
      list.add(getEffectiveProduct(m, amount));
    }
    return list;
  }

  @Override
  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> list = new ArrayList<ItemStack>();
    long b = refinable.getRequiredItem().amount;
    list.add(new ItemStack(refinable.getRequiredItem().item,(amount / b) * b));
    return list;
  }
}
