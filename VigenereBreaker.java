import java.util.*;
import edu.duke.*;
import java.io.*;
public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
       StringBuilder ans = new StringBuilder();
        for(int i= whichSlice; i < message.length(); i += totalSlices){
            ans.append(message.charAt(i));
        }
        return ans.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        for (int i=0; i < key.length; i++){
            String sliced = sliceString(encrypted,i,klength);
            CaesarCracker cc = new CaesarCracker(mostCommon);
            int k = cc.getKey(sliced);
            key[i]= k;
       }
        return key;
    }
    
    public void testTryKeyLength(){
       FileResource fr = new FileResource();
       String message = fr.asString();
       int[] array =tryKeyLength(message, 5, 'e');
       System.out.println(Arrays.toString(array));
   }
    
   public void breakVigenere () {
        FileResource fr = new FileResource();//encrypted message
        String message = fr.asString();
        //read all dictionaries
        HashMap<String, HashSet<String>> languages = new HashMap<String, HashSet<String>>();
        DirectoryResource dr = new DirectoryResource();
         for(File f : dr.selectedFiles()){
          String fname = f.getName();
          FileResource  currfr = new FileResource(f);
          HashSet<String> dictionary = readDirectory(currfr);
          languages.put(fname,dictionary);
          System.out.println("Read" + fname + "SuCCESS");
         }
        System.out.println("");
        breakForAllLangs(message, languages);
    }
   
   public HashSet<String> readDirectory(FileResource fr){
      HashSet<String> english = new HashSet<String>();
      for(String word : fr.lines()){
        word = word.toLowerCase();
        english.add(word);
      }
      return english ;
   }

   public String breakForLanguage(String encrypted, HashSet<String> dictionary, char common){
      int max = 0;
      String answer = "";
      int keylen = 0;
      int[] bestKey = new int[20];
       
       for(int i=1;i<=100;i++){
      int[] keyLength = tryKeyLength(encrypted, i, common);
      VigenereCipher vc = new VigenereCipher(keyLength);
      int count = countWords(vc.decrypt(encrypted),dictionary);
        if(count > max ){
           max = count;
           answer = vc.decrypt(encrypted);
           bestKey = keyLength; 
           keylen = i;
         
      }
    }
      System.out.println("----------------------");
      System.out.println("----------------------");
      System.out.println("----------------------");
      System.out.println("----------------------");
      System.out.println("----------------------");
      System.out.println(keylen);
      System.out.println(max);
      System.out.println(Arrays.toString(bestKey));
    
      return answer ;
   }

   public int countWords(String message, HashSet<String> dictionary){
      int count = 0;
      for(String word: message.split("\\W+")){
            word = word.toLowerCase();
            if(dictionary.contains(word)){
                count++;
            } 
       }
      return count;
    
    }
   
   public char mostCommonCharIn(HashSet<String> dictionary){
      HashMap<Character,Integer> charMap = new HashMap<Character,Integer>();
       for(String d : dictionary){
         for(char ch : d.toCharArray()){
            
             if(charMap.containsKey(ch)){
                charMap.put(ch, charMap.get(ch)+1);
                }
             else{
                charMap.put(ch,1);
                }   
            }
        }
        char common = findCommon(charMap);
        return common;
   } 
   
   //helper method of mostCommonCharIn to find out char with highest value.
    private char findCommon(HashMap<Character, Integer> map) {
        int count = 0;
        char chCommon = ' ';
        for(char ch : map.keySet()) {        
            int curr = map.get(ch);
            if(count == 0) {
                count = curr;
                chCommon = ch;
            }
            else {            
                if(curr > count) {                
                    count = curr;
                    chCommon = ch;
                }
            }
        }
        return chCommon;
   }
   
   public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages){
     String ans ="";
     String lang ="";
     int correctCounts =0;
     for(String langName : languages.keySet() ){
         HashSet<String> currDictionary = languages.get(langName);
         char currMostCommonChar = mostCommonCharIn(currDictionary);
         String currDecrypted = breakForLanguage(encrypted, currDictionary, currMostCommonChar);
         int currCounts = countWords(currDecrypted, currDictionary);
         if(correctCounts < currCounts){
            correctCounts = currCounts;
            ans = currDecrypted;
            lang = langName;
            }
     }
     System.out.println(ans);
     System.out.println("Correct Language :- "+ lang);
   }
}
