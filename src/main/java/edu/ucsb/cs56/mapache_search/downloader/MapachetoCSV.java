package edu.ucsb.cs56.ucsb_courses_search.downloaders;

import java.io.PrintWriter;

import edu.ucsb.cs56.ucsbapi.academics.curriculums.v1.classes.CoursePage;
import edu.ucsb.cs56.ucsbapi.academics.curriculums.v1.classes.Course;
import edu.ucsb.cs56.ucsbapi.academics.curriculums.v1.classes.Section;
import edu.ucsb.cs56.ucsbapi.academics.curriculums.v1.classes.Instructor;
import edu.ucsb.cs56.ucsbapi.academics.curriculums.v1.classes.TimeLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class CoursePageToCSV {

  private static final Logger logger = LoggerFactory.getLogger(CoursePageToCSV.class);

  public static String instructorsToString(List<Instructor> instructors) {
    switch (instructors.size()) {
    case 0:
      return "";
    case 1:
      return instructors.get(0).instructor;
    default:
      return instructors.stream().map(i -> i.instructor).collect(Collectors.toList()).toString();
    }
  }

  public static String secondaryStatus(Section s) {
    if (s.secondaryStatus==null)
      return "Section";
    if (s.secondaryStatus.equals("R"))
      return "Lecture";
    return "Section";
  }

  public static void writeSections(PrintWriter writer, CoursePage cp) {
    String[] CSV_HEADER = { "quarter", "courseId", "title", "type", "lectureInstructor", "sectionInstructor", "days", "beginTime", "endTime" };
    try (CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);) {
      csvWriter.writeNext(CSV_HEADER);
      for (Course c : cp.classes) {
        String lectureInstructor = instructorsToString(c.getClassSections().get(0).instructors);
        for (Section s : c.getClassSections()) {
          for (TimeLocation t : s.timeLocations) {
            String[] data = { c.getQuarter(), c.getCourseId(), c.getTitle(), secondaryStatus(s), lectureInstructor,
                instructorsToString(s.instructors), t.days, t.beginTime,
                t.endTime };
            csvWriter.writeNext(data);

          } // t
        } // s
      } // c

      logger.info("CSV generated successfully");
    } catch (Exception e) {
      logger.error("CSV generation error", e);
    } // try
  }
}