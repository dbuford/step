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

/**fetches data from server and prints comments to page*/
        function getComments(){

            fetch("/blobstore-url").then(response => response.json()).then((returnData) => {
                console.log(returnData);
                let comments=returnData.listOfComments;
                const messageForm = document.getElementById('my-form');
                messageForm.action = returnData.uploadUrl;

            
            if(comments.length==0){
                const divElement=document.createElement('div');
                const titleElement=document.createElement("h2");
                titleElement.innerText="No Previous Comments Currently Available";
                divElement.appendChild(titleElement);
                const commentList = document.getElementById('comments-list');
                commentList.appendChild(divElement);
            }
            else{
                const pageList=document.getElementById('page-number');
                var pagenum=1;
                if(comments.length%5==0){
                    for(let k=0; k<Math.floor(comments.length/5);k++){
                    const pageButton=document.createElement("button");
                    pageButton.innerText=pagenum.toString();
                    pageList.appendChild(pageButton);
                    pageButton.addEventListener("click",createComments(comments,pagenum));
                    if(pagenum===1){
                       pageButton.click();
                     }
                    pagenum++;
                }

                }
                else{
                    for(let k=0; k<Math.floor(comments.length/5)+1;k++){
                        const pageButton=document.createElement("button");
                        pageButton.innerText=pagenum.toString();
                        pageList.appendChild(pageButton);
                        pageButton.addEventListener("click",createComments(comments,pagenum));
                        if(pagenum===1){
                            pageButton.click();
                        }
                        pagenum++;
                    }
                    }    
           }

        });
       
        }
        function createComments(comments,pagenum){
            return function(){
                 const commentList = document.getElementById('comments-list');
                commentList.innerHTML="";
                for(let i=pagenum*5-5;i<5*pagenum;i++){
                commentList.appendChild(createCommentElement(comments[i]));
                }

            }

        }

        /** Creates a list element to display the comment */
        function createCommentElement(comment) {
            const divElement = document.createElement('div');

            const imageElement=document.createElement("img");
            if(comment[4]==null){
                imageElement.src="/images/anon.jpg";
            }
            else{
            imageElement.src=comment[4];
            }
            divElement.appendChild(imageElement);

            const titleElement=document.createElement("h2");
            titleElement.innerText=comment[0];
            divElement.appendChild(titleElement);

            const emailElement=document.createElement("h5");
            emailElement.innerText='Email: ' + comment[1];
            divElement.appendChild(emailElement);
            const breakElement=document.createElement("br");
            divElement.appendChild(breakElement);
            const commentElement=document.createElement("p");
            commentElement.innerText = comment[2];
            divElement.appendChild(commentElement);

            const deleteButtonElement = document.createElement('button');
            deleteButtonElement.innerText = 'Delete';
            deleteButtonElement.addEventListener('click', () => {
            deleteComment(comment);

            // Remove the task from the DOM.
            divElement.remove();
            });

            divElement.appendChild(deleteButtonElement);

            return divElement;
        }

        /** Tells the server to delete the task. */
        function deleteComment(comment) {
            const params = new URLSearchParams();
            params.append('id', comment[3]);
            fetch('/delete-data', {method: 'POST', body: params});
        }

let map;

/* Editable marker that displays when a user clicks in the map. */
let editMarker;

/** Creates a map that allows users to add markers. */
function createMap() {
  map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 38.5949, lng: -94.8923}, zoom: 4});
  // Create the search box and link it to the UI element.
  var input = document.getElementById('pac-input');
  var searchBox = new google.maps.places.SearchBox(input);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener('bounds_changed', function() {
    searchBox.setBounds(map.getBounds());
  });

  // Listen for the event fired when the user selects a prediction and retrieve
  // more details for that place.
  searchBox.addListener('places_changed', function() {
    var places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }

    // For each place, get the icon, name and location.
    var bounds = new google.maps.LatLngBounds();
    places.forEach(function(place) {
      if (!place.geometry) {
        console.log("Returned place contains no geometry");
        return;
      }

      if (place.geometry.viewport) {
        // Only geocodes have viewport.
        bounds.union(place.geometry.viewport);
      } else {
        bounds.extend(place.geometry.location);
      }
    });
    map.fitBounds(bounds);
  });
  map.addListener('click', (event) => {
    createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });
  fetchMarkers();
}

/** Fetches markers from the backend and adds them to the map. */
function fetchMarkers() {
  fetch('/markers').then(response => response.json()).then((markers) => {
    markers.forEach(
        (marker) => {
           
        createMarkerForDisplay(marker.lat, marker.lng, marker.content, marker.address);
        createListForDisplay(marker.content,marker.address)});

});
}

/** Creates a marker that shows a read-only info window when clicked. */
function createMarkerForDisplay(lat, lng, content, address) {
  const marker =
      new google.maps.Marker({position: {lat: lat, lng: lng}, map: map});

      content= "<b>"+ content+ "</b>" + "<br>" + address;

  const infoWindow = new google.maps.InfoWindow({ content:content});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
    map.setZoom(9);
    map.setCenter(marker.getPosition());
  });
 
}

/** Sends a marker to the backend for saving. */
function postMarker(lat, lng, content, address) {
  const params = new URLSearchParams();
  params.append('lat', lat);
  params.append('lng', lng);
  params.append('content', content);
  params.append('address',address);
  
 

  fetch('/markers', {method: 'POST', body: params});
}

/** Creates a marker that shows a textbox the user can edit. */
function createMarkerForEdit(lat, lng) {
  // If we're already showing an editable marker, then remove it.
  if (editMarker) {
    editMarker.setMap(null);
  }

  editMarker =
      new google.maps.Marker({position: {lat: lat, lng: lng}, map: map});

  const infoWindow =
      new google.maps.InfoWindow({content: buildInfoWindowInput(lat, lng)});

  // When the user closes the editable info window, remove the marker.
  google.maps.event.addListener(infoWindow, 'closeclick', () => {
    editMarker.setMap(null);
  });

  infoWindow.open(map, editMarker);
}

/**
 * Builds and returns HTML elements that show an editable textbox and a submit
 * button.
 */
function buildInfoWindowInput(lat, lng) {
  const textBox = document.createElement('textarea');
  const button = document.createElement('button');
  button.appendChild(document.createTextNode('Submit'));

  var geocoder = new google.maps.Geocoder;
  var latlng = {lat: parseFloat(lat), lng: parseFloat(lng)};
  geocoder.geocode({'location': latlng}, function(results, status) {
     

 

  button.onclick = () => {
    postMarker(lat, lng, textBox.value, results[0].formatted_address);
    createMarkerForDisplay(lat, lng, textBox.value, results[0].formatted_address);
    createListForDisplay(textBox.value, results[0].formatted_address);
    
    editMarker.setMap(null);
  };
});

  const containerDiv = document.createElement('div');
  containerDiv.appendChild(textBox);
  containerDiv.appendChild(document.createElement('br'));
  containerDiv.appendChild(button);

  return containerDiv;
}

function createListForDisplay(text,address){
    const containerDiv=document.createElement('div');
    const textElement=document.createElement('h4');
    textElement.innerText=text;
    containerDiv.appendChild(textElement);
    const List= document.getElementById('bucket-list');
    List.appendChild(containerDiv);
}










    
