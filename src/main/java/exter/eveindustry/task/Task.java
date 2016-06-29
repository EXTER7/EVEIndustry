package exter.eveindustry.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.market.Market;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

/**
 * @author exter
 * Base class for all industry tasks
 * Before creating any instance of this class,
 *  Task.setDataProvider() must be called with an IEVEDataProvider implementation.
 */
public abstract class Task
{
  /**
   * @author exter
   * Determines which market a material is bought/sold.
   */

  /**
   * @author exter
   * Listener for task side-effects. 
   */
  public interface ITaskListener
  {
    /**
     * Called when a change in the task parameter causes material set to change.
     * @param task The task that sent the event.
     */
    public void onMaterialSetChanged(Task task);

    /**
     * Called when a change in the task parameter causes a change in other parameters
     * @param task The task that sent the event.
     * @param parameter the parameter that that changed (task class specific).
     */
    public void onParameterChanged(Task task,int parameter);
  }
  
  public enum MarketAction
  {
    BUY,
    SELL,
    UNKNOWN
  }

  private List<ITaskListener> task_listeners;
  private Map<Integer, Market> material_markets;

  private List<ItemStack> required_materials;
  private List<ItemStack> produced_materials;
  

  
  
  protected final TaskFactory factory;
  
  static private final BigDecimal PERCENT = new BigDecimal("0.01");

  /**
   * Called when updating materials
   * Get raw produced materials by the task.
   * The result from this method is then condensed with the required materials
   * merging duplicate items, and canceling out intermediate materials.
   */
  protected abstract List<ItemStack> getRawProducedMaterials();
  
  /**
   * Called when updating materials
   * Get raw required materials for the task.
   * The result from this method is then condensed with the produced materials
   * merging duplicate items, and canceling out intermediate materials.
   */
  protected abstract List<ItemStack> getRawRequiredMaterials();

  /**
   * Get the amount of time the task takes to complete in seconds.
   */
  public abstract int getDuration();


  /**
   * Get a list of all produced items by the task,
   */
  public final List<ItemStack> getProducedMaterials()
  {
    return Collections.unmodifiableList(produced_materials);
  }

  /**
   * Get a list of all required items for the task,
   */
  public final List<ItemStack> getRequiredMaterials()
  {
    return Collections.unmodifiableList(required_materials);
  }


  /**
   * Called when a change in an attribute in the task
   *  causes the material set to change.
   */
  protected final void updateMaterials()
  {

    produced_materials = new ArrayList<ItemStack>();
    required_materials = new ArrayList<ItemStack>();
    
    // Condense both material lists, merging duplicate items,
    // and canceling out intermediate materials.
    Map<Integer,Long> materials = new HashMap<Integer,Long>();
    for(ItemStack mat:getRawProducedMaterials())
    {
      int id = mat.item_id.getID();
      long amount = Utils.mapGet(materials, id, 0L);
      materials.put(id, amount + mat.amount);
    }
    for(ItemStack mat:getRawRequiredMaterials())
    {
      int id = mat.item_id.getID();
      long amount = Utils.mapGet(materials, id, 0L);
      materials.put(id, amount - mat.amount);
    }
    
    // Separate required materials from produced materials.
    for(Map.Entry<Integer, Long> mat:materials.entrySet())
    {
      int id = mat.getKey();
      long amount = mat.getValue();
      if(amount > 0)
      {
        produced_materials.add(new ItemStack(factory.static_data.getItem(id), amount));
        if(!material_markets.containsKey(id))
        {
          material_markets.put(id, factory.dynamic_data.getDefaultProducedMarket());
        }
      }
      if(amount < 0)
      {
        required_materials.add(new ItemStack(factory.static_data.getItem(id),-amount));
        if(!material_markets.containsKey(id))
        {
          material_markets.put(id, factory.dynamic_data.getDefaultRequiredMarket());
        }
      }
    }
    
    // Notify all listeners.
    synchronized(task_listeners)
    {
      for(ITaskListener listener : task_listeners)
      {
        listener.onMaterialSetChanged(this);
      }
    }
  }

  
  protected final void notifyParameterChange(int param)
  {
    // Notify all listeners.
    synchronized(task_listeners)
    {
      for(ITaskListener listener : task_listeners)
      {
        listener.onParameterChanged(this, param);
      }
    }
  }
  
  /**
   * Set the market of a material.
   * @param item The material item.
   * @param market The market to change.
   */
  public final void setMaterialMarket(IItem item, Market market)
  {
    if(market != null)
    {
      material_markets.put(item.getID(), market);
    }
  }

  Task(TaskFactory factory)
  {
    this.factory = factory;
    task_listeners = new ArrayList<ITaskListener>();
    material_markets = new HashMap<Integer, Market>();
  }

  Task(TaskFactory factory,TSLObject tsl) throws TaskLoadException
  {
    this(factory);
    for(TSLObject tsl_market : tsl.getObjectList("market"))
    {
      IItem i = factory.static_data.getItem(tsl_market.getStringAsInt("item", -1));
      if(i != null)
      {
        setMaterialMarket(i, new Market(tsl_market,factory.dynamic_data));
      }
    }
  }

  /**
   * Load a task from a TSL Object.
   * @param tsl The TSL Object to load the task from.
   * @throws TaskLoadException If there was a fatal error loading the task.
   */


  /**
   * Get non-material extra expense (e.g. taxes, manufacturing cost, etc).
   */
  public BigDecimal getExtraExpense()
  {
    return BigDecimal.ZERO;
  }

  /**
   * Get the total ISK income from of task
   */
  public final BigDecimal getIncome()
  {
    BigDecimal sum = BigDecimal.ZERO;
    for(ItemStack m : getProducedMaterials())
    {
      sum = sum.add(getMaterialMarketPrice(m,MarketAction.SELL));
    }
    return sum;
  }

  /**
   * Get the total ISK expense from this task
   * @return The ISK cost of all material + extra expense.
   */
  public final BigDecimal getExpense()
  {
    BigDecimal sum = getExtraExpense();
    for(ItemStack m : getRequiredMaterials())
    {
      sum = sum.add(getMaterialMarketPrice(m,MarketAction.BUY));
    }
    return sum;
  }

  /**
   * Write the task to a TSL Object
   * @param tsl TSL Object to write the task.
   */
  public void writeToTSL(TSLObject tsl)
  {
    String type = TaskFactory.getTaskName(getClass());
    if(type == null)
    {
      throw new RuntimeException("Invalid task type");
    }
    tsl.putString("type", type);
    for(Map.Entry<Integer, Market> e : material_markets.entrySet())
    {
      TSLObject tsl_price = new TSLObject();
      e.getValue().writeToTSL(tsl_price);
      tsl_price.putString("item", String.valueOf(e.getKey()));
      tsl.putObject("market", tsl_price);
    }
  }


  public final void registerListener(ITaskListener listener)
  {
    if(listener != null)
    {
      synchronized(task_listeners)
      {
        if(!task_listeners.contains(listener))
        {
          task_listeners.add(listener);
        }
      }
    }
  }

  public final void unregisterListener(ITaskListener listener)
  {
    if(listener != null)
    {
      synchronized(task_listeners)
      {
        task_listeners.remove(listener);
      }
    }
  }

  /**
   * Get the market of a material.
   * @param item The material item to look up.
   */
  public final Market getMaterialMarket(IItem item)
  {
    return material_markets.get(item.getID());
  }

  /**
   * Get the market price of a material stack.
   * @param item the material stack to lookup the price.
   * @return The price/unit of the material multiplied by the stack amount.
   */
  public final BigDecimal getMaterialMarketPrice(ItemStack item)
  {
    return getMaterialMarketPrice(item,MarketAction.UNKNOWN);
  }

  /**
   * Get the market price of a material stack, including broker fees and tax.
   * @param item the material stack to lookup the price.
   * @param action the type of transaction being done.
   * @return The price/unit of the material multiplied by the stack amount.
   */
  public final BigDecimal getMaterialMarketPrice(ItemStack item, MarketAction action)
  {
    return getMaterialMarketPrice(item.item_id,action).multiply(new BigDecimal(item.amount));
  }

  /**
   * Get the market of a material item.
   * @param item the material stack to lookup the price.
   */
  public final BigDecimal getMaterialMarketPrice(IItem item)
  {
    return getMaterialMarketPrice(item,MarketAction.UNKNOWN);
  }
  
  /**
   * Get the market of a material item, including broker fees and tax.
   * @param item the material stack to lookup the price.
   * @param action the type of transaction being done.
   */
  public final BigDecimal getMaterialMarketPrice(IItem item, MarketAction action)
  {
    Market market = getMaterialMarket(item);
    if(market == null)
    {
      return BigDecimal.ZERO;
    }
    BigDecimal price = factory.dynamic_data.getMarketPrice(item, market);
    BigDecimal tax = price.multiply(market.transaction.multiply(PERCENT));
    BigDecimal broker = price.multiply(market.broker.multiply(PERCENT));
    switch(action)
    {
      case BUY:
        if(market.order == Market.Order.BUY)
        {
          price = price.add(broker);
        }
        break;
      case SELL:
        price = price.subtract(tax);
        if(market.order == Market.Order.SELL)
        {
          price = price.subtract(broker);
        }
        if(price.signum() < 0)
        {
          price = BigDecimal.ZERO;
        }
        break;
      default:
    }
    return price;
  }

  public final BigDecimal getMaterialBrokerFee(IItem item)
  {
    Market market = getMaterialMarket(item);
    if(market == null)
    {
      return BigDecimal.ZERO;
    }
    return factory.dynamic_data.getMarketPrice(item, market).multiply(market.broker.multiply(PERCENT));
  }

  public final BigDecimal getMaterialTransactionTax(IItem item)
  {
    Market market = getMaterialMarket(item);
    if(market == null)
    {
      return BigDecimal.ZERO;
    }
    return factory.dynamic_data.getMarketPrice(item, market).multiply(market.transaction.multiply(PERCENT));
  }
}
