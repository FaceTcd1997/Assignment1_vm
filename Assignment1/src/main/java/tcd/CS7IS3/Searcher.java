package tcd.CS7IS3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Searcher {
    public void queryIndex(Path index, List<Map<String, String>> queries, String[] args){

        int max_results = 0;
        String aType = "";
        String sType = "";

        //Selecting results limit over arg passed
        try{
            max_results = Integer.parseInt(args[2]);
        }catch (Exception e){
            System.out.println("--------------------------------");
            System.out.println("ERROR: not a valid Results limit");
            System.out.println("Results limit: \n" +
                    "n (an int number)");
            System.out.println("--------------------------------");
            System.exit(1);
        }

        try{
            Analyzer analyzer = null;
            //Selecting analyzer over arg passed
            switch (args[0]){
                case "-std":
                    analyzer = new StandardAnalyzer();
                    aType = aType.concat("Standard");
                    break;
                case "-w":
                    analyzer = new WhitespaceAnalyzer();
                    aType = aType.concat("Whitespace");
                    break;
                case "-s":
                    analyzer = new SimpleAnalyzer();
                    aType = aType.concat("Simple");
                    break;
                case "-e":
                    analyzer = new EnglishAnalyzer();
                    aType = aType.concat("English");
                    break;
                default:
                    System.out.println("----------------------------");
                    System.out.println("ERROR: not a valid Analyzer");
                    System.out.println("Analyzer: \n" +
                            "-std (Standard)\n" +
                            "-w (WhiteSpace)\n" +
                            "-s (Simple)\n" +
                            "-e (English)");
                    System.out.println("----------------------------");
                    System.exit(1);
            }

            // Open the folder that contains our search index
            Directory directory = FSDirectory.open(index);

            // create objects to read and search across the index
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            //Selecting Similarity measure over arg passed
            switch (args[1]){
                case "-t":
                    isearcher.setSimilarity(new ClassicSimilarity());
                    sType = sType.concat("TFIDF");
                    break;
                case "-b":
                    sType = sType.concat("BM25");
                    isearcher.setSimilarity(new BM25Similarity());
                    break;
                default:
                    System.out.println("-------------------------------------");
                    System.out.println("ERROR: not a valid Similarity measure");
                    System.out.println("Similarity: \n" +
                            "-t (TFIDF)\n" +
                            "-b (BM25)");
                    System.out.println("-------------------------------------");
                    System.exit(1);
            }

            List<String> resFileContent = new ArrayList<String>();

            //Iterates over all queries to retrieve documents
            for(int i = 0; i < queries.size(); i++){

                Map<String, String> qry = queries.get(i);

                MultiFieldQueryParser parser = new MultiFieldQueryParser(
                        new String[] {"Title", "Locations", "Authors", "Abstract"},
                        analyzer);
                Query q = parser.parse(qry.get("Content"));

                //Get the set of results
                ScoreDoc[] hits = isearcher.search(q, max_results).scoreDocs;

                for(int j = 0; j < hits.length; j++){
                    Document hitDoc = isearcher.doc(hits[j].doc);
                    resFileContent.add(qry.get("Number") + " Q0 " + hitDoc.get("ID") + " 0 " + hits[j].score + " STANDARD\n");
                }
            }

            // Create directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) outputDir.mkdir();

            Files.write(Paths.get("output/results"+ aType+ sType + max_results +".txt"), resFileContent, StandardCharsets.UTF_8);

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
