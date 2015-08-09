package exter.eveindustry.test.data.refine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class RefinableDA
{
  static public final Cache<Integer, Refinable> refinables = new Cache<Integer, Refinable>(new RefineCacheMiss());

  static private class RefineCacheMiss implements Cache.IMissListener<Integer, Refinable>
  {
    @Override
    public Refinable onCacheMiss(Integer refine)
    {
      try
      {
        InputStream raw = new FileInputStream("refine/" + String.valueOf(refine) + ".tsl");
        TSLReader reader = new TSLReader(raw);
        reader.moveNext();
        if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("refine"))
        {
          raw.close();
          return new Refinable(new TSLObject(reader));
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
