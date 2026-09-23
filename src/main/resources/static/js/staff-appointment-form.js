const appointmentForm = document.getElementById("appointment-form");
const customerSelect = document.getElementById("customer-id");
const treatmentSelect = document.getElementById("treatment-id");
const appointmentDateInput = document.getElementById("appointment-date");
const startTimeSelect = document.getElementById("start-time");
const endTimeInput = document.getElementById("end-time");
const errorMessage = document.getElementById("error-message");
const successMessage = document.getElementById("success-message");

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

async function getErrorMessage(response, defaultMessage) {
    try {
        const error = await response.json();
        return error.message || defaultMessage;
    } catch {
        return defaultMessage;
    }
}

function addPlaceholder(select, text) {
    select.textContent = "";

    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = text;
    select.appendChild(placeholder);
}

function loadTimeOptions() {
    addPlaceholder(startTimeSelect, "Επίλεξε ώρα");

    for (let hour = 9; hour < 18; hour++) {
        const time = `${String(hour).padStart(2, "0")}:00`;
        const option = document.createElement("option");
        option.value = time;
        option.textContent = time;
        startTimeSelect.appendChild(option);
    }
}

function updateEndTime() {
    if (!startTimeSelect.value) {
        endTimeInput.value = "";
        return;
    }

    const startHour = Number(startTimeSelect.value.substring(0, 2));
    endTimeInput.value = `${String(startHour + 1).padStart(2, "0")}:00`;
}

async function loadCustomers() {
    try {
        const response = await fetch("/api/staff/customers");

        if (!response.ok) {
            throw new Error("Δεν ήταν δυνατή η φόρτωση των πελατών.");
        }

        const customers = await response.json();
        addPlaceholder(customerSelect, "Επίλεξε πελάτη");

        for (const customer of customers) {
            const option = document.createElement("option");
            option.value = customer.id;
            option.textContent = `${customer.fullName} — ${customer.email}`;
            customerSelect.appendChild(option);
        }
    } catch (error) {
        showError(error.message);
    }
}

async function loadTreatments() {
    try {
        const response = await fetch("/api/treatments");

        if (!response.ok) {
            throw new Error("Δεν ήταν δυνατή η φόρτωση των θεραπειών.");
        }

        const treatments = await response.json();
        addPlaceholder(treatmentSelect, "Επίλεξε θεραπεία");

        for (const treatment of treatments) {
            const option = document.createElement("option");
            option.value = treatment.id;
            option.textContent = `${treatment.name} — ${treatment.durationMinutes} λεπτά`;
            treatmentSelect.appendChild(option);
        }
    } catch (error) {
        showError(error.message);
    }
}

async function createAppointment(event) {
    event.preventDefault();

    const appointment = {
        customerId: Number(customerSelect.value),
        treatmentId: Number(treatmentSelect.value),
        appointmentDate: appointmentDateInput.value,
        startTime: startTimeSelect.value,
        endTime: endTimeInput.value
    };

    try {
        const response = await fetch("/api/staff/appointments", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getCsrfToken()
            },
            body: JSON.stringify(appointment)
        });

        if (!response.ok) {
            throw new Error(await getErrorMessage(
                response,
                "Δεν ήταν δυνατή η δημιουργία του ραντεβού."
            ));
        }

        appointmentForm.reset();
        endTimeInput.value = "";
        showSuccess("Το ραντεβού δημιουργήθηκε επιτυχώς.");
    } catch (error) {
        showError(error.message);
    }
}

startTimeSelect.addEventListener("change", updateEndTime);
appointmentForm.addEventListener("submit", createAppointment);

appointmentDateInput.min = new Date().toISOString().substring(0, 10);
loadTimeOptions();
loadCustomers();
loadTreatments();
