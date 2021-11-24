package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {
CRF obj=new CRF(64);
  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;
  public TransactionBlock(Transaction[] t) {
    MerkleTree t1=new MerkleTree();
    Transaction[] dummy=new Transaction[t.length];
    for(int i=0;i<dummy.length;++i)
        dummy[i]=null;
    trarray=t;
    t=dummy;
    trsummary=t1.Build(trarray);
    Tree=t1;
    previous=null;
    dgst=null;
  }
  public void create_nonce(String start_string)
  {
     int nonce_int_form=1000000001;
     String dummy;
     if(previous==null)
       dummy=start_string;
     else
        dummy=previous.dgst;
    while((obj.Fn(dummy+"#"+trsummary+"#"+Integer.toString(nonce_int_form)).substring(0,4).equals("0000")==false)&&(nonce_int_form<Integer.MAX_VALUE)){
      nonce_int_form++;
 }
   try{
       nonce=Integer.toString(nonce_int_form);
   }
   catch(NumberFormatException e){}
  }
  public void find_dgst(String start_string)
  {
    String dummy;
    if(previous==null)
      dummy=start_string;
    else
      dummy=previous.dgst;
    dgst=obj.Fn(dummy+"#"+trsummary+"#"+nonce);
  }
  public boolean checkTransaction (Transaction temp) {
   if(temp.coinsrc_block!=null){
    Transaction[] t=temp.coinsrc_block.trarray;
    int x=t.length;
    for(int j=0;j<x;++j)
       {
         if(t[j].coinID.equals(temp.coinID))
         {
            if(t[j].Destination.UID.equals(temp.Source.UID))
              {
                TransactionBlock b=this.previous;
                if(this== temp.coinsrc_block)
                    return true;
                else{
                while((b!=temp.coinsrc_block)&&(b!=null))
                  {
                    for(int k=0;k<b.trarray.length;++k)
                    {
                      if(b.trarray[k].coinID.equals(temp.coinID))
                        return false;
                    } 
                    b=b.previous;
                   }
                 return true;   }
              }
            else 
             return false;  
         }
       }
    return false;
  }
  else
    return true;
}
}