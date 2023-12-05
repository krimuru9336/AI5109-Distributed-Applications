/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 23/11/2023
 */

// Submits data when Submit button is clicked. data is read from the form.
document.getElementById('sub-btn').addEventListener('click', function (e) {
    const inputs = document.getElementsByClassName('form-inputs');
    const params = {};

    for (const i of inputs) {
        params[i.name] = i.value;
    }

    fetch('/user', {
        method: 'POST',
        body: JSON.stringify(params),
        headers: { 'content-type': 'application/json' },
    })
        .then(r => r.json())
        .then(res => {
            const resSpan = document.getElementById('res');
            if (res.ok) {
                resSpan.classList.add('success');
                resSpan.classList.remove('error');
            } else {
                resSpan.classList.remove('success');
                resSpan.classList.add('error');
            }

            const uid_msg = "    Your uid: ";
            resSpan.innerText = res.message + `${res.ok ? uid_msg + res.payload.uid : ''}`;

        }).catch(err => {
            console.error(err);
        })
})



