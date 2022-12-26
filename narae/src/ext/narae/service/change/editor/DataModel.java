package ext.narae.service.change.editor;

import java.util.*;
import javax.swing.tree.*;
import wt.part.*;


public class DataModel
{
	BETreeNode root;
	DefaultTreeModel model;

	public DataModel(){}
	public DataModel(WTPart part){
		init(part);
	}
	public void setPart(WTPart part){
		init(part);
	}
	public void init(BETreeNode node){
		root = node;
		model = new BETreeModel(root);
	}
	public void init(WTPart part){
		BEContext.setModel(this);
		PartData data = new PartData(part);
		root = new BETreeNode(data);
		data.cellSet();
		root.add(new BETreeNode(new Boolean(true) ));
		setModel(root);	// TreeNode 를 이용해서 트리 봄트리구조를 만든다.
		model = new DefaultTreeModel(root);
	}
	public void init(DataModel old){
		BEContext.setModel(this);
		this.root = old.getRoot();
		model = new BETreeModel(root);
	}
	public TreeModel getTreeModel(){
		return model;
	}
	protected void setModel(BETreeNode node){
		try{
			BEContext.println("setModel : " + node.toString());
			PartData pd = (PartData)node.getUserObject();
			pd.expand(node,BEContext.getView());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public BETreeNode getRoot(){
		return root;
	}
	public void reload(BETreeNode node){
		Vector list = getSamePartNode(node);
		Enumeration en = list.elements();
		while(en.hasMoreElements()){
			BETreeNode n = (BETreeNode)en.nextElement();
			model.reload(n);
		}
	}
	public Vector getSamePartNode(BETreeNode node){
		WTPart part = ((PartData)node.getUserObject()).part;
		return getSamePartNode(part);
	}
	public Vector getSamePartNode(WTPart part){
		String partKey = BEContext.getKey(part);
		Vector result = new Vector();
		if(partKey.equals(((PartData)root.getUserObject()).getKey())){
			result.add(root);
			return result;
		}
		getSamePartNode(partKey,root,result);
		return result;
	}
	public void getSamePartNode(String partKey,BETreeNode node,Vector list){
		Enumeration v = node.children();
		while(v.hasMoreElements()){
			BETreeNode pnode = (BETreeNode)v.nextElement();
			Object o = pnode.getUserObject();
			if(o instanceof PartData){
				PartData pd = (PartData)o;
				if(partKey.equals(pd.getKey())){
					if(!list.contains(pnode))
						list.add(pnode);
				}
				getSamePartNode(partKey,pnode,list);
			}
		}
	}
	public BETreeNode getNewNode(WTPart part,WTPart parent,WTPartUsageLink link){

		Vector list = getSamePartNode(part);
		BETreeNode node = null;
		for(int i=0; i< list.size(); i++){
			node = (BETreeNode)list.get(i);
			BETreeNode pnode = (BETreeNode)node.getParent();
			if(pnode==null)continue;
			PartData pd = (PartData)pnode.getUserObject();
			if(pd.getKey() == BEContext.getKey(parent)){
				node = new BETreeNode(node.getUserObject());
				node.add(new BETreeNode(new Boolean(true)));
				return node;
			}
		}
		node = new BETreeNode(new PartData(part,link));
		node.add(new BETreeNode(new Boolean(true)));
		return node;
	}
}
