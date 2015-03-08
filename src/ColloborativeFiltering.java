
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ColloborativeFiltering 
{
    public Map<String,Map<String,String>> data;
    public Map<String,Map<String,String>> movieIdMap;
  
    public Map<String,Double> meanMap;
    String trainingDataPath;
    String testDataPath;
    double k;
    
    public ColloborativeFiltering()
    {
        data=new HashMap<>();
        movieIdMap=new HashMap<>();
        meanMap=new HashMap<>();
        k=1.0;
    }
    
    public void ExtractData()
    {
        try
        {
           FileReader reader=new FileReader(trainingDataPath);
           BufferedReader br=new BufferedReader(reader);
           String line=br.readLine();
           while(line!=null)
           {
               String [] s=line.split(",");
               String movieId=s[0];
               String userId=s[1];
               String rating=s[2];
               
               if(data.get(userId)==null)
               {
                   Map<String,String> m=new HashMap<>();
                   m.put(movieId,rating);
                   data.put(userId,m);
               }
               else
               {
                   data.get(userId).put(movieId,rating);
               }
               
               if(movieIdMap.get(movieId)==null)
               {
                   Map<String,String> m=new HashMap<>();
                   m.put(userId, rating);
                   movieIdMap.put(movieId, m);
               }
               else
               {
                   movieIdMap.get(movieId).put(userId, rating);
               }
               line=br.readLine();
           }
        }
        catch(Exception e)
        {}
        System.out.println("");  
    }
    
    public double calculateMean(String user)
    {
	int I=data.get(user).size();
	double totalRating=0.0;
	for(String s:data.get(user).keySet())
	{
		Double r=Double.parseDouble(data.get(user).get(s));
		totalRating=totalRating+r;
	}

	double meanRating=(double)(totalRating/I);
	return meanRating;
    }
    public void BuildMeanMap()
    {
        for(String s:data.keySet())
        {
            meanMap.put(s,calculateMean(s));
        }
    }
    
    public double CalculateCorrelationWeight(String usera,String useri)
    {
        //calculate j
        ArrayList<String> jList = new ArrayList<>();
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    jList.add(s);
                }
            }
        }
        
        double num = 0.0, den1 = 0.0, den2 = 0.0;
        for(int j = 0; j<jList.size(); j++)
        {
            String commonMovieId = jList.get(j);
            double vaj = Double.parseDouble(data.get(usera).get(commonMovieId));
            double meanVa = meanMap.get(usera);
            double vij = Double.parseDouble(data.get(useri).get(commonMovieId));
            double meanVi = meanMap.get(useri);
            
            num = num + ((vaj - meanVa) * (vij - meanVi)); 
            
            den1 = den1 + ((vaj - meanVa)*(vaj - meanVa));
            den2 = den2 + ((vij - meanVi) * (vij - meanVi));
            //if(den2==0 || den1 == 0)
                //System.out.println("asdasd");
        }
        double den = Math.sqrt(den1 * den2);
        double weightai = num/(den);
        if(Double.isNaN(weightai))
            return 0.0;
        return weightai;
        
        
        /*double totalNumerator=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vaj=Double.parseDouble(data.get(usera).get(s));
                    double meanva=calculateMean(usera);
                    double vij=Double.parseDouble(data.get(useri).get(s));
                    double meanvi=calculateMean(useri);
                    totalNumerator=(double)totalNumerator+((vaj-meanva)*(vij-meanvi));
                }
            }
        }
        
        double vajsquared=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vaj=Double.parseDouble(data.get(usera).get(s));
                    double meanva=calculateMean(usera);
                    vajsquared=vajsquared+((vaj*meanva)*(vaj*meanva));
                }
            }
        }
        
        double vijsquared=0.0;
        for(String s: movieIdMap.keySet())
        {
            if((movieIdMap.get(s).get(usera)!=null))
            {
                if(movieIdMap.get(s).get(useri)!=null)
                {
                    double vij=Double.parseDouble(data.get(useri).get(s));
                    double meanvi=calculateMean(useri);
                    vijsquared=vijsquared+((vij*meanvi)*(vij*meanvi));
                }
            }
        }
        
        double denominator=(double)vajsquared*vijsquared;
        double totalDenominator=(double)Math.sqrt(denominator);
        
        double w=(double)(totalNumerator/totalDenominator);
        return w;*/
        
    }
    public double CalculatePredictiveWeight(String usera,String moviej)
    {
        /*
        double m=0.0;
        for(String s:data.keySet()) //for each user
        {
            double w=CalculateCorrelationWeight(user,s);
            
            if(data.get(s).get(movie)!=null)
            {
                double vij=Double.parseDouble(data.get(s).get(movie));
                double meanvi=calculateMean(s);
                m=m+(w*(vij-meanvi));
            }
           
        }
        double meanva=calculateMean(user);
        double predictiveValue=(double)meanva+(k*m);
        return predictiveValue;
        */
        if(meanMap.get(usera) == null)
            return 0.0;
        double sum = 0.0;
        double summantionWai = 0.0;
        double meanVa = meanMap.get(usera);
        for(String useri:data.keySet())
        {
            double wai = CalculateCorrelationWeight(usera, useri);
            summantionWai += wai;
            if(data.get(useri).get(moviej) == null)
                continue;
            double vij = Double.parseDouble(data.get(useri).get(moviej));
            double meanVi = meanMap.get(useri);
            sum = sum + (wai * (vij - meanVi));
            
        }
        
        double paj = meanVa + ((1/summantionWai) * sum);
        if(paj < 0)
                System.out.println("asdas");
        return paj;
    }
    
   /* public void TrainData()
    {
        for(String a:data.keySet())
        {
            for(String j:movieIdMap.keySet())
            {
                double p=CalculatePredictiveWeight(a,j);
                Map<String,String> m=new HashMap<>();
                m.put(a, j);
                predictiveRatings.put(m, p);
            }
        }
        System.out.println("");
    }*/
    public void TestAlgorithm()
    {
        try
        {
           FileReader reader=new FileReader(testDataPath);
           BufferedReader br=new BufferedReader(reader);
           String line=br.readLine();
           int cnt=0;
           int correctcnt=0;
           double meanAbsoluteError=0.0;
           double rootMeanSquaredValue=0.0;
           while(line!=null)
           {
               cnt++;
               String [] s=line.split(",");
               String movieId=s[0];
               String userId=s[1];
               String rating=s[2];
               
               /**
                * check
                */
               if(data.get(userId)!=null)
               {
                   if(movieIdMap.get(movieId)!=null)
                   {
                        double p=Math.round(CalculatePredictiveWeight(userId,movieId));
                        double temp=(p-Double.parseDouble(rating));
                        meanAbsoluteError=meanAbsoluteError+temp;
                        rootMeanSquaredValue=rootMeanSquaredValue+(temp*temp);
                        //if(Double.parseDouble(rating)==p)
                          //  correctcnt++;
                   }
               }
               
               line=br.readLine();
               
           }
            System.out.println("Mean AbsoluteError:"+(double)(meanAbsoluteError/cnt));
            System.out.println("Root Mean Square Value:"+(double)(Math.sqrt(rootMeanSquaredValue/cnt)));
           // System.out.println("Accuracy : "+((double)correctcnt/cnt)*100);
        }
        catch(Exception e){
            System.out.println("errror while calculating accuracy"+e);}
        
    }
    public static void main(String args [])
    {
        ColloborativeFiltering c=new ColloborativeFiltering();
        c.trainingDataPath="/Users/bhumikasaivamani/sample_train.txt";
        c.testDataPath="/Users/bhumikasaivamani/sample_test.txt";
        c.ExtractData();
        c.BuildMeanMap();
        c.TestAlgorithm();
        //c.TrainData();
    }
    
}
