package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.*;
public class Members
 {
    public String UID;
    public List<Pair<String, TransactionBlock>> mycoins;//this list also stores the transaction block which stores the transaction through which member got a particular coin
    public Transaction[] in_process_trans = new Transaction[100];

    public void Remove(Transaction[] a, Transaction t) {
        for (int i = 0; i < 100; ++i) {
            if (a[i] == t)
                a[i] = null;
        }
    }

    public void Add(Transaction[] a, Transaction a1) {
        if (a != null) {
            for (int i = 0; i < 100; ++i) {
                if (a[i] == null) {
                    a[i] = a1;
                }
            }
        } else {
            Transaction[] A = new Transaction[100];
            A[0] = a1;
            a = A;
        }
    }

    public boolean repetitioncheck(Transaction[] a, Transaction t) {
        int c = 0;
        if (a != null) {
            while (a[c] != null) {
                if (a[c].coinID.equals(t.coinID))
                    return false;
                c++;
            }
            return true;
        } else
            return true;
    }

    public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
        Pair<String, TransactionBlock> p = mycoins.get(0);//First coin in the mycoins of "this" is used for transaction
        mycoins.remove(0);
        Transaction tobj = new Transaction();
        tobj.coinID = p.first;
        Members[] a = DSobj.memberlist;
        Members destination = new Members();
        Members source = new Members();
        for (int i = 0; i < a.length; ++i)//we are finding source and destination members
        {                          //Since we are calling initiate coin send function with UID=this.UID,source=this
            if (a[i].UID.equals(destUID))
                destination = a[i];
        }
        source = this;
        tobj.Destination = destination;
        tobj.coinsrc_block = p.second;
        tobj.Source = source;
        Add(in_process_trans, tobj);
        DSobj.pendingTransactions.AddTransactions(tobj);
    }
    public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
        Pair<String, TransactionBlock> p = mycoins.get(0);//First coin in the mycoins of "this" is used for transaction
        mycoins.remove(0);
        Transaction tobj = new Transaction();
        tobj.coinID = p.first;
        Members[] a = DSobj.memberlist;
        Members destination = new Members();
        Members source = new Members();
        for (int i = 0; i < a.length; ++i)//we are finding source and destination members
        {                          //Since we are calling initiate coin send function with UID=this.UID,source=this
            if (a[i].UID.equals(destUID))
                destination = a[i];
        }
        source = this;
        tobj.Destination = destination;
        tobj.coinsrc_block = p.second;
        tobj.Source = source;
        Add(in_process_trans, tobj);
        DSobj.pendingTransactions.AddTransactions(tobj);
    }
  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend(Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
        TransactionBlock tB = DSObj.bChain.lastBlock;
        int flag = -1;
        List<Pair<String, String>> l1 = new ArrayList<Pair<String, String>>();
        List<Pair<String, String>> l2 = new ArrayList<Pair<String, String>>();
        while ((tB != null) && (flag == -1)) {
            for (int i = 0; i < tB.trarray.length; ++i) {
                if (tB.trarray[i] == tobj)
                    flag = 1;
            }
            if (flag == -1)
                tB = tB.previous;
        }
        if (flag == -1)
            throw new MissingTransactionException();
        else {
            Pair<String, TransactionBlock> k = new Pair<String, TransactionBlock>(tobj.coinID,tB);
            int c = 0,flag1=0;
            while ((c<tobj.Destination.mycoins.size())&&(flag1==0))
            {
                if((k.first.compareTo(tobj.Destination.mycoins.get(c).first) > 0))
                    c++;
                else
                    flag1=1;
            }
            tobj.Destination.mycoins.add(c, k);
            l1 = tB.Tree.path(tobj);
            TransactionBlock temp = DSObj.bChain.lastBlock;
            while (temp != tB.previous) {
                Pair<String, String> p1 = new Pair<String, String>(temp.dgst, temp.previous.dgst + "#" + temp.trsummary + "#" + temp.nonce);
                l2.add(p1);
                temp = temp.previous;
            }
            Pair<String, String> p2 = new Pair<String, String>(tB.previous.dgst, null);
            l2.add(p2);
            Collections.reverse(l2);
        }
        Pair<List<Pair<String, String>>, List<Pair<String, String>>> p = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(l1, l2);
        int x = 0;
        while (x < 100) {
            if (in_process_trans[x] == tobj)
                Remove(in_process_trans, tobj);
            x++;
        }
        return p;
    }

    public void MineCoin(DSCoin_Honest DSObj) {
        int tr_count = DSObj.bChain.tr_count;
        int c = 0, flag = 0;
        Transaction[] temp = new Transaction[tr_count];
            while ((c < tr_count - 1)&&(DSObj.pendingTransactions.size()!=0)) {
                if (DSObj.bChain.lastBlock.checkTransaction(DSObj.pendingTransactions.GetTransaction(0)) == true) {
                    if (repetitioncheck(temp, DSObj.pendingTransactions.GetTransaction(0)) == true) {
                        temp[c] = DSObj.pendingTransactions.GetTransaction(0);
                        c++;
                    }
                }
                try {
                    DSObj.pendingTransactions.RemoveTransaction();
                } catch (EmptyQueueException e) {
                }
            }
        Transaction miner = new Transaction();
        miner.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
        DSObj.latestCoinID = miner.coinID;
        miner.Source = null;
        miner.Destination = this;
        miner.coinsrc_block = null;
        temp[tr_count - 1] = miner;
        TransactionBlock newblock = new TransactionBlock(temp);
        Pair<String, TransactionBlock> mine = new Pair<>(miner.coinID, newblock);
        this.mycoins.add(mine);
        DSObj.bChain.InsertBlock_Honest(newblock);
    }

    public void MineCoin(DSCoin_Malicious DSObj) {
        int tr_count = DSObj.bChain.tr_count;
        int c = 0;
        Transaction[] temp = new Transaction[tr_count];
        while ((c < tr_count - 1)&&(DSObj.pendingTransactions.size()!=0)) {     
            TransactionBlock t1=DSObj.bChain.FindLongestValidChain();
            if (t1.checkTransaction(DSObj.pendingTransactions.GetTransaction(0)) == true) {

                if (repetitioncheck(temp, DSObj.pendingTransactions.GetTransaction(0)) == true) {
                    temp[c] = DSObj.pendingTransactions.GetTransaction(0);
                    c++;
                }
            }
            try {
                DSObj.pendingTransactions.RemoveTransaction();
            } catch (EmptyQueueException e) {
            }
        }
        Transaction miner = new Transaction();
        miner.coinID = Integer.toString(Integer.parseInt((String) DSObj.latestCoinID) + 1);
        DSObj.latestCoinID = miner.coinID;
        miner.Source = null;
        Members[] a = DSObj.memberlist;
        miner.Destination = this;
        miner.coinsrc_block = null;
        temp[tr_count - 1] = miner;
        TransactionBlock newblock = new TransactionBlock(temp);
        Pair<String, TransactionBlock> mine = new Pair<String, TransactionBlock>(miner.coinID, newblock);
        this.mycoins.add(mine);
        DSObj.bChain.InsertBlock_Malicious(newblock);
    }
 
}
