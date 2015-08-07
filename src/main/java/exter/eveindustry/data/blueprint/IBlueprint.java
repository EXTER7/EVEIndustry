package exter.eveindustry.data.blueprint;

import java.util.List;
import java.util.Set;

import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.item.ItemStack;

/**
 * @author exter
 * Blueprint data.
 */
public interface IBlueprint
{

  /**
   * @author exter
   * Invention data for T2/T3 blueprints.
   */
  static public interface IInvention
  {
    /**
     * @author exter
     * Relics used for T3 invention.
     */
    static public interface IRelic
    {
      /**
       * Get the ID of the relic.
       */
      public int getID();
      /**
       * Get the relic item.
       */
      public IItem getItem();

      /**
       * Get the base runs of the relic.
       */
      public int getRuns();

      /**
       * Get the base success chance of the relic.
       */
      public double getChance();
    }
    
    
    /**
     * Get the invention time in seconds
     */
    public int getTime();

    /**
     * Get the base runs
     */
    public int getRuns();
    
    /**
     * Get the base chance of success.
     */
    public double getChance();

    /**
     * Get the material needed for invention (datacores).
     */
    public List<ItemStack> getMaterials();
    
    /**
     * Get the ID of the corresponding "Encrytion Methods" skill.
     */
    public int getEncryptionSkillID();

    /**
     * Get the IDs of the corresponding datacore science skills.
     */
    public Set<Integer> getDatacoreSkillIDs();
    
    /**
     * Return true if the invention uses relics instead of a T1 blueprint.
     */
    public boolean usesRelics();

    /**
     * Get the available relics (T3 blueprints).
     */
    public IRelic getRelic(int id);    

    /**
     * Get a relic from it's ID (T3 blueprints).
     */
    public Set<Integer> getRelicIDs();

    /**
     * Get the default relic (T3 blueprints).
     */
     public IRelic getDefaultRelic();
  }
  
  public int getID();
  public ItemStack getProduct();
  public List<ItemStack> getMaterials();
  public int getManufacturingTime();
 
  public Set<Integer> getSkills();
  
  public IInvention getInvention();
  
}
