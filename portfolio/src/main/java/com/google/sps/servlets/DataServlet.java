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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String text = getParameter(request, "text-input", "");

    //create instance of DatastoreService to store entity
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("title", text);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    datastore.put(commentEntity);


    // Break the text into individual words.
    String[] words = text.split("\\s*,\\s*");

    // Respond with the result.
    response.setContentType("text/html;");
    response.getWriter().println("Hi "+ words[0]+ " ! My name is Dominique, and welcome to my Portfolio");
}
private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}

   