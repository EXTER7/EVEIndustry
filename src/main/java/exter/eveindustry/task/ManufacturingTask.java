package exter.eveindustry.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exter.eveindustry.data.blueprint.Blueprint;
import exter.eveindustry.data.blueprint.InstallationGroup;
import exter.eveindustry.data.blueprint.InventionInstallation;
import exter.eveindustry.data.decryptor.Decryptor;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Manufacturing task.
 */
public final class ManufacturingTask extends Task
{
  // Slot 8 hardwiring
  public enum Hardwiring
  {
    None(0,0.0),
    ZainouBeancounterF40(1,0.01),
    ZainouBeancounterF50(2,0.02),
    ZainouBeancounterF60(3,0.04);
    
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
  
  /**
   * @author exter
   * Invention attributes for T2/T3 blueprints.
   */
  public class Invention
  {
    private Decryptor decryptor;
    private int attempts;
    private InventionInstallation installation;
    private Blueprint.Invention.Relic relic;
    private int invruns;
    
    Invention()
    {
      decryptor = null;
      attempts = 5;
      invruns = 1;
      
      installation = getDefaultInstallation();
      relic = blueprint.invention.default_relic;
    }
       
    public Invention(TSLObject tsl)
    {
      setAttempts(tsl.getStringAsInt("attempts",5));
      setInventionRuns(tsl.getStringAsInt("runs",1));
      setDecryptor(tsl.getStringAsInt("decryptor", -1));
      if(blueprint.invention.relics != null)
      {
        setInstallation(tsl.getStringAsInt("installation", -1));
        relic = blueprint.invention.relics.get(tsl.getStringAsInt("relic",-1));
        if(relic == null)
        {
          relic = blueprint.invention.default_relic;
        }
      } else
      {
        setInstallation(tsl.getStringAsInt("installation", -1));
        relic = null;
      }
    }
    
    public void writeToTSL(TSLObject tsl)
    {
      tsl.putString("attempts", attempts);
      tsl.putString("decryptor", decryptor == null?-1:decryptor.item.id);
      tsl.putString("installation", installation.id);
      tsl.putString("runs", invruns);
      if(relic != null)
      {
        tsl.putString("relic", relic.item.id);
      }
    }
    
    public int getAttempts()
    {
      return attempts;
    }
    
    public void setAttempts(int att)
    {
      attempts = Utils.clamp(att, 1, Integer.MAX_VALUE); 
      updateMaterials();
    }

    public int getInventionRuns()
    {
      return invruns;
    }
    
    public void setInventionRuns(int r)
    {
      invruns = Utils.clamp(r, 1, Integer.MAX_VALUE); 
      updateMaterials();
    }
    

    public Decryptor getDecryptor()
    {
      return decryptor;
    }
    
    public void setDecryptor(int decryptor_id)
    {
      decryptor = factory.decryptors.get(decryptor_id);
      updateMaterials();
    }
    
    public double getChance()
    {
      int science = 0;
      for(int s:blueprint.invention.datacore_skill_ids)
      {
        int l = Utils.mapGet(skills, s, 0);
        science += l; 
      }
      double basechance;
      if(relic != null)
      {
        basechance = relic.chance;
      } else
      {
        basechance = blueprint.invention.chance;
      }
      
      double sk = 1 + (double)science / 30 + (double)Utils.mapGet(skills, blueprint.invention.encryption_skill_id, 0) / 40;
      double decr = decryptor == null?1.0:decryptor.chance;
      double chance = basechance * sk * decr;
      if(chance > 1)
      {
        chance = 1;
      }
      return chance;
    }

    public int getME()
    {
      return decryptor == null?2:(decryptor.me + 2);
    }
    
    public int getTE()
    {
      return decryptor == null?4:(decryptor.te + 4);
    }
    
    public int getBlueprintRuns()
    {
      int runs;
      if(relic != null)
      {
        runs = relic.runs;
      } else
      {
        runs = blueprint.invention.runs;
      }
      return decryptor == null?runs:(decryptor.runs + runs);
    }
    
    public int getBlueprintCopies()
    {
      return (int)(attempts * invruns * getChance());
    }


    public InventionInstallation getInstallation()
    {
      return installation;
    }

    public void setInstallation(int inv_instalation_id)
    {
      installation = factory.invention_installations.get(inv_instalation_id);
      boolean rel = blueprint.invention.relics != null;
      if(installation == null || installation.relics != rel)
      {
        installation = getDefaultInstallation();
      }
    }
    
    private InventionInstallation getDefaultInstallation()
    {
      return factory.invention_installations.get(
          blueprint.invention.relics != null?factory.indsutry_data.relic_inv_inst_default:factory.indsutry_data.inv_inst_default);
    }
    
    public int getInventionTime()
    {
      int advindustry_skill = skills.get(factory.indsutry_data.skill_industry);
      return (int) Math.ceil(invruns * blueprint.invention.time * installation.time * (1.0 - 0.03 * advindustry_skill));
    }
    
    public Blueprint.Invention.Relic GetRelic()
    {
      return relic;
    }
    
    public void setRelic(int id)
    {
      Blueprint.Invention inv = blueprint.invention;
      Set<Integer> relics = inv.relics.keySet();
      if(relics != null && relics.contains(id))
      {
        relic = inv.relics.get(id);
        updateMaterials();
      }
    }
  }
  
  static public final int PARAMETER_INVENTION_RUNS = 0;
  static public final int PARAMETER_INVENTION_ATTEMPTS = 1;
  static public final int PARAMETER_SKILLS = 2;
  
  private Hardwiring hardwiring;

  private int me_level;
  private int te_level;
  private int runs;
  private int copies;
  
  private int system;
  private double tax;
  
  private Invention invention;
  
  private InstallationGroup installation;
  private Blueprint blueprint;
  
  private Map<Integer,Integer> skills;
  
  ManufacturingTask(TaskFactory factory,Blueprint blueprint)
  {
    super(factory);
    this.blueprint = blueprint;
    skills = new HashMap<Integer,Integer>();
    hardwiring = Hardwiring.None;
    if(blueprint.invention != null)
    {
      invention = new Invention();
    } else
    {
      invention = null;
    }
    updateSkills();
    setME(factory.dynamic_data.getDefaultBlueprintME(blueprint));
    setTE(factory.dynamic_data.getDefaultBlueprintTE(blueprint));
    system = factory.dynamic_data.getDefaultSolarSystem();
    runs = 1;
    copies = 1;
    tax = 10;
    setDefaultInstallation();
    updateMaterials();
  }
  

  ManufacturingTask(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    super(factory,tsl);
    int bid = tsl.getStringAsInt("blueprint", -1);
    blueprint = factory.blueprints.get(bid);
    if(blueprint == null)
    {
      throw new TaskLoadException("Blueprint with ID " + bid + " not found");
    }
    skills = new HashMap<Integer,Integer>();
    TSLObject tsl_inv = tsl.getObject("invention");
    if(tsl_inv != null)
    {
      invention = new Invention(tsl_inv);
    } else
    {
      invention = null;
    }
    updateSkills();

    hardwiring = Hardwiring.fromInt(tsl.getStringAsInt("hardwiring", 0));
    if(invention == null)
    {
      runs = Utils.clamp(tsl.getStringAsInt("runs", 1), 1, Integer.MAX_VALUE);
      copies = Utils.clamp(tsl.getStringAsInt("copies", 1), 1, Integer.MAX_VALUE);
      me_level = Utils.clamp(tsl.getStringAsInt("me", 0),0,10);
      te_level = Utils.clamp(tsl.getStringAsInt("te", 0),0,20);
    }
    installation = factory.installation_groups.get(tsl.getStringAsInt("installation", -1));
    if(installation == null || installation.group_id != blueprint.product.item.group_id)
    {
      setDefaultInstallation();
    }
    system = tsl.getStringAsInt("system", factory.dynamic_data.getDefaultSolarSystem());
    tax = Utils.clamp(tsl.getStringAsFloat("tax", 10),0,100);
    if(hardwiring == null)
    {
      hardwiring = Hardwiring.None;
    }

    List<TSLObject> tsl_skills = tsl.getObjectList("skill");
    for(TSLObject s:tsl_skills)
    {
      int id = s.getStringAsInt("id", -1);
      if(factory.items.get(id) != null)
      {
        if(skills.containsKey(id))
        {
          skills.put(id, Utils.clamp(s.getStringAsInt("level", 0), 0, 5));
        }
      }
    }
    
    updateMaterials();
  }
  
  private ItemStack getEffectiveMaterial(ItemStack material)
  {
    int r = getRuns();
    if(material.amount == 1)
    {
      return material.scaled(getRuns());
    } else
    {
      return material.scaledCeil(installation.material * (1.0 - getME() * 0.01) * r);
    }
  }
  
  public int getProductionTime()
  {
    double eff_time;
    int industry_skill = skills.get(factory.indsutry_data.skill_industry);
    int advindustry_skill = skills.get(factory.indsutry_data.skill_advancedindustry);

    eff_time = (double) blueprint.manufacture_time * installation.time * (1 - (double)getTE() / 100.0) * getRuns();
    eff_time *= (1.0 - 0.04 * industry_skill) * (1.0 - hardwiring.bonus) * (1.0 - advindustry_skill * 0.03);

    return (int)Math.ceil(eff_time);
  }

  @Override
  public BigDecimal getExtraExpense()
  {
    BigDecimal base_cost = BigDecimal.ZERO;
    ISolarSystemIndustryCost syscost = factory.dynamic_data.getSolarSystemIndustryCost(system);
    for(ItemStack m:blueprint.materials)
    {
      base_cost = base_cost.add(
          factory.dynamic_data.getItemBaseCost(m.item).multiply(new BigDecimal(m.amount)));
    }
    BigDecimal jobtax = base_cost.multiply(
        new BigDecimal(
            (double)(getRuns() * getCopies()) * syscost.getManufacturingCost() * (1.0 + tax / 100)));
    if(invention != null)
    {
      jobtax = jobtax.add(
          base_cost.multiply(
              new BigDecimal(invention.installation.cost * invention.attempts * syscost.getInventionCost() * 0.02 * (1.0 + tax / 100))));
    }
    return jobtax;
  }

  protected List<ItemStack> getRawRequiredMaterials()
  {
    List<ItemStack> required = new ArrayList<ItemStack>();

    int r = getRuns();
    int c = getCopies();

    if(r * c > 0)
    {
      for(ItemStack m : blueprint.materials)
      {
        required.add(getEffectiveMaterial(m).scaled(c));
      }
    }
    if(invention != null)
    {
      if(invention.decryptor != null)
      {
        required.add(new ItemStack(invention.decryptor.item, (long)invention.attempts * invention.invruns));
      }
      for(ItemStack m : blueprint.invention.materials)
      {
        required.add(m.scaled(invention.attempts * invention.invruns));
      }
      if(invention.relic != null)
      {
        required.add(new ItemStack(invention.relic.item, (long)invention.attempts * invention.invruns));
      }
    }
    return required;
  }

  protected List<ItemStack> getRawProducedMaterials()
  {
    List<ItemStack> produced = new ArrayList<ItemStack>();

    int r = getRuns();
    int c = getCopies();

    if(r * c > 0)
    {
      ItemStack prod = blueprint.product;
      produced.add(prod.scaled(r * c));
    }
    return produced;
  }


  private void setDefaultInstallation()
  {
    installation = blueprint.installation;
  }

  
  public Hardwiring getHardwiring()
  {
    return hardwiring;
  }

  public void setHardwiring(Hardwiring value)
  {
    hardwiring = value;
  }

  public int getME()
  {
    return invention != null?invention.getME():me_level;
  }

  public void setME(int level)
  {
    if(invention == null)
    {
      me_level = Utils.clamp(level, 0, 10);
      updateMaterials();
    }
  }
  
  public int getTE()
  {
    return invention != null?invention.getTE():te_level;
  }

  public void setTE(int level)
  {
    if(invention == null)
    {
      te_level = Utils.clamp(level, 0, 20);
    }
  }
  
  public int getRuns()
  {
    return invention != null?invention.getBlueprintRuns():runs;
  }
  
  public void setRuns(int value)
  {
    if(invention == null)
    {
      runs = Utils.clamp(value, 1, Integer.MAX_VALUE);
      updateMaterials();
    }
  }

  public int getCopies()
  {
    return invention != null?invention.getBlueprintCopies():copies;
  }
  
  public void setCopies(int value)
  {
    int c = Utils.clamp(value, 1, Integer.MAX_VALUE);
    if(invention != null)
    {
      invention.setInventionRuns(1);
      int ir = Utils.clamp(getProductionTime() / invention.getInventionTime(),1,(int)Math.ceil(c / invention.getChance()));
      invention.setInventionRuns(ir);
      invention.setAttempts((int)Math.ceil(c / (invention.getChance() * ir)));
      notifyParameterChange(PARAMETER_INVENTION_RUNS);
      notifyParameterChange(PARAMETER_INVENTION_ATTEMPTS);
    } else
    {
      copies = c;
      updateMaterials();
    }
  }


  public InstallationGroup getInstallation()
  {
    return installation;
  }

  public void setInstallation(int inst_id)
  {
    installation = factory.installation_groups.get(inst_id);
    if(installation == null || installation.group_id != blueprint.product.item.group_id)
    {
      setDefaultInstallation();
    }
    updateMaterials();
  }

  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);
    tsl.putString("blueprint", blueprint.product.item.id);
    tsl.putString("hardwiring", hardwiring.value);
    if(invention == null)
    {
      tsl.putString("runs", runs);
      tsl.putString("copies", copies);
      tsl.putString("me", me_level);
      tsl.putString("te", te_level);
    } else
    {
      TSLObject tsl_inv = new TSLObject();
      invention.writeToTSL(tsl_inv);
      tsl.putObject("invention", tsl_inv);
    }
    tsl.putString("installation", installation.id);
    tsl.putString("system", system);
    tsl.putString("tax", tax);
    
    for(Map.Entry<Integer, Integer> s: skills.entrySet())
    {
      TSLObject tsl_skill = new TSLObject();
      tsl_skill.putString("id", s.getKey());
      tsl_skill.putString("level", s.getValue());
      tsl.putObject("skill", tsl_skill);
    }
  }

  public Blueprint getBlueprint()
  {
    return blueprint;
  }
  
 
  @Override
  public int getDuration()
  {
    int prod_time = getProductionTime();
    if(invention != null)
    {
      int inv_time = invention.getInventionTime();
      if(inv_time > prod_time)
      {
        return inv_time;
      }
    }
    
    return prod_time;
  }
  
  public void setSolarSystem(int sys_id)
  {
    system = sys_id;
  }
  
  public int getSolarSystem()
  {
    return system;
  }

  public void setInstallationTax(double percent)
  {
    tax = Utils.clamp(percent, 0, Double.MAX_VALUE);
  }
  
  public double getInstallationTax()
  {
    return tax;
  }
  
  public Invention getInvention()
  {
    return invention;
  }
  
  public void setInventionEnabled(boolean enabled)
  {
    if(enabled && blueprint.invention != null)
    {
      if(invention == null)
      {
        invention = new Invention();
        updateSkills();
        updateMaterials();
      }
    } else
    {
      me_level = invention.getME();
      te_level = invention.getTE();
      runs = invention.getBlueprintRuns();
      copies = invention.getBlueprintCopies();
      invention = null;
      if(copies <= 0)
      {
        copies = 1;
      }      
      updateSkills();
      updateMaterials();
    }
  }
  
  public Set<Integer> getSkills()
  {
    return Collections.unmodifiableSet(skills.keySet());
  }
  
  public int getSkillLevel(int skill)
  {
    return Utils.mapGet(skills,skill,0);
  }
  
  public void setSkillLevel(int skill,int level)
  {
    if(skills.containsKey(skill))
    {
      skills.put(skill, Utils.clamp(level, 0, 5));
    }
  }
  
  //update the list of relevant skills
  private void updateSkills()
  {    
    Map<Integer,Integer> newskills = new HashMap<Integer,Integer>();
    newskills.put(factory.indsutry_data.skill_industry, factory.dynamic_data.getDefaultSkillLevel(factory.indsutry_data.skill_industry));
    newskills.put(factory.indsutry_data.skill_advancedindustry, factory.dynamic_data.getDefaultSkillLevel(factory.indsutry_data.skill_advancedindustry));
    for(int s:blueprint.skills)
    {
      newskills.put(s, factory.dynamic_data.getDefaultSkillLevel(s));
    }
    Blueprint.Invention inv = blueprint.invention;
    if(inv != null && invention != null)
    {
      int invs = inv.encryption_skill_id;
      newskills.put(invs, factory.dynamic_data.getDefaultSkillLevel(invs));
      for(int s:inv.datacore_skill_ids)
      {
        newskills.put(s, factory.dynamic_data.getDefaultSkillLevel(s));
      }
    }
    for(Map.Entry<Integer, Integer> s:skills.entrySet())
    {
      if(newskills.containsKey(s.getKey()))
      {
        newskills.put(s.getKey(), s.getValue());
      }
    }
    boolean notify = !skills.keySet().equals(newskills.keySet());
    skills = newskills;
    if(notify)
    {
      notifyParameterChange(PARAMETER_SKILLS);
    }
  }
}
