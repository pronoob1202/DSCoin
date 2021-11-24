package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

   public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;
  public void InsertBlock_Honest (TransactionBlock newBlock) {
     newBlock.previous=lastBlock;//previous is set
     newBlock.create_nonce(this.start_string);//nonce requires previous
     newBlock.find_dgst(this.start_string);//correct dgst requires nonce
     lastBlock=newBlock;
  }
}
