
document.getElementById('form').addEventListener('submit', submitForm);
     function submitForm(event){
        event.preventDefault();
        const loadingContainer = document.querySelector('.loading-container');
        loadingContainer.style.display = 'block';

        const name = document.getElementById('uname').value;
        const number = document.getElementById('number').value;

        console.log("entered event", name, number, JSON.stringify({name,number}));
        // Send the user data to the backend
        fetch('/addUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
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
    function fetchUserDetails() {
        // Fetch all user details from the backend
        fetch('/all')
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