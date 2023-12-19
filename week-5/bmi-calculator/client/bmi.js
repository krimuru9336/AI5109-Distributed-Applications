const url = 'http://localhost:8080';

function fetchBmis() {
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const bmiListDiv = document.getElementById('bmiList');
            console.log("HEEY", data);
            bmiListDiv.innerHTML = data.map(bmi => `<tr><td>${bmi.name}</td><td>${bmi.height}</td><td>${bmi.weight}</td><td>${bmi.bmiValue}</td></tr>`).join('');
        })
        .catch(error => console.error('Error fetching BMIs:', error));
}

function addBmi() {

    const name = document.getElementById('name').value;
    const weight = document.getElementById('weight').value;
    const height = document.getElementById('height').value;

    fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name,
                weight,
                height
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