package exter.eveindustry.data.filesystem;

import java.io.IOException;
import java.io.InputStream;

public interface IFileSystemHandler
{
  public interface IReadHandler<T>
  {
    T readFile(InputStream stream) throws IOException;
  }
  
  <T> T readFile(String path,IReadHandler<T> handler);  
}
