package exter.eveindustry.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.blueprint.IInstallationGroup;
import exter.eveindustry.data.blueprint.IInventionInstallation;
import exter.eveindustry.data.decryptor.IDecryptor;
import exter.eveindustry.data.inventory.IItem;
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
  // Solot 8 hardwiring
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
   * Invention attributes for T2 blueprints.
   */
  public class Invention
  {
    private IDecryptor decryptor;
    private int attempts;
    private IInventionInstallation installation;
    private IBlueprint.IInvention.IRelic relic;
    private int invruns;
    
    public Invention()
    {
      decryptor = null;
      attempts = 5;
      invruns = 1;
      
      installation = getDataProvider().getDefaultInventionInstallation(blueprint);
      relic = blueprint.getInvention().getDefaultRelic();
    }

    public Invention(TSLObject tsl)
    {
      IEVEDataProvider data = getDataProvider();
      setAttempts(tsl.getStringAsInt("attempts",5));
      setInventionRuns(tsl.getStringAsInt("runs",1));
      setDecryptor(data.getDecryptor(tsl.getStringAsInt("decryptor", -1)));
      if(blueprint.getInvention().usesRelics())
      {
        setInstallation(data.getInventionInstallation(tsl.getStringAsInt("installation", -1)));
        relic = blueprint.getInvention().getRelic(tsl.getStringAsInt("relic",-1));
        if(relic == null)
        {
          relic = blueprint.getInvention().getDefaultRelic();
        }
      } else
      {
        setInstallation(data.getInventionInstallation(tsl.getStringAsInt("installation", -1)));
        relic = null;
      }
    }
    
    public void writeToTSL(TSLObject tsl)
    {
      tsl.putString("attempts", attempts);
      tsl.putString("decryptor", decryptor == null?-1:decryptor.getID());
      tsl.putString("installation", installation.getID());
      tsl.putString("runs", invruns);
      if(relic != null)
      {
        tsl.putString("relic", relic.getID());
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
    

    public IDecryptor getDecryptor()
    {
      return decryptor;
    }
    
    public void setDecryptor(IDecryptor d)
    {
      decryptor = d;
      updateMaterials();
    }
    
    public double getChance()
    {
      IBlueprint.IInvention inv = blueprint.getInvention();
      int science = 0;
      for(int s:inv.getDatacoreSkillIDs())
      {
        int l = Utils.mapGet(skills, s, 0);
        science += l; 
      }
      double basechance;
      if(relic != null)
      {
        basechance = relic.getChance();
      } else
      {
        basechance = inv.getChance();
      }
      
      double sk = 1 + (double)science / 30 + (double)Utils.mapGet(skills, inv.getEncryptionSkillID(), 0) / 40;
      double decr = decryptor == null?1.0:decryptor.getModifierChance();
      double chance = basechance * sk * decr;
      if(chance > 1)
      {
        chance = 1;
      }
      return chance;
    }

    public int getME()
    {
      return decryptor == null?2:(decryptor.getModifierME() + 2);
    }
    
    public int getTE()
    {
      return decryptor == null?4:(decryptor.getModifierTE() + 4);
    }
    
    public int getBlueprintRuns()
    {
      int runs;
      if(relic != null)
      {
        runs = relic.getRuns();
      } else
      {
        runs = blueprint.getInvention().getRuns();
      }
      return decryptor == null?runs:(decryptor.getModifierRuns() + runs);
    }
    
    public int getBlueprintCopies()
    {
      return (int)(attempts * invruns * getChance());
    }


    public IInventionInstallation GetInstallation()
    {
      return installation;
    }

    public void setInstallation(IInventionInstallation ins)
    {
      installation = ins;
      boolean rel = blueprint.getInvention().usesRelics();
      if(installation == null || installation.isForRelics() != rel)
      {
        installation = getDataProvider().getDefaultInventionInstallation(blueprint);
      }
    }
    
    public int getInventionTime()
    {
      int advindustry_skill = skills.get(getDataProvider().getIndustrySkillID());
      return (int) Math.ceil(invruns * blueprint.getInvention().getTime() * installation.getTimeBonus() * (1.0 - 0.03 * advindustry_skill));
    }
    
    public IBlueprint.IInvention.IRelic GetRelic()
    {
      return relic;
    }
    
    public void setRelic(int id)
    {
      IBlueprint.IInvention inv = blueprint.getInvention();
      Set<Integer> relics = inv.getRelicIDs();
      if(relics != null && relics.contains(id))
      {
        relic = inv.getRelic(id);
        updateMaterials();
      }
    }
  }
  
  static private class SkillComparator implements Comparator<Integer>
  {
    @Override
    public int compare(Integer lhs, Integer rhs)
    {
      IEVEDataProvider data = getDataProvider();
      IItem l = data.getItem(lhs);
      IItem r = data.getItem(rhs);
      int cl = l.getGroupID();
      int cr = r.getGroupID(); 
      if(cl == cr)
      {
        return lhs.intValue() - rhs.intValue();
      }
      return cl - cr;
    }
  }

  private Hardwiring hardwiring;

  private int me_level;
  private int te_level;
  private int runs;
  private int copies;
  
  private int system;
  private double tax;
  
  private Invention invention;
  
  private IInstallationGroup installation;
  private IBlueprint blueprint;
  
  private Map<Integer,Integer> skills;
  private Set<Integer> skill_ids;
  
  protected ManufacturingTask()
  {
    super();
  }
  
  public ManufacturingTask(IBlueprint aBlueprint)
  {
    blueprint = aBlueprint;
    skills = new HashMap<Integer,Integer>();
    hardwiring = Hardwiring.None;
    if(blueprint.getInvention() != null)
    {
      invention = new Invention();
    } else
    {
      invention = null;
    }
    updateSkills();
    IEVEDataProvider data = getDataProvider();
    setME(data.getDefaultBlueprintME(aBlueprint));
    setTE(data.getDefaultBlueprintTE(aBlueprint));
    system = getDataProvider().getDefaultSolarSystem();
    runs = 1;
    copies = 1;
    tax = 10;
    setDefaultInstallation();
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
      return material.scaledCeil(installation.getMaterialBonus() * (1.0 - getME() * 0.01) * r);
    }
  }
  
  public int getProductionTime()
  {
    double eff_time;
    int advindustry_skill = skills.get(getDataProvider().getIndustrySkillID());
    int industry_skill = skills.get(getDataProvider().getAdvancedIndustrySkillID());

    eff_time = (double) blueprint.getManufacturingTime() * installation.getTimeBonus() * (1 - (double)getTE() / 100.0) * getRuns();
    eff_time *= (1.0 - 0.04 * industry_skill) * (1.0 - hardwiring.bonus) * (1.0 - advindustry_skill * 0.03);

    return (int)Math.ceil(eff_time);
  }

  @Override
  public BigDecimal getExtraExpense()
  {
    BigDecimal base_cost = BigDecimal.ZERO;
    ISolarSystemIndustryCost syscost = getDataProvider().getSolarSystemIndustryCost(system);
    for(ItemStack m:blueprint.getMaterials())
    {
      base_cost = base_cost.add(
          getDataProvider().getItemBaseCost(m.item).multiply(new BigDecimal(m.amount)));
    }
    BigDecimal jobtax = base_cost.multiply(
        new BigDecimal(
            (double)(getRuns() * getCopies()) * syscost.getManufacturingCost() * (1.0 + tax / 100)));
    if(invention != null)
    {
      jobtax = jobtax.add(
          base_cost.multiply(
              new BigDecimal(invention.installation.getCostBonus() * invention.attempts * syscost.getInventionCost() * 0.02 * (1.0 + tax / 100))));
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
      for(ItemStack m : blueprint.getMaterials())
      {
        required.add(getEffectiveMaterial(m).scaled(c));
      }
    }
    if(invention != null)
    {
      if(invention.decryptor != null)
      {
        required.add(new ItemStack(invention.decryptor.getItem(), (long)invention.attempts * invention.invruns));
      }
      for(ItemStack m : blueprint.getInvention().getMaterials())
      {
        required.add(m.scaled(invention.attempts * invention.invruns));
      }
      if(invention.relic != null)
      {
        required.add(new ItemStack(invention.relic.getItem(), (long)invention.attempts * invention.invruns));
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
      ItemStack prod = blueprint.getProduct();
      produced.add(prod.scaled(r * c));
    }
    return produced;
  }


  private void setDefaultInstallation()
  {
    installation = getDataProvider().getDefaultInstallation(blueprint);
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

  public void setME(int value)
  {
    if(invention == null)
    {
      me_level = Utils.clamp(value, 0, 10);
      updateMaterials();
    }
  }
  
  public int getTE()
  {
    return invention != null?invention.getTE():te_level;
  }

  public void setTE(int value)
  {
    if(invention == null)
    {
      te_level = Utils.clamp(value, 0, 20);
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
    } else
    {
      copies = c;
      updateMaterials();
    }
  }

  @Override
  protected void onLoadDataFromTSL(TSLObject tsl) throws TaskLoadException
  {
    IEVEDataProvider data = getDataProvider(); 
    blueprint = data.getBlueprint(tsl.getStringAsInt("blueprint", -1));
    if(blueprint == null)
    {
      throw new TaskLoadException();
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
    installation = data.getInstallationGroup(tsl.getStringAsInt("installation", -1));
    if(installation == null || installation.getGroupID() != blueprint.getProduct().item.getGroupID())
    {
      setDefaultInstallation();
    }
    system = tsl.getStringAsInt("system", data.getDefaultSolarSystem());
    tax = Utils.clamp(tsl.getStringAsFloat("tax", 10),0,100);
    if(hardwiring == null)
    {
      hardwiring = Hardwiring.None;
    }

    List<TSLObject> tsl_skills = tsl.getObjectList("skill");
    for(TSLObject s:tsl_skills)
    {
      int id = s.getStringAsInt("id", -1);
      if(data.getItem(id) != null)
      {
        if(skills.containsKey(id))
        {
          skills.put(id, Utils.clamp(s.getStringAsInt("level", 0), 0, 5));
        }
      }
    }
    
    updateMaterials();
  }

  public IInstallationGroup getInstallation()
  {
    return installation;
  }

  public void setInstallation(IInstallationGroup ins)
  {
    installation = ins;
    if(installation == null || installation.getGroupID() != blueprint.getProduct().item.getGroupID())
    {
      setDefaultInstallation();
    }
    updateMaterials();
  }

  @Override
  public void writeToTSL(TSLObject tsl)
  {
    super.writeToTSL(tsl);
    tsl.putString("blueprint", blueprint.getID());
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
    tsl.putString("installation", installation.getID());
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

  public IBlueprint getBlueprint()
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
  
  public void setSolarSystem(int sys)
  {
    system = sys;
  }
  
  public int getSolarSystem()
  {
    return system;
  }

  public void setInstallationTax(double t)
  {
    tax = t;
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
    if(enabled && blueprint.getInvention() != null)
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
    return Collections.unmodifiableSet(skill_ids);
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
  
  private void updateSkills()
  {
    IEVEDataProvider data = getDataProvider();
    
    Map<Integer,Integer> newskills = new HashMap<Integer,Integer>();
    int ind = data.getIndustrySkillID();
    newskills.put(ind, data.getDefaultSkillLevel(ind));
    ind = data.getAdvancedIndustrySkillID();
    newskills.put(ind, data.getDefaultSkillLevel(ind));
    for(int s:blueprint.getSkills())
    {
      newskills.put(s, data.getDefaultSkillLevel(s));
    }
    IBlueprint.IInvention inv = blueprint.getInvention();
    if(inv != null && invention != null)
    {
      int invs = inv.getEncryptionSkillID();
      newskills.put(invs, data.getDefaultSkillLevel(invs));
      for(int s:inv.getDatacoreSkillIDs())
      {
        newskills.put(s, data.getDefaultSkillLevel(s));
      }
    }
    for(Map.Entry<Integer, Integer> s:skills.entrySet())
    {
      if(newskills.containsKey(s.getKey()))
      {
        newskills.put(s.getKey(), s.getValue());
      }
    }
    skills = newskills;
    skill_ids = new TreeSet<Integer>(new SkillComparator());
    skill_ids.addAll(skills.keySet());
  }
}
