package exter.eveindustry.data.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public abstract class FileData<T>
{
  private class Reader implements IFileSystemHandler.IReadHandler<Map<Integer,T>>
  {
    @Override
    public Map<Integer, T> readFile(InputStream stream) throws IOException
    {
      Map<Integer, T> map = new HashMap<Integer, T>();
      try
      {
        TSLReader tsl = new TSLReader(stream);
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
            T obj = createObject(new TSLObject(tsl));
            map.put(getID(obj), obj);
          }
        }
        return map;
      } catch(InvalidTSLException e)
      {
        throw new RuntimeException(e);
      }
    }
  }
  private Map<Integer, T> data = null;
  private final IFileSystemHandler fs;
  private final String file;

  protected abstract T createObject(TSLObject tsl);
  protected abstract int getID(T obj);

  public FileData(IFileSystemHandler fs,String file)
  {
    this.fs = fs;
    this.file = file;
  }
  
  public T get(int id)
  {
    if(data == null)
    {
      data = fs.readFile(file, new Reader());
    }
    return data.get(id);
  }
}
