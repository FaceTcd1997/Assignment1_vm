package tcd.CS7IS3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {
    public List<Map<String, String>> parseDocuments(String dir){
        List<Map<String, String>> docList = new ArrayList<Map<String, String>>();

        try{
            File file = new File(dir + "/cran.all.1400");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            HashMap<String, String> doc = new HashMap<>();
            String line;

            String id = "";
            String title = "";
            String authors = "";
            String locations = "";
            String abst = "";

            String tag = ".I";
            boolean first = true;
            int lineCount = 0;


            try{
                while ((line = reader.readLine()) != null) {

                    lineCount++;
                    String[] elements = line.split("\\s+");

                    switch (elements[0]){
                        case ".I":
                            if(!first){
                                //Beginning of a new doc, every field of the previous is complete, so we initialize the document
                                doc.put("Abstract", abst);
                                doc.put("Locations", locations);
                                doc.put("Authors", authors);
                                doc.put("Title", title);
                                doc.put("ID", id);
                                //Document is added to the list of documents
                                docList.add(doc);

                                //Fields and document reset to process the new document
                                id = "";
                                title = "";
                                authors = "";
                                locations = "";
                                abst = "";
                                doc = new HashMap<>();
                            }else{
                                first = false; //Next .I will be a new document
                            }
                            tag = "id";
                            id = elements[1];
                            break;
                        case ".T":
                            tag = "title";
                            break;
                        case ".A":
                            tag = "authors";
                            break;
                        case ".B":
                            tag = "locations";
                            break;
                        case ".W":
                            tag = "abst";
                            break;
                        default:
                            switch (tag){
                                case "id":
                                    break;
                                case "title":
                                    title = title.concat(line).concat(" ");
                                    break;
                                case "authors":
                                    authors = authors.concat(line).concat(" ");
                                    break;
                                case "locations":
                                    locations = locations.concat(line).concat(" ");
                                    break;
                                case "abst":
                                    abst = abst.concat(line).concat(" ");
                                    break;
                                default:
                                    System.out.println("ERROR PARSING DOCUMENTS - line: " + lineCount);
                                    break;
                            }
                            break;
                    }
                }
                //Add last document
                doc.put("ID", id);
                doc.put("Title", title);
                doc.put("Authors", authors);
                doc.put("Locations", locations);
                doc.put("Abstract", abst);
                docList.add(doc);

            } catch (Exception e) {
                System.out.println("ERROR PARSING DOCUMENTS - line: " + lineCount);
                e.printStackTrace();
            }

        }catch (Exception e){
            System.out.println("ERROR PARSING DOCUMENTS");
            e.printStackTrace();
            System.exit(1);
        }

        return docList;
    }

    public List<Map<String, String>> parseQueries(String dir){
        List<Map<String, String>> qryList = new ArrayList<Map<String, String>>();

        try{
            File file = new File(dir + "/cran.qry");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            HashMap<String, String> qry = new HashMap<>();
            String line;

            String id = "";
            int qryNumber = 1;
            String content = "";

            String tag = ".I";
            boolean first = true;
            int lineCount = 0;

            try {
                while ((line = reader.readLine()) != null) {

                    lineCount++;

                    //'?' not allowed as first character in WildcardQuery
                    line = line.replace("?", "");
                    String[] elements = line.split("\\s+");

                    switch (elements[0]){
                        case ".I":
                            if(!first){
                                //Beginning of a new query, every field of the previous is complete, so we initialize the query
                                qry.put("ID", id);
                                qry.put("Number", String.valueOf(qryNumber));
                                qry.put("Content", content);

                                //Query is added to the list of documents
                                qryList.add(qry);

                                //Fields and query reset to process the new query
                                id = "";
                                content = "";
                                qryNumber++;
                                qry = new HashMap<>();
                            }else{
                                first = false; //Next .I will be a new query
                            }
                            tag = "id";
                            id = elements[1];
                            break;
                        case ".W":
                            tag = "content";
                            break;
                        default:
                            switch (tag){
                                case "id":
                                    break;
                                case "content":
                                    content = content.concat(line).concat(" ");
                                    break;
                                default:
                                    System.out.println("ERROR PARSING QUERIES - line: " + lineCount);
                                    break;
                            }
                            break;
                    }
                }
                //Add last query
                qry.put("ID", id);
                qry.put("Number", String.valueOf(qryNumber));
                qry.put("Content", content);
                qryList.add(qry);

            } catch (Exception e) {
                System.out.println("ERROR PARSING QUERIES - line: " + lineCount);
                e.printStackTrace();
            }

        }catch (Exception e){
            System.out.println("ERROR PARSING QUERIES");
            e.printStackTrace();
            System.exit(1);
        }

        return qryList;
    }

}