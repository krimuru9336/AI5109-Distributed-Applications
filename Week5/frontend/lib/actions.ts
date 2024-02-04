'use server';

export async function calculateBMI(formData: FormData) {
    const name = formData.get('name');
    const height = formData.get('height');
    const weight = formData.get('weight');

    await fetch('http://localhost:8080/bmi/calculateBMI', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, height, weight })
    }).then(response => response.text());
}

export async function getBMIList() {
    const list = await fetch('http://localhost:8080/bmi/').then(response => response.json());
    return list;
}