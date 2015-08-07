package exter.eveindustry.data.planet;

import java.util.List;

import exter.eveindustry.item.ItemStack;


public interface IPlanetBuilding
{
  public int getID();
  public ItemStack getProduct();
  public int getCustomsOfficeTax();
  public int getLevel();
  public List<ItemStack> getMaterials();
}
