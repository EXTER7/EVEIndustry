package exter.eveindustry.test.data.starmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class Starmap
{

  static public final Map<Integer, SolarSystem> systems = new HashMap<Integer, SolarSystem>();

  static
  {
    try
    {
      InputStream raw = new FileInputStream("starmap.tsl");
      TSLReader reader = new TSLReader(raw);

      reader.moveNext();

      while(true)
      {
        reader.moveNext();
        TSLReader.State type = reader.getState();
        if(type == TSLReader.State.ENDOBJECT)
        {
          break;
        } else if(type == TSLReader.State.OBJECT)
        {
          String node_name = reader.getName();
          if(node_name.equals("s"))
          {
            TSLObject obj = new TSLObject(reader);
            systems.put(obj.getStringAsInt("id", -1), new SolarSystem(obj));
          } else
          {
            reader.skipObject();
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
