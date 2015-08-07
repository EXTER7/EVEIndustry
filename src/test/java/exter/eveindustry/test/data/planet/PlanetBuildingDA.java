package exter.eveindustry.test.data.planet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class PlanetBuildingDA
{

  static public final Cache<Integer, PlanetBuilding> buildings = new Cache<Integer, PlanetBuilding>(new ProductCacheMiss());

  static private class ProductCacheMiss implements Cache.IMissListener<Integer, PlanetBuilding>
  {
    @Override
    public PlanetBuilding onCacheMiss(Integer pid)
    {
      try
      {
        InputStream raw = new FileInputStream("planet/" + String.valueOf(pid) + ".tsl");
        TSLReader reader = new TSLReader(raw);
        reader.moveNext();
        if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("planetbuilding"))
        {
          raw.close();
          return new PlanetBuilding(new TSLObject(reader));
        } else
        {
          raw.close();
          return null;
        }
      } catch(InvalidTSLException e)
      {
        return null;
      } catch(IOException e)
      {
        return null;
      }
    }
  }
}
