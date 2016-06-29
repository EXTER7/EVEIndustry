package exter.eveindustry.data.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DirectoryFileSystemHandler implements IFileSystemHandler
{
  private final File directory;
  public DirectoryFileSystemHandler(File directory)
  {
    this.directory = directory;
  }
  
  @Override
  public <T> T readFile(String path, IReadHandler<T> handler)
  {
    String p = directory + File.separator + path;
    try
    {
      InputStream stream = new FileInputStream(p);
      try
      {
        T result = handler.readFile(stream);
        stream.close();
        return result;
      } catch(IOException e)
      {
        return null;
      } finally
      {
        stream.close();
      }
    } catch(IOException e1)
    {
      return null;
    }
  }
}
