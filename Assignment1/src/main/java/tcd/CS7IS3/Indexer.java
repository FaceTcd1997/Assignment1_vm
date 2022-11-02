package tcd.CS7IS3;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Indexer {

    public void createIndex(Path index, List<Map<String, String>> documents, String[] args){

        try{
            //Selecting analyzer over arg passed
            Analyzer analyzer = null;
            switch (args[0]){
                case "-std":
                    analyzer = new StandardAnalyzer();
                    break;
                case "-w":
                    analyzer = new WhitespaceAnalyzer();
                    break;
                case "-s":
                    analyzer = new SimpleAnalyzer();
                    break;
                case "-e":
                    analyzer = new EnglishAnalyzer();
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

            // Open the directory that contains the search index
            Directory directory = FSDirectory.open(index);

            // Set up an index writer to add process and save documents to the index
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            //Selecting Similarity measure over arg passed
            switch (args[1]){
                case "-t":
                    config.setSimilarity(new ClassicSimilarity());
                    break;
                case "-b":
                    config.setSimilarity(new BM25Similarity());
                    break;
                default:
                    System.out.println("-------------------------------------");
                    System.out.println("ERROR: not a valid Similarity measure");
                    System.out.println("Similarity: \n" +
                            "-t (TFIDF)\n" +
                            "-b (BM25)\n");
                    System.out.println("-------------------------------------");
                    System.exit(1);
            }
            IndexWriter iwriter = new IndexWriter(directory, config);

            for (int i = 0; i < documents.size(); i++) {
                addDocument(iwriter, documents.get(i));
            }

            iwriter.close();
            directory.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private void addDocument(IndexWriter iwriter, Map<String, String> doc) throws IOException {
        Document document = new Document();
        document.add(new StringField("ID", doc.get("ID"), Field.Store.YES));
        document.add(new TextField("Title", doc.get("Title"), Field.Store.YES));
        document.add(new TextField("Locations", doc.get("Locations"), Field.Store.YES));
        document.add(new TextField("Authors", doc.get("Authors"), Field.Store.YES));
        document.add(new TextField("Abstract", doc.get("Abstract"), Field.Store.YES));
        iwriter.addDocument(document);
    }

    public void deleteIndex(File file) {

        File[] contents = file.listFiles();
        if (contents != null) {

            for (File f: contents) {
                deleteIndex(f);
            }
        }
        file.delete();
    }
}
