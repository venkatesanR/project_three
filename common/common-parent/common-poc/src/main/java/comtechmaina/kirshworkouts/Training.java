package comtechmaina.kirshworkouts;

public class Training {

	public static void main(String[] args) {
		
		TNode tnode=new TNode();
		tnode.setData(2);
		
		TNode n2=new TNode();
		n2.setData(4);
		n2.setNext(null);
		tnode.setNext(n2);
		
		while(tnode	!=null) {
			System.out.println(tnode.getData());
			tnode=tnode.getNext();
		}

	}

}
