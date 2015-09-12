package exter.eveindustry.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


import exter.eveindustry.item.ItemStack;
import exter.eveindustry.task.ManufacturingTask;
import exter.eveindustry.task.PlanetTask;
import exter.eveindustry.task.ReactionTask;
import exter.eveindustry.task.RefiningTask;
import exter.eveindustry.task.Task;
import exter.eveindustry.test.data.TestDataProvider;

public class EVEIndustryTest
{
  static private final TestDataProvider provider = new TestDataProvider();
  
  static private Map<Integer,ItemStack> mapMaterials(List<ItemStack> materials)
  {
    Map<Integer,ItemStack> map = new HashMap<Integer,ItemStack>();
    for(ItemStack m:materials)
    {
      map.put(m.item.getID(), m);
    }
    return map;
  }

  @Test
  public void testInventoryDA()
  {
    Assert.assertNotEquals(null, provider.getItem(18));
    Assert.assertNotEquals(null, provider.getItem(34));
    Assert.assertNotEquals(null, provider.getItem(35));
    Assert.assertNotEquals(null, provider.getItem(36));
  }

  @Test
  public void testBlueprintDA()
  {
    Assert.assertNotEquals(null, provider.getBlueprint(178));
  }

  @Test
  public void testInstallationDA()
  {
    Assert.assertNotEquals(null, provider.getInstallationGroup(8105));
    Assert.assertNotEquals(null, provider.getInventionInstallation(38));
    Assert.assertNotEquals(null, provider.getInventionInstallation(158));
  }

  @Test
  public void testDecryptorDA()
  {
    Assert.assertNotEquals(null, provider.getDecryptor(34201));
  }

  @Test
  public void testRefinableDA()
  {
    Assert.assertNotEquals(null, provider.getRefinable(18));
  }

  @Test
  public void testReactionDA()
  {
    Assert.assertNotEquals(null, provider.getReaction(16671));
  }

  @Test
  public void testPlanetBuildingDA()
  {
    Assert.assertNotEquals(null, provider.getPlanetBuilding(2267));
    Assert.assertNotEquals(null, provider.getPlanetBuilding(2272));
    Assert.assertNotEquals(null, provider.getPlanetBuilding(2398));
    Assert.assertNotEquals(null, provider.getPlanetBuilding(2400));
    Assert.assertNotEquals(null, provider.getPlanetBuilding(3828));
  }

  @Test
  public void testPlanetDA()
  {
    Assert.assertNotEquals(provider.getPlanet(2015), null);
  }
  
  @Test
  public void testStarmap()
  {
    Assert.assertNotEquals(provider.getSolarSystemIndustryCost(30000142), null);
    Assert.assertNotEquals(provider.getSolarSystemIndustryCost(30002798), null);
  }
  
  @Test
  public void testManufacturingTask()
  {
    ManufacturingTask task = new ManufacturingTask(provider.getBlueprint(178));
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
    PlanetTask task = new PlanetTask(provider.getPlanet(2015));
    Assert.assertEquals(0,task.getProducedMaterials().size());
    Assert.assertEquals(0,task.getRequiredMaterials().size());
    task.addBuilding(provider.getPlanetBuilding(3828));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(provider.getPlanetBuilding(2398));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(provider.getPlanetBuilding(2400));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(2,task.getRequiredMaterials().size());
    task.addBuilding(provider.getPlanetBuilding(2267));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    task.addBuilding(provider.getPlanetBuilding(2272));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(0,task.getRequiredMaterials().size());
  }

  @Test
  public void testReactionTask()
  {
    ReactionTask task = new ReactionTask(provider.getStarbaseTower(16213));
    Assert.assertEquals(0,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    Assert.assertEquals(4051,task.getRequiredMaterials().get(0).item.getID());
    Assert.assertEquals(960,task.getRequiredMaterials().get(0).amount);
    task.addReaction(provider.getReaction(16671));
    Assert.assertEquals(1,task.getProducedMaterials().size());
    Assert.assertEquals(3,task.getRequiredMaterials().size());
  }

  @Test
  public void testRefiningTask()
  {
    RefiningTask task = new RefiningTask(provider.getRefinable(18));
    Assert.assertEquals(3,task.getProducedMaterials().size());
    Assert.assertEquals(1,task.getRequiredMaterials().size());
    Assert.assertEquals(100,task.getRequiredMaterials().get(0).amount);
    Map<Integer,ItemStack> materials = mapMaterials(task.getProducedMaterials());
    Assert.assertEquals(50,materials.get(34).amount);
    Assert.assertEquals(101,materials.get(35).amount);
    Assert.assertEquals(50,materials.get(36).amount);
    task.setRefineryTax(0);
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
    Assert.assertEquals(task.getRequiredMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().get(0).amount,1300);
  }

  static
  {
    Task.setDataProvider(provider);
  }
}
