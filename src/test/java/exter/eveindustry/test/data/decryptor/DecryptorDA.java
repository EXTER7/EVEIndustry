package exter.eveindustry.test.data.decryptor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class DecryptorDA
{
  static public final Map<Integer, Decryptor> decryptors = new HashMap<Integer, Decryptor>();

  static
  {
    TSLReader tsl = null;
    InputStream raw = null;
    try
    {
      raw = new FileInputStream("blueprint/decryptors.tsl");
      tsl = new TSLReader(raw);

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
          Decryptor d = new Decryptor(new TSLObject(tsl));
          decryptors.put(d.getID(), d);
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