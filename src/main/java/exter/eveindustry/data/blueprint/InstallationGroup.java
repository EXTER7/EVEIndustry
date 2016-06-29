package exter.eveindustry.data.blueprint;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public class InstallationGroup
{
  public final int id;
  public final int group_id;
  public final int installation_id;
  public final double time;
  public final double material;
  public final double cost;
  
  public InstallationGroup(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    group_id = tsl.getStringAsInt("group", -1);
    installation_id = tsl.getStringAsInt("installation", -1);
    time = tsl.getStringAsDouble("time", -1);
    material = tsl.getStringAsDouble("material", -1);
    cost = tsl.getStringAsDouble("cost", -1);
  }

  @Override
  public int hashCode()
  {
    return group_id * 16553 + installation_id;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(this == obj)
    {
      return true;
    }
    if(getClass() != obj.getClass())
    {
      return false;
    }
    InstallationGroup other = (InstallationGroup) obj;
    if(group_id != other.group_id || installation_id != other.installation_id)
    {
      return false;
    }
    return true;
  }
  
  static public class Data extends DirectoryData<InstallationGroup>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "blueprint/installation/group");
    }

    @Override
    protected InstallationGroup createObject(TSLObject tsl)
    {
      return new InstallationGroup(tsl);
    }
  }
}
