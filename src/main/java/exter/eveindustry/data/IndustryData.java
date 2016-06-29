package exter.eveindustry.data;

import java.io.IOException;
import java.io.InputStream;

import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.tsl.InvalidTSLException;
import exter.tsl.TSLObject;
import exter.tsl.TSLReader;

public class IndustryData
{
  public final long build_time;
  public final int skill_industry;
  public final int skill_advancedindustry;
  public final int skill_reprocessing;
  public final int skill_reprocessing_efficiency;
  public final int inst_default;
  public final int inv_inst_default;
  public final int relic_inv_inst_default;
  
  private int getInt(TSLObject tsl,String name)
  {
    int i = tsl.getStringAsInt(name, -1);
    if(i < 0)
    {
      throw new RuntimeException("Missing industry data value: " + name);
    }
    return i;
  }

  private long getLong(TSLObject tsl,String name)
  {
    long i = tsl.getStringAsInt(name, -1);
    if(i < 0)
    {
      throw new RuntimeException("Missing industry data value: " + name);
    }
    return i;
  }

  IndustryData(TSLObject tsl)
  {
    build_time = getLong(tsl,"build_time");
    skill_industry = getInt(tsl,"skill_industry");
    skill_advancedindustry = getInt(tsl,"skill_advancedindustry");
    skill_reprocessing = getInt(tsl,"skill_reprocessing");
    skill_reprocessing_efficiency = getInt(tsl,"skill_reprocessing_efficiency");
    inst_default = getInt(tsl,"inst_default");
    inv_inst_default = getInt(tsl,"inv_inst_default");
    relic_inv_inst_default = getInt(tsl,"relic_inv_inst_default");
  }

  static public class Reader implements IFileSystemHandler.IReadHandler<IndustryData>
  {
    @Override
    public IndustryData readFile(InputStream stream) throws IOException
    {
      try
      {
        TSLReader reader = new TSLReader(stream);
        reader.moveNext();
        
        return new IndustryData(new TSLObject(reader));
      } catch(InvalidTSLException e)
      {
        return null;
      }
    }    
  }
}
