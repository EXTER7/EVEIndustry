package exter.eveindustry.data;

import java.math.BigDecimal;

import exter.eveindustry.data.blueprint.Blueprint;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.market.Market;

/**
 * @author exter
 * Provides all the dynamic data (data usually from HTTP or user preferences) needed for tasks.
 */
public interface IDynamicDataProvider
{
  /**
   * Get the ID of the default solar system.
   */
  public int getDefaultSolarSystem();

  /**
   * Get an item's base cost.
   * This value can be obtained from CCP's CREST API.
   */
  public BigDecimal getItemBaseCost(Item item);
  
  /**
   * Get the solar system's industry cost.
   */
  public ISolarSystemIndustryCost getSolarSystemIndustryCost(int system_id);

  /**
   * Get the market price for an item.
   * This can be obtained from CCP's CREST API, or EVE-Central API.
   * @param item The item to look up the price.
   * @param market The market in which to to look up the price.
   * @return The ISK price per unit of the item.
   */
  public BigDecimal getMarketPrice(Item item, Market market);

  /**
   * Get per-skill default level.
   */
  public int getDefaultSkillLevel(int skill_id);
  
  /**
   * Get the default market for produced items.
   */
  public Market getDefaultProducedMarket();

  /**
   * Get the default market for required items.
   */
  public Market getDefaultRequiredMarket();
  
  /**
   * Get the default ME for a blueprint.
   */
  public int getDefaultBlueprintME(Blueprint bp);

  /**
   * Get the default TE for a blueprint.
   */
  public int getDefaultBlueprintTE(Blueprint bp);
  
  /**
   * Get the default broker fee.
   */
  public BigDecimal getDefaultBrokerFee();

  /**
   * Get the default transaction tax.
   */
  public BigDecimal getDefaultTransactionTax();
}
