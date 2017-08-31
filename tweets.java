import java.io.*;
import java.util.*;

public class tweets
{
	public static ArrayList<String> List1=new ArrayList<String>();
	public static ArrayList<String> List2=new ArrayList<String>();
	public static ArrayList<String> List3=new ArrayList<String>();
	public static int level=0;

	public static void main(String[] args) throws FileNotFoundException   
	{
		File f= new File(args[3]);
		FileOutputStream fStream= new FileOutputStream(f);
		PrintStream ps= new PrintStream(fStream);
		System.setOut(ps);
		String removeChars=",.-|!'@#";
		level=Integer.parseInt(args[0]);

		tweetCluster[] Cluster=new tweetCluster[251];
		for(int d=0;d<251;d++)
		{
			Cluster[d]=new tweetCluster();
		}
		Scanner scan2 = null;
		Scanner scan3 = null;
		try {
			scan2 = new Scanner(new File(args[2]));
			scan3 = new Scanner(new File(args[1]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
		}
		int k1=0;
		while (scan2.hasNextLine()) {
			int count=0;
			String l1=new String();

			String id="";
			l1=scan2.nextLine();
			String[] st2 =  l1.split(" ");
			l:for(int i=1;i<st2.length;i++){
				if(st2[i].equals("null,")||st2[i].startsWith("http")||st2[i].startsWith("\"profile_image_url\":"))  {break l;}  
				count++;
			}for(int m=0;m<st2.length;m++)	
			{
				if(st2[m].equals("\"id\":")){
					id=st2[++m];
				} 
			}
			String l2[]=new String[count];

			int k=1;
			for(int j=0;j<count;j++){
				String l4=new String();
				l4=CharRemove(st2[k++] ,removeChars);  
				l2[j]=l4;}
			Cluster[k1].SetInst(id,l2);	
			k1++;
		}
		while(scan3.hasNextLine()){
			String s=new String();
			s=scan3.nextLine();
			if(s.equals("325946633986641920")){
				s="325946633986641920,";}
			List1.add(s);                 // initial seeds
		} 

		f:for(int it=0;it<25;it++){
			System.out.println();
			System.out.println();
			System.out.println("Iteration No:"+(it+1));
			System.out.println("---------------------------------------------------------------------");
			int k,l;
			for(int i1=0;i1<251;i1++){
				double total[]=new double[level];
				String temp6=new String();
				String temp7=Cluster[i1].getInst();
				for(k=0;k<level;k++){
					for( l=0;l<251;l++){
						String s5=List1.get(k);
						String s6=Cluster[l].getInstNum();
						if(s5.equals(s6)){
							temp6=Cluster[l].getInst(); 
						}
					}
					total[k]=CalculateJaccardDist(temp6,temp7);
				}
				double minDis=total[0];
				int closestId=0;
				for( l=0;l<total.length;l++){
					if(total[l]<minDis){minDis=total[l];
					closestId=l;
					}
				}

				Cluster[i1].SetClusNum(closestId);
			}
			int flag=0;
			for(int d=0;d<level;d++){
				System.out.println("Cluster No:"+(d+1));
				System.out.println("");
				System.out.print("");
				for(int x=0;x<251;x++){
					if(Cluster[x].GetClusNum()==d){flag=0;
					System.out.print(Cluster[x].InstNum);}}
				if(flag==1){System.out.print("No points in this tweet cluster");}
				System.out.println();
				System.out.println();
			}
			ArrayList<String> Li5=new ArrayList<String>(List1);
			int cc=0;
			UpdateCluster(Cluster);
			for(int g=0;g<level;g++){
				if(List1.get(g).equals(Li5.get(g))){cc++;}
			}
			if(cc==level){System.out.println();
			System.out.println();
			break f;}
		}
		CalculateSSE(Cluster);
	}

	public static void CalculateSSE(tweetCluster[] Cluster){
		double[] total=new double[25];
		double CalculateSSE=0;
		int l,k,i,j,h;
		for(l=0;l<level;l++){	
			for(k=0;k<251;k++){
				if(Cluster[k].getInstNum().equals(List1.get(l))){
					List2.add(Cluster[k].getInst());
				}  
			}
		}	
		for(i=0;i<level;i++){
			for(j=0;j<251;j++){
				if(Cluster[j].GetClusNum()==i){
					total[i]=total[i]+Math.pow(CalculateJaccardDist(Cluster[j].getInst(),List2.get(i)),2);
				}
			}}
		for(h=0;h<level;h++){
			CalculateSSE=CalculateSSE+total[h];
		}
		
		System.out.println("The SSE value for K-MEANS is: "+CalculateSSE);

	}
	public static double CalculateJaccardDist(String s1,String s2){
		String[] ptr1=s1.split("  ");
		String[] ptr2=s2.split("  ");
		ArrayList<String> List3=new ArrayList<String>();
		int i,j;
		int count=0;
		for(i=0;i<ptr1.length;i++){
			l: for(j=0;j<ptr2.length;j++){

				if(ptr1[i].equals(ptr2[j])){
					List3.add(ptr1[i]);
					count++;
					break l;
				}

			}
		}
		int Cluster= (ptr1.length+ptr2.length-count);
		double wae=(1-(0.01*(count*100/Cluster)));
		return wae;	
	}

	public static void UpdateCluster(tweetCluster[] Cluster)
	{

		int cnt12=0;
		for(int i=0;i<level;i++){
			ArrayList<String> Li1=new ArrayList<String>();
			ArrayList<String> Li2=new ArrayList<String>();
			for(int j=0;j<251;j++){
				int cno=Cluster[j].GetClusNum();
				if(cno==i){
					Li1.add(Cluster[j].getInst()); 
					Li2.add(Cluster[j].getInstNum()); 
				}  
			}
			double[] total=new double[Li1.size()];
			for(int k=0;k<Li1.size();k++){
				total[k]=0;

				for(int l=0;l<Li1.size();l++){
					total[k]=total[k]+CalculateJaccardDist(Li1.get(k),Li1.get(l));
				}
			} 
			if(total.length!=0){
				double minDis=total[0];
				cnt12=0;
				for(int m=0;m<total.length;m++){
					if(total[m]<=minDis){minDis=total[m];
					cnt12=m;}
				}  
			}
			if(Li2.size()!=0)	
			{
				List1.set(i,Li2.get(cnt12));
			}  
		}
		
	}
	public static String CharRemove(String FirstStr ,String ScndStr)
	{
		boolean[]  BTemp = new boolean[128];
		char[] FirstArr=FirstStr.toCharArray();
		char[] remArr=ScndStr.toCharArray();
		int s1,e1=0;

		for(s1=0;s1 < remArr.length;++s1)
		{
			BTemp[remArr[s1]]=true;
		}
		for(s1=0;s1 < FirstArr.length;++s1)
		{
			if(!BTemp[FirstArr[s1]])
			{
				FirstArr[e1++]=FirstArr[s1];
			}
		}
		return new String(FirstArr,0,e1);
	}

}

class tweetCluster{

	public int ClusterNum,len;
	public String InstNum;
	public String tweet,tweet1, tweet2, tweet3;
	public void SetClusNum(int n){ClusterNum=n;}
	public int GetClusNum(){return ClusterNum;}
	public int TweetSize(){return len;}
	public void SetInst(String num,String[] st)
	{
		InstNum=num;	
		tweet=Arrays.toString(st); 
		tweet1=tweet.replace(',',' ');
		tweet2=tweet1.replace('[',' ');
		tweet3=tweet2.replace(']',' ');
		len=st.length;
	}
	public String getInst()
	{
		return tweet3;
	}
	public String getInstNum()
	{
		return InstNum;
	}
}