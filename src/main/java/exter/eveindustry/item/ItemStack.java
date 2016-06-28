package exter.eveindustry.item;

import exter.eveindustry.data.inventory.IItem;

/**
 * @author exter
 * A stack of an item with a specified amount.
 */
public final class ItemStack
{
  /**
   * Item of the stack
   */
  public final IItem item_id;
  
  /**
   * Amount of units of the item the stack.
   */
  public final long amount;
  
  /**
   * @param aItem Item of the stack.
   * @param aAmount Amount of units in the stack.
   * @throws IllegalArgumentException If the item is null or the amount is < 1.
   */
  public ItemStack(IItem item,long amount)
  {
    if(item == null)
    {
      throw new IllegalArgumentException("Item is null.");
    }
    if(amount <= 0)
    {
      throw new IllegalArgumentException("Amount must be a positive integer.");
    }
    this.item_id = item;
    this.amount = amount; 
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (amount ^ (amount >>> 32));
    result = prime * result + ((item_id == null) ? 0 : item_id.getID());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(this == obj)
    {
      return true;
    }
    if(obj == null)
    {
      return false;
    }
    if(getClass() != obj.getClass())
    {
      return false;
    }
    ItemStack other = (ItemStack) obj;
    if(amount != other.amount)
    {
      return false;
    }
    if(item_id == null)
    {
      if(other.item_id != null)
      {
        return false;
      }
    } else if(item_id.getID() != (other.item_id.getID()))
    {
      return false;
    }
    return true;
  }

  /**
   * Return a new stack with the amount multiplied (does not modify this stack).
   * @param scale Amount to scale the stack
   * @return The stack with the scale applied.
   * @throws IllegalArgumentException if the scale < 1.
   */
  public ItemStack scaled(int scale)
  {
    if(scale <= 0)
    {
      throw new IllegalArgumentException("scale must be a positive integer.");
    }
    return new ItemStack(item_id,amount * scale);
  }

  /**
   * Return a new stack with the amount multiplied (does not modify this stack).
   * @param scale Amount to scale the stack
   * @return The stack with the scale applied.
   * @throws IllegalArgumentException if the scale < 1.
   */
  public ItemStack scaled(long scale)
  {
    if(scale <= 0)
    {
      throw new IllegalArgumentException("scale must be a positive integer.");
    }
    return new ItemStack(item_id,amount * scale);
  }

  /**
   * Return a new stack with the amount multiplied and rounded down (does not modify this stack).
   * @param scale Amount to scale the stack
   * @return The stack with the scale applied, or null if the result amount would be 0.
   * @throws IllegalArgumentException if the scale < 1.
   */
  public ItemStack scaledFloor(double scale)
  {
    if(scale <= 0)
    {
      throw new IllegalArgumentException("scale must be a positive integer.");
    }
    long scaled = (long)(amount * scale);
    if(scaled == 0)
    {
      return null;
    }
    return new ItemStack(item_id,scaled);
  }

  /**
   * Return a new stack with the amount multiplied and rounded up (does not modify this stack).
   * @param scale Amount to scale the stack
   * @return The stack with the scale applied.
   * @throws IllegalArgumentException if the scale <= 0.
   */
  public ItemStack scaledCeil(double scale)
  {
    if(scale <= 0)
    {
      throw new IllegalArgumentException("scale must be a positive integer.");
    }
    return new ItemStack(item_id,(long)Math.ceil((amount * scale)));
  }

  /**
   * Return a new stack with the amount multiplied and rounded to the nearest integer (does not modify this stack).
   * @param scale Amount to scale the stack
   * @return The stack with the scale applied, or null if the result amount would be 0.
   * @throws IllegalArgumentException if the scale <= 0.
   */
  public ItemStack scaledRounded(double scale)
  {
    if(scale <= 0)
    {
      throw new IllegalArgumentException("scale must be a positive integer.");
    }
    long scaled = (long)Math.round((amount * scale));
    if(scaled == 0)
    {
      return null;
    }
    return new ItemStack(item_id,scaled);
  }
}
