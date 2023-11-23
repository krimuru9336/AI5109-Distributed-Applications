
inject()

function inject() {
    // create a new `Date` object
    const now = new Date();

    // get the current date and time as a string
    const currentDateTime = now.toLocaleDateString("de-DE", { day: "2-digit", month: "2-digit", year: "numeric" });

    const elements = document.querySelectorAll('.info');

    elements.forEach((element) => {
        const p = document.createElement("p");
        p.classList.add("lead");
        p.innerHTML = "Nick Stolbov, Matrikel Nr.: 1269907, Date: " + currentDateTime;
        element.appendChild(p);
    })
}