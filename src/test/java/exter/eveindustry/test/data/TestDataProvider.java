package exter.eveindustry.test.data;

import java.math.BigDecimal;

import exter.eveindustry.data.IDynamicDataProvider;
import exter.eveindustry.data.blueprint.Blueprint;
import exter.eveindustry.data.item.Item;
import exter.eveindustry.data.systemcost.SolarSystemIndustryCost;
import exter.eveindustry.market.Market;

public class TestDataProvider implements IDynamicDataProvider
{
  @Override
  public BigDecimal getItemBaseCost(Item item)
  {
    return BigDecimal.ZERO;
  }

  @Override
  public SolarSystemIndustryCost getSolarSystemIndustryCost(int system_id)
  {
    return new SolarSystemIndustryCost(0,0);
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
