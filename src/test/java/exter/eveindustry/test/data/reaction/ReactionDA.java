package exter.eveindustry.test.data.reaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.test.data.cache.Cache;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class ReactionDA
{
  static public final Cache<Integer, Reaction> reactions = new Cache<Integer, Reaction>(new ReactionCacheMiss());

  static private class ReactionCacheMiss implements Cache.IMissListener<Integer, Reaction>
  {
    @Override
    public Reaction onCacheMiss(Integer rid)
    {
      try
      {
        InputStream raw = new FileInputStream("reaction/" + String.valueOf(rid) + ".tsl");

        TSLReader reader = new TSLReader(raw);
        reader.moveNext();
        if(reader.getState() == TSLReader.State.OBJECT && reader.getName().equals("reaction"))
        {
          raw.close();
          return new Reaction(new TSLObject(reader));
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
