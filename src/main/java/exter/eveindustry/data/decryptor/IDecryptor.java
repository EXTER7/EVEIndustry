package exter.eveindustry.data.decryptor;

import exter.eveindustry.data.inventory.IItem;

/**
 * @author exter
 * Decryptor used in invention.
 */
public interface IDecryptor
{
  /**
   * Get the unique ID of the decryptor.
   */
  public int getID();
  
  /**
   * Get the decryptor item.
   */
  public IItem getItem();
  
  /**
   * Get the ME modifier.
   */
  public int getModifierME();

  /**
   * Get the TE modifier.
   */
  public int getModifierTE();

  /**
   * Get the runs modifier.
   */
  public int getModifierRuns();

  /**
   * Get the success chance modifier.
   */
  public double getModifierChance();
}
