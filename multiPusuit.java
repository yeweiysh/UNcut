package graphClustering;

import isa.myQuickSort;

public class multiPusuit {
	double[] a_best;
//	double[] vector;
	public multiPusuit(double[][] x) throws Exception{
		int n=x.length, m=x[0].length;
		double dip_best=1;
		double[] a_best = new double[m];
		double[] a = new double[m];
		double[] a1 = new double[m];
		double[][] y = new double[2][n];
		double[] sorted = new double[n];
		int[] subscript = new int[n];
		double[] beta=new double[m];
		double[] gamma=new double[m];
		double[] derivative=new double[m];
		double eta,beta1=0,gamma1=0,alpha=0.05;
		int[] triple = new int[3];
		double dip;
		double tmp,tmp1;
		int i,j,i1,j1,k;
		for (i=0;i<m;i++)
			a_best[i]=Math.random();
		
		for (i=0;i<m;i++){
			for (j=0;j<m;j++)
				a[j]=Math.random();
			
			a1=nonnegASC(a);
			for (i1=0;i1<m;i1++)
				a[i1]=a1[i1];
			
			while(true){
				for (i1=0;i1<n;i1++){
					y[0][i1]=0;
					y[1][i1]=i1;
					for (j1=0;j1<m;j1++){
						y[0][i1]+=x[i1][j1]*a[j1];
					}
				}
				myQuickSort.quickSort(y, 0, n-1);
				for (i1=0;i1<n;i1++){
					sorted[i1]=y[0][i1];
					subscript[i1]=(int)y[1][i1];
				}
				
				hartigansDipSignifTest Diptest = new hartigansDipSignifTest(sorted);
				triple=Diptest.getTriple();
				dip=Diptest.getDip();
				if (dip==0){
					for (i1=0;i1<m;i1++)
						a_best[i1]=a[i1];
					break;
				}
					
				
				if (dip<dip_best){
					dip_best=dip;
					for (i1=0;i1<m;i1++)
						a_best[i1]=a[i1];
				}
				else
					break;
				
				int ind1=triple[0],ind2=triple[1],ind3=triple[2];
				
//				System.out.println("triple: "+triple[0]+','+triple[1]+','+triple[2]);
//				if (ind1==0){
//					for (i1=0;i1<n;i1++){
//						System.out.print(sorted[i1]);
//						System.out.print(" ");
//					}
//					System.out.println();
//				}
				
				for (i1=0;i1<m;i1++){
					beta[i1]=x[subscript[ind2]][i1]-x[subscript[ind1]][i1];
					gamma[i1]=x[subscript[ind3]][i1]-x[subscript[ind1]][i1];
				}
				
				for (i1=0;i1<m;i1++){
					beta1+=beta[i1]*a[i1];
					gamma1+=gamma[i1]*a[i1];
				}
				
				eta=ind2-ind1-(ind3-ind1)*beta1/gamma1;
				tmp1=0;
				for (j1=0;j1<m;j1++){
					tmp1+=gamma[j1]*a[j1];
				}
				
				
				for (i1=0;i1<m;i1++){
					tmp=0;
					for (j1=0;j1<m;j1++){
						tmp+=(beta[i1]*gamma[j1]-beta[j1]*gamma[i1])*a[i1];
						
					}
					
					derivative[i1]=(1.0*(ind3-ind1)/n)*tmp/Math.pow(tmp1, 2.0);
					if (eta>0)
						derivative[i1]=-1*derivative[i1];
				}
				
				for (i1=0;i1<m;i1++){
					a[i1]=a[i1]-alpha*derivative[i1];
//					System.out.print(a[i1]);
//					System.out.print(" ");
				}
//				System.out.println();
				a1=nonnegASC(a);
				for (i1=0;i1<m;i1++)
					a[i1]=a1[i1];
				
			}
		}
		
//		double[] vector = new double[n];
//		for (i1=0;i1<n;i1++){
//			vector[i1]=0;
//			for (j1=0;j1<m;j1++){
//				vector[i1]+=x[i1][j1]*a_best[j1];
//			}
//		}
		
		this.a_best=a_best;
//		this.vector=vector;
	}
	
	public double[] getProVector(){
		return a_best;
	}
	
//	public double[] getProData(){
//		return vector;
//	}
	
	public double[] nonnegASC(double[] B){
		int n=B.length;
		myQuickSort.quickSort(B, 0, n-1);
		double[] B_sort=new double[n];
		double[] cum_B=new double[n];
		double[] A=new double[n];
		double[] sigma=new double[n];
		
		int i,j;
		for (i=0;i<n;i++)
			B_sort[i]=B[n-1-i];
		
		cum_B[0]=B_sort[0];
		for (i=1;i<n;i++)
			cum_B[i]=cum_B[i-1]+B_sort[i];
		
		for (i=0;i<n;i++)
			A[i]=i+1;
		
		int idx=0;
		for (i=0;i<n;i++){
			sigma[i]=B_sort[i]-1.0*(cum_B[i]-1)/A[i];
			if (sigma[i]>0)
				idx++;
		}
		double[] tmp=new double[n];
		for (i=0;i<n;i++)
			tmp[i]=B_sort[i]-sigma[i];
		
		
		double sigma1;
		sigma1=tmp[idx-1];
		
		double[] X=new double[n];
		double a;
		for (i=0;i<n;i++){
			a=B[i]-sigma1;
			if (a<0)
				X[i]=0;
			else
				X[i]=a;
		}
			
		return X;
	}
}
