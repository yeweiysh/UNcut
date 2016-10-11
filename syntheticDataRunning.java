package graphClustering;
import java.io.File;

public class syntheticDataRunning {

	public static void main(String[] args) throws Exception{
		String[] arg = new String[2];
		arg[1]="200";
		int[] index = new int[5];
		index[0]=20;
		index[1]=40;
		index[2]=60;
		index[3]=80;
		index[4]=100;
//		int[] index = new int[5];
//		index[0]=200;
//		index[1]=400;
//		index[2]=600;
//		index[3]=800;
//		index[4]=1000;
//		index[5]=1600;
//		index[6]=3200;
		for (int i=1;i<=10;i++){
			System.out.println(i);
			for (int j=0;j<index.length;j++){
				
				arg[0]="C:/Users/ye/Desktop/new data/synthetic data/200nodes_varying attribute numbers/"+String.valueOf(i)+"/"+String.valueOf(index[j])+"/";
//				arg[1]=String.valueOf(index[j]);
				attributedGraphMining_eigenvector.main(arg);
//				attributedGraphMining.main(arg);
			}
		}

	}

}
