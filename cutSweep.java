package graphClustering;

import java.util.ArrayList;

public class cutSweep {
	double[] conductance;

	public cutSweep(int[][] A, int[] p) throws Exception {
		int n = A.length;
		double[] conductance = new double[n];
		double[] cut = new double[n];
		double[] vol = new double[n];
		double[] curset = new double[n];
		int[] d = new int[n];
		int i,j,vert;
		for (i=0;i<n;i++){
			d[i]=0;
			cut[i]=0;
			vol[i]=0;
			curset[i]=0;
		}

		for (i=0;i<n;i++){
			for(j=0;j<n;j++){
				if (A[i][j]==1)
					d[i]++;
			}
		}

		int curcut=0,curvol=0;
		double volG;
		

		for (i=0;i<n;i++){
			vert=p[i];
			double[] neighs = new double[d[vert]];
			int pp=0;
			for (j=0;j<n;j++){
				if (A[j][vert]==1){
					neighs[pp]=j;
					pp++;
				}
			}

			for (double ele:neighs){
				if (ele==vert)
					continue;
				if (curset[(int)ele]==1)
					curcut-=A[(int)ele][vert];
				else
					curcut+=A[(int)ele][vert];	
			}
			curset[vert]=1;
			curvol+=d[vert];
			cut[i]=curcut;
			vol[i]=curvol;
		}
		volG=vol[n-1];
		
			
		for (i=0;i<n;i++){
			if (vol[i]<volG-vol[i]){
				if (vol[i]!=0)
					conductance[i]=cut[i]/vol[i];
				else
					conductance[i]=1E6;
			}
			else{
				if (volG-vol[i]!=0)
					conductance[i]=cut[i]/(volG-vol[i]);
				else
					conductance[i]=1E6;
			}
		}
		this.conductance=conductance;
	}

	public double[] getConductance() {
		return conductance;
	}

}
