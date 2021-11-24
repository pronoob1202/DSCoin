package HelperClasses;

import DSCoinPackage.Transaction;
import java.util.*;

public class MerkleTree {

  // Check the TreeNode.java file for more details
  public TreeNode rootnode;
  public int numdocs;

  void nodeinit(TreeNode node, TreeNode l, TreeNode r, TreeNode p, String val) {
    node.left = l;
    node.right = r;
    node.parent = p;
    node.val = val;
  }

  public String get_str(Transaction tr) {
    CRF obj = new CRF(64);
    String val = tr.coinID;
    if (tr.Source == null)
      val = val + "#" + "Genesis"; 
    else
      val = val + "#" + tr.Source.UID;

    val = val + "#" + tr.Destination.UID;

    if (tr.coinsrc_block == null)
      val = val + "#" + "Genesis";
    else
      val = val + "#" + tr.coinsrc_block.dgst;

    return obj.Fn(val);
  }

  public String Build(Transaction[] tr) {
    CRF obj = new CRF(64);
    int num_trans = tr.length;
    numdocs = num_trans;
    List<TreeNode> q = new ArrayList<TreeNode>();
    for (int i = 0; i < num_trans; i++) {
      TreeNode nd = new TreeNode();
      String val = get_str(tr[i]);
      nodeinit(nd, null, null, null, val);
      q.add(nd);
    }
    TreeNode l, r;
    while (q.size() > 1) {
      l = q.get(0);
      q.remove(0);
      r = q.get(0);
      q.remove(0);
      TreeNode nd = new TreeNode();
      String l_val = l.val;
      String r_val = r.val;
      String data = obj.Fn(l_val + "#" + r_val);
      nodeinit(nd, l, r, null, data);
      l.parent = nd;
      r.parent = nd;
      q.add(nd);
    }
    rootnode = q.get(0);

    return rootnode.val;
  }
  public List<Pair<String, String>> path(Transaction tobj) {
        int y = numdocs;
        TreeNode N2 = new TreeNode();
        N2 = this.rootnode;
        TreeNode N = new TreeNode();
        Transaction temp = tobj;
        int z = 0;
        while (temp != null) {
            temp = temp.previous;
            z++;
        }
        List<Pair<String, String>> l = new ArrayList<Pair<String, String>>(3);
        Pair<String, String> p1 = new Pair<String, String>(this.rootnode.val, null);
        l.add(p1);
        while (y != 1) {
            Pair<String, String> p = new Pair<String, String>(N2.left.val, N2.right.val);
            l.add(p);
            if (z > y / 2) {
                N = N2.right;
                y /= 2;
                z -= y;
            } else {
                N = N2.left;
                y /= 2;
            }
            N2 = N;
        }
        Collections.reverse(l);
        return l;
    }
}
