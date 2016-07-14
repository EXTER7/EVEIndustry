package exter.eveindustry.test.data;

import java.math.BigDecimal;

import exter.eveindustry.data.IDynamicDataProvider;
import exter.eveindustry.data.blueprint.Blueprint;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.data.systemcost.ISolarSystemIndustryCost;
import exter.eveindustry.market.Market;
import exter.eveindustry.test.data.systemcost.TestSystemCost;

public class TestDataProvider implements IDynamicDataProvider
{
  @Override
  public BigDecimal getItemBaseCost(Item item)
  {
    return BigDecimal.ZERO;
  }

  @Override
  public ISolarSystemIndustryCost getSolarSystemIndustryCost(int system_id)
  {
    return new TestSystemCost(system_id);
  }

  @Override
  public int getDefaultSkillLevel(int skill_id)
  {
    return 0;
  }

  @Override
  public Market getDefaultProducedMarket()
  {
    return new Market(getDefaultSolarSystem(), Market.Order.SELL, BigDecimal.ZERO, new BigDecimal("3"), new BigDecimal("2"));
  }

  @Override
  public Market getDefaultRequiredMarket()
  {
    return new Market(getDefaultSolarSystem(), Market.Order.SELL, BigDecimal.ZERO, new BigDecimal("3"), new BigDecimal("2"));
  }

  @Override
  public int getDefaultBlueprintME(Blueprint bp)
  {
    return 0;
  }

  @Override
  public int getDefaultBlueprintTE(Blueprint bp)
  {
    return 0;
  }

  @Override
  public int getDefaultSolarSystem()
  {
    return 30000142;
  }

  @Override
  public BigDecimal getMarketPrice(Item item, Market market)
  {
    return BigDecimal.ZERO;
  }
}
