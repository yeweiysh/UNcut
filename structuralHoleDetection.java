package graphClustering;

import java.io.*;
import java.util.*;

import isa.myQuickSort;

public class structuralHoleDetection {
	ArrayList<Integer> structureHole;
	//	int counter;
	double[] prov;

	public structuralHoleDetection(double[][] X, double[][] edgeWeight, int[][] Affinity, int[] label) throws Exception{
		int t= edgeWeight.length,nTCex=0,i,j;
		int n=X.length,m=X[0].length;
		for (i=0;i<n;i++){
			if (label[i]==1)
				nTCex++;
		}

		double[][] subData = new double[nTCex][m];
		ArrayList<Integer> pTCex = new ArrayList<Integer>();
		int index=0;
		for (i=0;i<n;i++){
			if (label[i]==1){
				for (j=0;j<m;j++){
					subData[index][j]=X[i][j];
				}
				pTCex.add(i);
				index++;
			}
		}

		multiPusuit mp=new multiPusuit(subData);
		double[] prov = new double[m];
		prov=mp.getProVector();

		double[][] weight = new double[m][m];
		for (i=0;i<m-1;i++){
			for (j=i+1;j<m;j++){
				weight[i][j]=0;
				weight[j][i]=0;
			}
		}

		for (i=0;i<m;i++){
			weight[i][i]=prov[i];
		}

		double[] s=new double[m];
		double[] tmp=new double[m];
		double tmpp=0;
		int row,col,k;
		for (i=0;i<t;i++){
			row=(int)edgeWeight[i][0];
			col=(int)edgeWeight[i][1];
			for (j=0;j<m;j++){
				s[j]=X[row][j]-X[col][j];
			}

			for (j=0;j<m;j++){
				tmpp=0;
				for (k=0;k<m;k++){
					tmpp+=s[k]*weight[k][j];
				}
				tmp[j]=tmpp;	
			}

			tmpp=0;
			for (j=0;j<m;j++)
				tmpp+=tmp[j]*s[j];

			edgeWeight[i][2]=1.0/(1.0+Math.pow(tmpp,0.5));
		}

		ArrayList<double[]> vol = new ArrayList<double[]>();
		ArrayList<double[]> candidateSet = new ArrayList<double[]>();
		index=0;
		boolean ismember;
		int i1,j1;
		for (i=0;i<nTCex;i++){
			ArrayList<double[]> currentNeighbor = new ArrayList<double[]>();
			for (j=0;j<t;j++){
				if((int)edgeWeight[j][0]==pTCex.get(i)){
					vol.add(edgeWeight[j]);
					currentNeighbor.add(edgeWeight[j]);
				}
			}
			for (i1=0;i1<currentNeighbor.size();i1++){
				ismember=false;
				for (j1=0;j1<nTCex;j1++){
					if ((int)currentNeighbor.get(i1)[1]==pTCex.get(j1)){
						ismember=true;
						break;
					}
				}
				if (!ismember){
					candidateSet.add(currentNeighbor.get(i1));
				}
			}

		}
		
		ArrayList<Integer> structureHole = new ArrayList<Integer>();
		
		if (candidateSet.size()==0){
			structureHole.add(-1);
			this.structureHole=structureHole;
			//			this.counter=counter;
			this.prov=prov;
		}
		else{

			double old_WVOL_tcex=0, old_WCUT_tcex=0;
			for (i=0;i<vol.size();i++)
				old_WVOL_tcex+=vol.get(i)[2];

			for (i=0;i<candidateSet.size();i++)
				old_WCUT_tcex+=candidateSet.get(i)[2];

			double conductanceBest;
			conductanceBest=old_WCUT_tcex / old_WVOL_tcex;

			double[] tmpCandidateSet = new double[candidateSet.size()];
			for (i=0;i<candidateSet.size();i++)
				tmpCandidateSet[i]=candidateSet.get(i)[1];

			myQuickSort.quickSort(tmpCandidateSet, 0, candidateSet.size()-1);
//			System.out.println(tmpCandidateSet.length);
			LinkedList<Integer> uniqueCandidateSet = new LinkedList<Integer>();
			uniqueCandidateSet.add((int)tmpCandidateSet[0]);
			for (i=1;i<candidateSet.size();i++){
				if (tmpCandidateSet[i]!=tmpCandidateSet[i-1])
					uniqueCandidateSet.add((int)tmpCandidateSet[i]);
			}

			double minConductance=0,new_WCUT_tcex,new_WVOL_tcex;
			int Subbest;

			
			int counter=0;
			while(uniqueCandidateSet.size()>0){

				greedyNeighborSearch grdyNS = new greedyNeighborSearch(old_WCUT_tcex, old_WVOL_tcex,pTCex, 
						uniqueCandidateSet, candidateSet, edgeWeight);
				new_WCUT_tcex=grdyNS.getNewWCUT();
				new_WVOL_tcex=grdyNS.getNewWVOL();
				Subbest=grdyNS.getBestIndex();
				minConductance=grdyNS.getMinConductance();

				candidateSet=null;
				candidateSet = new ArrayList<double[]>();

				if (minConductance<conductanceBest){
					conductanceBest=minConductance;
					old_WCUT_tcex = new_WCUT_tcex;
					old_WVOL_tcex = new_WVOL_tcex;
					pTCex.add(uniqueCandidateSet.get(Subbest));
					label[uniqueCandidateSet.get(Subbest)] = 1;
					structureHole.add(uniqueCandidateSet.get(Subbest));
					counter++;

					for(i=0;i<t;i++){
						if (edgeWeight[i][0]==uniqueCandidateSet.get(Subbest)){
							vol.add(edgeWeight[i]);
						}

					}
					uniqueCandidateSet.remove(Subbest);

					for (i=0;i<vol.size();i++){
						ismember=false;
						for (j=0;j<pTCex.size();j++){
							if ((int)vol.get(i)[1]==pTCex.get(j)){
								ismember=true;
								break;
							}
						}
						if (!ismember){
							candidateSet.add(vol.get(i));
						}

					}

				}
				else 
					break;
			}

			if (counter==0)
				structureHole.add(-1);
			
			this.structureHole=structureHole;
			//			this.counter=counter;
			this.prov=prov;
		}
	}

	public ArrayList<Integer> getSturctureHole(){
		return structureHole;
	}

	public double[] getProVector(){
		return prov;
	}

	//	public int getCounter(){
	//		return counter;
	//	}

}
