const API_URL = "/api/staff/treatments";

const treatmentForm = document.getElementById("treatment-form");
const nameInput = document.getElementById("treatment-name");
const durationInput = document.getElementById("duration-minutes");
const priceInput = document.getElementById("treatment-price");
const activeInput = document.getElementById("treatment-active");
const submitButton = document.getElementById("submit-button");
const formTitle = document.getElementById("form-title");
const cancelEditButton = document.getElementById("cancel-edit-button");
const loadingMessage = document.getElementById("loading-message");
const errorMessage = document.getElementById("error-message");
const successMessage = document.getElementById("success-message");
const emptyState = document.getElementById("empty-state");
const treatmentsContainer = document.getElementById("treatments");
const treatmentCount = document.getElementById("treatment-count");

let editingTreatmentId = null;

const priceFormatter = new Intl.NumberFormat("el-GR", {
    style: "currency",
    currency: "EUR"
});

function getCsrfToken() {
    const cookies = document.cookie.split("; ");

    for (const cookie of cookies) {
        if (cookie.startsWith("XSRF-TOKEN=")) {
            return cookie.substring("XSRF-TOKEN=".length);
        }
    }

    return "";
}

function showError(message) {
    successMessage.hidden = true;
    errorMessage.textContent = message;
    errorMessage.hidden = false;
}

function showSuccess(message) {
    errorMessage.hidden = true;
    successMessage.textContent = message;
    successMessage.hidden = false;
}

function clearMessages() {
    errorMessage.hidden = true;
    successMessage.hidden = true;
}

async function getErrorMessage(response, defaultMessage) {
    try {
        const error = await response.json();
        return error.message || defaultMessage;
    } catch {
        return defaultMessage;
    }
}

function createActionButton(label, onClick) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "action-button";
    button.textContent = label;
    button.addEventListener("click", onClick);

    return button;
}

function createTreatmentCard(treatment) {
    const card = document.createElement("article");
    card.className = "treatment-card";

    if (!treatment.active) {
        card.classList.add("is-inactive");
    }

    const topRow = document.createElement("div");
    topRow.className = "treatment-top-row";

    const name = document.createElement("h3");
    name.textContent = treatment.name;

    const status = document.createElement("span");
    status.className = "status-badge";

    if (treatment.active) {
        status.classList.add("is-active");
        status.textContent = "Ενεργή";
    } else {
        status.classList.add("is-inactive");
        status.textContent = "Ανενεργή";
    }

    topRow.append(name, status);

    const details = document.createElement("p");
    details.className = "treatment-details";
    details.textContent = `Διάρκεια: ${treatment.durationMinutes} λεπτά`;

    const footer = document.createElement("div");
    footer.className = "treatment-footer";

    const price = document.createElement("p");
    price.className = "treatment-price";
    price.textContent = priceFormatter.format(treatment.price);

    const actions = document.createElement("div");
    actions.className = "treatment-actions";

    const editButton = createActionButton("Επεξεργασία", function () {
        startEditingTreatment(treatment);
    });

    actions.appendChild(editButton);

    if (treatment.active) {
        const deactivateButton = createActionButton("Απενεργοποίηση", function () {
            changeTreatmentStatus(treatment.id, "deactivate");
        });
        actions.appendChild(deactivateButton);
    } else {
        const activateButton = createActionButton("Ενεργοποίηση", function () {
            changeTreatmentStatus(treatment.id, "activate");
        });
        actions.appendChild(activateButton);
    }

    footer.append(price, actions);
    card.append(topRow, details, footer);

    return card;
}

function startEditingTreatment(treatment) {
    editingTreatmentId = treatment.id;
    nameInput.value = treatment.name;
    durationInput.value = treatment.durationMinutes;
    priceInput.value = treatment.price;
    activeInput.checked = treatment.active;
    formTitle.textContent = "Επεξεργασία θεραπείας";
    submitButton.textContent = "Αποθήκευση αλλαγών";
    cancelEditButton.hidden = false;
    clearMessages();
    treatmentForm.scrollIntoView({ behavior: "smooth", block: "start" });
}

function resetTreatmentForm() {
    editingTreatmentId = null;
    treatmentForm.reset();
    activeInput.checked = true;
    formTitle.textContent = "Νέα θεραπεία";
    submitButton.textContent = "Προσθήκη θεραπείας";
    cancelEditButton.hidden = true;
}

function getTreatmentFromForm() {
    return {
        name: nameInput.value.trim(),
        durationMinutes: Number(durationInput.value),
        price: Number(priceInput.value),
        active: activeInput.checked
    };
}

async function saveTreatment(event) {
    event.preventDefault();
    clearMessages();

    const treatment = getTreatmentFromForm();
    const isEditing = editingTreatmentId !== null;
    const url = isEditing ? `${API_URL}/${editingTreatmentId}` : API_URL;
    const method = isEditing ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getCsrfToken()
            },
            body: JSON.stringify(treatment)
        });

        if (!response.ok) {
            throw new Error(await getErrorMessage(
                response,
                "Δεν ήταν δυνατή η αποθήκευση της θεραπείας."
            ));
        }

        resetTreatmentForm();
        showSuccess(isEditing
            ? "Οι αλλαγές αποθηκεύτηκαν."
            : "Η θεραπεία προστέθηκε επιτυχώς."
        );
        loadTreatments();
    } catch (error) {
        showError(error.message);
    }
}

async function changeTreatmentStatus(treatmentId, action) {
    const actionMessage = action === "activate"
        ? "Θέλεις να ενεργοποιήσεις τη θεραπεία;"
        : "Θέλεις να απενεργοποιήσεις τη θεραπεία;";

    if (!window.confirm(actionMessage)) {
        return;
    }

    clearMessages();

    try {
        const response = await fetch(`${API_URL}/${treatmentId}/${action}`, {
            method: "PATCH",
            headers: {
                "X-XSRF-TOKEN": getCsrfToken()
            }
        });

        if (!response.ok) {
            throw new Error(await getErrorMessage(
                response,
                "Δεν ήταν δυνατή η αλλαγή της κατάστασης."
            ));
        }

        showSuccess(action === "activate"
            ? "Η θεραπεία ενεργοποιήθηκε."
            : "Η θεραπεία απενεργοποιήθηκε."
        );
        loadTreatments();
    } catch (error) {
        showError(error.message);
    }
}

async function loadTreatments() {
    loadingMessage.hidden = false;
    emptyState.hidden = true;
    treatmentsContainer.textContent = "";

    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Δεν ήταν δυνατή η φόρτωση των θεραπειών.");
        }

        const treatments = await response.json();
        treatmentCount.textContent = treatments.length;
        loadingMessage.hidden = true;

        if (treatments.length === 0) {
            emptyState.hidden = false;
            return;
        }

        for (const treatment of treatments) {
            const card = createTreatmentCard(treatment);
            treatmentsContainer.appendChild(card);
        }
    } catch (error) {
        loadingMessage.hidden = true;
        showError(error.message);
    }
}

treatmentForm.addEventListener("submit", saveTreatment);
cancelEditButton.addEventListener("click", resetTreatmentForm);

loadTreatments();
