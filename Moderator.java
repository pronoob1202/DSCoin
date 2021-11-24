package DSCoinPackage;
import HelperClasses.Pair;
import java.util.*;
public class Moderator
 {
  public Members Moderator=new Members();
  //We are making transaction blocks which contain transactions from moderator to members
  //coinsrc of moderator to member transaction will be null because moderator did not get the coin from some transaction
  //When members spend the coins,the coinsrc of the transaction will be the transaction block of moderator to member transaction
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    int coinID=100000;
    Moderator.UID="Moderator";
    int y=DSObj.bChain.tr_count;
    Members[] m=DSObj.memberlist;
    int r=0;
    Transaction[][] trarray=new Transaction[coinCount/y][y];//coincount/trcount number of transaction blocks
    for(int i=0;i<coinCount/y;++i)
    {
    	for(int j=0;j<y;++j)
    	{
        Transaction temp=new Transaction();
    		temp.coinsrc_block=null;
    		temp.Source=Moderator;
        if(r<m.length)
          {
           temp.Destination=m[r];
            r++;
          }
        else
           {
             temp.Destination=m[0];
             r=1;
           }
        temp.coinID=String.valueOf(coinID+j+i*y);
        trarray[i][j]=temp;
    	}
    }
    Members[] m1=DSObj.memberlist;
    int c=0;
    for(int i=0;i<coinCount/y;++i)
    {
      Transaction[] a=new Transaction[y];
      for(int j=0;j<y;++j)
          a[j]=trarray[i][j];
      TransactionBlock t=new TransactionBlock(a);
      for(int j=0;j<y;++j)
      {
        Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(String.valueOf(coinID+j+i*y),t);
        if(c<m1.length)
        {
         if(m1[c].mycoins!=null)
           m1[c].mycoins.add(p);
         else
         {
           List<Pair<String, TransactionBlock>> l=new ArrayList<Pair<String,TransactionBlock>>();
           l.add(p);
           m1[c].mycoins=l;
         }
         c++;
        }
        else
        {
          c=0;
          m1[c].mycoins.add(p);
          c++;
        }
      }
       DSObj.bChain.InsertBlock_Honest(t);//transaction block with tr_count coins is added to block chain
    }
     DSObj.latestCoinID=String.valueOf(coinID+coinCount-1);
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
      int coinID=100000;
      Moderator.UID="Moderator";
      int y=DSObj.bChain.tr_count;
      Members[] m=DSObj.memberlist;
      int r=0;
      Transaction[][] trarray=new Transaction[coinCount/y][y];//coincount/trcount number of transaction blocks
      for(int i=0;i<coinCount/y;++i)
      {
          for(int j=0;j<y;++j)
          {
              Transaction temp=new Transaction();
              temp.coinsrc_block=null;
              temp.Source=Moderator;
              if(r<m.length)
              {
                  temp.Destination=m[r];
                  r++;
              }
              else
              {
                  temp.Destination=m[0];
                  r=1;
              }
              temp.coinID=String.valueOf(coinID+j+i*y);
              trarray[i][j]=temp;
          }
      }
      Members[] m1=DSObj.memberlist;
      int c=0;
      for(int i=0;i<coinCount/y;++i)
      {
          Transaction[] a=new Transaction[y];
          for(int j=0;j<y;++j)
          {
              a[j]=trarray[i][j]; 
          }
          TransactionBlock t=new TransactionBlock(a);
          for(int j=0;j<y;++j)
          {
              Pair<String,TransactionBlock> p=new Pair<String,TransactionBlock>(String.valueOf(coinID+j+i*y),t);
              if(c<m1.length)
              {
                  if(m1[c].mycoins!=null)
                      m1[c].mycoins.add(p);
                  else
                  {
                      List<Pair<String, TransactionBlock>> l=new ArrayList<Pair<String,TransactionBlock>>();
                      l.add(p);
                      m1[c].mycoins=l;
                  }
                  c++;
              }
              else
              {
                  c=0;
                  m1[c].mycoins.add(p);
                  c++;
              }
          }
          DSObj.bChain.InsertBlock_Malicious(t);//transaction block with tr_count coins is added to block chain
      }
      DSObj.latestCoinID=String.valueOf(coinID+coinCount-1);
}
}
