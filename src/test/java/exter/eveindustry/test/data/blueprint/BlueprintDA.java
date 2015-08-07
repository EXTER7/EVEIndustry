package exter.eveindustry.test.data.blueprint;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class BlueprintDA
{
  static public final Cache<Integer, Blueprint> blueprints = new Cache<Integer, Blueprint>(new BlueprintMissListener());

  static private class BlueprintMissListener implements Cache.IMissListener<Integer, Blueprint>
  {
    @Override
    public Blueprint onCacheMiss(Integer key)
    {
      Blueprint bp = null;
      try
      {
        InputStream raw = new FileInputStream("blueprint/" + String.valueOf(key) + ".tsl");
        try
        {
          TSLReader reader = new TSLReader(raw);
          reader.moveNext();
          if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("blueprint"))
          {
            bp = new Blueprint(new TSLObject(reader));
          }
        } catch(InvalidTSLException e)
        {
          raw.close();
          return null;
        }
      } catch(IOException e)
      {
        return null;
      }
      return bp;
    }
  }
}
