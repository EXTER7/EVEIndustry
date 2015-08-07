package exter.eveindustry.test.data.starbase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class StarbaseTowerDA
{

  static public final Map<Integer, StarbaseTower> towers = new HashMap<Integer, StarbaseTower>();

  static
  {
    try
    {
      InputStream raw = new FileInputStream("starbases.tsl");
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
          StarbaseTower t = new StarbaseTower(new TSLObject(tsl));
          towers.put(t.TowerItem.ID, t);
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
