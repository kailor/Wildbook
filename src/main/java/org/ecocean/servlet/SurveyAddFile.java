/*
 * The Shepherd Project - A Mark-Recapture Framework
 * Copyright (C) 2011 Jason Holmberg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.ecocean.servlet;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

import org.ecocean.CommonConfiguration;
import org.ecocean.Survey;
import org.ecocean.Shepherd;
import org.ecocean.SuperSpot;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

//import javax.jdo.*;
//import com.poet.jdo.*;


public class SurveyAddFile extends HttpServlet {

  static SuperSpot tempSuperSpot;

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    //set up our Shepherd
    String context="context0";
    context=ServletUtilities.getContext(request);
    Shepherd myShepherd = new Shepherd(context);
    myShepherd.setAction("SurveyAddFile.class");
    
    //setup data dir
    String rootWebappPath = getServletContext().getRealPath("/");
    File webappsDir = new File(rootWebappPath).getParentFile();
    File shepherdDataDir = new File(webappsDir, CommonConfiguration.getDataDirectoryName(context));
    //if(!shepherdDataDir.exists()){shepherdDataDir.mkdirs();}
    File surveyDir=new File(shepherdDataDir.getAbsolutePath()+"/surveys");
    if(!surveyDir.exists()) {
        surveyDir.mkdirs();
    }

    //set up for response
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    boolean locked = false;

    String fileName = "None";
    String surveyName = "None";

    try {
      MultipartParser mp = new MultipartParser(request, (CommonConfiguration.getMaxMediaSizeInMegabytes(context) * 1048576));
      Part part;
      while ((part = mp.readNextPart()) != null) {
        String name = part.getName();
        if (part.isParam()) {

          ParamPart paramPart = (ParamPart) part;
          String value = paramPart.getStringValue();
          if (name.equals("survey")) {
            surveyName = value;
          }
        }

        if (part.isFile()) {
          FilePart filePart = (FilePart) part;
          fileName = ServletUtilities.cleanFileName(filePart.getFileName());
          if (fileName != null) {

            File thisDir = new File(surveyDir.getAbsolutePath()+"/"+surveyName);
               

            if (!(thisDir.exists())) {
              thisDir.mkdirs();
            }
            long file_size = filePart.writeTo(new File(thisDir, fileName));
          }
        }
      }

      myShepherd.beginDBTransaction();
      if (myShepherd.isSurvey(surveyName)) {
        Survey sv = myShepherd.getSurvey(surveyName);

        try {

          sv.addDataFile(fileName);
          sv.addComments("<p><em>" + request.getRemoteUser() + " on " + (new java.util.Date()).toString() + "</em><br>" + "Submitted new file: " + fileName + ".</p>");
        } catch (Exception le) {
          locked = true;
          myShepherd.rollbackDBTransaction();
        }

        if (!locked) {
          myShepherd.commitDBTransaction();
          out.println(ServletUtilities.getHeader(request));
          out.println("<strong>Success:</strong> I have successfully uploaded your data file.");
          out.println("<p><a href=\""+request.getScheme()+"://" + CommonConfiguration.getURLLocation(request) + "/surveys/survey.jsp?surveyID=" + surveyName + "\">Return to survey " + surveyName + "</a></p>\n");
          out.println(ServletUtilities.getFooter(context));
          //String message="A new data file named "+fileName+" has been added to "+request.getParameter("individual")+".";
        } else {

          out.println(ServletUtilities.getHeader(request));
          out.println("<strong>Failure:</strong> I failed to add your file. This record is currently being modified by another user. Please try to add the file again in a few seconds.");
          out.println("<p><a href=\""+request.getScheme()+"://" + CommonConfiguration.getURLLocation(request) + "/surveys/survey.jsp?surveyID=" + surveyName + "\">Return to survey " + surveyName + "</a></p>\n");
          out.println(ServletUtilities.getFooter(context));
        }
      } else {
        myShepherd.rollbackDBTransaction();
        out.println(ServletUtilities.getHeader(request));
        out.println("<strong>Error:</strong> I was unable to upload your file. I cannot find the record that you intended it for in the database.");
        out.println(ServletUtilities.getFooter(context));

      }
    } catch (IOException lEx) {
      lEx.printStackTrace();
      out.println(ServletUtilities.getHeader(request));
      out.println("<strong>Error:</strong> I was unable to upload your file.");
      out.println(ServletUtilities.getFooter(context));


    }
    out.close();
    myShepherd.closeDBTransaction();
  }


}
	
	