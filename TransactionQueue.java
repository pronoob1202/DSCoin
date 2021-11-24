package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
      if(firstTransaction==null)
      {
         firstTransaction=transaction;
         lastTransaction=transaction;
      }
      else
      {
          lastTransaction.next=transaction;
          transaction.previous=lastTransaction;
          lastTransaction=transaction;
      }
      numTransactions++;
  }
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(firstTransaction==null)
      throw new EmptyQueueException();
    else
    {
      Transaction t=firstTransaction;
      firstTransaction=firstTransaction.next;
      if(firstTransaction==null)
         lastTransaction=null;
      numTransactions--;
      return t;
    }
  }
  public Transaction GetTransaction(int i)
  {
    Transaction t=firstTransaction;
    int x=0;
    while(x<i)
    {
      t=t.next;
      x++;
    }
    return t;
  }

  public int size() {
    return numTransactions;
  }
}
