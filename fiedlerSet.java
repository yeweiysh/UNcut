package graphClustering;

import smile.math.Math;
import smile.math.matrix.EigenValueDecomposition;
import isa.myQuickSort;

import java.io.*;
import java.util.*;

public class fiedlerSet {

	int[] bestset;
	double phi_star;
	double min;
	public fiedlerSet(int[][] A, double[][] data, double omega, int checkNumber) throws Exception{

		int n=A.length,dim=data[0].length;
		int i,j;
		double[] conductance;
		double[] D = new double[n];
		int[] p = new int[n];
		for (i = 0; i < n; i++) {
			for (j = 0; j < n; j++) {
				D[i] += A[i][j];
			}

			if (D[i] == 0.0) {
				throw new IllegalArgumentException("Isolated vertex: " + i);                    
			}

			D[i] = 1.0 / Math.sqrt(D[i]);
		}

		double[][] L = new double[n][n];
		for (i = 0; i < n; i++) {
			for (j = 0; j < i; j++) {
				L[i][j] = D[i] * A[i][j] * D[j];
				L[j][i] = L[i][j];
			}
		}

		EigenValueDecomposition eigen = Math.eigen(L, 2);
		double[][] Y = eigen.getEigenVectors();
		double[][] x=new double[2][n];
		for (i=0; i<n;i++){
			x[0][i]=Y[i][1];
			x[1][i]=i;
		}

		double[] sum = new double[n];
		for (i=0;i<n;i++)
			sum[i]=0;
		for (i=0; i<n;i++){
			for (j=0;j<n;j++)
				sum[i]+=A[i][j];
			sum[i]=Math.sqrt(sum[i]);
			x[0][i]/=sum[i];
		}

		myQuickSort.quickSort(x, 0, x[0].length-1);
		for (i=0;i<n;i++)
			p[i]=(int)x[1][i];

		cutSweep sweepCut = new cutSweep(A,p);
		conductance=sweepCut.getConductance();

		double[][] conduc = new double[2][conductance.length];
		for (i=0;i<conductance.length;i++){
			conduc[0][i]=conductance[i];
			conduc[1][i]=i;
		}
		myQuickSort.quickSort(conduc, 0, conductance.length-1);

		int[] tmpbestset;
		int index;
		double[] unicomp = new double[checkNumber];
		double[][] tmpData;
		double[] bestKconductance = new double[checkNumber];

		for (i=0;i<checkNumber;i++){
			unicomp[i]=1000;
			bestKconductance[i]=conduc[0][i];
		}

		ArrayList<int[]> storeset = new ArrayList<int[]>(); 
		double[] phi = new double[checkNumber];

		for (i=0;i<checkNumber;i++){
			index=(int)conduc[1][i];
			if (index<=3||n-index<=3){
				phi[i]=1000;
				continue;
			}
			else{
				if (index<(n-1)/2.0){
					tmpbestset = new int[index];
					for (j=0;j<index;j++)
						tmpbestset[j]=p[j];

				}
				else{
					tmpbestset = new int[n-index];
					for (j=index;j<n;j++)
						tmpbestset[j-index]=p[j];
				}

				storeset.add(tmpbestset);

				tmpData=new double[tmpbestset.length][dim];
				for (j=0;j<tmpbestset.length;j++){
					for(int k=0;k<dim;k++)
						tmpData[j][k]=data[tmpbestset[j]][k];
				}

				unimodCompact compactness = new unimodCompact(tmpData);
				
				unicomp[i]=compactness.getUniComp();
				phi[i]=bestKconductance[i]+omega*unicomp[i];
			}
		}

		index=0;
		double min=phi[0];
		for (i=1;i<checkNumber;i++){
			if (min>phi[i]){
				min=phi[i];
				index=i;
			}
		}

		int[] bestset;
		bestset=storeset.get(index);
		double phi_star=bestKconductance[index];

		this.bestset=bestset;
		this.phi_star=phi_star;
		this.min=min;
	}

	public int[] getBestset() {
		return bestset;
	}

	public double getBestConductance() {
		return phi_star;
	}

	public double getBestObj() {
		return min;
	}

}
