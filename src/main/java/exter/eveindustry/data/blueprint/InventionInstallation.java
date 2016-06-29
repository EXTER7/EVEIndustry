package exter.eveindustry.data.blueprint;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public class InventionInstallation
{
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(cost);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + id;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    temp = Double.doubleToLongBits(time);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    InventionInstallation other = (InventionInstallation) obj;
    if(Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
      return false;
    if(id != other.id)
      return false;
    if(name == null)
    {
      if(other.name != null)
        return false;
    } else if(!name.equals(other.name))
      return false;
    if(Double.doubleToLongBits(time) != Double.doubleToLongBits(other.time))
      return false;
    return true;
  }

  public final int id;
  public final String name;
  public final double time;
  public final double cost;
  public final boolean relics;
  
  public InventionInstallation(TSLObject tsl)
  {
    name = tsl.getString("name", null);
    id = tsl.getStringAsInt("id", -1);
    time = tsl.getStringAsDouble("time", -1);
    cost = tsl.getStringAsDouble("cost", -1);
    relics = tsl.getStringAsInt("relics", 0) != 0;
  }
  
  static public class Data extends DirectoryData<InventionInstallation>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "blueprint/installation/invention");
    }

    @Override
    protected InventionInstallation createObject(TSLObject tsl)
    {
      return new InventionInstallation(tsl);
    }
  }
}
