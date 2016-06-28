package exter.eveindustry.market;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import exter.eveindustry.data.IEVEDataProvider;
import exter.eveindustry.util.Utils;
import exter.tsl.TSLObject;

public final class Market
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
  
 
  static private final BigDecimal HOUNDRED = new BigDecimal("100");

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

  public Market(int system, Order order, BigDecimal manual, BigDecimal broker,BigDecimal transaction)
  {
    this.system = system;
    this.order = order;
    this.manual = Utils.clamp(manual,BigDecimal.ZERO,null);
    this.broker = Utils.clamp(broker,BigDecimal.ZERO,HOUNDRED);
    this.transaction = Utils.clamp(transaction,BigDecimal.ZERO,HOUNDRED);
  }

  public Market(Market p)
  {
    system = p.system;
    order = p.order;
    manual = p.manual;
    broker = p.broker;
    transaction = p.transaction;
  }

  public Market(TSLObject tsl,IEVEDataProvider provider)
  {
    system = tsl.getStringAsInt("system", provider.getDefaultSolarSystem());
    order = Order.fromInt(tsl.getStringAsInt("order", tsl.getStringAsInt("source", Order.SELL.value)));
    manual = Utils.clamp(tsl.getStringAsBigDecimal("manual", BigDecimal.ZERO),BigDecimal.ZERO,null);
    broker = Utils.clamp(tsl.getStringAsBigDecimal("broker", provider.getDefaultBrokerFee()),BigDecimal.ZERO,HOUNDRED);
    transaction = Utils.clamp(tsl.getStringAsBigDecimal("transaction", provider.getDefaultTransactionTax()),BigDecimal.ZERO,HOUNDRED);
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
