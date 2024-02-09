function submitForm(event) {
    event.preventDefault(); // Prevent default form submission behavior

    const formData = new FormData(document.getElementById('userForm'));

    fetch('http://74.235.107.94:8080/api/saveUser', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        return response;
    })
    .then(() => {
        // Clear form fields
        document.getElementById('fname').value = '';
        document.getElementById('phone').value = '';

        // Fetch latest user data and update table
        fetchLatestUserData();
    })
    .catch(error => {
        console.error('Error saving user data:', error);
    });
}

function fetchLatestUserData() {
    fetch('http://74.235.107.94:8080/api/users')
    .then(response => response.json())
    .then(users => {
        const tableBody = document.getElementById('userTableBody');
        tableBody.innerHTML = ''; // Clear existing table body

        // Append new rows for each user
        users.forEach(user => {
            const row = tableBody.insertRow();
            const nameCell = row.insertCell(0);
            const phoneCell = row.insertCell(1);

            nameCell.textContent = user.name;
            phoneCell.textContent = user.phone;
        });
    })
    .catch(error => {
        console.error('Error fetching user data:', error);
    });
}

// Fetch initial user data when the page loads
fetchLatestUserData();