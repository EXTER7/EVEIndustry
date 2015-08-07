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
  public void testBlueprintDA()
  {
    Assert.assertNotEquals(provider.getBlueprint(178), null);
  }

  @Test
  public void testInstallationDA()
  {
    Assert.assertNotEquals(provider.getInstallationGroup(8105), null);
    Assert.assertNotEquals(provider.getInventionInstallation(38), null);
    Assert.assertNotEquals(provider.getInventionInstallation(158), null);
  }

  @Test
  public void testDecryptorDA()
  {
    Assert.assertNotEquals(provider.getDecryptor(34201), null);
  }

  @Test
  public void testRefinableDA()
  {
    Assert.assertNotEquals(provider.getRefinable(18), null);
  }

  @Test
  public void testReactionDA()
  {
    Assert.assertNotEquals(provider.getReaction(16671), null);
  }

  @Test
  public void testPlanetBuildingDA()
  {
    Assert.assertNotEquals(provider.getPlanetBuilding(2267), null);
    Assert.assertNotEquals(provider.getPlanetBuilding(2272), null);
    Assert.assertNotEquals(provider.getPlanetBuilding(2398), null);
    Assert.assertNotEquals(provider.getPlanetBuilding(2400), null);
    Assert.assertNotEquals(provider.getPlanetBuilding(3828), null);
  }

  @Test
  public void testPlanetDA()
  {
    Assert.assertNotEquals(provider.getPlanet(2015), null);
  }

  @Test
  public void testManufacturingTask()
  {
    ManufacturingTask task = new ManufacturingTask(provider.getBlueprint(178));
    Assert.assertEquals(task.getProductionTime(),300);
    Map<Integer,ItemStack> materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(materials.get(34).amount,27);
    Assert.assertEquals(materials.get(35).amount,21);
    task.setRuns(10);
    Assert.assertEquals(task.getProductionTime(),3000);
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(materials.get(34).amount,270);
    Assert.assertEquals(materials.get(35).amount,210);
    task.setCopies(10);
    Assert.assertEquals(task.getProductionTime(),3000);
    materials = mapMaterials(task.getRequiredMaterials());
    Assert.assertEquals(materials.get(34).amount,2700);
    Assert.assertEquals(materials.get(35).amount,2100);
  }

  @Test
  public void testPlanetTask()
  {
    PlanetTask task = new PlanetTask(provider.getDefaultPlanet());
    Assert.assertEquals(task.getProducedMaterials().size(),0);
    Assert.assertEquals(task.getRequiredMaterials().size(),0);
    task.addBuilding(provider.getPlanetBuilding(3828));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),2);
    task.addBuilding(provider.getPlanetBuilding(2398));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),2);
    task.addBuilding(provider.getPlanetBuilding(2400));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),2);
    task.addBuilding(provider.getPlanetBuilding(2267));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),1);
    task.addBuilding(provider.getPlanetBuilding(2272));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),0);
  }

  @Test
  public void testReactionTask()
  {
    ReactionTask task = new ReactionTask(provider.getDefaultStarbaseTower());
    Assert.assertEquals(task.getProducedMaterials().size(),0);
    Assert.assertEquals(task.getRequiredMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().get(0).item.getID(),4051);
    Assert.assertEquals(task.getRequiredMaterials().get(0).amount,960);
    task.addReaction(provider.getReaction(16671));
    Assert.assertEquals(task.getProducedMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().size(),3);
  }

  @Test
  public void testRefiningTask()
  {
    RefiningTask task = new RefiningTask(provider.getRefinable(18));
    Assert.assertEquals(task.getProducedMaterials().size(),3);
    Assert.assertEquals(task.getRequiredMaterials().size(),1);
    Assert.assertEquals(task.getRequiredMaterials().get(0).amount,100);
    Map<Integer,ItemStack> materials = mapMaterials(task.getProducedMaterials());
    Assert.assertEquals(materials.get(34).amount,51);
    Assert.assertEquals(materials.get(35).amount,101);
    Assert.assertEquals(materials.get(36).amount,51);
  }

  static
  {
    Task.setDataProvider(provider);
  }
}
