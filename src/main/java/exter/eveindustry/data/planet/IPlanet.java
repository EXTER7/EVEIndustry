package exter.eveindustry.data.planet;

import java.util.List;

import exter.eveindustry.data.inventory.IItem;

public interface IPlanet
{
  public List<IItem> getResources();
  public int getID();
  public boolean isAdvanced();
}
