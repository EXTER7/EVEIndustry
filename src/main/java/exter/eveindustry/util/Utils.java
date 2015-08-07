package exter.eveindustry.util;

import java.util.Map;

/**
 * @author exter
 * Generic utility methods.
 */
public final class Utils
{
  static public int divCeil(int a,int b)
  {
    return a / b + ((a % b == 0) ? 0 : 1);
  }

  static public long divCeil(long a,long b)
  {
    return a / b + ((a % b == 0) ? 0 : 1);
  }

  static public int clamp(int v,int min,int max)
  {
    if(v < min)
    {
      return min;
    }
    if(v > max)
    {
      return max;
    }
    return v;
  }
  
  static public long clamp(long v,long min,long max)
  {
    if(v < min)
    {
      return min;
    }
    if(v > max)
    {
      return max;
    }
    return v;
  }

  static public double clamp(double v,double min,double max)
  {
    if(v < min)
    {
      return min;
    }
    if(v > max)
    {
      return max;
    }
    return v;
  }

  static public float clamp(float v,float min,float max)
  {
    if(v < min)
    {
      return min;
    }
    if(v > max)
    {
      return max;
    }
    return v;
  }
  
  static public <K,V> V mapGet(Map<K,V> map, K key,V def)
  {
    V res = map.get(key);
    if(res == null)
    {
      return def;
    }
    return res;
  }
}
