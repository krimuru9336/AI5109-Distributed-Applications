const url = 'https://cat-bmi-backend.azurewebsites.net/api/bmi';

function fetchBmis() {
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const bmiListDiv = document.getElementById('bmiList');
            bmiListDiv.innerHTML = data.map(bmi => `<tr><td>${bmi.name}</td><td>${bmi.gender}</td><td>${bmi.ribCage}</td><td>${bmi.legLength}</td><td>${parseFloat(bmi.bmi).toFixed(2)}</td></tr>`).join('');
        })
        .catch(error => console.error('Error fetching BMIs:', error));
}

function addBmi() {
    const name = document.getElementById('name').value;
    const gender = document.getElementById('gender').value;
    const ribCage = document.getElementById('ribCage').value;
    const legLength = document.getElementById('legLength').value;

    fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name,
                gender,
                ribCage,
                legLength
            })
        })
        .then(response => {
            if (response.ok) {
                fetchBmis();
            } else {
                console.error('Error adding BMI:', response.statusText);
            }
        })
        .catch(error => console.error('Error adding BMI:', error));
}

fetchBmis();