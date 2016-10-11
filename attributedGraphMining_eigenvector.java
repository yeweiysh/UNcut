package graphClustering;

import java.io.*;
import java.util.*;

import isa.myQuickSort;
import smile.math.Math;

public class attributedGraphMining_eigenvector {

	public static void main(String[] args) throws Exception{
		String dir = "C:/Users/ye/Desktop/graph clustering/graph data/dfb/";
		int numInstance = 100;
		int numCluster = 6;
//		String dir = args[0];
//		int numInstance = Integer.parseInt(args[1]);

		String fileAffinity=dir+"edgeWeight.txt";
		readAffinity rdf = new readAffinity(fileAffinity,numInstance);
		int[][] Affinity = new int[numInstance][numInstance];
		Affinity=rdf.getData();
		ArrayList<double[]> edgeWeightList;
		edgeWeightList=rdf.getDataList();
		int num=edgeWeightList.size();
		double[][] edgeWeight = new double[num][3];
		int i,j;
		for (i=0;i<num;i++){
			edgeWeight[i][0]=edgeWeightList.get(i)[0];
			edgeWeight[i][1]=edgeWeightList.get(i)[1];
			edgeWeight[i][2]=edgeWeightList.get(i)[2];
		}

		String fileData=dir+"Data.txt";
		double[][] data;
		readData rdD=new readData(fileData);
		data=rdD.getData();
		
//		String fileLabel=dir+"label.txt";
//		int[] trueLabel;
//		readLabel rdL=new readLabel(fileLabel);
//		trueLabel=rdL.getData();
//		
//		myQuickSort.quickSort(trueLabel, 0, trueLabel.length-1);
//		int numCluster=trueLabel[trueLabel.length-1];

		long start = System.currentTimeMillis();
		double omega=1;

		int[] cluster;
		double phi_star;
		double minobj;

		System.out.println("clustering...");
		
		int[] actuallabel = new int[numInstance];
		
		
		eigenDecomposition Eigen = new eigenDecomposition(Affinity,data,omega,numCluster);
		actuallabel=Eigen.getClusterLabel();

		System.out.println("Detecting the structural hole spanners...");
		ArrayList<Integer> structureHole = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> sHs = new ArrayList<ArrayList<Integer>>();
		ArrayList<double[]> proVector = new ArrayList<double[]>();
		double[] a_best = new double[data[0].length];

		for (i=0;i<numInstance;i++)
			actuallabel[i]+=1;
		
		String clusterlabel= dir+"clusterlabel.txt";
		PrintWriter clout = new PrintWriter(new FileWriter(clusterlabel));
		for (i = 0; i < numInstance; i++) {
			clout.println(actuallabel[i]);
		}
		clout.close();
		
		int[] label;	
		for (i=1;i<=numCluster;i++){
			label=new int[numInstance];
			for (j=0;j<numInstance;j++){
				if (actuallabel[j]==i)
					label[j]=1;
			}
//			for(i1=0;i1<numInstance;i1++){
//				System.out.print(label[i1]);
//				System.out.print(" ");
//			}
//			System.out.println();
			structuralHoleDetection SH = new structuralHoleDetection(data, edgeWeight, Affinity, label);
			//			if (SH.getCounter()!=0){
			structureHole=SH.getSturctureHole();
			sHs.add(structureHole);
			a_best=SH.getProVector();
			proVector.add(a_best);
			//			}	
		}

		long end = System.currentTimeMillis();
		long difference = end - start;
//		String filename=dir+ String.valueOf(omega);
//		File f = new File(filename);
//        f.mkdir();
//		String clusterlabel= dir+String.valueOf(omega) + "/clusterlabel.txt";
//		PrintWriter clout = new PrintWriter(new FileWriter(clusterlabel));
//		for (i = 0; i < numInstance; i++) {
//			clout.println(actuallabel[i]);
//		}
//		clout.close();
		
		System.out.println("eclipsed time: "+ difference/1000.0 +'s');

		

		String projectedvector= dir+"projectedvector.txt";
		PrintWriter pvout = new PrintWriter(new FileWriter(projectedvector));
		for (i = 0; i < proVector.size(); i++) {
			for (j=0;j<proVector.get(i).length;j++){
				pvout.print(proVector.get(i)[j]);
				pvout.print("\t");
			}
			pvout.println();	
		}
		pvout.close();

		String structurehole= dir+"structurehole.txt";
		PrintWriter shout = new PrintWriter(new FileWriter(structurehole));

		for (i = 0; i < sHs.size(); i++) {
			if (sHs.get(i).get(0)!=-1){
				for (j=0;j<sHs.get(i).size();j++){
					shout.print(sHs.get(i).get(j)+"\t");
				}
				shout.println();	
			}
		}
		shout.close();

		String results= dir + "/results.txt";
		PrintWriter rout = new PrintWriter(new FileWriter(results));
		rout.print("eclipsed time: ");
		rout.print(difference/1000.0);
		rout.println("sec");
		rout.close();

		System.out.println("Done!");

	}

}
