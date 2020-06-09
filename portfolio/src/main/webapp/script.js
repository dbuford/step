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







    
