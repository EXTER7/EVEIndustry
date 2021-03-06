package exter.eveindustry.data.decryptor;

import exter.eveindustry.data.IdData;
import exter.eveindustry.data.access.FileData;
import exter.eveindustry.data.filesystem.IFileSystemHandler;
import exter.eveindustry.data.item.Item;
import exter.tsl.TSLObject;

public final class Decryptor extends IdData
{
  public final Item item;
  public final int me;
  public final int te;
  public final int runs;
  public final double chance;

  public Decryptor(TSLObject tsl,Item.Data inventory)
  {
    super(tsl);
    item = inventory.get(this.id);
    me = tsl.getStringAsInt("me",0);
    te = tsl.getStringAsInt("te",0);
    runs = tsl.getStringAsInt("runs",0);
    chance = tsl.getStringAsDouble("chance",1);
  }
  
  static public class Data extends FileData<Decryptor>
  {
    private final Item.Data items;
    
    public Data(IFileSystemHandler fs,Item.Data items)
    {
      super(fs, "blueprint/decryptors.tsl");
      this.items = items;
    }

    @Override
    protected Decryptor createObject(TSLObject tsl)
    {
      return new Decryptor(tsl,items);
    }
  }
}
