// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/** Servlet that returns some example content. TODO: modify this file to handle comments data **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String hobbies = getParameter(request, "hobbies-input", "");
    String contact= getParameter(request, "contact-input", "");
    long timestamp = System.currentTimeMillis();


    if(contact.equals("")){
        response.setContentType("text/html");
        response.getWriter().println("Please enter a valid name and email");
        return;
    }
    

    // Break the text into individual words.
    String[] words = contact.split("\\s*,\\s*");

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("body",hobbies);
    commentEntity.setProperty("name", words[0]);
    commentEntity.setProperty("email", words[1]);
    commentEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.setContentType("text/html");
    
     /* Redirect back to the HTML page.*/
    response.sendRedirect("response.html");

  }
    
private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
 @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //create instance of DatastoreService to store entity
    
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);


    ArrayList<ArrayList> comments = new ArrayList<>();

    for (Entity entity : results.asIterable()){
        String name = (String) entity.getProperty("name");
        String email= (String) entity.getProperty("email");
        String body= (String) entity.getProperty("body");
        long myid = entity.getKey().getId();
        Long id=new Long(myid);


        ArrayList<Object> info=new ArrayList<>();
        info.add(name);
        info.add(email);
        info.add(body);
        info.add(id);

        comments.add(info);
    }
       
    String json=convertToJsonUsingGson(comments);

    // Send the JSON as the response

    response.setContentType("application/json;");
    response.getWriter().println(json);
    
       

  }
 private String convertToJsonUsingGson(ArrayList messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}
  

