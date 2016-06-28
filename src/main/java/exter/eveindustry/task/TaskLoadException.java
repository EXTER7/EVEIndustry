package exter.eveindustry.task;

/**
 * @author exter
 * Exception when a task contains an invalid parameter.
 */
public final class TaskLoadException extends Exception
{
  private static final long serialVersionUID = -5878679822079381477L;
  
  TaskLoadException(String message)
  {
    super(message);
  }
}
