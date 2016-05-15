package exter.eveindustry.task;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.data.inventory.IItem;
import exter.eveindustry.item.ItemStack;
import exter.eveindustry.task.Task.Market.MarketAction;
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
  static public final class Market
  {
    @Override
    public int hashCode()
    {
      final int prime = 3389;
      int result = 1;
      result = prime * result + ((broker == null) ? 0 : broker.hashCode());
      result = prime * result + ((manual == null) ? 0 : manual.hashCode());
      result = prime * result + ((order == null) ? 0 : order.hashCode());
      result = prime * result + system;
      result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if(this == obj)
        return true;
      if(obj == null)
        return false;
      if(getClass() != obj.getClass())
        return false;
      Market other = (Market) obj;
      if(broker == null)
      {
        if(other.broker != null)
          return false;
      } else if(!broker.equals(other.broker))
        return false;
      if(manual == null)
      {
        if(other.manual != null)
          return false;
      } else if(!manual.equals(other.manual))
        return false;
      if(order != other.order)
        return false;
      if(system != other.system)
        return false;
      if(transaction == null)
      {
        if(other.transaction != null)
          return false;
      } else if(!transaction.equals(other.transaction))
        return false;
      return true;
    }

    /**
     * @author exter
     * Market order type (sell orders, buy orders, manually priced)
     */
    public enum Order
    {
      // Market sell orders
      SELL(0),
      
      // Market buy orders
      BUY(1),

      // Manual price
      MANUAL(2);

      public final int value;

      Order(int v)
      {
        value = v;
      }

      static private Map<Integer, Order> intmap;

      static public Order fromInt(int i)
      {
        if(intmap == null)
        {
          intmap = new HashMap<Integer, Order>();
          for(Order v : values())
          {
            intmap.put(v.value, v);
          }
        }
        return intmap.get(i);
      }
    }
    
    public enum MarketAction
    {
      BUY,
      SELL,
      UNKOWN
    }

    // ID of the solar system of the market.
    public final int system;

    // Order type.
    public final Order order;
    
    // Manual price used in the case of Order.MANUAL
    public final BigDecimal manual;
    
    // Broker fees.
    public final BigDecimal broker;

    // Transaction tax.
    public final BigDecimal transaction;

    public Market(int system, Order order, BigDecimal manual, double broker,double transaction)
    {
      this.system = system;
      this.order = order;
      this.manual = manual;
      this.broker = new BigDecimal(broker / 100);
      this.transaction = new BigDecimal(transaction/ 100);
    }

    public Market(int system, Order order)
    {
      this(system,order,BigDecimal.ZERO,0.03,0.02);
    }

    public Market(Market p)
    {
      system = p.system;
      order = p.order;
      manual = p.manual;
      broker = p.broker;
      transaction = p.transaction;
    }

    public Market()
    {
      this(getDataProvider().getDefaultSolarSystem(),Order.SELL);
    }

    public Market(TSLObject tsl)
    {
      system = tsl.getStringAsInt("system", getDataProvider().getDefaultSolarSystem());
      order = Order.fromInt(tsl.getStringAsInt("order", tsl.getStringAsInt("source", Order.SELL.value)));
      manual = Utils.clamp(tsl.getStringAsBigDecimal("manual", BigDecimal.ZERO),BigDecimal.ZERO,null);
      broker = Utils.clamp(tsl.getStringAsBigDecimal("broker", new BigDecimal(0.03)),BigDecimal.ZERO,BigDecimal.ONE);
      transaction = Utils.clamp(tsl.getStringAsBigDecimal("transaction", new BigDecimal(0.02)),BigDecimal.ZERO,BigDecimal.ONE);
    }

    public void writeToTSL(TSLObject tsl)
    {
      tsl.putString("system", system);
      tsl.putString("order", order.value);
      tsl.putString("manual", manual);
      tsl.putString("broker", broker);
      tsl.putString("transaction", transaction);
    }

  }

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


  private List<ITaskListener> task_listeners;
  private Map<Integer, Market> material_markets;

  private List<ItemStack> required_materials;
  private List<ItemStack> produced_materials;
  
  // Used for loading/saving from/to TSL Objects.
  static private Map<String, Class<? extends Task>> task_types;
  static private Map<Class<? extends Task>, String> task_names;
  
  
  static private IEVEDataProvider provider = null;
  
  /**
   * Called when the task is being loaded from a TSL object.
   * @param tsl TSL Object to load data from.
   * @throws TaskLoadException When the task contains an invalid attribute
   *  which invalidates the task (e.g. non-existent blueprints).
   */
  protected abstract void onLoadDataFromTSL(TSLObject tsl) throws TaskLoadException;

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
    IEVEDataProvider data = getDataProvider();

    produced_materials = new ArrayList<ItemStack>();
    required_materials = new ArrayList<ItemStack>();
    
    // Condense both material lists, merging duplicate items,
    // and canceling out intermediate materials.
    Map<Integer,Long> materials = new HashMap<Integer,Long>();
    for(ItemStack mat:getRawProducedMaterials())
    {
      int id = mat.item.getID();
      long amount = Utils.mapGet(materials, id, 0L);
      materials.put(id, amount + mat.amount);
    }
    for(ItemStack mat:getRawRequiredMaterials())
    {
      int id = mat.item.getID();
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
        produced_materials.add(new ItemStack(data.getItem(id), amount));
        if(!material_markets.containsKey(id))
        {
          material_markets.put(id, getDataProvider().getDefaultProducedMarket());
        }
      }
      if(amount < 0)
      {
        required_materials.add(new ItemStack(data.getItem(id),-amount));
        if(!material_markets.containsKey(id))
        {
          material_markets.put(id, getDataProvider().getDefaultRequiredMarket());
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

  protected Task()
  {
    if(provider == null)
    {
      throw new IllegalStateException("EVE Data provider not set.");
    }
    task_listeners = new ArrayList<ITaskListener>();
    material_markets = new HashMap<Integer, Market>();
  }


  /**
   * Load a task from a TSL Object.
   * @param tsl The TSL Object to load the task from.
   * @throws TaskLoadException If there was a fatal error loading the task.
   */
  static public final Task loadPromTSL(TSLObject tsl) throws TaskLoadException
  {
    Task task = null;
    try
    {
      String type = tsl.getString("type", null);
      Class<? extends Task> clazz = task_types.get(type);
      if(clazz == null)
      {
        throw new TaskLoadException();
      }
      IEVEDataProvider data = getDataProvider();
      task = clazz.getDeclaredConstructor().newInstance();
      for(TSLObject tsl_market : tsl.getObjectList("market"))
      {
        IItem i = data.getItem(tsl_market.getStringAsInt("item", -1));
        if(i != null)
        {
          task.setMaterialMarket(i, new Market(tsl_market));
        }
      }
      task.onLoadDataFromTSL(tsl);
      task.updateMaterials();
    } catch(InstantiationException e)
    {
      throw new TaskLoadException();
    } catch(IllegalAccessException e)
    {
      throw new TaskLoadException();
    } catch(InvocationTargetException e)
    {
      throw new TaskLoadException();
    } catch(NoSuchMethodException e)
    {
      throw new TaskLoadException();
    }
    return task;
  }

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
    tsl.putString("type", task_names.get(getClass()));
    for(Map.Entry<Integer, Market> e : material_markets.entrySet())
    {
      TSLObject tsl_price = new TSLObject();
      e.getValue().writeToTSL(tsl_price);
      tsl_price.putString("item", String.valueOf(e.getKey()));
      tsl.putObject("market", tsl_price);
    }
  }

  static private void registerTaskType(String name, Class<? extends Task> type)
  {
    task_types.put(name, type);
    task_names.put(type, name);
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
    return getMaterialMarketPrice(item,MarketAction.UNKOWN);
  }

  /**
   * Get the market price of a material stack, including broker fees and tax.
   * @param item the material stack to lookup the price.
   * @param action the type of transaction being done.
   * @return The price/unit of the material multiplied by the stack amount.
   */
  public final BigDecimal getMaterialMarketPrice(ItemStack item, MarketAction action)
  {
    return getMaterialMarketPrice(item.item,action).multiply(new BigDecimal(item.amount));
  }

  /**
   * Get the market of a material item.
   * @param item the material stack to lookup the price.
   */
  public final BigDecimal getMaterialMarketPrice(IItem item)
  {
    return getMaterialMarketPrice(item,MarketAction.UNKOWN);
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
    BigDecimal price = getDataProvider().getMarketPrice(item, market);
    BigDecimal tax = price.multiply(market.transaction);
    BigDecimal broker = price.multiply(market.broker);
    switch(action)
    {
      case BUY:
        if(market.order == Market.Order.BUY)
        {
          price.add(broker);
        }
        break;
      case SELL:
        price.subtract(tax);
        if(market.order == Market.Order.SELL)
        {
          price.subtract(broker);
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
    return getDataProvider().getMarketPrice(item, market).multiply(market.broker);
  }

  public final BigDecimal getMaterialTransactionTax(IItem item)
  {
    Market market = getMaterialMarket(item);
    if(market == null)
    {
      return BigDecimal.ZERO;
    }
    return getDataProvider().getMarketPrice(item, market).multiply(market.transaction);
  }

  /**
   * Get the current data provider.
   */
  static public final IEVEDataProvider getDataProvider()
  {
    return provider;
  }

  /**
   * Set the data provider.
   * This method must be called before creating any Task instance.
   * @param prov Data provider implementation.
   */
  static public final void setDataProvider(IEVEDataProvider prov)
  {
    provider = prov;
  }

  static
  {
    task_types = new HashMap<String, Class<? extends Task>>();
    task_names = new HashMap<Class<? extends Task>, String>();
    registerTaskType("manufacturing", ManufacturingTask.class);
    registerTaskType("refining", RefiningTask.class);
    registerTaskType("reaction", ReactionTask.class);
    registerTaskType("planet", PlanetTask.class);
    registerTaskType("group", GroupTask.class);
  }
}
