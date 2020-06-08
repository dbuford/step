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

/**
 * When the fetch() function requests the /blobstore-url URL, the content of the response is
 * the URL that allows a user to upload a file to Blobstore.
 */
@WebServlet("/blobstore-url")
public class BlobstoreUploadServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/my-form-handler");


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
        String url= (String) entity.getProperty("image");
        String url2=uploadUrl;
        Long id=new Long(myid);


        ArrayList<Object> info=new ArrayList<>();
        info.add(name);
        info.add(email);
        info.add(body);
        info.add(id);
        info.add(url);
        info.add(url2);

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