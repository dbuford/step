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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
/**fetches data from server and prints comments to page*/
        function getComments(){
            fetch("/data").then(response => response.json()).then((comments) => {
            console.log(comments.toString());
            console.log(5+6);
            const commentList = document.getElementById('comments-list');
            commentList.innerHTML="";
            for(let i=0; i<=4&& i<=comments.length-1;i++){
                commentList.appendChild(createCommentElement(comments[i]));
            }
                }); 
        }

        /** Creates a list element to display the comment */
        function createCommentElement(comment) {
            const divElement = document.createElement('div');

            const imageElement=document.createElement("img");
            imageElement.src="/images/anon.jpg";
            divElement.appendChild(imageElement);

            const titleElement=document.createElement("h2");
            titleElement.innerText=comment[0];
            divElement.appendChild(titleElement);

            const emailElement=document.createElement("h5");
            emailElement.innerText='Email: ' + comment[1];
            divElement.appendChild(emailElement);
            const commentElement=document.createElement("p");
            commentElement.innerText = 'Favorite Activities: '+ comment[2];
            divElement.appendChild(commentElement);

            
            

            return divElement;
        }






