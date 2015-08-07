package exter.eveindustry.test.data.inventory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class InventoryDA
{
  static public final Map<Integer, Item> items = new HashMap<Integer, Item>();
  static public final Map<Integer, ItemGroup> groups = new HashMap<Integer, ItemGroup>();
  static public final Map<Integer, ItemCategory> categories = new HashMap<Integer, ItemCategory>();
  static public final Map<Integer, ItemMetaGroup> metagroups = new HashMap<Integer, ItemMetaGroup>();
  static public final Map<Integer, List<Integer>> category_groups = new HashMap<Integer, List<Integer>>();

  static
  {
    try
    {
      InputStream raw = new FileInputStream("inventory.tsl");
      TSLReader tsl = new TSLReader(raw);

      tsl.moveNext();

      while(true)
      {
        tsl.moveNext();
        if(tsl.getState() == TSLReader.State.ENDOBJECT)
        {
          break;
        }
        if(tsl.getState() == TSLReader.State.OBJECT)
        {
          if(tsl.getName().equals("c"))
          {
            TSLObject obj = new TSLObject(tsl);
            categories.put(obj.getStringAsInt("id", -1), new ItemCategory(obj));
          } else if(tsl.getName().equals("g"))
          {
            TSLObject obj = new TSLObject(tsl);
            groups.put(obj.getStringAsInt("id", -1), new ItemGroup(obj));
          } else if(tsl.getName().equals("m"))
          {
            TSLObject obj = new TSLObject(tsl);
            metagroups.put(obj.getStringAsInt("id", -1), new ItemMetaGroup(obj));
          } else if(tsl.getName().equals("i"))
          {
            TSLObject obj = new TSLObject(tsl);
            items.put(obj.getStringAsInt("id", -1), new Item(obj));
          } else
          {
            tsl.skipObject();
          }
        }
      }
      raw.close();
    } catch(InvalidTSLException e)
    {
      throw new RuntimeException(e);
    } catch(IOException e)
    {
      throw new RuntimeException(e);
    }
  }
}
