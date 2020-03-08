package edu.ucsb.cs56.mapache_search.controllers;

import edu.ucsb.cs56.mapache_search.downloader.MapachetoCSV;
import edu.ucsb.cs56.mapache_search.entities.UserVote;
import edu.ucsb.cs56.mapache_search.entities.AppUser;
import edu.ucsb.cs56.mapache_search.entities.ResultTag;
import edu.ucsb.cs56.mapache_search.entities.SearchResultEntity;
import edu.ucsb.cs56.mapache_search.entities.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CSVDownloadController {


    @GetMapping("/VoteHistory")
    public void downloadCSV(HttpServletResponse response) throws IOException {
        response.setContentType("VoteHistory/csv");
        response.setHeader("Content-Disposition", "attachment; file=VoteHistory.csv");


        MapachetoCSV.writeSections(response.getWriter());
    }

    

}