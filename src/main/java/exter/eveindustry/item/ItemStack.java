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
  public final IItem item;
  
  /**
   * Amount of units of the item the stack.
   */
  public final long amount;
  
  /**
   * @param aItem Item of the stack.
   * @param aAmount Amount of units in the stack.
   * @throws IllegalArgumentException If the item is null or the amount is < 1.
   */
  public ItemStack(IItem aItem,long aAmount)
  {
    if(aItem == null)
    {
      throw new IllegalArgumentException("Item is null.");
    }
    if(aAmount <= 0)
    {
      throw new IllegalArgumentException("Amount must be a positive integer.");
    }
    item = aItem;
    amount = aAmount; 
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (amount ^ (amount >>> 32));
    result = prime * result + ((item == null) ? 0 : item.getID());
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
    if(item == null)
    {
      if(other.item != null)
      {
        return false;
      }
    } else if(item.getID() != (other.item.getID()))
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
    return new ItemStack(item,amount * scale);
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
    return new ItemStack(item,amount * scale);
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
      scaled = 1;
    }
    return new ItemStack(item,scaled);
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
    return new ItemStack(item,(long)Math.ceil((amount * scale)));
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
    return new ItemStack(item,scaled);
  }
}
