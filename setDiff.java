package graphClustering;

import isa.myQuickSort;

public class setDiff {
	int[] C;
	public setDiff(int[] A, int[] B){//A includes B
		int n=A.length;
		int m=B.length;
		int i,j,index=0;
		myQuickSort.quickSort(A,0,n-1);
		myQuickSort.quickSort(B,0,m-1);
		boolean ismember;
		
		int[] C = new int[n-m];
		for (i=0;i<n;i++){
			ismember=false;
			for (j=0;j<m;j++){
				if(A[i]==B[j]){
					ismember=true;
					break;
				}
			}
			if(!ismember){
				C[index]=A[i];
				index++;
			}
		}
		
		if (index==0)
			System.out.println("The two sets have no intersections");
		
		this.C=C;
	}
	
	public int[] getSet() {
		return C;
	}

}
