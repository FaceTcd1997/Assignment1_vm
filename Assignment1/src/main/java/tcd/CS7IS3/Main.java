package tcd.CS7IS3;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // Check if argument were passed
        if (args.length != 3)
        {
            System.out.println("------------------------------------------------------------------");
            System.out.println("Select Analyzer, Similarity measure and Results limit to be passed");
            System.out.println("Analyzer: \n" +
                    "-std (Standard)\n" +
                    "-w (WhiteSpace)\n" +
                    "-s (Simple)\n" +
                    "-e (English)");
            System.out.println("Similarity: \n" +
                    "-t (TFIDF)\n" +
                    "-b (BM25)");
            System.out.println("Results limit: \n" +
                    "n (an int number)");
            System.out.println("------------------------------------------------------------------");
            System.exit(1);
        }

        FileParser parser = new FileParser();
        Indexer indexer = new Indexer();
        Searcher searcher = new Searcher();
        Path indexPath = Paths.get("index/cran.index");

        //Parsing documents
        System.out.println("Parsing documents...");
        List<Map<String, String>> documents = parser.parseDocuments("cran");

        //Parsing queries
        System.out.println("Parsing queries...");
        List<Map<String, String>> queries = parser.parseQueries("cran");

        indexer.deleteIndex(new File("index/cran.index"));

        //Creating index in index/cran.index file
        System.out.println("Creating index...");
        indexer.createIndex(indexPath, documents, args);

        //Scanning documents over queries
        System.out.println("Searching documents with queries...");
        searcher.queryIndex(indexPath, queries,args);

        System.out.println("Executed");
    }
}
