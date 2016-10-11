package graphClustering;

import smile.math.Math;
import smile.math.matrix.EigenValueDecomposition;
import smile.clustering.KMeans;
import isa.myQuickSort;

import eiggs.eiggs;
//import compeigs.eigsdecomp;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import java.io.*;
import java.util.*;

public class eigenDecomposition {

	int[] clusterLabel;


	public eigenDecomposition(int[][] A, double[][] data, double omega, int numCluster) throws Exception{

		int n=A.length,dim=data[0].length;
		int i,j;

//		double[] D = new double[n];
//		int[] p = new int[n];
//		for (i = 0; i < n; i++) {
//			for (j = 0; j < n; j++) {
//				D[i] += A[i][j];
//			}
//
//			if (D[i] == 0.0) {
//				throw new IllegalArgumentException("Isolated vertex: " + i);                    
//			}
//
//			D[i] = 1.0 / Math.sqrt(D[i]);
//		}
//
//		double[][] L = new double[n][n];
//		for (i = 0; i < n; i++) {
//			for (j = 0; j < i; j++) {
//				L[i][j] = D[i] * A[i][j] * D[j];
//				L[j][i] = L[i][j];
//			}
//		}
		
		//fast independent subspace analysis
		
		int numEigenVec=10*numCluster;
//		int numEigenVec=n-2;
		double[][] Y = new double[n][numEigenVec];
		double[][] lamda = new double[numEigenVec][numEigenVec];
		MWNumericArray input1 = new MWNumericArray(A, MWClassID.DOUBLE);

		Object[] output = new Object[2];
		eiggs eigs=new eiggs();
		output=eigs.eigenDecomp(2,input1, numEigenVec, 2);
		MWNumericArray result1 = null;
		MWNumericArray result2 = null;
		result1 = (MWNumericArray) output[0];
		result2 = (MWNumericArray) output[1];
		
		Y = (double[][]) result1.toArray();
		lamda=(double[][]) result2.toArray();


		result1.dispose();
		result2.dispose();
		MWArray.disposeArray(output);
		input1.dispose();
		input1=null;
		eigs.dispose();
		eigs=null;

		

//		double[] eigenvalues=new double[numEigenVec];
//
//		EigenValueDecomposition eigen = Math.eigen(L, numEigenVec);
//		double[][] Y = eigen.getEigenVectors();
//		eigenvalues=eigen.getEigenValues();
		
//		int[] clusterLabel = new int[n]; 
//		KMeans kmeans = new KMeans(Y, numCluster);
//		clusterLabel=kmeans.getClusterLabel();

		double[][] eigenvector = new double[n][2];
		int[] label = new int[n];
		int cnt1=0;
		int cnt2=0;
		ArrayList<Integer> index1 = new ArrayList<Integer>();
		ArrayList<Integer> index2 = new ArrayList<Integer>();
		double[] unicomp = new double[numEigenVec];
		double[][] subdata;
		for (i=0;i<numEigenVec;i++)
			unicomp[i]=1000;

		for (i=0;i<numEigenVec;i++){
			for (j=0;j<n;j++){
				eigenvector[j][0]=Y[j][i];
				eigenvector[j][1]=0;
			}
			KMeans kmeans = new KMeans(eigenvector, 2);
			label=kmeans.getClusterLabel();
			cnt1=0;
			cnt2=0;
			for (j=0;j<n;j++){
				if (label[j]==0){
					cnt1++;
					index1.add(j);
				}
				else{
					cnt2++;
					index2.add(j);
				}

			}
			if (cnt1<cnt2){
				subdata = new double[cnt1][dim];
				for (int k=0;k<cnt1;k++){
					for (j=0;j<dim;j++)
						subdata[k][j]=data[index1.get(k)][j];
				}
			}
			else{
				subdata = new double[cnt2][dim];
				for (int k=0;k<cnt2;k++){
					for (j=0;j<dim;j++)
						subdata[k][j]=data[index2.get(k)][j];
				}
			}

			if (cnt1==0||cnt2==0){
				unicomp[i]=1000;
			}else{
				unimodCompact compactness = new unimodCompact(subdata);
				unicomp[i]=compactness.getUniComp();
			}
		}
		

		double[][] phi = new double[2][numEigenVec];
		for (i=0;i<numEigenVec;i++){
			phi[0][i]=lamda[i][i]+omega*unicomp[i];
			phi[1][i]=i;
		}



		myQuickSort.quickSort(phi, 0, numEigenVec-1);

//		for (int k1=0;k1<numEigenVec;k1++){
//			System.out.print(lamda[k1][k1]);
//			System.out.print(' ');
//		}
//		System.out.println();
//		for (int k1=0;k1<numEigenVec;k1++){
//			System.out.print(unicomp[k1]);
//			System.out.print(' ');
//		}
//		System.out.println();


		double[][] resultEigenvector = new double[n][numCluster];

		for (i=0;i<numCluster;i++){
			for (j=0;j<n;j++){
				resultEigenvector[j][i]=Y[j][(int)phi[1][i]];
			}
		}
		int[] clusterLabel = new int[n]; 
		KMeans kmeans = new KMeans(resultEigenvector, numCluster);
		clusterLabel=kmeans.getClusterLabel();

		this.clusterLabel=clusterLabel;
	}

	public int[] getClusterLabel() {
		return clusterLabel;
	}

}
