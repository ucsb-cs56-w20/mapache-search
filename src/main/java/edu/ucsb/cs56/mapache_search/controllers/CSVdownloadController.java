package edu.ucsb.mapache_search.controller;

import edu.ucsb.mapache_search.downloaders.MapacheoCSV;
import edu.ucsb.mapache_search.entities.UserVote;
import edu.ucsb.mapache_search.entities.AppUser;
import edu.ucsb.mapache_search.entities.ResultTag;
import edu.ucsb.mapache_search.entities.SearchResultEntity;
import edu.ucsb.mapache_search.entities.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ucsb.cs56.mapache_search.instructorDashboard.CSVdownload.html;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CSVDownloadController {

    @Autowired
    private UserVote uservote;

    @GetMapping("/mapacheCSV")
    public void downloadCSV() throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=courses.csv");

        String json = curriculumService.getJSON(subjectArea, quarter, courseLevel);

        CoursePage cp = CoursePage.fromJSON(json);

        MapachetoCSV.writeSections(response.getWriter(), cp);
    }

    

}