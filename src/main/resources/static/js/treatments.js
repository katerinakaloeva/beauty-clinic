const treatmentsContainer = document.getElementById("treatments");
const loadingMessage = document.getElementById("loading-message");
const errorMessage = document.getElementById("error-message");

const priceFormatter = new Intl.NumberFormat("el-GR", {
    style: "currency",
    currency: "EUR"
});

function createTreatmentCard(treatment) {
    const card = document.createElement("article");
    card.className = "treatment-card";

    const icon = document.createElement("div");
    icon.className = "treatment-icon";
    icon.textContent = "✦";

    const name = document.createElement("h3");
    name.textContent = treatment.name;

    const metadata = document.createElement("p");
    metadata.className = "treatment-meta";
    metadata.textContent = `Διάρκεια: ${treatment.durationMinutes} λεπτά`;

    const price = document.createElement("p");
    price.className = "treatment-price";
    price.textContent = priceFormatter.format(treatment.price);

    card.append(icon, name, metadata, price);

    return card;
}

async function loadTreatments() {
    try {
        const response = await fetch("/api/treatments");

        if (!response.ok) {
            throw new Error("Δεν ήταν δυνατή η φόρτωση των θεραπειών.");
        }

        const treatments = await response.json();

        loadingMessage.hidden = true;

        if (treatments.length === 0) {
            loadingMessage.textContent = "Δεν υπάρχουν διαθέσιμες θεραπείες.";
            loadingMessage.hidden = false;
            return;
        }

        treatments.forEach(treatment => {
            treatmentsContainer.appendChild(createTreatmentCard(treatment));
        });
    } catch (error) {
        loadingMessage.hidden = true;
        errorMessage.textContent = error.message;
        errorMessage.hidden = false;
    }
}

loadTreatments();
