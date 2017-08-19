package exter.eveindustry.data;

import exter.eveindustry.data.item.Item;
import exter.tsl.TSLObject;

public abstract class IdData
{
  public final int id;

  protected IdData(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
  }
  
  @Override
  public int hashCode()
  {
    return this.id;
  }

  public boolean equals(Object obj)
  {
    return (obj.getClass() == this.getClass()) && ((Item)obj).id == this.id;
  }

}
