package edu.ucsb.cs56.mapache_search.downloader;

import java.io.PrintWriter;

import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.Item;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.Tag;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchTermsRepository;
import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.repositories.ResultTagRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

public class MapachetoCSV {
  

  private static final Logger logger = LoggerFactory.getLogger(MapachetoCSV.class);


  public static void writeSections(PrintWriter writer, SearchResultRepository searchrepos) {
    
    List<SearchResultEntity> Searchresult= searchrepos.findAll();
    String[] CSV_HEADER = { "HtmlTitle","Url", "Total Vote"};
    try (CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);) {
      csvWriter.writeNext(CSV_HEADER);

        for(int i=0;i<Searchresult.size();i++){
            String data [] = {Searchresult.get(i).getHtmlTitle(),Searchresult.get(i).getLink(),Long.toString(Searchresult.get(i).getVotecount())}; 
            
            csvWriter.writeNext(data);
            if(i>500)
              break;
        }
      logger.info("CSV generated successfully");
    } catch (Exception e) {
      logger.error("CSV generation error", e);
    }
  }

  public static void UserwriteSections(PrintWriter writer,UserRepository userRepository, ResultTagRepository resultTagRepository) {
    
    List<AppUser> Users= userRepository.findAll();

    String[] CSV_HEADER = { "Username","Tag","Html Title","Url", "Vote"};
    try (CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);) {
      csvWriter.writeNext(CSV_HEADER);

        for(int i=0;i<Users.size();i++){
          List<ResultTag> resultTag = resultTagRepository.findByUser(Users.get(i));
            for(int j=0;j<resultTag.size();j++){
              String data [] = {Users.get(i).getUsername(),resultTag.get(j).getTag().getName(),
              resultTag.get(j).getResult().getHtmlTitle(),resultTag.get(j).getResult().getLink(),resultTag.get(j).getResult().getVotecount().toString()};
              csvWriter.writeNext(data);
            }
            if(i>500)
              break;
        }
      logger.info("CSV generated successfully");
    } catch (Exception e) {
      logger.error("CSV generation error", e);
    }
  }
  
}