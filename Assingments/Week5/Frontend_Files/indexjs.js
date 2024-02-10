/*
Author : Sheikh Zubeena Shireen
ScreenCaptured Date : 16/11/2023
Matriculation Number : 1492765
 */
document.getElementById('form').addEventListener('submit', submitForm);
     function submitForm(event){
        event.preventDefault();
        const loadingContainer = document.querySelector('.loading-container');
        loadingContainer.style.display = 'block';

        const name = document.getElementById('uname').value;
        const number = document.getElementById('number').value;

        console.log("entered event", name, number, JSON.stringify({name,number}));
        // Send the user data to the backend
        // http://20.92.165.215:8443/
        // fetch('/addUser', {
            
        fetch('https://20.92.165.215:8443/addUser', {
            method: 'POST',
			//  mode: 'no-cors',
            headers: {
                'x-ms-blob-content-type': 'application/*+json',
				'Content-Type':'application/json',
				'Accept':'*/*',
                'Connection':'keep-alive',
               'Access-Control-Allow-Origin':'*'
			  
            },
            body: JSON.stringify({"name":name,"number":number})
        })
        .then(response => { loadingContainer.style.display = 'none';
                               if (response.status === 200) {
                                  return response.text();
                                } else {
                                  throw new Error(`Error fetching response: ${response.status}`);
                                }})
        .then(data => {
        loadingContainer.style.display = 'none';
            console.log('Data added:', data);
            // Call function to update table with the latest data
            if (data == "true"){
            fetchUserDetails();
            } else{
            window.alert("Please enter a valid phone number");
            }
        })
        .catch(error => {loadingContainer.style.display = 'none';
                        console.error('Error:', error)});

    }
/*Author : Sheikh Zubeena Shireen    ScreenCaptured Date : 16/11/2023   Matriculation Number : 1492765*/
    function fetchUserDetails() {
        // Fetch all user details from the backend
        // fetch('/all')
        fetch('https://20.92.165.215:8443/all', {
    method: 'GET',
    // mode: 'no-cors', // Set the mode to 'no-cors',
	headers:{
                // 'x-ms-blob-content-type': 'application/json',
				'Content-Type':'application/json'}
})
        .then(response => response.json())
        .then(data => { console.log(data);
            displayUserDetails(data);
        })
        .catch(error => console.error('Error:', error));
    }
    function displayUserDetails(userData) {
            const table = document.getElementById('usersTableBody');
            table.innerHTML = '';

            console.log("fetched data of form", userData);

            userData.forEach(user => {
                const row = table.insertRow();
                const cell1 = row.insertCell(0);
                const cell2 = row.insertCell(1);
                const cell3 = row.insertCell(2);
                cell1.innerHTML = user.id;
                cell2.innerHTML = user.name;
                cell3.innerHTML = user.number;
            });
        }

/*Author : Sheikh Zubeena Shireen    ScreenCaptured Date : 7/11/2023   Matriculation Number : 1492765*/
    document.addEventListener('DOMContentLoaded', function(){

    function checkFields() {
        const nameValue = document.getElementById('uname').value;
        const numberValue = document.getElementById('number').value;
        const submitButton = document.getElementById('submitButton');

        // Enable the submit button only when both fields have values
        if (nameValue && numberValue) {
            submitButton.disabled = false;
        } else {
            submitButton.disabled = true;
        }

    };

document.getElementById('uname').addEventListener('input', checkFields);
    document.getElementById('number').addEventListener('input', checkFields);
});