package graphClustering;

import java.util.*;

import isa.myQuickSort;

public class greedyNeighborSearch {
	private double WCUT_tcex, WVOL_tcex, MinConductance;
	private int Subbest;
	
	public greedyNeighborSearch(double old_WCUT_tcex, double old_WVOL_tcex, ArrayList<Integer> pTCex, 
			LinkedList<Integer> uniqueCandidateSet, ArrayList<double[]> candidateSet, double[][] edgeWeight){
		int numUniq=uniqueCandidateSet.size();
		int i,j,k,cnt;
		double tmp_unique, tmp_one_neighbor,new_WCUT,new_WVOL,new_conductance;
		ArrayList<double[]> one_neighbours_outside;
		ArrayList<Integer> tmp_unique_position;
		ArrayList<Integer> tmp_one_neighbours_position;
		double[] new_WCUT_tcex = new double[numUniq];
		double[] new_WVOL_tcex = new double[numUniq];
		double[][] conductance_new = new double[2][numUniq];
		double[][] one_neighbours;
		boolean ismember;
		
		for (i=0;i<numUniq;i++){
			
			tmp_unique_position = new ArrayList<Integer>();
			tmp_one_neighbours_position = new ArrayList<Integer>();
			
			for (j=0;j<candidateSet.size();j++){
				if (candidateSet.get(j)[1]==uniqueCandidateSet.get(i)){
					tmp_unique_position.add(j);
				}
			}
			
			for (j=0;j<edgeWeight.length;j++){
				if (edgeWeight[j][0]==uniqueCandidateSet.get(i)){
					tmp_one_neighbours_position.add(j);
				}
			}
			
			one_neighbours=new double[tmp_one_neighbours_position.size()][3];
			for (j=0;j<tmp_one_neighbours_position.size();j++){
				one_neighbours[j][0]=edgeWeight[tmp_one_neighbours_position.get(j)][0];
				one_neighbours[j][1]=edgeWeight[tmp_one_neighbours_position.get(j)][1];
				one_neighbours[j][2]=edgeWeight[tmp_one_neighbours_position.get(j)][2];
			}
			cnt=0;
			one_neighbours_outside = new ArrayList<double[]>();
			for (j=0;j<one_neighbours.length;j++){
				ismember=false;
				for (k=0;k<pTCex.size();k++){
					if (one_neighbours[j][1]==pTCex.get(k)){
						ismember=true;
						break;
					}		
				}
				
				if(!ismember){
				one_neighbours_outside.add(one_neighbours[j]);
				cnt++;
				}
			}
			
			tmp_unique=0;
			tmp_one_neighbor=0;
			int sub;
			if (cnt!=0){
				for (j=0;j<tmp_unique_position.size();j++){
					sub=tmp_unique_position.get(j);
					tmp_unique+=candidateSet.get(sub)[2];
				}
				for (j=0;j<cnt;j++){
					tmp_one_neighbor+=one_neighbours_outside.get(j)[2];
				}
				
				new_WCUT_tcex[i]=old_WCUT_tcex-tmp_unique+tmp_one_neighbor;
				new_WVOL_tcex[i]=old_WVOL_tcex+tmp_unique+tmp_one_neighbor;
				conductance_new[0][i]=new_WCUT_tcex[i]/new_WVOL_tcex[i];
				conductance_new[1][i]=i;
			}
			else{
				new_WCUT_tcex[i]=1;
				new_WVOL_tcex[i]=1;
				conductance_new[0][i]=10000.0;
				conductance_new[1][i]=i;
			}
			one_neighbours_outside=null;
			tmp_unique_position=null;
			tmp_one_neighbours_position=null;
		}
		myQuickSort.quickSort(conductance_new, 0, numUniq-1);
		this.Subbest=(int)conductance_new[1][0];
		this.WCUT_tcex=new_WCUT_tcex[(int)conductance_new[1][0]];
		this.WVOL_tcex=new_WVOL_tcex[(int)conductance_new[1][0]];
		this.MinConductance=conductance_new[0][0];
	}
	
	public double getNewWCUT(){
		return WCUT_tcex;
	}
	
	public double getNewWVOL(){
		return WVOL_tcex;
	}
	
	public double getMinConductance(){
		return MinConductance;
	}
	
	public int getBestIndex(){
		return Subbest;
	}

}
