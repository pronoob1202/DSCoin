package DSCoinPackage;
import java.util.*;
import HelperClasses.Pair;
import HelperClasses.CRF;

public class BlockChain_Malicious {

      static CRF ob = new CRF(64);
    public int tr_count;
    public static final String start_string = "DSCoin";
    public TransactionBlock[] lastBlocksList;

    public static boolean checkTransactionBlock(TransactionBlock tB) {
        String dummy = start_string;
        if (tB.previous != null)
            dummy = tB.previous.dgst;
        if (!tB.dgst.substring(0, 4).equals("0000"))
            return false;
        else {
            if (!tB.dgst.equals(ob.Fn(dummy + "#" + tB.trsummary + "#" + tB.nonce)))
                return false;
            else {
                for (int i = 0; i < tB.trarray.length; ++i) {
                    if (!tB.checkTransaction(tB.trarray[i]))
                        return false;
                }
                if (!tB.trsummary.equals(tB.Tree.Build(tB.trarray)))
                    return false;
                else
                    return true;
            }
        }
    }

    public TransactionBlock FindLongestValidChain() {
        List<Pair<TransactionBlock, Integer>> l = new ArrayList<Pair<TransactionBlock, Integer>>();
        if(lastBlocksList!=null){
        for (int i = 0; i < lastBlocksList.length; ++i) {
            int length = 0;
            TransactionBlock temp = lastBlocksList[i];
            TransactionBlock special = temp;
            while (temp != null) {
                if (!checkTransactionBlock(temp)) {
                    special = temp.previous;
                    length = 0;
                }
                temp = temp.previous;
                length++;
            }
            Pair<TransactionBlock, Integer> p = new Pair<TransactionBlock, Integer>(special, length);
            l.add(p);
        }}
        else
            return null;
        int max = 0, max_index = 0;
        for (int j = 0; j < l.size(); ++j) {
            if (l.get(j).second >= max) {
                max = l.get(j).second;
                max_index = j;
            }
        }
        return l.get(max_index).first;
    }

    public void InsertBlock_Malicious(TransactionBlock newBlock) {
        TransactionBlock lastblock = this.FindLongestValidChain();
        int x = 0;
        newBlock.previous = lastblock;//previous is set
        newBlock.create_nonce(start_string);//nonce requires previous
        newBlock.find_dgst(start_string);//correct dgst requires nonce
        int flag=0,c=0;
        while((c< lastBlocksList.length)&&(flag==0)){
            if ((lastBlocksList[c] == lastblock)&&(lastblock!=null))
            {
                x = c;
                flag=1;
            }
            else if(lastblock!=null)
            {
                 TransactionBlock temp=lastBlocksList[c];
                 while((temp!=null)&&(temp!=lastblock))
                     temp=temp.previous;
                 if(temp!=null)
                     flag=2;
            }
            else if(lastblock==null)
                flag=1;
            c++;
        }
        if(flag==1)
          lastBlocksList[x] = newBlock;
        else if(flag==2)
        {
            int x1=0;
            while(lastBlocksList[x1]!=null)
                x1++;
            lastBlocksList[x1]=newBlock;
        }
    }
}
