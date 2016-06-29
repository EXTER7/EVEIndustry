package exter.eveindustry.test.data;

import java.math.BigDecimal;

import exter.eveindustry.data.IDynamicDataProvider;
import exter.eveindustry.data.IStaticDataProvider;
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
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.market.Market;
import exter.eveindustry.test.data.blueprint.BlueprintDA;
import exter.eveindustry.test.data.blueprint.InstallationDA;
import exter.eveindustry.test.data.blueprint.InstallationGroup;
import exter.eveindustry.test.data.decryptor.DecryptorDA;
import exter.eveindustry.test.data.inventory.InventoryDA;
import exter.eveindustry.test.data.inventory.Item;
import exter.eveindustry.test.data.planet.PlanetBuildingDA;
import exter.eveindustry.test.data.planet.PlanetDA;
import exter.eveindustry.test.data.reaction.ReactionDA;
import exter.eveindustry.test.data.refine.RefinableDA;
import exter.eveindustry.test.data.starbase.StarbaseTowerDA;
import exter.eveindustry.test.data.systemcost.TestSystemCost;

public class TestDataProvider implements IStaticDataProvider,IDynamicDataProvider
{

  @Override
  public int getDefaultSolarSystem()
  {
    return 30000142;
  }

  @Override
  public IItem getItem(int item_id)
  {
    return InventoryDA.items.get(item_id);
  }

  @Override
  public IBlueprint getBlueprint(int blueprint_id)
  {
    return BlueprintDA.blueprints.get(blueprint_id);
  }

  @Override
  public IInstallationGroup getDefaultInstallation(IBlueprint blueprint)
  {
    for(InstallationGroup ig:InstallationDA.group_installations.get(blueprint.getProduct().item_id.getGroupID()))
    {
      if(ig.InstallationID == 6)
      {
        return ig;
      }
    }
    return null;
  }

  @Override
  public IInstallationGroup getInstallationGroup(int inst_group_id)
  {
    return InstallationDA.installation_groups.get(inst_group_id);
  }

  @Override
  public IInventionInstallation getInventionInstallation(int inv_inst_id)
  {
    return InstallationDA.invention_installations.get(inv_inst_id);
  }

  @Override
  public IInventionInstallation getDefaultInventionInstallation(IBlueprint blueprint)
  {
    int id = blueprint.getInvention().usesRelics()?38:151;
    return  InstallationDA.invention_installations.get(id);
  }

  @Override
  public IDecryptor getDecryptor(int decryptor_id)
  {
    return DecryptorDA.decryptors.get(decryptor_id);
  }

  @Override
  public IPlanet getPlanet(int planet_id)
  {
    return PlanetDA.planets.get(planet_id);
  }

  @Override
  public IPlanetBuilding getPlanetBuilding(int building_id)
  {
    return PlanetBuildingDA.buildings.get(building_id);
  }

  @Override
  public IPlanetBuilding getPlanetBuilding(IItem building_product)
  {
    return PlanetBuildingDA.buildings.get(building_product.getID());
  }

  @Override
  public IReaction getReaction(int reaction_id)
  {
    return ReactionDA.reactions.get(reaction_id);
  }

  @Override
  public IRefinable getRefinable(int refinable_id)
  {
    return RefinableDA.refinables.get(refinable_id);
  }

  @Override
  public IStarbaseTower getStarbaseTower(int tower_id)
  {
    return StarbaseTowerDA.towers.get(tower_id);
  }

  @Override
  public BigDecimal getItemBaseCost(IItem item)
  {
    return BigDecimal.ZERO;
  }

  @Override
  public ISolarSystemIndustryCost getSolarSystemIndustryCost(int system_id)
  {
    return new TestSystemCost(system_id);
  }

  @Override
  public BigDecimal getMarketPrice(IItem item, Market market)
  {
    switch(market.order)
    {
      case BUY:
        return ((Item)item).Buy;
      case MANUAL:
        return market.manual;
      case SELL:
        return ((Item)item).Sell;
      default:
        return BigDecimal.ZERO;
    }
  }

  @Override
  public int getIndustrySkillID()
  {
    return 3380;
  }

  @Override
  public int getAdvancedIndustrySkillID()
  {
    return 3388;
  }

  @Override
  public int getRefiningSkillID()
  {
    return 3385;
  }

  @Override
  public int getRefineryEfficiencySkillID()
  {
    return 3389;
  }

  @Override
  public int getDefaultSkillLevel(int skill_id)
  {
    return 0;
  }

  @Override
  public Market getDefaultProducedMarket()
  {
    return new Market(getDefaultSolarSystem(), Market.Order.SELL, BigDecimal.ZERO, getDefaultBrokerFee(), getDefaultTransactionTax());
  }

  @Override
  public Market getDefaultRequiredMarket()
  {
    return new Market(getDefaultSolarSystem(), Market.Order.SELL, BigDecimal.ZERO, getDefaultBrokerFee(), getDefaultTransactionTax());
  }

  @Override
  public int getDefaultBlueprintME(IBlueprint bp)
  {
    return 0;
  }

  @Override
  public int getDefaultBlueprintTE(IBlueprint bp)
  {
    return 0;
  }

  @Override
  public BigDecimal getDefaultBrokerFee()
  {
    return new BigDecimal("3");
  }

  @Override
  public BigDecimal getDefaultTransactionTax()
  {
    return new BigDecimal("2");
  }
}
