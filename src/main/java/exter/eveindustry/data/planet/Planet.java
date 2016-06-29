package exter.eveindustry.data.planet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exter.eveindustry.data.access.FileData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.tsl.TSLObject;

public class Planet
{
  public final List<Item> resources;
  public final String type_name;
  public final int id;
  public final boolean advanced;
  
  Planet(TSLObject tsl,Item.Data inventory)
  {
    id = tsl.getStringAsInt("id",-1);
    type_name = tsl.getString("name",null);
    advanced = (tsl.getStringAsInt("advanced",0) != 0);
    
    List<Item> res = new ArrayList<Item>();
    for(Integer id:tsl.getStringAsIntegerList("resource"))
    {
      res.add(inventory.get(id));
    }
    resources = Collections.unmodifiableList(res);
  }
  
  static public class Data extends FileData<Planet>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "planet/planets.tsl");
      this.items = items;
    }

    @Override
    protected Planet createObject(TSLObject tsl)
    {
      return new Planet(tsl,items);
    }

    @Override
    protected int getID(Planet obj)
    {
      return obj.id;
    }
  }
}
