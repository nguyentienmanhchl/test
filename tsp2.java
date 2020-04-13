package tsp2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.File;


public class tsp2 {
	public static int SizeP=100;
	public static int StopP=200;
	public static int CONDITION_STOP = 10;
	public static Random rand = new Random(); 
	public static int[][] khoangcach= new int[100][100];
	public static int n, max=9999;// n: do dai gen
	public static int[][] pop = new int[200][100];//pop: population, mảng pop luu tru gen của các cá thể trong quần thể , ban dau khởi tạo 100 cá thể
	// quần thể con được lưu ở 100 phần tử cuối của mảng pop.
	public static double[] fitness = new double[200];
	public static double pm= 0.1,pc=0.5;
	public static int best,min_cost;

	// Read and Write file
	public static void loadGraph() throws IOException
	{
		String inp; int a;
		String[] inpl;
	    BufferedReader br =null;
	    FileReader fr= null;
	    fr = new FileReader("c:/java/inPut.txt");
	    br = new BufferedReader(fr);
	    inp = br.readLine();
	    n = Integer.parseInt(inp);
	    // tao ma tran ke:
	    for(int i=0;i<n;i++)
	    {
	    	inp= br.readLine();
	    	inpl=inp.split(" ");
	    	for(int j=0;j<n;j++)
	    	{
	    	     a= Integer.parseInt(inpl[j]);
	    	     if(a==0) 
	    	    	 khoangcach[i][j]= max;
	    	     else  
	    	    	 khoangcach[i][j]=a;     
	    	}		
		}
		br.close();

	}
	public static void ghiFile() throws IOException
	{
		Writer wr= new FileWriter("c:/java/outPut.txt");
		BufferedWriter bw = new BufferedWriter(wr);
		bw.write("Giai thuat GA cho ket qua :\n");
		for(int i=0;i<n;i++)
		{
			bw.write(String.valueOf(pop[best][i]));
			bw.write(" --> ");	
		}
		bw.write(String.valueOf(pop[best][0]));
		bw.write("\nChi Phi Nho Nhat: ");
		bw.write(String.valueOf(min_cost));
        bw.close();
	}

    // fitness
	public static double fit(int k)
	{  
		int cost=0;
		for(int i=1;i<n;i++)
			cost+= khoangcach[pop[k][i-1]][pop[k][i]];
		cost+=khoangcach[pop[k][n-1]][pop[k][0]];
		return 1.0/cost;
	}	

	// Init
	
    public static void khoitao()
    {
		// init
		System.out.printf("Quan the ban dau la :\n");
    	for (int k=0; k< SizeP;k++)
    	{
           for (int i=0;i<n;i++)
               pop[k][i]= i;
           for (int i = n - 1; i > 0; i--)
           {   
			   if (rand.nextInt(2)==1){
               int index = rand.nextInt(i + 1);
               int temp = pop[k][index];
               pop[k][index]=pop[k][i];
               pop[k][i]=temp;}
			}
		   for (int i=0;i<n;i++)
			   System.out.print(pop[k][i]+"  ");
		   System.out.println();
           fitness[k]=fit(k);
	   }
	   System.out.printf("______________________\n");
    }

	// selection
	public static void selection()
	{
	      for (int i=0;i<SizeP;i++)
	      {
	    	  int tg=i;
	    	  for(int j=i+1;j<StopP;j++)
	    	  {
	    		  if(fitness[j]>fitness[tg])
	    			  tg=j;
	    	  }
	    	  if(tg!=i)
	    	  {
	    		  double tgian=fitness[i];
	    		  fitness[i]=fitness[tg];
	    		  fitness[tg]=tgian;
             
	    		  int[] gen_tg= pop[i];
	    		  pop[i]= pop[tg];
	    		  pop[tg]=gen_tg;  
	    	  }
	      }     
	}

	// crossover  &  mutation
 
   public static void crossover(int a, int b,int c)// lai ghép kiểu OX
   {
	   int[] child1= new int[100];
	   int[] child2= new int[100];
	   int c1= rand.nextInt(n);// c1,c2 la 2 điểm cắt
	   int c2= rand.nextInt(n);
	   while(c2==c1)
	   {
		   c2= rand.nextInt(n);
	   }
	   if(c2<c1)
	   {
		   int tg= c2;
		   c2=c1;
		   c1= tg;
	   }
	   
	   for (int i=c1;i<=c2;i++){
		child1[i]=pop[a][i];
		child2[i]=pop[b][i];
	   }
       int[] visited= new int[100];
	   int[] temp= new int [100];
	   for (int i=0;i<n;i++)
	      temp[i]=pop[b][i];
	
	// sắp xếp lại temp
	    for (int i=n-1;i>=n-1-c2;i--)
		    temp[i]=temp[i-(n-1-c2)];
	    for (int i=0;i<n-1-c2;i++)
		    temp[i]=pop[b][c2+i+1];
	  
				 
	//trong temp bỏ đi những thành phố đã xuất hiện trong child1
	   for (int i=0;i<n;i++)
		   visited[i]=0;
	   for (int i=c1;i<=c2;i++)
		    visited[child1[i]]=1;
	   for (int i=0;i<n;i++)
		 if (visited[temp[i]]==1){
			temp[i]=-1;
		 }//cách thức : ở trong temp, những gen đã xuất hiện trong child1 ta đổi thành -1 sau đó xóa những phần tử có giá trị =-1
		 int dem=0, m=n;
		while(dem<m){
			if (temp[dem]==-1)
			{
				for (int i=dem;i<m-1;i++)
				   temp[i]=temp[i+1];
				   m--;
			}else dem++;
		}
	 // điền các phần tử trong temp vào các chỗ trống trong child1
    	 int k1=0;
	    for (int i=c2+1;i<n;i++){
		   child1[i]=temp[k1];
		   k1++;}
	    for (int i=0;i<c1;i++)  {
		   child1[i]=temp[k1];
		   k1++;}

	 // child2 làm tương tự như child1
	    for (int i=0;i<n;i++)
	       temp[i]=pop[a][i];
	// sắp xếp lại temp
	    for (int i=n-1;i>=n-1-c2;i--)
	    	temp[i]=temp[i-(n-1-c2)];
	    for (int i=0;i<n-1-c2;i++)
		    temp[i]=pop[a][c2+i+1];
	//trong temp bỏ đi những thành phố đã xuất hiện trong child2
     	for (int i=0;i<n;i++)
		    visited[i]=0;
	    for (int i=c1;i<=c2;i++)
		    visited[child2[i]]=1;
	    for (int i=0;i<n;i++)
		     if (visited[temp[i]]==1){
		     	 temp[i]=-1;
		 }
		 dem=0;m=n;
		 while(dem<m){
			if (temp[dem]==-1)
			{
				for (int i=dem;i<m-1;i++)
				   temp[i]=temp[i+1];
				   m--;
			}else dem++;
		}
	 // điền các phần tử trong temp vào các chỗ trống trong child2
	  int k2=0;
	    for (int i=c2+1;i<n;i++){
		    child2[i]=temp[k2];
		    k2++;}
	    for (int i=0;i<c1;i++)  {
		     child2[i]=temp[k2];
		    k2++;}
	   
	  
	  for (int i=0;i<n;i++)
	  pop[c][i]=child1[i];
	   fitness[c]=fit(c);
	  for (int i=0;i<n;i++)
	  pop[c+1][i]=child2[i];
	  fitness[c+1]=fit(c+1);
   }

   public static void mutation(int k,int c)
   {
	   int c1= rand.nextInt(n);
	   int c2= rand.nextInt(n);
	   int[] child = new int [100];
	   for (int i=0;i<n;i++)
	   child[i]=pop[k][i];
	   while(c2==c1)
	   {
		   c2= rand.nextInt(n);
	   }
	   int temp=child[c1];
	   child[c1]=child[c2];
	   child[c2]= temp; 
	   for (int i=0;i<n;i++)
	      pop[c][i]=child[i];
	   fitness[c]=fit(c);
   }
   public  static void LaiGhepDotBien()
   {
	   int Soluong=100;
	   while(Soluong<StopP)
	   {
           int dad = rand.nextInt(SizeP);
           int mom = rand.nextInt(SizeP);
           while(mom==dad)
           {
               mom = rand.nextInt(SizeP);
           }
           double r = rand.nextDouble();
           
		  if(r < pm) 
		  {            mutation(dad,Soluong);
					   mutation(mom,Soluong+1);
					   Soluong+=2;}// dot bien cha me duoc gen mới cho vao quan the con, gen bo me van giu nguyen
          else if (r<pc) {
			   crossover(dad,mom,Soluong);// lai ghép bố mẹ
			   
			   Soluong+=2;
           }
	    }
   }

   public static void KetQua()
   {
	   double max_fitness=0;
	   for(int i=0; i< SizeP;i++)
		   if (fitness[i]>max_fitness)
		   {
			   max_fitness= fitness[i];
			   best=i;
		   }
	   
	   for(int i=0;i< n;i++)
		   System.out.printf("%-3d", pop[best][i]);
	   min_cost = (int) (1/max_fitness);
	   System.out.printf("-->  %d\n", min_cost); 
	 
   }	     

	public static void main(String[] args) throws IOException{
		loadGraph();// tao ma tran trong so 
		khoitao();// thuc hien tinh fitness cua moi ca the trong ham khoitao()
		for (int i=0;i<CONDITION_STOP;i++)
		{
			
            KetQua();
			LaiGhepDotBien();// lai ghep va dot bien khi so ca the gap 2 lan kich thuoc toi da thi dung lai , moi lan tao ra con moi deu tinh fitness ca the moi do
			selection();// lua chon 100 ca the  co fitness cao nhat de tao quan the moi cho the he tiep theo 			
			
		}
		 
		ghiFile();

	}
}
