package exter.eveindustry.data.access;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public abstract class DirectoryData<T>
{
  private class CacheMiss implements Cache.IMissListener<Integer, T>, IFileSystemHandler.IReadHandler<T>
  {
    @Override
    public T onCacheMiss(Integer pid)
    {
      return fs.readFile(directory + "/" + String.valueOf(pid) + ".tsl",this);
    }

    @Override
    public T readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        
        return createObject(new TSLObject(reader));
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }
  }
  
  private final Cache<Integer, T> cache = new Cache<Integer, T>(new CacheMiss());
  
  private final IFileSystemHandler fs;
  private final String directory;

  protected abstract T createObject(TSLObject tsl);

  
  
  public DirectoryData(IFileSystemHandler fs, String directory)
  {
    this.fs = fs;
    this.directory = directory;
  }
  
  public T get(int id)
  {
    return cache.get(id);
  }
}
