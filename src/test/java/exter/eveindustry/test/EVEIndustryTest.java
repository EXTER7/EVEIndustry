package exter.eveindustry.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.data.filesystem.DirectoryFileSystemHandler;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.task.ManufacturingTask;
import exter.eveindustry.task.PlanetTask;
import exter.eveindustry.task.ReactionTask;
import exter.eveindustry.task.RefiningTask;
import exter.eveindustry.task.TaskFactory;
import exter.eveindustry.test.data.TestDataProvider;

public class EVEIndustryTest
{
  static private TaskFactory factory = new TaskFactory(new DirectoryFileSystemHandler(new File("testdata")),new TestDataProvider());
  
  static private Map<Integer,ItemStack> mapMaterials(List<ItemStack> materials)
  {
    Map<Integer,ItemStack> map = new HashMap<Integer,ItemStack>();
    for(ItemStack m:materials)
    {
      map.put(m.item.id, m);
    }
    return map;
  }
  
  @Test
  public void testIndustryData()
  {
    Assert.assertEquals(3380,factory.indsutry_data.skill_industry);
    Assert.assertEquals(3388,factory.indsutry_data.skill_advancedindustry);
    Assert.assertEquals(3385,factory.indsutry_data.skill_reprocessing);
    Assert.assertEquals(3389,factory.indsutry_data.skill_reprocessing_efficiency);
    Assert.assertEquals(6,factory.indsutry_data.inst_default);
    Assert.assertEquals(38,factory.indsutry_data.inv_inst_default);
    Assert.assertEquals(158,factory.indsutry_data.relic_inv_inst_default);
  }

  @Test
  public void testInventoryDA()
  {
    Assert.assertNotEquals(null, factory.items.get(18));
    Assert.assertNotEquals(null, factory.items.get(34));
    Assert.assertNotEquals(null, factory.items.get(35));
    Assert.assertNotEquals(null, factory.items.get(36));
  }

  @Test
  public void testBlueprintDA()
  {
    Assert.assertNotEquals(null, factory.blueprints.get(178));
    Assert.assertNotEquals(null, factory.blueprints.get(2046));
    Assert.assertNotEquals(null, factory.blueprints.get(2048));
  }

  @Test
  public void testInstallationDA()
  {
    Assert.assertNotEquals(null, factory.installation_groups.get(141305));
    Assert.assertNotEquals(null, factory.invention_installations.get(38));
    Assert.assertNotEquals(null, factory.invention_installations.get(158));
  }

  @Test
  public void testDecryptorDA()
  {
    Assert.assertEquals(1, factory.decryptors.getIDs().size());
    Assert.assertNotEquals(null, factory.decryptors.get(34201));
  }

  @Test
  public void testRefinableDA()
  {
    Assert.assertNotEquals(null, factory.refinables.get(18));
  }

  @Test
  public void testReactionDA()
  {
    Assert.assertNotEquals(null, factory.reactions.get(16671));
  }

  @Test
  public void testPlanetBuildingDA()
  {
    Assert.assertNotEquals(null, factory.planetbuildings.get(2267));
    Assert.assertNotEquals(null, factory.planetbuildings.get(2272));
    Assert.assertNotEquals(null, factory.planetbuildings.get(2398));
    Assert.assertNotEquals(null, factory.planetbuildings.get(2400));
    Assert.assertNotEquals(null, factory.planetbuildings.get(3828));
  }

  @Test
  public void testPlanetDA()
  {
    Assert.assertEquals(1, factory.planets.getIDs().size());
    Assert.assertNotEquals(null, factory.planets.get(2015));
  }
  
  @Test
  public void testStarmap()
  {
    Assert.assertNotEquals(null, factory.solarsystems.get(30000142));
  }

//  @Test
//  public void testPriceData()
//  {
//    Item item = factory.items.get(34);
//    Assert.assertEquals(new BigDecimal("5"), factory.dynamic_data.getMarketPrice(item, new Market(30000142,Market.Order.BUY,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO)));
//    Assert.assertEquals(new BigDecimal("6"), factory.dynamic_data.getMarketPrice(item, new Market(30000142,Market.Order.SELL,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO)));
//  }

  @Test
  public void testManufacturingTaskMaterials()
  {
    ManufacturingTask task = factory.newManufacturing(178);
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
    task.setME(5);
    Assert.assertEquals(3000,task.getProductionTime());
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(2570,materials.get(34).amount);
    Assert.assertEquals(2000,materials.get(35).amount);
    task.setME(10);
    Assert.assertEquals(3000,task.getProductionTime());
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(2430,materials.get(34).amount);
    Assert.assertEquals(1890,materials.get(35).amount);
  }

  @Test
  public void testManufacturingTaskTime()
  {
    ManufacturingTask task = factory.newManufacturing(178);
    Assert.assertEquals(300,task.getProductionTime());
    task.setSkillLevel(factory.indsutry_data.skill_industry, 3);
    Assert.assertEquals(264,task.getProductionTime());
    task.setSkillLevel(factory.indsutry_data.skill_industry, 5);
    Assert.assertEquals(240,task.getProductionTime());
    task.setSkillLevel(factory.indsutry_data.skill_advancedindustry, 3);
    Assert.assertEquals(219,task.getProductionTime());
    task.setSkillLevel(factory.indsutry_data.skill_advancedindustry, 5);
    Assert.assertEquals(205,task.getProductionTime());
    task.setRuns(10);
    Assert.assertEquals(2041,task.getProductionTime());
    task.setTE(10);
    Assert.assertEquals(1837,task.getProductionTime());
    task.setTE(20);
    Assert.assertEquals(1633,task.getProductionTime());
  }

  @Test
  public void testManufacturingInvention()
  {
    ManufacturingTask task = factory.newManufacturing(2048);
    task.getInvention().setAttempts(10);
    task.getInvention().setInventionRuns(2);
    Assert.assertEquals(2,task.getME());
    Assert.assertEquals(4,task.getTE());
    Assert.assertEquals(6,task.getCopies());
    Assert.assertEquals(10,task.getRuns());
    task.setSkillLevel(23121, 4);
    task.setSkillLevel(11529, 4);
    task.setSkillLevel(11442, 4);
    Assert.assertEquals(9,task.getCopies());
    Assert.assertEquals(10,task.getRuns());
    task.getInvention().setDecryptor(34201);
    Assert.assertEquals(11,task.getCopies());
    Assert.assertEquals(11,task.getRuns());
    Assert.assertEquals(4,task.getME());
    Assert.assertEquals(14,task.getTE());
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
    Assert.assertEquals(4051,task.getRequiredMaterials().get(0).item.id);
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
