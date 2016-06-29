package exter.eveindustry.data;

import java.math.BigDecimal;

import exter.eveindustry.data.blueprint.IBlueprint;
import exter.eveindustry.data.blueprint.IInstallationGroup;
import exter.eveindustry.data.blueprint.IInventionInstallation;
import exter.eveindustry.data.decryptor.IDecryptor;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.data.planet.IPlanet;
import exter.eveindustry.data.planet.IPlanetBuilding;
import exter.eveindustry.data.reaction.IReaction;
import exter.eveindustry.data.reaction.IStarbaseTower;
import exter.eveindustry.data.refinable.IRefinable;

/**
 * @author exter
 * Provides all the static data (data from the EVE SDE) needed for tasks.
 */
public interface IStaticDataProvider
{
  /**
   * Get an item from it's ID.
   */
  public IItem getItem(int item_id);

  /**
   * Get a blueprint from it's ID.
   */
  public IBlueprint getBlueprint(int blueprint_id);

  /**
   * Get the default installation-category of a blueprint. 
   */
  public IInstallationGroup getDefaultInstallation(IBlueprint blueprint);

  /**
   * Get a installation-group by ID. 
   */
  public IInstallationGroup getInstallationGroup(int inst_group_id);

  /**
   * Get a invention installation by ID. 
   */
  public IInventionInstallation getInventionInstallation(int inv_inst_id);

  /**
   * Get a default invention installation of a blueprint. 
   */
  public IInventionInstallation getDefaultInventionInstallation(IBlueprint blueprint);
  
  /**
   * Get a decryptor from it's ID.
   */
  public IDecryptor getDecryptor(int decryptor_id);

  /**
   * Get a planet type from it's ID.
   */
  public IPlanet getPlanet(int planet_id);

  /**
   * Get a planetary building from it's ID.
   */
  public IPlanetBuilding getPlanetBuilding(int building_id);
  
  /**
   * Get a planetary building from it's produced item.
   */
  public IPlanetBuilding getPlanetBuilding(IItem building_product);

  /**
   * Get a reaction from it's ID.
   */
  public IReaction getReaction(int reaction_id);

  /**
   * Get a refinable item from it's ID.
   */
  public IRefinable getRefinable(int refinable_id);

  /**
   * Get a starbase control tower from it's ID.
   */
  public IStarbaseTower getStarbaseTower(int tower_id);

  /**
   * Get an item's base cost.
   * This value can be obtained from CCP's CREST API.
   */
  public BigDecimal getItemBaseCost(IItem item);
  
  /**
   * Get the ID of the "Industry" skill.
   */
  public int getIndustrySkillID();

  /**
   * Get the ID of the "Advanced Industry" skill.
   */
  public int getAdvancedIndustrySkillID();

  /**
   * Get the ID of the "Refining" skill.
   */
  public int getRefiningSkillID();
  
  /**
   * Get the ID of the "Refinery Efficiency" skill.
   */
  public int getRefineryEfficiencySkillID();
}
