package exter.eveindustry.test;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.market.Market;
import exter.eveindustry.task.ManufacturingTask;
import exter.eveindustry.task.PlanetTask;
import exter.eveindustry.task.ReactionTask;
import exter.eveindustry.task.RefiningTask;
import exter.eveindustry.task.TaskFactory;
import exter.eveindustry.test.data.TestDataProvider;

public class EVEIndustryTest
{
  static private final TaskFactory factory = new TaskFactory(new TestDataProvider());
  
  static private Map<Integer,ItemStack> mapMaterials(List<ItemStack> materials)
  {
    Map<Integer,ItemStack> map = new HashMap<Integer,ItemStack>();
    for(ItemStack m:materials)
    {
      map.put(m.item_id.getID(), m);
    }
    return map;
  }

  @Test
  public void testInventoryDA()
  {
    Assert.assertNotEquals(null, factory.provider.getItem(18));
    Assert.assertNotEquals(null, factory.provider.getItem(34));
    Assert.assertNotEquals(null, factory.provider.getItem(35));
    Assert.assertNotEquals(null, factory.provider.getItem(36));
  }

  @Test
  public void testBlueprintDA()
  {
    Assert.assertNotEquals(null, factory.provider.getBlueprint(178));
  }

  @Test
  public void testInstallationDA()
  {
    Assert.assertNotEquals(null, factory.provider.getInstallationGroup(8105));
    Assert.assertNotEquals(null, factory.provider.getInventionInstallation(38));
    Assert.assertNotEquals(null, factory.provider.getInventionInstallation(158));
  }

  @Test
  public void testDecryptorDA()
  {
    Assert.assertNotEquals(null, factory.provider.getDecryptor(34201));
  }

  @Test
  public void testRefinableDA()
  {
    Assert.assertNotEquals(null, factory.provider.getRefinable(18));
  }

  @Test
  public void testReactionDA()
  {
    Assert.assertNotEquals(null, factory.provider.getReaction(16671));
  }

  @Test
  public void testPlanetBuildingDA()
  {
    Assert.assertNotEquals(null, factory.provider.getPlanetBuilding(2267));
    Assert.assertNotEquals(null, factory.provider.getPlanetBuilding(2272));
    Assert.assertNotEquals(null, factory.provider.getPlanetBuilding(2398));
    Assert.assertNotEquals(null, factory.provider.getPlanetBuilding(2400));
    Assert.assertNotEquals(null, factory.provider.getPlanetBuilding(3828));
  }

  @Test
  public void testPlanetDA()
  {
    Assert.assertNotEquals(null, factory.provider.getPlanet(2015));
  }
  
  @Test
  public void testStarmap()
  {
    Assert.assertNotEquals(null, factory.provider.getSolarSystemIndustryCost(30000142));
    Assert.assertNotEquals(null, factory.provider.getSolarSystemIndustryCost(30002798));
  }

  @Test
  public void testPriceData()
  {
    IItem item = factory.provider.getItem(34);
    Assert.assertEquals(new BigDecimal("5"), factory.provider.getMarketPrice(item, new Market(30000142,Market.Order.BUY,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO)));
    Assert.assertEquals(new BigDecimal("6"), factory.provider.getMarketPrice(item, new Market(30000142,Market.Order.SELL,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO)));
  }

  @Test
  public void testManufacturingTask()
  {
    ManufacturingTask task = factory.newManufacturing(178);
    Assert.assertEquals(300,task.getProductionTime());
    Map<Integer,ItemStack> materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(27,materials.get(34).amount);
    Assert.assertEquals(21,materials.get(35).amount);
    task.setRuns(10);
    Assert.assertEquals(3000,task.getProductionTime());
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(270,materials.get(34).amount);
    Assert.assertEquals(210,materials.get(35).amount);
    task.setCopies(10);
    Assert.assertEquals(3000,task.getProductionTime());
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(2700,materials.get(34).amount);
    Assert.assertEquals(2100,materials.get(35).amount);
  }

  @Test
  public void testPlanetTask()
  {
    PlanetTask task = factory.newPlanet(2015);
    Assert.assertEquals(0,task.getProducedMaterials().size());
    Assert.assertEquals(0,task.getRequiredMaterials().size());
    task.addBuilding(3828);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(2398);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(2400);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(2267);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    task.addBuilding(2272);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(0,task.getRequiredMaterials().size());
  }

  @Test
  public void testReactionTask()
  {
    ReactionTask task = factory.newReaction(16213);
    Assert.assertEquals(0,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    Assert.assertEquals(4051,task.getRequiredMaterials().get(0).item_id.getID());
    Assert.assertEquals(960,task.getRequiredMaterials().get(0).amount);
    task.addReaction(16671);
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(3,task.getRequiredMaterials().size());
  }

  @Test
  public void testRefiningTask()
  {
    RefiningTask task = factory.newRefining(18);
    Assert.assertEquals(3,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    Assert.assertEquals(100,task.getRequiredMaterials().get(0).amount);
    Map<Integer,ItemStack> materials = mapMaterials(task.getProducedMaterials());
    materials = mapMaterials(task.getProducedMaterials());
    Assert.assertEquals(53,materials.get(34).amount);
    Assert.assertEquals(106,materials.get(35).amount);
    Assert.assertEquals(53,materials.get(36).amount);
    task.setReprocessingSkillLevel(5);
    task.setReprocessingEfficiencySkillLevel(5);
    task.setOreProcessingSkillLevel(5);
    materials = mapMaterials(task.getProducedMaterials());
    Assert.assertEquals(74,materials.get(34).amount);
    Assert.assertEquals(148,materials.get(35).amount);
    Assert.assertEquals(74,materials.get(36).amount);
    task.setOreAmount(1337);
    materials = mapMaterials(task.getProducedMaterials());
    Assert.assertEquals(967,materials.get(34).amount);
    Assert.assertEquals(1926,materials.get(35).amount);
    Assert.assertEquals(967,materials.get(36).amount);
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    Assert.assertEquals(1300,task.getRequiredMaterials().get(0).amount);
  }
//
//  static private final DecimalFormat ISK_FORMATTER = new DecimalFormat("#.##");
//  
//  @Test
//  public void testTaskMarket()
//  {
//    IItem product = factory.provider.getItem(178);
//    ManufacturingTask task = factory.newManufacturing(178);
//    Assert.assertEquals("6",ISK_FORMATTER.format(task.getMaterialMarketPrice(factory.provider.getItem(34),MarketAction.BUY)));
//    Assert.assertEquals("12",ISK_FORMATTER.format(task.getMaterialMarketPrice(factory.provider.getItem(35),MarketAction.BUY)));
//    Assert.assertEquals("2.85",ISK_FORMATTER.format(task.getMaterialMarketPrice(product,MarketAction.SELL)));
//    task.setMaterialMarket(product, new Market(factory.provider.getDefaultSolarSystem(),Market.Order.BUY));
//    Assert.assertEquals("1.96",ISK_FORMATTER.format(task.getMaterialMarketPrice(product,MarketAction.SELL)));
//    task.setMaterialMarket(product, new Market(factory.provider.getDefaultSolarSystem(),Market.Order.SELL,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO));
//    Assert.assertEquals("3",ISK_FORMATTER.format(task.getMaterialMarketPrice(product,MarketAction.SELL)));
//    
//    task.setMaterialMarket(product, new Market(factory.getDefaultSolarSystem(),Market.Order.SELL));
//    Assert.assertEquals("414",ISK_FORMATTER.format(task.getExpense()));
//    Assert.assertEquals("285",ISK_FORMATTER.format(task.getIncome()));
//  }
}
