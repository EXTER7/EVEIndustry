package exter.eveindustry.data.blueprint;

import exter.eveindustry.data.access.DirectoryData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.TSLObject;

public class Installation
{
  public final int id;
  public final String name;
  
  
  Installation(TSLObject tsl)
  {
    id = tsl.getStringAsInt("id", -1);
    name = tsl.getString("name", null);
  }

  @Override
  public int hashCode()
  {
    return id;
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
    Installation other = (Installation) obj;
    if(id != other.id)
    {
      return false;
    }
    return true;
  }
  
  static public class Data extends DirectoryData<Installation>
  {
    public Data(IFileSystemHandler fs)
    {
      super(fs, "blueprint/installation");
    }

    @Override
    protected Installation createObject(TSLObject tsl)
    {
      return new Installation(tsl);
    }
  }

}
