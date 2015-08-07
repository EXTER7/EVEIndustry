package exter.eveindustry.test.data.planet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetDA
{

  static public final Map<Integer, Planet> planets = new HashMap<Integer, Planet>();

  static
  {
      try
      {
        InputStream raw = new FileInputStream("planet/planets.tsl");

        TSLReader tsl = new TSLReader(raw);
        tsl.moveNext();

        while(true)
        {
          tsl.moveNext();
          TSLReader.State type = tsl.getState();
          if(type == TSLReader.State.ENDOBJECT)
          {
            break;
          }

          if(type == TSLReader.State.OBJECT)
          {
            Planet p = new Planet(new TSLObject(tsl));
            planets.put(p.ID, p);
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
