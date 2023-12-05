# Week5 assignment for Distributed application

## Front-end repository

### By iman.emadi@informatik.hs-fulda.de

- server repository: https://github.com/Iman-Emadi/distributed_apps_week4

**This Repository contains the front-end for the Assignment week5**, which is basically a single `html` file, used to display a form with
2 text inputs and a button.

A Javascript code is written to handle the `onClick` event for the button.

```js
function submitForm() {
  var lat = document.getElementById("latitude").value;
  var long = document.getElementById("longitude").value;

  fetch(`http://20.56.22.249:80/getData?lat=${lat}&long=${long}`, {
    method: "GET",
  })
    .then((r) => r.json())
    .then((r) => {
      const rBox = document.getElementById("response-box");
      const txtSpan = document.getElementById("textSpan");
      rBox.style.display = "block";
      txtSpan.innerText = r.message;
    });
}
```

This function is executed when user clicks on the button to submit the form.\
The values of the Inputs are accessed and put into the request url, then it sends a GET request to the server and fetches the response.
\
The response is turned into the `JSON` format.\
The server response is accessed in the response JSON, and displayed in the specified html tag.

- Please find the related screens shots in the submitted PDF document.
